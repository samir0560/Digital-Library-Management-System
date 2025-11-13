package com.example.demo.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.example.demo.model.FinePayment;
import com.example.demo.model.Issue;
import com.example.demo.model.User;


public interface FinePaymentRepository extends MongoRepository<FinePayment, String> {
    List<FinePayment> findByIssue(Issue issue);
    List<FinePayment> findByStudent(User student);
}

