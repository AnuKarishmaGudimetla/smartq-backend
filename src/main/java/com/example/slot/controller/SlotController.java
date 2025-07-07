package com.example.slot.controller;

import java.security.Principal;
import java.time.LocalDate;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.slot.dto.ApiResponse;
import com.example.slot.dto.SlotResponse;
import com.example.slot.service.SlotService;

@RestController
@RequestMapping("/api/slots")
public class SlotController {
    private final SlotService service;
    public SlotController(SlotService service) { this.service = service; }
    // Public student endpoints
    @GetMapping("/available")
    public ResponseEntity<List<SlotResponse>> available() {
        return ResponseEntity.ok(service.getAvailableSlots());
    }
    @GetMapping("/available/{date}")
    @PreAuthorize("hasAuthority('ROLE_STUDENT')")
    public ResponseEntity<List<SlotResponse>> getAvailableSlotsByDate(
    @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
    return ResponseEntity.ok(service.getAvailableSlotsForDate(date));
    }
    @PostMapping("/book/{slotId}")
    @PreAuthorize("hasAuthority('ROLE_STUDENT')")
    public ResponseEntity<ApiResponse> book(@PathVariable String slotId,
                                            Principal principal) {
        String msg = service.bookSlot(slotId, principal.getName());
        return ResponseEntity.ok(new ApiResponse(msg));
    }
    @GetMapping("/my")
    @PreAuthorize("hasAuthority('ROLE_STUDENT')")
    public ResponseEntity<List<SlotResponse>> myBookings(Principal principal) {
        return ResponseEntity.ok(service.getMyBookings(principal.getName()));
    }
    @PutMapping("/cancel/{slotId}")
    @PreAuthorize("hasAnyAuthority('ROLE_STUDENT', 'ROLE_ADMIN')")
    public ResponseEntity<ApiResponse> cancel(@PathVariable String slotId,
                                          Principal principal) {
    String username = principal.getName();
    String msg = service.cancelSlot(slotId, username); 
    return ResponseEntity.ok(new ApiResponse(msg));
    }
    // Admin-only endpoints
    @PostMapping("/admin/create/{date}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<?> createSlots(
    @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
    try {
        List<SlotResponse> slots = service.createSlotsForDate(date);
        return ResponseEntity.ok(slots);
    } catch (RuntimeException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
    } catch (Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                             .body("Unexpected error: " + ex.getMessage());
    }
    }
    @GetMapping("/admin/all")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<List<SlotResponse>> allSlots() {
    return ResponseEntity.ok(service.getAllSlots());
    }
    @DeleteMapping("/admin/delete/{date}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<ApiResponse> deleteSlotsByDate(
    @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
    String message = service.deleteSlotsByDate(date);
    return ResponseEntity.ok(new ApiResponse(message));
}
}