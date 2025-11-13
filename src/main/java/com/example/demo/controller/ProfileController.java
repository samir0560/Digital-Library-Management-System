package com.example.demo.controller;

import com.example.demo.model.User;
import com.example.demo.service.ActivityLogService;
import com.example.demo.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Controller
@RequestMapping("/profile")
public class ProfileController {

    @Autowired
    private UserService userService;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private ActivityLogService activityLogService;

    @Value("${app.upload.dir:uploads/profile-pictures}")
    private String uploadDir;

    @PostMapping("/update")
    public String updateProfile(@RequestParam(required = false) String fullName,
                               @RequestParam(required = false) String username,
                               @RequestParam(required = false) String email,
                               @RequestParam(required = false) String phone,
                               @RequestParam(required = false) String address,
                               @RequestParam(required = false) String studentId,
                               @RequestParam(required = false) MultipartFile profilePicture,
                               Authentication authentication,
                               HttpServletRequest request,
                               RedirectAttributes redirectAttributes) {
        User user = userService.findByUsername(authentication.getName());
        
        if (user == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "User not found!");
            return "redirect:/student/dashboard";
        }

        // Update full name
        if (fullName != null && !fullName.isEmpty()) {
            user.setFullName(fullName);
        }

        // Update username if provided and different
        if (username != null && !username.isEmpty() && !username.equals(user.getUsername())) {
            // Check if username already exists (excluding current user)
            User existingUser = userService.findByUsername(username);
            if (existingUser != null && !existingUser.getId().equals(user.getId())) {
                redirectAttributes.addFlashAttribute("errorMessage", "Username already exists! Please choose a different username.");
                return "redirect:/student/dashboard";
            }
            user.setUsername(username);
            // Note: After username change, user needs to logout and login again
            // because Spring Security uses username for authentication
        }

        // Update email with uniqueness check
        if (email != null && !email.isEmpty() && !email.equals(user.getEmail())) {
            // Check if email already exists (excluding current user)
            User existingUser = userService.findByEmail(email);
            if (existingUser != null && !existingUser.getId().equals(user.getId())) {
                redirectAttributes.addFlashAttribute("errorMessage", "Email already exists! Please choose a different email.");
                return "redirect:/student/dashboard";
            }
            user.setEmail(email);
        }

        if (phone != null && !phone.isEmpty()) user.setPhone(phone);
        if (address != null && !address.isEmpty()) user.setAddress(address);
        if (studentId != null && !studentId.isEmpty()) user.setStudentId(studentId);

        // Handle profile picture upload
        if (profilePicture != null && !profilePicture.isEmpty()) {
            try {
                String fileName = saveProfilePicture(profilePicture, user.getId());
                if (fileName != null) {
                    // Delete old profile picture if exists
                    if (user.getProfilePicture() != null && !user.getProfilePicture().isEmpty()) {
                        deleteOldProfilePicture(user.getProfilePicture());
                    }
                    user.setProfilePicture(fileName);
                }
            } catch (Exception e) {
                redirectAttributes.addFlashAttribute("errorMessage", "Failed to upload profile picture: " + e.getMessage());
                return "redirect:/student/dashboard";
            }
        }

        userService.save(user);

        activityLogService.logActivity("UPDATE_PROFILE", "USER", user.getId(),
                authentication, "Profile updated", request);

        redirectAttributes.addFlashAttribute("successMessage", "Profile updated successfully!" + 
            (username != null && !username.equals(authentication.getName()) ? 
             " Note: Please logout and login again to use your new username." : ""));
        
        String role = authentication.getAuthorities().iterator().next().getAuthority();
        if (role.contains("ADMIN")) {
            return "redirect:/admin/dashboard";
        } else {
            return "redirect:/student/dashboard";
        }
    }

    private String saveProfilePicture(MultipartFile file, String userId) throws IOException {
        // Validate file type
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || originalFilename.isEmpty()) {
            return null;
        }

        String extension = "";
        int lastDotIndex = originalFilename.lastIndexOf('.');
        if (lastDotIndex > 0) {
            extension = originalFilename.substring(lastDotIndex);
        }

        // Validate image file extensions
        if (!extension.matches("(?i)\\.(jpg|jpeg|png|gif|webp)")) {
            throw new IOException("Invalid file type. Only JPG, PNG, GIF, and WebP images are allowed.");
        }

        // Validate file size (max 5MB)
        if (file.getSize() > 5 * 1024 * 1024) {
            throw new IOException("File size too large. Maximum size is 5MB.");
        }

        // Create upload directory if it doesn't exist
        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        // Generate unique filename
        String fileName = userId + "_" + UUID.randomUUID().toString() + extension;
        Path filePath = uploadPath.resolve(fileName);

        // Save file
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        return fileName;
    }

    private void deleteOldProfilePicture(String fileName) {
        try {
            Path filePath = Paths.get(uploadDir, fileName);
            if (Files.exists(filePath)) {
                Files.delete(filePath);
            }
        } catch (IOException e) {
            // Log error but don't throw exception
            System.err.println("Failed to delete old profile picture: " + e.getMessage());
        }
    }

    @PostMapping("/change-password")
    public String changePassword(@RequestParam String oldPassword,
                                @RequestParam String newPassword,
                                Authentication authentication,
                                HttpServletRequest request,
                                RedirectAttributes redirectAttributes) {
        User user = userService.findByUsername(authentication.getName());
        
        if (user == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "User not found!");
            String role = authentication.getAuthorities().iterator().next().getAuthority();
            if (role.contains("ADMIN")) {
                return "redirect:/admin/dashboard";
            } else {
                return "redirect:/student/dashboard";
            }
        }

        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            redirectAttributes.addFlashAttribute("errorMessage", "Current password is incorrect!");
            String role = authentication.getAuthorities().iterator().next().getAuthority();
            if (role.contains("ADMIN")) {
                return "redirect:/admin/dashboard";
            } else {
                return "redirect:/student/dashboard";
            }
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        userService.save(user);

        activityLogService.logActivity("CHANGE_PASSWORD", "USER", user.getId(),
                authentication, "Password changed", request);

        redirectAttributes.addFlashAttribute("successMessage", "Password changed successfully!");
        
        String role = authentication.getAuthorities().iterator().next().getAuthority();
        if (role.contains("ADMIN")) {
            return "redirect:/admin/dashboard";
        } else {
            return "redirect:/student/dashboard";
        }
    }
}

