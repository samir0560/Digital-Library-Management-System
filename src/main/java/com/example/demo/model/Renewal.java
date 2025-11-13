package com.example.demo.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDate;

@Document(collection = "renewals")
public class Renewal {
    @Id
    private String id;
    @DBRef
    private Issue issue;
    private LocalDate oldReturnDate;
    private LocalDate newReturnDate;
    private LocalDate renewalDate;
    private String renewedBy;

    public Renewal() {
        this.renewalDate = LocalDate.now();
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public Issue getIssue() { return issue; }
    public void setIssue(Issue issue) { this.issue = issue; }
    public LocalDate getOldReturnDate() { return oldReturnDate; }
    public void setOldReturnDate(LocalDate oldReturnDate) { this.oldReturnDate = oldReturnDate; }
    public LocalDate getNewReturnDate() { return newReturnDate; }
    public void setNewReturnDate(LocalDate newReturnDate) { this.newReturnDate = newReturnDate; }
    public LocalDate getRenewalDate() { return renewalDate; }
    public void setRenewalDate(LocalDate renewalDate) { this.renewalDate = renewalDate; }
    public String getRenewedBy() { return renewedBy; }
    public void setRenewedBy(String renewedBy) { this.renewedBy = renewedBy; }
}
