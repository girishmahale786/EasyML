package com.easyml.controller;

import com.easyml.model.User;
import com.easyml.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class LoginController {
    private final UserService userService;
    public LoginController(UserService userService) {
        this.userService = userService;
    }
    @GetMapping("/login")
    public String login(Model model) {
        model.addAttribute("LoginRequest", new User());
        return "login";
    }
    @GetMapping("/register")
    public String register(Model model) {
        model.addAttribute("RegisterRequest", new User());
        return "login";
    }
    @PostMapping("/register")
    public String register(@ModelAttribute User users, Model model, RedirectAttributes redirectAttributes) {
        User registeredUser = userService.registerUser(users.getName(), users.getEmail(), users.getPassword());
        if (registeredUser == null){
            redirectAttributes.addFlashAttribute("errorMsg", "User already exists");
            redirectAttributes.addFlashAttribute("backLink", "/login");
            return "redirect:/error";
        }
        return "redirect:/login";
    }
    @PostMapping("/login")
    public String authenticate(@ModelAttribute User users, Model model, RedirectAttributes redirectAttributes) {
        User authenticated = userService.authenticate(users.getEmail(), users.getPassword());
        if (authenticated == null) {
            redirectAttributes.addFlashAttribute("errorMsg", "Invalid authentication credentials");
            redirectAttributes.addFlashAttribute("backLink", "/login");
            return "redirect:/error";
        } else {
            redirectAttributes.addFlashAttribute("userLogin", authenticated.getName());
            return "redirect:/dashboard";
        }
    }
}
