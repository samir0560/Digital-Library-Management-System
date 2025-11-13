
package com.example.demo.controller;

import com.example.demo.model.Book;
import com.example.demo.model.Issue;
import com.example.demo.model.User;
import com.example.demo.service.ActivityLogService;
import com.example.demo.service.BookService;
import com.example.demo.service.DashboardService;
import com.example.demo.service.IssueService;
import com.example.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired private BookService bookService;
    @Autowired private UserService userService;
    @Autowired private IssueService issueService;
    @Autowired private DashboardService dashboardService;
    @Autowired private ActivityLogService activityLogService;

    @GetMapping("/dashboard")
    public String dashboard(Model model, Authentication authentication, @RequestParam(required = false) String login) {
        String username = authentication.getName();
        User admin = userService.findByUsername(username);

        if ("success".equals(login)) {
            model.addAttribute("successMessage", "Login successful! Welcome back.");
        }

        // Dashboard statistics
        try {
            Map<String, Object> stats = dashboardService.getAdminStatistics();
            model.addAttribute("stats", stats);
        } catch (Exception e) {
            // If statistics fail, set empty stats
            Map<String, Object> emptyStats = new java.util.HashMap<>();
            emptyStats.put("totalBooks", 0);
            emptyStats.put("issuedBooks", 0);
            emptyStats.put("availableBooks", 0);
            emptyStats.put("totalStudents", 0);
            emptyStats.put("activeIssues", 0);
            emptyStats.put("totalRevenue", 0);
            emptyStats.put("overdueIssues", 0);
            model.addAttribute("stats", emptyStats);
        }

        // Overdue books
        try {
            List<Issue> overdueBooks = dashboardService.getOverdueBooks();
            model.addAttribute("overdueBooks", overdueBooks != null ? overdueBooks : new java.util.ArrayList<>());
        } catch (Exception e) {
            model.addAttribute("overdueBooks", new java.util.ArrayList<>());
        }

        // Recent activities
        try {
            List<com.example.demo.model.ActivityLog> recentActivities = activityLogService.getRecentActivities(10);
            model.addAttribute("recentActivities", recentActivities != null ? recentActivities : new java.util.ArrayList<>());
        } catch (Exception e) {
            model.addAttribute("recentActivities", new java.util.ArrayList<>());
        }

        model.addAttribute("admin", admin);
        model.addAttribute("books", bookService.findAll());
        model.addAttribute("students", userService.findAllStudents());
        // Only show issued books that are NOT returned
        List<Issue> activeIssues = issueService.findAll().stream()
                .filter(i -> !i.isReturned())
                .toList();
        model.addAttribute("issues", activeIssues);
        model.addAttribute("book", new Book());
        return "admin_dashboard";
    }

    // 🔹 New Returned Books Page
    @GetMapping("/returned_books")
    public String returnedBooks(Model model, Authentication authentication) {
        // Fetch only returned books
        List<Issue> returnedIssues = issueService.findAll().stream()
                .filter(Issue::isReturned)
                .toList();

        model.addAttribute("returnedIssues", returnedIssues);
        return "returned_books";
    }
}