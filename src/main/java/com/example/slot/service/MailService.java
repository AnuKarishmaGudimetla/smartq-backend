package com.example.slot.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class MailService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendSlotCancellationEmail(String to, String slotInfo) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Slot Cancellation Notice");
        message.setText("Dear user,\n\nYour slot has been cancelled by the admin.\n" + slotInfo + "\n\nRegards,\nSlot Booking Team");
        mailSender.send(message);
    }
    public void sendSlotBookingEmail(String to, String slotInfo) {
    SimpleMailMessage message = new SimpleMailMessage();
    message.setTo(to);
    message.setSubject("Slot Booking Confirmation");
    message.setText("Dear user,\n\nYour slot has been successfully booked.\n" + slotInfo + "\n\nRegards,\nSlot Booking Team");
    mailSender.send(message);
    }
}
