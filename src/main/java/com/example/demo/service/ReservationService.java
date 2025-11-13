package com.example.demo.service;

import com.example.demo.model.Book;
import com.example.demo.model.Reservation;
import com.example.demo.model.User;
import com.example.demo.repository.ReservationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReservationService {

    @Autowired
    private ReservationRepository reservationRepository;

    public Reservation createReservation(Book book, User student) {
        if (book == null || student == null) {
            throw new RuntimeException("Book or student cannot be null");
        }
        
        // Check if already reserved by this student
        List<Reservation> existing = reservationRepository.findByBookAndFulfilledFalse(book);
        if (existing != null) {
            boolean alreadyReserved = existing.stream()
                    .anyMatch(r -> r.getStudent() != null && r.getStudent().getId() != null 
                            && r.getStudent().getId().equals(student.getId()));
            
            if (alreadyReserved) {
                throw new RuntimeException("You have already reserved this book");
            }
        }

        Reservation reservation = new Reservation();
        reservation.setBook(book);
        reservation.setStudent(student);
        
        // Calculate queue position
        List<Reservation> pendingReservations = reservationRepository
                .findByBookAndFulfilledFalse(book);
        int queueSize = (pendingReservations != null) ? pendingReservations.size() : 0;
        reservation.setQueuePosition(queueSize + 1);

        return reservationRepository.save(reservation);
    }

    public List<Reservation> getReservationsByBook(Book book) {
        if (book == null) {
            return new java.util.ArrayList<>();
        }
        List<Reservation> reservations = reservationRepository.findByBook(book);
        return (reservations != null) ? reservations : new java.util.ArrayList<>();
    }

    public List<Reservation> getReservationsByStudent(User student) {
        if (student == null) {
            return new java.util.ArrayList<>();
        }
        List<Reservation> reservations = reservationRepository.findByStudent(student);
        return (reservations != null) ? reservations : new java.util.ArrayList<>();
    }

    public List<Reservation> getPendingReservations() {
        List<Reservation> reservations = reservationRepository.findByFulfilledFalse();
        return (reservations != null) ? reservations : new java.util.ArrayList<>();
    }

    public void fulfillReservation(Reservation reservation) {
        if (reservation != null) {
            reservation.setFulfilled(true);
            reservationRepository.save(reservation);
            
            // Update queue positions for remaining reservations
            Book book = reservation.getBook();
            if (book != null) {
                updateQueuePositions(book);
            }
        }
    }

    public void cancelReservation(String reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId).orElse(null);
        if (reservation != null) {
            Book book = reservation.getBook();
            reservationRepository.delete(reservation);
            if (book != null) {
                updateQueuePositions(book);
            }
        }
    }

    private void updateQueuePositions(Book book) {
        if (book == null) {
            return;
        }
        List<Reservation> pending = reservationRepository.findByBookAndFulfilledFalse(book);
        if (pending == null || pending.isEmpty()) {
            return;
        }
        
        List<Reservation> sorted = pending.stream()
                .filter(r -> r != null && r.getReservationDate() != null)
                .sorted((r1, r2) -> r1.getReservationDate().compareTo(r2.getReservationDate()))
                .collect(Collectors.toList());
        
        for (int i = 0; i < sorted.size(); i++) {
            sorted.get(i).setQueuePosition(i + 1);
            reservationRepository.save(sorted.get(i));
        }
    }

    public List<Reservation> getReservationsToNotify() {
        List<Reservation> reservations = reservationRepository.findByFulfilledFalse();
        if (reservations == null || reservations.isEmpty()) {
            return new java.util.ArrayList<>();
        }
        return reservations.stream()
                .filter(r -> {
                    if (r == null || r.isNotified() || r.getBook() == null) {
                        return false;
                    }
                    Integer available = r.getBook().getAvailable();
                    return available != null && available > 0;
                })
                .toList();
    }
}

