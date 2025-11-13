package com.example.demo.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.example.demo.model.Issue;
import com.example.demo.model.Renewal;

public interface RenewalRepository extends MongoRepository<Renewal, String> {
    List<Renewal> findByIssue(Issue issue);
}

