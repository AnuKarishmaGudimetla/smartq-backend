package com.example.slot.exception;
public class SlotUnavailableException extends RuntimeException {
    public SlotUnavailableException(String msg) { super(msg); }
}