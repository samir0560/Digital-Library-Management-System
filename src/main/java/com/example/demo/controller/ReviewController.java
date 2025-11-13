package com.example.demo.controller;

import com.example.demo.model.Book;
import com.example.demo.model.User;
import com.example.demo.service.ActivityLogService;
import com.example.demo.service.BookService;
import com.example.demo.service.ReviewService;
import com.example.demo.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/review")
public class ReviewController {

    @Autowired
    private ReviewService reviewService;

    @Autowired
    private BookService bookService;

    @Autowired
    private UserService userService;

    @Autowired
    private ActivityLogService activityLogService;

    @PostMapping("/submit")
    public String submitReview(@RequestParam String bookId,
                              @RequestParam int rating,
                              @RequestParam(required = false) String comment,
                              Authentication authentication,
                              HttpServletRequest request,
                              RedirectAttributes redirectAttributes) {
        try {
            Book book = bookService.findById(bookId);
            User student = userService.findByUsername(authentication.getName());

            if (book == null) {
                redirectAttributes.addFlashAttribute("errorMessage", "Book not found!");
                return "redirect:/student/dashboard";
            }

            reviewService.createOrUpdateReview(book, student, rating, comment);

            activityLogService.logActivity("REVIEW", "BOOK", bookId, authentication,
                    "Review submitted: " + rating + " stars", request);

            redirectAttributes.addFlashAttribute("successMessage", "Review submitted successfully!");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }

        return "redirect:/student/dashboard";
    }

    @PostMapping("/delete")
    public String deleteReview(@RequestParam String reviewId,
                              Authentication authentication,
                              HttpServletRequest request,
                              RedirectAttributes redirectAttributes) {
        reviewService.deleteReview(reviewId);

        activityLogService.logActivity("DELETE_REVIEW", "REVIEW", reviewId,
                authentication, "Review deleted", request);

        redirectAttributes.addFlashAttribute("successMessage", "Review deleted successfully!");
        return "redirect:/student/dashboard";
    }
}

