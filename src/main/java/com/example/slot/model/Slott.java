package com.example.slot.model;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "slots")
public class Slott {
    @Id
    private String id;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String bookedBy; // username of the student
    private Status status;

    public enum Status {
        AVAILABLE, BOOKED, CANCELLED, EXPIRED, ONGOING
    }

    public Slott() {}

    public Slott(LocalDateTime startTime, LocalDateTime endTime) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.status = Status.AVAILABLE;
    }

    // ─── Getters ────────────────────────────────────────────
    public String getId() {
        return id;
    }
    public LocalDateTime getStartTime() {
        return startTime;
    }
    public LocalDateTime getEndTime() {
        return endTime;
    }
    public String getBookedBy() {
        return bookedBy;
    }
    public Status getStatus() {
        return status;
    }

    // ─── Setters ────────────────────────────────────────────
    public void setId(String id) {
        this.id = id;
    }
    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }
    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }
    public void setBookedBy(String bookedBy) {
        this.bookedBy = bookedBy;
    }
    public void setStatus(Status status) {
        this.status = status;
    }
}
