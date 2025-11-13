package com.example.demo.controller;

import com.example.demo.model.Book;
import com.example.demo.model.Issue;
import com.example.demo.service.BookService;
import com.example.demo.service.DashboardService;
import com.example.demo.service.IssueService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/admin/reports")
public class ReportController {

    @Autowired
    private DashboardService dashboardService;

    @Autowired
    private BookService bookService;

    @Autowired
    private IssueService issueService;

    @GetMapping("/export/books")
    public void exportBooksCSV(HttpServletResponse response) throws IOException {
        response.setContentType("text/csv");
        response.setHeader("Content-Disposition", "attachment; filename=books.csv");

        PrintWriter writer = response.getWriter();
        writer.println("ID,Title,Author,Quantity,Available");

        List<Book> books = bookService.findAll();
        for (Book book : books) {
            writer.println(String.format("%s,%s,%s,%d,%d",
                    book.getId(), book.getTitle(), book.getAuthor(),
                    book.getQuantity(), book.getAvailable()));
        }

        writer.flush();
    }

    @GetMapping("/export/issues")
    public void exportIssuesCSV(HttpServletResponse response) throws IOException {
        response.setContentType("text/csv");
        response.setHeader("Content-Disposition", "attachment; filename=issues.csv");

        PrintWriter writer = response.getWriter();
        writer.println("ID,Book Title,Student,Issue Date,Return Date,Returned,Fine");

        List<Issue> issues = issueService.findAll();
        for (Issue issue : issues) {
            writer.println(String.format("%s,%s,%s,%s,%s,%s,%d",
                    issue.getId(),
                    issue.getBook() != null ? issue.getBook().getTitle() : "N/A",
                    issue.getStudent() != null ? issue.getStudent().getUsername() : "N/A",
                    issue.getIssueDate(),
                    issue.getReturnDate(),
                    issue.isReturned(),
                    issue.getFine()));
        }

        writer.flush();
    }

    @GetMapping("/popular-books")
    public String popularBooksReport(org.springframework.ui.Model model) {
        List<Book> books = bookService.findAll();
        // This would typically calculate based on issue history
        model.addAttribute("books", books);
        return "popular_books_report";
    }

    @GetMapping("/statistics")
    public String statisticsReport(org.springframework.ui.Model model) {
        Map<String, Object> stats = dashboardService.getAdminStatistics();
        model.addAttribute("stats", stats);
        return "statistics_report";
    }
}

