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
import java.util.List;

@Controller
@RequestMapping("/admin/bulk")
public class BulkOperationController {

    @Autowired
    private IssueService issueService;

    @Autowired
    private BookService bookService;

    @Autowired
    private UserService userService;

    @Autowired
    private ActivityLogService activityLogService;

    @PostMapping("/issue")
    public String bulkIssue(@RequestParam List<String> studentIds,
                           @RequestParam List<String> bookIds,
                           Authentication authentication,
                           HttpServletRequest request,
                           RedirectAttributes redirectAttributes) {
        int successCount = 0;
        int failCount = 0;

        for (int i = 0; i < studentIds.size() && i < bookIds.size(); i++) {
            try {
                User student = userService.findById(studentIds.get(i));
                Book book = bookService.findById(bookIds.get(i));

                if (student != null && book != null && book.getAvailable() > 0) {
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

                    activityLogService.logActivity("ISSUE", "BOOK", book.getId(),
                            authentication, "Bulk issue: " + book.getTitle(), request);

                    successCount++;
                } else {
                    failCount++;
                }
            } catch (Exception e) {
                failCount++;
            }
        }

        redirectAttributes.addFlashAttribute("successMessage",
                "Bulk issue completed! Success: " + successCount + ", Failed: " + failCount);
        return "redirect:/admin/dashboard";
    }

    @PostMapping("/return")
    public String bulkReturn(@RequestParam List<String> issueIds,
                            Authentication authentication,
                            HttpServletRequest request,
                            RedirectAttributes redirectAttributes) {
        int successCount = 0;

        for (String issueId : issueIds) {
            Issue issue = issueService.findById(issueId);
            if (issue != null && !issue.isReturned()) {
                issue.setReturned(true);
                
                LocalDate today = LocalDate.now();
                if (today.isAfter(issue.getReturnDate())) {
                    long lateDays = java.time.temporal.ChronoUnit.DAYS.between(issue.getReturnDate(), today);
                    int fine = (int) (lateDays * 10);
                    issue.setFine(fine);
                }

                issueService.save(issue);

                Book book = issue.getBook();
                if (book != null) {
                    book.setAvailable(book.getAvailable() + 1);
                    bookService.save(book);
                }

                String bookTitle = (book != null) ? book.getTitle() : "Unknown";
                activityLogService.logActivity("RETURN", "ISSUE", issueId,
                        authentication, "Bulk return: " + bookTitle, request);

                successCount++;
            }
        }

        redirectAttributes.addFlashAttribute("successMessage",
                "Bulk return completed! " + successCount + " books returned.");
        return "redirect:/admin/dashboard";
    }
}

