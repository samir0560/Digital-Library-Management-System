package com.example.demo.controller;

import com.example.demo.model.User;
import com.example.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @GetMapping("/")
    public String index() {
        return "index";
    }

    @GetMapping("/signup")
    public String signupForm(Model model) {
        model.addAttribute("user", new User());
        return "signup";
    }

    @PostMapping("/signup")
    public String signupSubmit(@ModelAttribute User user, 
                               @RequestParam(required = false) String confirmPassword,
                               RedirectAttributes redirectAttributes) {
        // Validate password confirmation
        if (confirmPassword == null || confirmPassword.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Please confirm your password.");
            return "redirect:/signup";
        }
        
        if (!user.getPassword().equals(confirmPassword)) {
            redirectAttributes.addFlashAttribute("errorMessage", "Passwords do not match! Please try again.");
            return "redirect:/signup";
        }
        
        // Force STUDENT role for all signups
        user.setRole("STUDENT");
        // Hash password with BCrypt
        String hashedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(hashedPassword);
        userService.save(user);
        redirectAttributes.addFlashAttribute("successMessage", "Account created successfully! Please login.");
        return "redirect:/login";
    }

    @GetMapping("/login")
    public String loginForm(Model model, @RequestParam(required = false) String logout, @RequestParam(required = false) String error) {
        // Check for logout message
        if ("true".equals(logout)) {
            model.addAttribute("successMessage", "You have been logged out successfully.");
        }
        if ("true".equals(error)) {
            model.addAttribute("errorMessage", "Invalid username or password.");
        }
        return "login";
    }

    @GetMapping("/login-error")
    public String loginError(Model model) {
        model.addAttribute("error", "Invalid username or password");
        return "login";
    }
}
