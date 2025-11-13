package com.example.demo.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Value("${app.admin.username}")
    private String adminUsername;

    @Value("${app.admin.password}")
    private String adminPassword;

    @Override
    public UserDetails loadUserByUsername(String usernameOrEmail) throws UsernameNotFoundException {
        // Try to find user by username first
        User user = userRepository.findByUsername(usernameOrEmail);
        
        // If not found by username, try to find by email
        if (user == null) {
            user = userRepository.findByEmail(usernameOrEmail);
        }

        // If user not found, check if it's admin from properties (only by username)
        if (user == null && usernameOrEmail.equals(adminUsername)) {
            // Create admin user if not exists
            user = new User();
            user.setUsername(adminUsername);
            user.setPassword(passwordEncoder.encode(adminPassword));
            user.setRole("ADMIN");
            userRepository.save(user);
        }

        if (user == null) {
            throw new UsernameNotFoundException("User not found with username/email: " + usernameOrEmail);
        }

        // Update admin password if needed (only for username, not email)
        if (usernameOrEmail.equals(adminUsername) && user.getUsername().equals(adminUsername) && 
            !passwordEncoder.matches(adminPassword, user.getPassword())) {
            user.setPassword(passwordEncoder.encode(adminPassword));
            user.setRole("ADMIN");
            userRepository.save(user);
        }

        // Check if user is active
        if (!user.isActive()) {
            throw new UsernameNotFoundException("User account is inactive: " + usernameOrEmail);
        }

        return org.springframework.security.core.userdetails.User
                .withUsername(user.getUsername())
                .password(user.getPassword())
                .authorities(getAuthorities(user.getRole()))
                .build();
    }

    private Collection<? extends GrantedAuthority> getAuthorities(String role) {
        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_" + role));
        return authorities;
    }
}

