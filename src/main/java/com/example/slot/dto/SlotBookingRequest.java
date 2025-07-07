package com.example.slot.dto;

public class SlotBookingRequest {
    private String slotId;
    private String username;

    public SlotBookingRequest() {}

    public SlotBookingRequest(String slotId, String username) {
        this.slotId = slotId;
        this.username = username;
    }

    public String getSlotId() {
        return slotId;
    }
    public void setSlotId(String slotId) {
        this.slotId = slotId;
    }

    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }
}
