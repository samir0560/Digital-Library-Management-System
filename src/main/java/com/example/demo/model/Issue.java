package com.example.demo.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;

/**
 * Issue entity (MongoDB Document)
 * Represents a book issue transaction in the library system
 */
@Document(collection = "issues")
public class Issue {

    @Id
    private String id;

    @DBRef
    private Book book;

    @DBRef
    private User student;

    private LocalDate issueDate;
    private LocalDate returnDate;
    private boolean returned;
    private int fine;
    private boolean returnArchived = false;
    private int renewalCount = 0; // Track number of renewals

    // Default constructor
    public Issue() {
        this.returned = false;
        this.fine = 0;
        this.renewalCount = 0;
    }

    // Parameterized constructor
    public Issue(Book book, User student, LocalDate issueDate, LocalDate returnDate) {
        this.book = book;
        this.student = student;
        this.issueDate = issueDate;
        this.returnDate = returnDate;
        this.returned = false;
        this.fine = 0;
        this.renewalCount = 0;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Book getBook() {
        return book;
    }

    public void setBook(Book book) {
        this.book = book;
    }

    public User getStudent() {
        return student;
    }

    public void setStudent(User student) {
        this.student = student;
    }

    public LocalDate getIssueDate() {
        return issueDate;
    }

    public void setIssueDate(LocalDate issueDate) {
        this.issueDate = issueDate;
    }

    public LocalDate getReturnDate() {
        return returnDate;
    }

    public void setReturnDate(LocalDate returnDate) {
        this.returnDate = returnDate;
    }

    public boolean isReturned() {
        return returned;
    }

    public void setReturned(boolean returned) {
        this.returned = returned;
    }

    public int getFine() {
        return fine;
    }

    public void setFine(int fine) {
        this.fine = fine;
    }

    public boolean isReturnArchived() {
        return returnArchived;
    }

    public void setReturnArchived(boolean returnArchived) {
        this.returnArchived = returnArchived;
    }

    public int getRenewalCount() {
        return renewalCount;
    }

    public void setRenewalCount(int renewalCount) {
        this.renewalCount = renewalCount;
    }

    /**
     * Check if the book is overdue
     * @return true if return date has passed and book is not returned
     */
    public boolean isOverdue() {
        return !returned && LocalDate.now().isAfter(returnDate);
    }

    /**
     * Calculate days overdue
     * @return number of days overdue, 0 if not overdue
     */
    public long getDaysOverdue() {
        if (!isOverdue()) {
            return 0;
        }
        return java.time.temporal.ChronoUnit.DAYS.between(returnDate, LocalDate.now());
    }

    @Override
    public String toString() {
        return "Issue{" +
                "id='" + id + '\'' +
                ", book=" + (book != null ? book.getTitle() : "null") +
                ", student=" + (student != null ? student.getUsername() : "null") +
                ", issueDate=" + issueDate +
                ", returnDate=" + returnDate +
                ", returned=" + returned +
                ", fine=" + fine +
                ", renewalCount=" + renewalCount +
                '}';
    }
}