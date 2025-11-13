package com.example.demo.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.example.demo.model.ActivityLog;

public interface ActivityLogRepository extends MongoRepository<ActivityLog, String> {
    List<ActivityLog> findByUsername(String username);
    List<ActivityLog> findByAction(String action);
    List<ActivityLog> findByTimestampBetween(LocalDateTime start, LocalDateTime end);
    List<ActivityLog> findTop50ByOrderByTimestampDesc();
    List<ActivityLog> findByTimestampBefore(LocalDateTime timestamp);
}

