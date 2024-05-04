package com.easyml.service;

import com.easyml.exception.EmailException;
import com.easyml.exception.InvalidOtp;
import com.easyml.exception.UserNotFound;
import com.easyml.model.User;
import com.easyml.repository.UserRepository;
import com.easyml.util.OtpUtil;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

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

    public User registerUser(String name, String email, String password) throws UserNotFound, EmailException {
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

    public void sendOtp(String email) throws EmailException, UserNotFound {
        User user = getUserByEmail(email);
        if (user == null) {
            throw new UserNotFound();
        }
        Integer otp = otpUtil.generateOtp();
        otpUtil.sendOtpEmail(user.getName(), email, otp);
        user.setOtp(otp);
        user.setOtpGeneratedTime(LocalDateTime.now());
        userRepository.save(user);
    }

    public void verifyOtp(String email, Integer otp) throws InvalidOtp, UserNotFound {
        User user = getUserByEmail(email);
        if (user == null) {
            throw new UserNotFound();
        }
        boolean verified = user.getOtp().equals(otp) && Duration.between(user.getOtpGeneratedTime(), LocalDateTime.now()).getSeconds() < (3 * 60);
        if (!verified) {
            throw new InvalidOtp();
        }
        user.setActive(true);
        userRepository.save(user);
    }

    public User authenticate(String email, String password) {
        User user = getUserByEmail(email);
        if (user != null) {
            return isPasswordValid(password, user.getPassword()) ? user : null;
        }
        return null;
    }

    public void changePassword(String email, String password) throws UserNotFound {
        User user = getUserByEmail(email);
        if (user == null) {
            throw new UserNotFound();
        }
        user.setPassword(encodePassword(password));
        userRepository.save(user);

    }

    public User getUser(Long userId) {
        return userRepository.findById(userId).orElse(null);
    }

    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email).orElse(null);
    }

}
