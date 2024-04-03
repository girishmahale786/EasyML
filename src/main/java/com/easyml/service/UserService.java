package com.easyml.service;

import com.easyml.repository.UserRepository;
import org.springframework.stereotype.Service;
import com.easyml.model.User;

@Service
public class UserService {
    private final UserRepository userRepository;
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    public User registerUser(String name, String email, String password) {
        if (name == null || name.isEmpty() || email == null || email.isEmpty() || password == null || password.isEmpty()) {
            return null;
        } else {
            if (userRepository.findByEmail(email).isPresent() || userRepository.findByName(name).isPresent()) {
                System.err.println("User already exists");
                return null;
            } else {
                User user = new User();
                user.setName(name);
                user.setEmail(email);
                user.setPassword(password);
                return userRepository.save(user);
            }
        }
    }
    public User authenticate(String email, String password) {
        return userRepository.findByEmailAndPassword(email, password).orElse(null);
    }
}


