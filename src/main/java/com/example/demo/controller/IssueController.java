package com.example.demo.controller;

import com.example.demo.model.Book;
import com.example.demo.model.Issue;
import com.example.demo.model.User;
import com.example.demo.service.ActivityLogService;
import com.example.demo.service.BookService;
import com.example.demo.service.IssueService;
import com.example.demo.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Controller
@RequestMapping("/admin")
public class IssueController {

    @Autowired private IssueService issueService;
    @Autowired private UserService userService;
    @Autowired private BookService bookService;
    @Autowired private ActivityLogService activityLogService;

    @PostMapping("/issue")
    public String issueBookSubmit(@RequestParam String studentId,
                                  @RequestParam String bookId,
                                  Authentication authentication,
                                  HttpServletRequest request,
                                  RedirectAttributes redirectAttributes) {

        User student = userService.findById(studentId);
        Book book = bookService.findById(bookId);

        if (student == null || book == null || book.getAvailable() <= 0) {
            redirectAttributes.addFlashAttribute("warningMessage",
                    "Book is not available right now.");
            return "redirect:/admin/dashboard";
        }

        Issue issue = new Issue();
        issue.setBook(book);
        issue.setStudent(student);
        issue.setIssueDate(LocalDate.now());
        issue.setReturnDate(LocalDate.now().plusDays(7));
        issue.setReturned(false);
        issue.setFine(0);
        issueService.save(issue);

        book.setAvailable(book.getAvailable() - 1);
        bookService.save(book);

        activityLogService.logActivity("ISSUE", "BOOK", bookId, authentication,
                "Book issued: " + book.getTitle() + " to " + student.getUsername(), request);

        redirectAttributes.addFlashAttribute("successMessage", "Book issued successfully!");
        return "redirect:/admin/dashboard";
    }

    @PostMapping("/return")
    public String returnBook(@RequestParam String issueId, 
                            Authentication authentication,
                            HttpServletRequest request,
                            RedirectAttributes redirectAttributes) {
        Issue issue = issueService.findById(issueId);
        if (issue != null && !issue.isReturned()) {
            issue.setReturned(true);

            LocalDate today = LocalDate.now();
            if (today.isAfter(issue.getReturnDate())) {
                long lateDays = ChronoUnit.DAYS.between(issue.getReturnDate(), today);
                int fine = (int) (lateDays * 10);
                issue.setFine(fine);
            } else {
                issue.setFine(0);
            }

            issueService.save(issue);

            Book book = issue.getBook();
            if (book != null) {
                book.setAvailable(book.getAvailable() + 1);
                bookService.save(book);
            }

            String bookTitle = (book != null) ? book.getTitle() : "Unknown";
            activityLogService.logActivity("RETURN", "ISSUE", issueId, authentication,
                    "Book returned: " + bookTitle + " Fine: ₹" + issue.getFine(), request);

            redirectAttributes.addFlashAttribute("successMessage", "Book returned successfully!");
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to return book.");
        }
        return "redirect:/admin/dashboard";
    }
}
