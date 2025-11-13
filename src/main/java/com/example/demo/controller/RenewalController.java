package com.example.demo.controller;

import com.example.demo.model.Issue;
import com.example.demo.model.Renewal;
import com.example.demo.service.ActivityLogService;
import com.example.demo.service.IssueService;
import com.example.demo.service.RenewalService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/renewal")
public class RenewalController {

    @Autowired
    private RenewalService renewalService;

    @Autowired
    private IssueService issueService;

    @Autowired
    private ActivityLogService activityLogService;

    @PostMapping("/renew")
    public String renewBook(@RequestParam String issueId, 
                           Authentication authentication,
                           HttpServletRequest request,
                           RedirectAttributes redirectAttributes) {
        try {
            Issue issue = issueService.findById(issueId);
            if (issue == null) {
                redirectAttributes.addFlashAttribute("errorMessage", "Issue not found!");
                return "redirect:/student/dashboard";
            }

            String username = authentication.getName();
            Renewal renewal = renewalService.renewIssue(issue, username, issueService);

            String bookTitle = (issue.getBook() != null) ? issue.getBook().getTitle() : "Unknown";
            activityLogService.logActivity("RENEW", "ISSUE", issueId, authentication,
                    "Book renewed: " + bookTitle, request);

            redirectAttributes.addFlashAttribute("successMessage", 
                    "Book renewed successfully! New return date: " + renewal.getNewReturnDate());
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }

        String role = authentication.getAuthorities().iterator().next().getAuthority();
        if (role.contains("ADMIN")) {
            return "redirect:/admin/dashboard";
        } else {
            return "redirect:/student/dashboard";
        }
    }
}

