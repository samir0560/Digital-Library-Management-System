package com.example.demo.service;

import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepo;

    public User save(User user) {
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null");
        }
        return userRepo.save(user);
    }
    
    public User findById(String id) {
        if (id == null || id.isEmpty()) {
            return null;
        }
        return userRepo.findById(id).orElse(null);
    }
    
    public User findByUsername(String username) {
        if (username == null || username.isEmpty()) {
            return null;
        }
        return userRepo.findByUsername(username);
    }

    /**
     * Find user by username or email
     * @param usernameOrEmail username or email
     * @return User if found, null otherwise
     */
    public User findByUsernameOrEmail(String usernameOrEmail) {
        if (usernameOrEmail == null || usernameOrEmail.isEmpty()) {
            return null;
        }
        // First try to find by username
        User user = userRepo.findByUsername(usernameOrEmail);
        if (user != null) {
            return user;
        }
        // If not found, try to find by email
        user = userRepo.findByEmail(usernameOrEmail);
        return user;
    }

    /**
     * Find user by email
     * @param email email address
     * @return User if found, null otherwise
     */
    public User findByEmail(String email) {
        if (email == null || email.isEmpty()) {
            return null;
        }
        return userRepo.findByEmail(email);
    }
    
    public List<User> findAllStudents() {
        List<User> students = userRepo.findByRole("STUDENT");
        return (students != null) ? students : new java.util.ArrayList<>();
    }
    
    public List<User> findAll() {
        List<User> users = userRepo.findAll();
        return (users != null) ? users : new java.util.ArrayList<>();
    }
}
