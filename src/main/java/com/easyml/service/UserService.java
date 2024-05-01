package com.easyml.service;

import com.easyml.model.User;
import com.easyml.repository.UserRepository;
import com.easyml.util.EmailUtil;
import com.easyml.util.EncryptionUtil;
import com.easyml.util.OtpUtil;
import jakarta.mail.MessagingException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.Duration;
import java.time.LocalDateTime;


@Service
public class UserService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final OtpUtil otpUtil;
    private final EmailUtil emailUtil;

    public UserService(UserRepository userRepository, OtpUtil otpUtil, EmailUtil emailUtil) {
        this.userRepository = userRepository;
        this.bCryptPasswordEncoder = new BCryptPasswordEncoder();
        this.otpUtil = otpUtil;
        this.emailUtil = emailUtil;
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
                String otp = otpUtil.generateOtp();
                try {
                    emailUtil.sendOtpEmail(email, otp);
                } catch (MessagingException e) {
                    throw new RuntimeException("Unable to send otp please try again");
                }
                User user = new User();
                user.setName(name);
                user.setEmail(email);
                user.setPassword(encodePassword(password));
                user.setOtp(otp);
                user.setOtpGeneratedTime(LocalDateTime.now());
                return userRepository.save(user);
            }
        }
    }

    public String regenerateOtp(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found with this email: " + email));
        String otp = otpUtil.generateOtp();
        try {
            emailUtil.sendOtpEmail(email, otp);
        } catch (MessagingException e) {
            throw new RuntimeException("Unable to send otp please try again");
        }
        user.setOtp(otp);
        user.setOtpGeneratedTime(LocalDateTime.now());
        userRepository.save(user);
        return "Email sent... please verify account within 1 minute";
    }

    public String verifyAccount(String email, String otp) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found with this email: " + email));
        if (user.getOtp().equals(otp) && Duration.between(user.getOtpGeneratedTime(),
                LocalDateTime.now()).getSeconds() < (3 * 60)) {
            user.setActive(true);
            userRepository.save(user);
            return "OTP verified you can login";
        }
        return "Please regenerate otp and try again";
    }

    public User authenticate(String email, String password) {
        User user = userRepository.findByEmail(email).orElse(null);
        if (user != null) {
            if (!user.isActive()) {
                return null;
            }
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

    public void setError(Model model, String errorMsg) {
        model.addAttribute("error", true);
        model.addAttribute("errorMsg", errorMsg);
    }

    public void setFlashError(RedirectAttributes redirectAttributes, String errorMsg) {
        redirectAttributes.addFlashAttribute("error", true);
        redirectAttributes.addFlashAttribute("errorMsg", errorMsg);
    }
}
