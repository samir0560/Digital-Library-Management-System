package com.example.demo.scheduler;

import com.example.demo.service.ActivityLogService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ActivityLogCleanupScheduler {

    private static final Logger logger = LoggerFactory.getLogger(ActivityLogCleanupScheduler.class);

    @Autowired
    private ActivityLogService activityLogService;

    /**
     * Cleanup old activity logs every hour
     */
    @Scheduled(cron = "0 0 * * * ?")
    public void cleanupOldActivityLogs() {
        try {
            long deletedCount = activityLogService.cleanupOldActivities();
            if (deletedCount > 0) {
                logger.info("Cleaned up {} old activity logs (older than 24 hours)", deletedCount);
            }
        } catch (Exception e) {
            logger.error("Error cleaning up old activity logs", e);
        }
    }

    /**
     * Daily cleanup at midnight
     */
    @Scheduled(cron = "0 0 0 * * ?")
    public void dailyCleanup() {
        try {
            long deletedCount = activityLogService.cleanupOldActivities();
            logger.info("Daily cleanup: Removed {} old activity logs", deletedCount);
        } catch (Exception e) {
            logger.error("Error during daily cleanup", e);
        }
    }
}

