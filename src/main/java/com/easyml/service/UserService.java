package com.easyml.service;

import com.easyml.model.User;
import com.easyml.repository.UserRepository;
import com.easyml.util.EncryptionUtil;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;


@Service
public class UserService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
        this.bCryptPasswordEncoder = new BCryptPasswordEncoder();
    }

    public String encodePassword(String password) {
        return bCryptPasswordEncoder.encode(password);
    }

    public boolean isPasswordValid(String rawPassword, String encodedPassword) {
        return bCryptPasswordEncoder.matches(rawPassword, encodedPassword);
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
                user.setPassword(encodePassword(password));
                return userRepository.save(user);
            }
        }
    }

    public User authenticate(String email, String password) {
        User user = userRepository.findByEmail(email).orElse(null);
        if (user != null) {
            return isPasswordValid(password, user.getPassword()) ? user : null;
        }
        return null;
    }

    public User getUser(Long userId) {
        return userRepository.findById(userId).orElse(null);
    }

    public void setUserLogin(Model model, @CookieValue(value = "user_id", required = false) String userId) throws Exception {
        model.addAttribute("loggedIn", false);
        if (userId != null) {
            Long id = Long.valueOf(EncryptionUtil.decrypt(userId));
            User user = getUser(id);
            model.addAttribute("loggedIn", true);
            model.addAttribute("user", user);
        }
    }
}
