package com.example.demo.service;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.model.Book;
import com.example.demo.model.FinePayment;
import com.example.demo.model.Issue;
import com.example.demo.model.User;

@Service
public class DashboardService {

    @Autowired
    private BookService bookService;

    @Autowired
    private IssueService issueService;

    @Autowired
    private UserService userService;

    @Autowired
    private FinePaymentService finePaymentService;

    public Map<String, Object> getAdminStatistics() {
        Map<String, Object> stats = new HashMap<>();
        
        try {
            List<Book> allBooks = bookService.findAll();
            List<Issue> allIssues = issueService.findAll();
            List<User> allStudents = userService.findAllStudents();
            
            if (allBooks == null) allBooks = new java.util.ArrayList<>();
            if (allIssues == null) allIssues = new java.util.ArrayList<>();
            if (allStudents == null) allStudents = new java.util.ArrayList<>();
            
            // Book statistics
            int totalBooks = allBooks.size();
            int totalQuantity = allBooks.stream().mapToInt(Book::getQuantity).sum();
            int availableBooks = allBooks.stream().mapToInt(Book::getAvailable).sum();
            int issuedBooks = allIssues.stream()
                    .filter(i -> !i.isReturned())
                    .mapToInt(i -> 1)
                    .sum();
            
            stats.put("totalBooks", totalBooks);
            stats.put("totalQuantity", totalQuantity);
            stats.put("availableBooks", availableBooks);
            stats.put("issuedBooks", issuedBooks);
            
            // Student statistics
            int totalStudents = allStudents.size();
            int activeStudents = (int) allStudents.stream()
                    .filter(User::isActive)
                    .count();
            
            stats.put("totalStudents", totalStudents);
            stats.put("activeStudents", activeStudents);
            
            // Issue statistics
            int activeIssues = (int) allIssues.stream()
                    .filter(i -> !i.isReturned())
                    .count();
            
            int overdueIssues = (int) allIssues.stream()
                    .filter(i -> !i.isReturned())
                    .filter(i -> LocalDate.now().isAfter(i.getReturnDate()))
                    .count();
            
            stats.put("activeIssues", activeIssues);
            stats.put("overdueIssues", overdueIssues);
            
            // Revenue statistics
            int totalFines = allIssues.stream().mapToInt(Issue::getFine).sum();
            int totalRevenue = 0;
            try {
                List<FinePayment> payments = finePaymentService.getAllPayments();
                if (payments != null && !payments.isEmpty()) {
                    totalRevenue = payments.stream()
                            .mapToInt(FinePayment::getAmountPaid)
                            .sum();
                }
            } catch (Exception e) {
                // If no payments exist yet, totalRevenue remains 0
                totalRevenue = 0;
            }
            
            stats.put("totalFines", totalFines);
            stats.put("totalRevenue", totalRevenue);
            stats.put("pendingRevenue", Math.max(0, totalFines - totalRevenue));
        } catch (Exception e) {
            // Set default values on error
            stats.put("totalBooks", 0);
            stats.put("totalQuantity", 0);
            stats.put("availableBooks", 0);
            stats.put("issuedBooks", 0);
            stats.put("totalStudents", 0);
            stats.put("activeStudents", 0);
            stats.put("activeIssues", 0);
            stats.put("overdueIssues", 0);
            stats.put("totalFines", 0);
            stats.put("totalRevenue", 0);
            stats.put("pendingRevenue", 0);
        }
        
        return stats;
    }

    public List<Issue> getOverdueBooks() {
        return issueService.findAll().stream()
                .filter(i -> !i.isReturned())
                .filter(i -> LocalDate.now().isAfter(i.getReturnDate()))
                .collect(Collectors.toList());
    }

    public Map<String, Long> getMonthlyRevenue() {
        // This would typically query by month, simplified for now
        Map<String, Long> monthlyRevenue = new HashMap<>();
        // Implementation would group payments by month
        return monthlyRevenue;
    }
}

