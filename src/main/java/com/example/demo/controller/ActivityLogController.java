package com.example.demo.controller;

import com.example.demo.model.ActivityLog;
import com.example.demo.service.ActivityLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("/admin")
public class ActivityLogController {

    @Autowired
    private ActivityLogService activityLogService;

    @GetMapping("/activity-log")
    public String viewActivityLog(Model model, 
                                 Authentication authentication,
                                 @RequestParam(defaultValue = "50") int limit) {
        // Only show activities from the last 24 hours
        List<ActivityLog> activities = activityLogService.getRecentActivities24Hours(limit);
        model.addAttribute("activities", activities);
        model.addAttribute("totalCount", activities.size());
        return "activity_log";
    }
}


