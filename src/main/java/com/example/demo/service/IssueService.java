package com.example.demo.service;

import com.example.demo.model.Book;
import com.example.demo.model.Issue;
import com.example.demo.model.User;
import com.example.demo.repository.IssueRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class IssueService {

    @Autowired
    private IssueRepository issueRepository;

    public Issue findById(String id) {
        return issueRepository.findById(id).orElse(null);
    }

    public void save(Issue issue) {
        issueRepository.save(issue);
    }

    public List<Issue> findAll() {
        List<Issue> issues = issueRepository.findAll();
        return (issues != null) ? issues : new java.util.ArrayList<>();
    }

    public List<Issue> findByStudent(User student) {
        if (student == null) {
            return new java.util.ArrayList<>();
        }
        List<Issue> issues = issueRepository.findByStudent(student);
        return (issues != null) ? issues : new java.util.ArrayList<>();
    }
    
    public List<Issue> findAllReturned() {
        List<Issue> issues = issueRepository.findByReturnedTrue();
        return (issues != null) ? issues : new java.util.ArrayList<>();
    }

    public List<Issue> findActiveIssuesByBook(Book book) {
        if (book == null) {
            return new java.util.ArrayList<>();
        }
        List<Issue> issues = issueRepository.findByBookAndReturnedFalse(book);
        return (issues != null) ? issues : new java.util.ArrayList<>();
    }

}
