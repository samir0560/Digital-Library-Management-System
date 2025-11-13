package com.example.demo.service;

import com.example.demo.model.FinePayment;
import com.example.demo.model.Issue;
import com.example.demo.model.User;
import com.example.demo.repository.FinePaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FinePaymentService {

    @Autowired
    private FinePaymentRepository finePaymentRepository;

    public FinePayment recordPayment(Issue issue, User student, int amountPaid, 
                                     String paymentMethod, String transactionId, String notes) {
        FinePayment payment = new FinePayment();
        payment.setIssue(issue);
        payment.setStudent(student);
        payment.setAmountPaid(amountPaid);
        payment.setPaymentMethod(paymentMethod);
        payment.setTransactionId(transactionId);
        payment.setNotes(notes);
        
        return finePaymentRepository.save(payment);
    }

    public FinePayment waiveFine(Issue issue, User student, String waivedBy, String notes) {
        FinePayment payment = new FinePayment();
        payment.setIssue(issue);
        payment.setStudent(student);
        payment.setAmountPaid(issue.getFine());
        payment.setWaived(true);
        payment.setWaivedBy(waivedBy);
        payment.setNotes(notes);
        
        return finePaymentRepository.save(payment);
    }

    public List<FinePayment> getPaymentsByIssue(Issue issue) {
        return finePaymentRepository.findByIssue(issue);
    }

    public List<FinePayment> getPaymentsByStudent(User student) {
        return finePaymentRepository.findByStudent(student);
    }

    public int getTotalPaidForIssue(Issue issue) {
        List<FinePayment> payments = finePaymentRepository.findByIssue(issue);
        if (payments == null || payments.isEmpty()) {
            return 0;
        }
        return payments.stream()
                .mapToInt(FinePayment::getAmountPaid)
                .sum();
    }

    public int getTotalPaidByStudent(User student) {
        List<FinePayment> payments = finePaymentRepository.findByStudent(student);
        if (payments == null || payments.isEmpty()) {
            return 0;
        }
        return payments.stream()
                .mapToInt(FinePayment::getAmountPaid)
                .sum();
    }

    public int getRemainingFine(Issue issue) {
        if (issue == null) {
            return 0;
        }
        int totalFine = issue.getFine();
        int totalPaid = getTotalPaidForIssue(issue);
        return Math.max(0, totalFine - totalPaid);
    }

    public List<FinePayment> getAllPayments() {
        List<FinePayment> payments = finePaymentRepository.findAll();
        return (payments != null) ? payments : new java.util.ArrayList<>();
    }
}

