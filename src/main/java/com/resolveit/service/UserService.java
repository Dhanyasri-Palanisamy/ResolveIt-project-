package com.resolveit.service;

import com.resolveit.model.User;
import com.resolveit.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {
    
    @Autowired
    private UserRepository userRepository;
    
    public User registerUser(User user) {
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new RuntimeException("Email already exists");
        }
        // In production, use proper password encryption (BCrypt)
        return userRepository.save(user);
    }
    
    public User loginUser(String email, String password) {
        Optional<User> userOpt = userRepository.findByEmail(email);
        
        if (userOpt.isEmpty()) {
            throw new RuntimeException("User not found");
        }
        
        User user = userOpt.get();
        
        // In production, use BCrypt password matching
        if (!user.getPassword().equals(password)) {
            throw new RuntimeException("Invalid password");
        }
        
        return user;
    }
}