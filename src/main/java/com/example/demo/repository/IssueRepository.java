package com.example.demo.repository;

import com.example.demo.model.Book;
import com.example.demo.model.Issue;
import com.example.demo.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;

public interface IssueRepository extends MongoRepository<Issue, String> {
    List<Issue> findByStudent(User student);
    List<Issue> findByReturnedTrue();
    List<Issue> findByBookAndReturnedFalse(Book book);
}
