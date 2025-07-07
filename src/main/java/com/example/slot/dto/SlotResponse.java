package com.example.slot.dto;

import java.time.LocalDateTime;

public class SlotResponse {
    private final String id;
    private final LocalDateTime startTime;
    private final LocalDateTime endTime;
    private final String status;
    private final String bookedBy;

    public SlotResponse(String id, LocalDateTime startTime, LocalDateTime endTime,
                        String status, String bookedBy) {
        this.id = id;
        this.startTime = startTime;
        this.endTime = endTime;
        this.status = status;
        this.bookedBy = bookedBy;
    }

    public String getId() {
        return id;
    }
    public LocalDateTime getStartTime() {
        return startTime;
    }
    public LocalDateTime getEndTime() {
        return endTime;
    }
    public String getStatus() {
        return status;
    }
    public String getBookedBy() {
        return bookedBy;
    }
}
