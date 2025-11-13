package com.example.demo.service;

import com.example.demo.model.Book;
import com.example.demo.repository.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BookService {

    @Autowired
    private BookRepository bookRepo;

    public Book save(Book book) {
        if (book == null) {
            throw new IllegalArgumentException("Book cannot be null");
        }
        return bookRepo.save(book);
    }
    
    public Book findById(String id) {
        if (id == null || id.isEmpty()) {
            return null;
        }
        return bookRepo.findById(id).orElse(null);
    }
    
    public void deleteById(String id) {
        if (id != null && !id.isEmpty()) {
            bookRepo.deleteById(id);
        }
    }
    
    public List<Book> findAll() {
        List<Book> books = bookRepo.findAll();
        return (books != null) ? books : new java.util.ArrayList<>();
    }
}
