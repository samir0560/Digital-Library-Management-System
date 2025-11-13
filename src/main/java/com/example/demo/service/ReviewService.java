package com.example.demo.service;

import com.example.demo.model.Book;
import com.example.demo.model.Review;
import com.example.demo.model.User;
import com.example.demo.repository.ReviewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReviewService {

    @Autowired
    private ReviewRepository reviewRepository;

    public Review createOrUpdateReview(Book book, User student, int rating, String comment) {
        if (book == null || student == null) {
            throw new RuntimeException("Book or student cannot be null");
        }
        if (rating < 1 || rating > 5) {
            throw new RuntimeException("Rating must be between 1 and 5");
        }

        Review review = reviewRepository.findByBookAndStudent(book, student);
        if (review == null) {
            review = new Review();
            review.setBook(book);
            review.setStudent(student);
        }

        review.setRating(rating);
        if (comment != null) {
            review.setComment(comment);
        }

        return reviewRepository.save(review);
    }

    public List<Review> getReviewsByBook(Book book) {
        if (book == null) {
            return new java.util.ArrayList<>();
        }
        List<Review> reviews = reviewRepository.findByBook(book);
        return (reviews != null) ? reviews : new java.util.ArrayList<>();
    }

    public List<Review> getReviewsByStudent(User student) {
        if (student == null) {
            return new java.util.ArrayList<>();
        }
        List<Review> reviews = reviewRepository.findByStudent(student);
        return (reviews != null) ? reviews : new java.util.ArrayList<>();
    }

    public double getAverageRating(Book book) {
        if (book == null) {
            return 0.0;
        }
        List<Review> reviews = reviewRepository.findByBook(book);
        if (reviews == null || reviews.isEmpty()) {
            return 0.0;
        }
        return reviews.stream()
                .mapToInt(Review::getRating)
                .average()
                .orElse(0.0);
    }

    public int getReviewCount(Book book) {
        if (book == null) {
            return 0;
        }
        List<Review> reviews = reviewRepository.findByBook(book);
        return (reviews != null) ? reviews.size() : 0;
    }

    public void deleteReview(String reviewId) {
        reviewRepository.deleteById(reviewId);
    }
}

