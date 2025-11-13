package com.example.demo.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.example.demo.model.Book;
import com.example.demo.model.Reservation;
import com.example.demo.model.User;


public interface ReservationRepository extends MongoRepository<Reservation, String> {
    List<Reservation> findByBook(Book book);
    List<Reservation> findByStudent(User student);
    List<Reservation> findByBookAndFulfilledFalse(Book book);
    List<Reservation> findByFulfilledFalse();
}

