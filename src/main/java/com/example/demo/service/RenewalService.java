package com.example.demo.service;

import com.example.demo.model.Issue;
import com.example.demo.model.Renewal;
import com.example.demo.repository.RenewalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class RenewalService {

    @Autowired
    private RenewalRepository renewalRepository;

    private static final int MAX_RENEWALS = 2; // Maximum 2 renewals per book
    private static final int RENEWAL_DAYS = 7; // Extend by 7 days

    @Transactional
    public Renewal renewIssue(Issue issue, String renewedBy, IssueService issueService) {
        if (issue == null) {
            throw new RuntimeException("Issue not found");
        }
        
        if (issue.getRenewalCount() >= MAX_RENEWALS) {
            throw new RuntimeException("Maximum renewals reached for this book");
        }

        if (issue.isReturned()) {
            throw new RuntimeException("Cannot renew a returned book");
        }

        Renewal renewal = new Renewal();
        renewal.setIssue(issue);
        renewal.setOldReturnDate(issue.getReturnDate());
        renewal.setNewReturnDate(issue.getReturnDate().plusDays(RENEWAL_DAYS));
        renewal.setRenewedBy(renewedBy);
        
        issue.setReturnDate(renewal.getNewReturnDate());
        issue.setRenewalCount(issue.getRenewalCount() + 1);
        
        // Save the updated issue
        issueService.save(issue);

        return renewalRepository.save(renewal);
    }

    public List<Renewal> getRenewalsByIssue(Issue issue) {
        return renewalRepository.findByIssue(issue);
    }

    public List<Renewal> getAllRenewals() {
        return renewalRepository.findAll();
    }
}

