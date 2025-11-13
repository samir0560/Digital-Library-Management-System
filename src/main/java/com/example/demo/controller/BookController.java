package com.example.demo.controller;

import com.example.demo.model.Book;
import com.example.demo.model.Issue;
import com.example.demo.service.ActivityLogService;
import com.example.demo.service.BookService;
import com.example.demo.service.IssueService;
import com.example.demo.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/admin/books")
public class BookController {

    @Autowired private BookService bookService;
    @Autowired private UserService userService;
    @Autowired private IssueService issueService;
    @Autowired private ActivityLogService activityLogService;

    @PostMapping("/add")
    public String addBookSubmit(@ModelAttribute Book book, 
                               Authentication authentication,
                               HttpServletRequest request,
                               RedirectAttributes redirectAttributes) {
        if (book.getId() != null && !book.getId().isEmpty()) {
            Book existing = bookService.findById(book.getId());
            if (existing != null) {
                int delta = book.getQuantity() - existing.getQuantity();
                existing.setTitle(book.getTitle());
                existing.setAuthor(book.getAuthor());
                existing.setQuantity(book.getQuantity());
                existing.setAvailable(Math.max(0, existing.getAvailable() + delta));
                bookService.save(existing);

                activityLogService.logActivity("UPDATE", "BOOK", book.getId(), authentication,
                        "Book updated: " + book.getTitle(), request);

                redirectAttributes.addFlashAttribute("successMessage", "Book updated successfully!");
                return "redirect:/admin/dashboard";
            }
        }

        book.setAvailable(book.getQuantity());
        Book saved = bookService.save(book);

        activityLogService.logActivity("ADD", "BOOK", saved.getId(), authentication,
                "Book added: " + book.getTitle(), request);

        redirectAttributes.addFlashAttribute("successMessage", "Book added successfully!");
        return "redirect:/admin/dashboard";
    }

    @PostMapping("/delete")
    public String deleteBook(@RequestParam String id, 
                            Authentication authentication,
                            HttpServletRequest request,
                            RedirectAttributes redirectAttributes) {
        Book book = bookService.findById(id);
        
        if (book == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Book not found!");
            return "redirect:/admin/dashboard";
        }

        List<Issue> activeIssues = issueService.findActiveIssuesByBook(book);
        
        if (!activeIssues.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", 
                "Cannot delete book: \"" + book.getTitle() + "\" is currently issued to " + 
                activeIssues.size() + " student(s). Please return all issued copies before deleting.");
            return "redirect:/admin/dashboard";
        }

        String bookTitle = book.getTitle();
        bookService.deleteById(id);

        activityLogService.logActivity("DELETE", "BOOK", id, authentication,
                "Book deleted: " + bookTitle, request);

        redirectAttributes.addFlashAttribute("successMessage", "Book deleted successfully!");
        return "redirect:/admin/dashboard";
    }

    @GetMapping("/edit/{id}")
    public String editBookForm(@PathVariable String id, Model model, Authentication authentication) {
        Book book = bookService.findById(id);
        if (book == null) return "redirect:/admin/dashboard";

        model.addAttribute("book", book);
        model.addAttribute("books", bookService.findAll());
        model.addAttribute("students", userService.findAllStudents());
        model.addAttribute("issues", issueService.findAll());
        return "admin_dashboard";
    }
}
