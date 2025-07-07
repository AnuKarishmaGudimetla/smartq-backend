package com.example.slot.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.example.slot.dto.ApiResponse;

@ControllerAdvice
public class RestExceptionHandler {

  @ExceptionHandler(SlotNotFoundException.class)
  public ResponseEntity<ApiResponse> handleNotFound(SlotNotFoundException ex) {
    return ResponseEntity
        .status(HttpStatus.NOT_FOUND)
        .body(new ApiResponse(ex.getMessage()));
  }

  @ExceptionHandler(SlotUnavailableException.class)
  public ResponseEntity<ApiResponse> handleUnavailable(SlotUnavailableException ex) {
    return ResponseEntity
        .status(HttpStatus.CONFLICT)
        .body(new ApiResponse(ex.getMessage()));
  }

  @ExceptionHandler(SlotAlreadyBookedException.class)
  public ResponseEntity<ApiResponse> handleAlreadyBooked(SlotAlreadyBookedException ex) {
    return ResponseEntity
        .status(HttpStatus.BAD_REQUEST)
        .body(new ApiResponse(ex.getMessage()));
  }

  @ExceptionHandler(RuntimeException.class)
  public ResponseEntity<ApiResponse> handleRuntime(RuntimeException ex) {
    return ResponseEntity
        .status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body(new ApiResponse("Internal error"));
  }
}
