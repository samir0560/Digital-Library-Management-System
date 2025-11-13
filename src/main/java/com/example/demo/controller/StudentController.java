package com.example.demo.controller;

import com.example.demo.model.Issue;
import com.example.demo.model.User;
import com.example.demo.service.IssueService;
import com.example.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class StudentController {

    @Autowired
    private IssueService issueService;

    @Autowired
    private UserService userService;

    @GetMapping("/student/dashboard")
    public String dashboard(Model model, Authentication authentication, @RequestParam(required = false) String login) {
        String username = authentication.getName();
        User student = userService.findByUsername(username);

        if (student == null) {
            model.addAttribute("errorMessage", "Student not found. Please contact administrator.");
            return "redirect:/login?error=true";
        }

        if ("success".equals(login)) {
            model.addAttribute("successMessage", "Login successful! Welcome back.");
        }

        // 🔹 Add student profile
        model.addAttribute("student", student);

        // Fetch only this student's issued books
        List<Issue> issues = issueService.findByStudent(student);
        if (issues == null) {
            issues = new java.util.ArrayList<>();
        }
        model.addAttribute("issues", issues);

        // 🔹 Calculate total fine
        int totalFine = issues.stream().mapToInt(Issue::getFine).sum();
        model.addAttribute("totalFine", totalFine);

        return "student_dashboard";
    }
}
