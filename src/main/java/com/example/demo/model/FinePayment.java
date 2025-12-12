package com.example.demo.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDate;

@Document(collection = "fine_payments")
public class FinePayment {
    @Id
    private String id;
    @DBRef
    private Issue issue;
    @DBRef
    private User student;
    private int amountPaid;
    private LocalDate paymentDate;
    private String paymentMethod;
    private String transactionId;
    private String notes;
    private boolean isWaived = false;
    private String waivedBy;

    public FinePayment() {
        this.paymentDate = LocalDate.now();
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public Issue getIssue() { return issue; }
    public void setIssue(Issue issue) { this.issue = issue; }
    public User getStudent() { return student; }
    public void setStudent(User student) { this.student = student; }
    public int getAmountPaid() { return amountPaid; }
    public void setAmountPaid(int amountPaid) { this.amountPaid = amountPaid; }
    public LocalDate getPaymentDate() { return paymentDate; }
    public void setPaymentDate(LocalDate paymentDate) { this.paymentDate = paymentDate; }
    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }
    public String getTransactionId() { return transactionId; }
    public void setTransactionId(String transactionId) { this.transactionId = transactionId; }
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
    public boolean isWaived() { return isWaived; }
    public void setWaived(boolean waived) { this.isWaived = waived; }
    public String getWaivedBy() { return waivedBy; }
    public void setWaivedBy(String waivedBy) { this.waivedBy = waivedBy; }
}

