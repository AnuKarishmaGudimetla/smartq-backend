package com.example.slot.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.example.slot.dto.SlotResponse;
import com.example.slot.exception.SlotAlreadyBookedException;
import com.example.slot.exception.SlotNotFoundException;
import com.example.slot.exception.SlotUnavailableException;
import com.example.slot.model.Slott;
import com.example.slot.repository.SlotRepository;
import com.example.slot.repository.UserRepository;

@Service
public class SlotService {
    private final SlotRepository repo;
    private final UserRepository userRepo;
    private final int slotDuration;
    private final int startHour;
    private final int endHour;
    private final int lunchStart;
    private final int lunchEnd;

    @Autowired
    private JavaMailSender mailSender;
    @Autowired
    private MailService mailService;


    public SlotService(SlotRepository repo,
                       UserRepository userRepo,
                       @Value("${app.slot.duration}") int slotDuration,
                       @Value("${app.slot.start}") int startHour,
                       @Value("${app.slot.end}") int endHour,
                       @Value("${app.slot.lunchbreak.start}") int lunchStart,
                       @Value("${app.slot.lunchbreak.end}") int lunchEnd) {
        this.repo = repo;
        this.userRepo = userRepo;
        this.slotDuration = slotDuration;
        this.startHour = startHour;
        this.endHour = endHour;
        this.lunchStart = lunchStart;
        this.lunchEnd = lunchEnd;
    }

    public List<SlotResponse> getAvailableSlots() {
        LocalDateTime now = LocalDateTime.now();
        return repo.findByStatusAndStartTimeAfterOrderByStartTime(
                Slott.Status.AVAILABLE, now)
            .stream()
            .map(this::toDto)
            .collect(Collectors.toList());
    }
    public List<SlotResponse> getAvailableSlotsForDate(LocalDate date) {
    LocalDateTime now = LocalDateTime.now();

    // If student selected TODAY, don't show past time slots
    LocalDateTime startTime = date.equals(now.toLocalDate()) ? now : date.atStartOfDay();

    LocalDateTime endTime = date.atTime(23, 59, 59);

    return repo.findByStatusAndStartTimeBetweenOrderByStartTime(
            Slott.Status.AVAILABLE, startTime, endTime)
        .stream()
        .map(this::toDto)
        .collect(Collectors.toList());
    }
    public List<SlotResponse> getMyBookings(String username) {
        return repo.findByBookedBy(username)
            .stream()
            .map(this::toDto)
            .collect(Collectors.toList());
    }
    public String bookSlot(String slotId, String username) {
    Slott slot = repo.findById(slotId)
        .orElseThrow(() -> new SlotNotFoundException("Slot not found"));

    if (slot.getStatus() != Slott.Status.AVAILABLE ||
        slot.getStartTime().isBefore(LocalDateTime.now())) {
        throw new SlotUnavailableException("Slot not available");
    }
    LocalDate slotDate = slot.getStartTime().toLocalDate();
    LocalDateTime startOfDay = slotDate.atStartOfDay();
    LocalDateTime endOfDay = slotDate.atTime(23, 59, 59);
    List<Slott> existing = repo.findByBookedByAndStartTimeBetween(username, startOfDay, endOfDay);

    if (!existing.isEmpty()) {
        throw new SlotAlreadyBookedException("You already booked a slot on this day.");
    }
    slot.setStatus(Slott.Status.BOOKED);
    slot.setBookedBy(username);
    repo.save(slot);
    userRepo.findByUsername(username).ifPresent(user -> {
        String slotDetails = "Slot booked:\nDate: " + slot.getStartTime().toLocalDate() +
                             "\nTime: " + slot.getStartTime().toLocalTime() + " - " +
                             slot.getEndTime().toLocalTime();
        mailService.sendSlotBookingEmail(user.getEmail(), slotDetails);
    });
    return "Slot booking successful";
    }
    public String cancelSlot(String slotId, String username) {
        Slott slot = repo.findById(slotId)
            .orElseThrow(() -> new SlotNotFoundException("Slot not found"));
        LocalDateTime now = LocalDateTime.now();
        if (slot.getStartTime().isBefore(now)) {
            return "Cannot cancel past or ongoing slot.";
        }
        boolean isAdmin = userRepo.findByUsername(username)
                           .map(user -> user.getRole().name().equalsIgnoreCase("ADMIN"))
                           .orElse(false);
        String bookedBy = slot.getBookedBy();
        if (isAdmin) {
            slot.setStatus(Slott.Status.CANCELLED);
            // send cancellation email to the user
            if (bookedBy != null) {
                userRepo.findByUsername(bookedBy).ifPresent(user -> {
                    sendCancellationEmail(user.getEmail(), slot);
                });
            }
        } else {
            if (!username.equals(bookedBy)) {
                throw new SlotUnavailableException("You are not allowed to cancel this slot.");
            }
            slot.setStatus(Slott.Status.AVAILABLE);
        }
        slot.setBookedBy(null);
        repo.save(slot);
        return isAdmin ? "Slot has been cancelled by admin." : "Slot cancelled and available for booking.";
    }

    private void sendCancellationEmail(String to, Slott slot) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Your Slot Has Been Cancelled");
        message.setText("Dear " + to + ",\n\nYour slot scheduled on "
            + slot.getStartTime().toLocalDate() + " at "
            + slot.getStartTime().toLocalTime()
            + " has been cancelled by the admin.\n\nRegards,\nSlot Booking Team");
        mailSender.send(message);
    }
    public List<SlotResponse> createSlotsForDate(LocalDate date) {
    LocalTime time = LocalTime.of(startHour, 0);
    List<SlotResponse> createdSlots = new ArrayList<>();

    while (time.isBefore(LocalTime.of(endHour, 0))) {
        if (time.getHour() < lunchStart || time.getHour() >= lunchEnd) {
            LocalDateTime start = LocalDateTime.of(date, time);
            LocalDateTime end = start.plusMinutes(slotDuration);

            // ❗ Check if a slot with this time already exists
            boolean exists = repo.existsByStartTimeAndEndTime(start, end);
            if (!exists) {
                Slott slot = new Slott(start, end);
                repo.save(slot);
                createdSlots.add(toDto(slot));
            }
        }
        time = time.plusMinutes(slotDuration);
    }

    if (createdSlots.isEmpty()) {
        throw new RuntimeException("All slots for this date already exist.");
    }

    return createdSlots;
   }
    @Scheduled(fixedRate = 60000) // runs every 1 minute
    public void updateSlotStatusesScheduler() {
    LocalDateTime now = LocalDateTime.now();
    // 1. Expire slots that ended already
    List<Slott> expiredSlots = repo.findByStatusAndEndTimeBefore(Slott.Status.AVAILABLE, now);
    for (Slott slot : expiredSlots) {
        slot.setStatus(Slott.Status.EXPIRED);
        repo.save(slot);
    }
    // 2. Mark currently ongoing slots
    List<Slott> ongoingSlots = repo.findByStatus(Slott.Status.AVAILABLE).stream()
        .filter(slot -> !slot.getStartTime().isAfter(now) && slot.getEndTime().isAfter(now))
        .collect(Collectors.toList());

    for (Slott slot : ongoingSlots) {
        slot.setStatus(Slott.Status.ONGOING);
        repo.save(slot);
    }
    }
    @Scheduled(cron = "0 0 0 * * ?")
    public void scheduleNextDaySlots() {
        createSlotsForDate(LocalDate.now().plusDays(1));
    }

    public List<SlotResponse> getAllSlots() {
        return repo.findAll()
                   .stream()
                   .map(this::toDto)
                   .collect(Collectors.toList());
    }
    public String deleteSlotsByDate(LocalDate date) {
        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.atTime(23, 59, 59);
        List<Slott> slots = repo.findByStartTimeBetween(startOfDay, endOfDay);
        if (slots.isEmpty()) {
            return "No slots found on " + date;
        }
        repo.deleteAll(slots);
        return "Deleted " + slots.size() + " slot(s) on " + date;
    }

    private SlotResponse toDto(Slott s) {
        return new SlotResponse(
            s.getId(), s.getStartTime(), s.getEndTime(),
            s.getStatus().name(), s.getBookedBy());
    }
}
