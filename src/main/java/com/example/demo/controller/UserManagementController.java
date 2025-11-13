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
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/admin/users")
public class UserManagementController {

    @Autowired
    private UserService userService;

    @Autowired
    private ActivityLogService activityLogService;

    @Autowired
    private IssueService issueService;

    @Autowired
    private FinePaymentService finePaymentService;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @GetMapping("/manage")
    public String manageUsers(Model model, Authentication authentication) {
        List<User> students = userService.findAllStudents();
        model.addAttribute("students", students);
        return "user_management";
    }

    @PostMapping("/deactivate")
    public String deactivateUser(@RequestParam String userId,
                                Authentication authentication,
                                HttpServletRequest request,
                                RedirectAttributes redirectAttributes) {
        User user = userService.findById(userId);
        if (user != null) {
            user.setActive(false);
            userService.save(user);

            activityLogService.logActivity("DEACTIVATE_USER", "USER", userId,
                    authentication, "User deactivated: " + user.getUsername(), request);

            redirectAttributes.addFlashAttribute("successMessage", "User deactivated successfully!");
        }
        return "redirect:/admin/users/manage";
    }

    @PostMapping("/activate")
    public String activateUser(@RequestParam String userId,
                              Authentication authentication,
                              HttpServletRequest request,
                              RedirectAttributes redirectAttributes) {
        User user = userService.findById(userId);
        if (user != null) {
            user.setActive(true);
            userService.save(user);

            activityLogService.logActivity("ACTIVATE_USER", "USER", userId,
                    authentication, "User activated: " + user.getUsername(), request);

            redirectAttributes.addFlashAttribute("successMessage", "User activated successfully!");
        }
        return "redirect:/admin/users/manage";
    }

    @GetMapping("/details/{userId}")
    public String viewUserDetails(@PathVariable String userId, Model model) {
        User user = userService.findById(userId);
        if (user != null) {
            model.addAttribute("user", user);
            
            // Add statistics for students
            if ("STUDENT".equals(user.getRole())) {
                List<Issue> allIssues = issueService.findByStudent(user);
                long activeIssues = allIssues.stream().filter(i -> !i.isReturned()).count();
                int totalFines = allIssues.stream().mapToInt(Issue::getFine).sum();
                int paidFines = finePaymentService.getTotalPaidByStudent(user);
                
                model.addAttribute("totalIssues", allIssues.size());
                model.addAttribute("activeIssues", activeIssues);
                model.addAttribute("totalFines", totalFines);
                model.addAttribute("paidFines", paidFines);
            }
        }
        return "user_details";
    }

    @GetMapping("/edit/{userId}")
    public String editUserForm(@PathVariable String userId, Model model) {
        User user = userService.findById(userId);
        if (user != null) {
            model.addAttribute("user", user);
            return "user_edit";
        }
        return "redirect:/admin/users/manage";
    }

    @PostMapping("/update")
    public String updateStudent(@RequestParam String userId,
                               @RequestParam(required = false) String fullName,
                               @RequestParam(required = false) String email,
                               @RequestParam(required = false) String phone,
                               @RequestParam(required = false) String address,
                               @RequestParam(required = false) String studentId,
                               @RequestParam(required = false) String username,
                               Authentication authentication,
                               HttpServletRequest request,
                               RedirectAttributes redirectAttributes) {
        User user = userService.findById(userId);
        
        if (user == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "User not found!");
            return "redirect:/admin/users/manage";
        }

        // Update full name
        if (fullName != null && !fullName.isEmpty()) {
            user.setFullName(fullName);
        }

        // Update username with uniqueness check
        if (username != null && !username.isEmpty() && !username.equals(user.getUsername())) {
            // Check if username already exists (excluding current user)
            User existingUser = userService.findByUsername(username);
            if (existingUser != null && !existingUser.getId().equals(userId)) {
                redirectAttributes.addFlashAttribute("errorMessage", "Username already exists! Please choose a different username.");
                return "redirect:/admin/users/manage";
            }
            user.setUsername(username);
        }

        // Update email with uniqueness check
        if (email != null && !email.isEmpty() && !email.equals(user.getEmail())) {
            // Check if email already exists (excluding current user)
            User existingUser = userService.findByEmail(email);
            if (existingUser != null && !existingUser.getId().equals(userId)) {
                redirectAttributes.addFlashAttribute("errorMessage", "Email already exists! Please choose a different email.");
                return "redirect:/admin/users/manage";
            }
            user.setEmail(email);
        }

        if (phone != null && !phone.isEmpty()) user.setPhone(phone);
        if (address != null && !address.isEmpty()) user.setAddress(address);
        if (studentId != null && !studentId.isEmpty()) user.setStudentId(studentId);

        userService.save(user);

        activityLogService.logActivity("UPDATE_USER", "USER", userId, authentication,
                "Student information updated: " + user.getUsername(), request);

        redirectAttributes.addFlashAttribute("successMessage", "Student information updated successfully!");
        return "redirect:/admin/users/manage";
    }

    @PostMapping("/change-password")
    public String changePassword(@RequestParam String userId,
                                 @RequestParam String newPassword,
                                 @RequestParam(required = false) String confirmPassword,
                                 Authentication authentication,
                                 HttpServletRequest request,
                                 RedirectAttributes redirectAttributes) {
        User user = userService.findById(userId);
        
        if (user == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "User not found!");
            return "redirect:/admin/users/manage";
        }

        // Validate password confirmation if provided
        if (confirmPassword != null && !confirmPassword.isEmpty()) {
            if (!newPassword.equals(confirmPassword)) {
                redirectAttributes.addFlashAttribute("errorMessage", "Passwords do not match!");
                return "redirect:/admin/users/details/" + userId;
            }
        }

        // Validate password length
        if (newPassword == null || newPassword.length() < 6) {
            redirectAttributes.addFlashAttribute("errorMessage", "Password must be at least 6 characters long!");
            return "redirect:/admin/users/details/" + userId;
        }

        // Hash and update password
        String hashedPassword = passwordEncoder.encode(newPassword);
        user.setPassword(hashedPassword);
        userService.save(user);

        activityLogService.logActivity("CHANGE_PASSWORD", "USER", userId, authentication,
                "Password changed for user: " + user.getUsername(), request);

        redirectAttributes.addFlashAttribute("successMessage", "Password changed successfully!");
        return "redirect:/admin/users/details/" + userId;
    }
}

