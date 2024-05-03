package com.easyml.service;

import com.easyml.model.User;
import com.easyml.repository.UserRepository;
import com.easyml.util.EncryptionUtil;
import com.easyml.util.OtpUtil;
import jakarta.mail.MessagingException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;

import java.time.Duration;
import java.time.LocalDateTime;


@Service
public class UserService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final OtpUtil otpUtil;

    public UserService(UserRepository userRepository, OtpUtil otpUtil) {
        this.userRepository = userRepository;
        this.bCryptPasswordEncoder = new BCryptPasswordEncoder();
        this.otpUtil = otpUtil;
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
        }
        if (userRepository.findByEmail(email).isPresent() || userRepository.findByName(name).isPresent()) {
            System.err.println("User already exists");
            return null;
        }
        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setPassword(encodePassword(password));
        user = userRepository.save(user);
        sendOtp(email);
        return user;
    }

    public Boolean sendOtp(String email) {
        User user = getUserByEmail(email);
        if (user == null) {
            return false;
        }
        Integer otp = otpUtil.generateOtp();
        try {
            otpUtil.sendOtpEmail(email, otp);
        } catch (MessagingException e) {
            System.err.println("Error sending otp email: " + e);
        }
        user.setOtp(otp);
        user.setOtpGeneratedTime(LocalDateTime.now());
        userRepository.save(user);
        return true;
    }

    public Boolean verifyOtp(String email, Integer otp) {
        User user = getUserByEmail(email);
        if (user == null) {
            return false;
        }
        if (user.getOtp().equals(otp) && Duration.between(user.getOtpGeneratedTime(), LocalDateTime.now()).getSeconds() < (3 * 60)) {
            user.setActive(true);
            userRepository.save(user);
            return true;
        }
        return false;
    }

    public User authenticate(String email, String password) {
        User user = getUserByEmail(email);
        if (user != null) {
            return isPasswordValid(password, user.getPassword()) ? user : null;
        }
        return null;
    }

    public Boolean changePassword(String email, String password) {
        User user = getUserByEmail(email);
        if (user == null) {
            return false;
        }
        user.setPassword(encodePassword(password));
        userRepository.save(user);
        return true;
    }

    public User getUser(Long userId) {
        return userRepository.findById(userId).orElse(null);
    }

    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email).orElse(null);
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

    public void setPage(Model model, String pageTitle, String pageName) {
        model.addAttribute("title", pageTitle);
        model.addAttribute("page", pageName);
    }


}
