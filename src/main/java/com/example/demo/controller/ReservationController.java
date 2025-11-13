package com.example.demo.controller;

import com.example.demo.model.Book;
import com.example.demo.model.Reservation;
import com.example.demo.model.User;
import com.example.demo.service.ActivityLogService;
import com.example.demo.service.BookService;
import com.example.demo.service.ReservationService;
import com.example.demo.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/reservation")
public class ReservationController {

    @Autowired
    private ReservationService reservationService;

    @Autowired
    private BookService bookService;

    @Autowired
    private UserService userService;

    @Autowired
    private ActivityLogService activityLogService;

    @PostMapping("/create")
    public String createReservation(@RequestParam String bookId,
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
            
            if (student == null) {
                redirectAttributes.addFlashAttribute("errorMessage", "User not found!");
                return "redirect:/student/dashboard";
            }

            Reservation reservation = reservationService.createReservation(book, student);

            activityLogService.logActivity("RESERVE", "BOOK", bookId, authentication,
                    "Book reserved: " + book.getTitle(), request);

            redirectAttributes.addFlashAttribute("successMessage", 
                    "Book reserved successfully! Queue position: " + reservation.getQueuePosition());
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }

        return "redirect:/student/dashboard";
    }

    @PostMapping("/cancel")
    public String cancelReservation(@RequestParam String reservationId,
                                   Authentication authentication,
                                   HttpServletRequest request,
                                   RedirectAttributes redirectAttributes) {
        reservationService.cancelReservation(reservationId);

        activityLogService.logActivity("CANCEL_RESERVATION", "RESERVATION", reservationId,
                authentication, "Reservation cancelled", request);

        redirectAttributes.addFlashAttribute("successMessage", "Reservation cancelled successfully!");
        return "redirect:/student/dashboard";
    }
}

