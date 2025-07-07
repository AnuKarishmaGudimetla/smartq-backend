package com.example.slot.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.example.slot.model.Slott;

public interface SlotRepository extends MongoRepository<Slott, String> {
    List<Slott> findByStatusAndStartTimeAfterOrderByStartTime(
        Slott.Status status, LocalDateTime time);
    List<Slott> findByBookedBy(String username);
    List<Slott> findByBookedByAndStartTimeBetween(String username, LocalDateTime start, LocalDateTime end);
    List<Slott> findByStatusAndStartTimeBetweenOrderByStartTime(
    Slott.Status status, LocalDateTime start, LocalDateTime end);
    List<Slott> findByStartTimeBetween(LocalDateTime start, LocalDateTime end);
    boolean existsByStartTimeAndEndTime(LocalDateTime startTime, LocalDateTime endTime);
    List<Slott> findByStatusAndEndTimeBefore(Slott.Status status, LocalDateTime time);
    List<Slott> findByStatus(Slott.Status status);
}