package com.example.demo.service;

import com.example.demo.model.ActivityLog;
import com.example.demo.repository.ActivityLogRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ActivityLogService {

    @Autowired
    private ActivityLogRepository activityLogRepository;

    public void logActivity(String action, String entityType, String entityId, 
                           String username, String userRole, String description, 
                           HttpServletRequest request) {
        ActivityLog log = new ActivityLog();
        log.setAction(action);
        log.setEntityType(entityType);
        log.setEntityId(entityId);
        log.setUsername(username);
        log.setUserRole(userRole);
        log.setDescription(description);
        log.setTimestamp(LocalDateTime.now());
        if (request != null) {
            log.setIpAddress(request.getRemoteAddr());
        }
        activityLogRepository.save(log);
    }

    public void logActivity(String action, String entityType, String entityId, 
                           Authentication authentication, String description, 
                           HttpServletRequest request) {
        if (authentication != null && authentication.getAuthorities() != null) {
            String username = authentication.getName();
            String role = "UNKNOWN";
            try {
                if (authentication.getAuthorities().iterator().hasNext()) {
                    role = authentication.getAuthorities().iterator().next().getAuthority();
                }
            } catch (Exception e) {
                // Default to UNKNOWN if role cannot be determined
            }
            logActivity(action, entityType, entityId, username, role, description, request);
        }
    }

    public List<ActivityLog> getRecentActivities(int limit) {
        return activityLogRepository.findTop50ByOrderByTimestampDesc()
                .stream()
                .limit(limit)
                .toList();
    }

    public List<ActivityLog> getActivitiesByUser(String username) {
        return activityLogRepository.findByUsername(username);
    }

    public List<ActivityLog> getActivitiesByAction(String action) {
        return activityLogRepository.findByAction(action);
    }

    /**
     * Cleans up activity logs older than 24 hours
     * @return number of deleted logs
     */
    public long cleanupOldActivities() {
        LocalDateTime cutoffTime = LocalDateTime.now().minusHours(24);
        List<ActivityLog> oldActivities = activityLogRepository.findByTimestampBefore(cutoffTime);
        long count = oldActivities.size();
        if (count > 0) {
            activityLogRepository.deleteAll(oldActivities);
        }
        return count;
    }

    /**
     * Gets activities from the last 24 hours only
     */
    public List<ActivityLog> getRecentActivities24Hours(int limit) {
        LocalDateTime cutoffTime = LocalDateTime.now().minusHours(24);
        return activityLogRepository.findTop50ByOrderByTimestampDesc()
                .stream()
                .filter(log -> log.getTimestamp().isAfter(cutoffTime))
                .limit(limit)
                .toList();
    }
}

