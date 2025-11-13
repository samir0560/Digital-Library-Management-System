package com.example.demo.controller;

import com.example.demo.model.Issue;
import com.example.demo.model.User;
import com.example.demo.service.ActivityLogService;
import com.example.demo.service.FinePaymentService;
import com.example.demo.service.IssueService;
import com.example.demo.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/fine")
public class FinePaymentController {

    @Autowired
    private FinePaymentService finePaymentService;

    @Autowired
    private IssueService issueService;

    @Autowired
    private UserService userService;

    @Autowired
    private ActivityLogService activityLogService;

    @PostMapping("/pay")
    public String payFine(@RequestParam String issueId,
                         @RequestParam int amountPaid,
                         @RequestParam String paymentMethod,
                         @RequestParam(required = false) String transactionId,
                         @RequestParam(required = false) String notes,
                         Authentication authentication,
                         HttpServletRequest request,
                         RedirectAttributes redirectAttributes) {
        Issue issue = issueService.findById(issueId);
        User student = userService.findByUsername(authentication.getName());

        if (issue == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Issue not found!");
            return "redirect:/student/dashboard";
        }

        finePaymentService.recordPayment(issue, student, amountPaid,
                paymentMethod, transactionId, notes);

        activityLogService.logActivity("PAY_FINE", "ISSUE", issueId, authentication,
                "Fine payment: ₹" + amountPaid, request);

        redirectAttributes.addFlashAttribute("successMessage", "Payment recorded successfully!");
        return "redirect:/student/dashboard";
    }

    @PostMapping("/waive")
    public String waiveFine(@RequestParam String issueId,
                           @RequestParam(required = false) String notes,
                           Authentication authentication,
                           HttpServletRequest request,
                           RedirectAttributes redirectAttributes) {
        Issue issue = issueService.findById(issueId);
        
        if (issue == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Issue not found!");
            return "redirect:/admin/dashboard";
        }

        User student = issue.getStudent();
        String adminUsername = authentication.getName();
        int fineAmount = issue.getFine();

        finePaymentService.waiveFine(issue, student, adminUsername, notes);
        issue.setFine(0);
        issueService.save(issue);

        activityLogService.logActivity("WAIVE_FINE", "ISSUE", issueId, authentication,
                "Fine waived: ₹" + fineAmount, request);

        redirectAttributes.addFlashAttribute("successMessage", "Fine waived successfully!");
        return "redirect:/admin/dashboard";
    }
}

