package com.example.demo.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.example.demo.model.Book;
import com.example.demo.model.Review;
import com.example.demo.model.User;

public interface ReviewRepository extends MongoRepository<Review, String> {
    List<Review> findByBook(Book book);
    List<Review> findByStudent(User student);
    Review findByBookAndStudent(Book book, User student);
}

