package com.easyml.controller;

import com.easyml.model.User;
import com.easyml.service.UserService;
import com.easyml.util.EncryptionUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/login")
    public String login(Model model, @CookieValue(value = "user_id", required = false) String userId) {
        if (userId != null) {
            return "redirect:/dashboard";
        }
        model.addAttribute("LoginRequest", new User());
        return "login";
    }

    @GetMapping("/logout")
    public String logout(Model model, HttpServletResponse response) {
        Cookie deleteUser = new Cookie("user_id", null);
        deleteUser.setMaxAge(0);
        response.addCookie(deleteUser);
        return "redirect:/login";
    }

    @GetMapping("/register")
    public String register(Model model, @CookieValue(value = "user_id", required = false) String userId) {
        if (userId != null) {
            return "redirect:/dashboard";
        }
        model.addAttribute("RegisterRequest", new User());
        return "login";
    }

    @PostMapping("/register")
    public String register(@ModelAttribute User users, RedirectAttributes redirectAttributes) {
        User registeredUser = userService.registerUser(users.getName(), users.getEmail(), users.getPassword());
        if (registeredUser == null) {
            redirectAttributes.addFlashAttribute("errorMsg", "User already exists");
            redirectAttributes.addFlashAttribute("backLink", "/login");
            return "redirect:/error";
        }
        return "redirect:/login";
    }
    @GetMapping("/verify-account")
    public ResponseEntity<String> verifyAccount(@RequestParam String email,
                                                @RequestParam String otp) {
        return new ResponseEntity<>(userService.verifyAccount(email, otp), HttpStatus.OK);
    }
    @PostMapping("/login")
    public String login(@ModelAttribute User users, RedirectAttributes redirectAttributes, HttpServletResponse response) throws Exception {
        User authenticated = userService.authenticate(users.getEmail(), users.getPassword());
        if (authenticated == null) {
            redirectAttributes.addFlashAttribute("errorMsg", "Invalid authentication credentials");
            redirectAttributes.addFlashAttribute("backLink", "/login");
            return "redirect:/error";
        }
        Long userId = authenticated.getId();
        Cookie userCookie = new Cookie("user_id", EncryptionUtil.encrypt(userId.toString()));
        userCookie.setMaxAge(60 * 60 * 24);
        userCookie.setSecure(true);
        userCookie.setHttpOnly(true);
        response.addCookie(userCookie);
        return "redirect:/dashboard";
    }
}
