package com.easyml.controller;

import com.easyml.model.User;
import com.easyml.service.BackendService;
import com.easyml.service.UserService;
import com.easyml.util.EncryptionUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class UserController {
    private final UserService userService;
    private final BackendService backendService;

    public UserController(UserService userService, BackendService backendService) {
        this.userService = userService;
        this.backendService = backendService;
    }

    @GetMapping("/login")
    public String login(Model model, @CookieValue(value = "user_id", required = false) String userId, RedirectAttributes redirectAttributes) {
        if (userId != null) {
            backendService.setMessage(redirectAttributes, "info", "Signed in already, please proceed to dashboard.");
            return "redirect:/dashboard";
        }
        model.addAttribute("formObject", new User());
        backendService.setAuthPage(model, "Login to your account", "login");
        return "base";
    }

    @GetMapping("/logout")
    public String logout(HttpServletResponse response, RedirectAttributes redirectAttributes) {
        Cookie deleteUser = new Cookie("user_id", null);
        deleteUser.setMaxAge(0);
        response.addCookie(deleteUser);
        backendService.setMessage(redirectAttributes, "success", "Logged out successfully.");
        return "redirect:/";
    }

    @GetMapping("/register")
    public String register(Model model, @CookieValue(value = "user_id", required = false) String userId, RedirectAttributes redirectAttributes) {
        if (userId != null) {
            backendService.setMessage(redirectAttributes, "info", "Signed in already, please proceed to dashboard.");
            return "redirect:/dashboard";
        }
        model.addAttribute("formObject", new User());
        backendService.setAuthPage(model, "Create new account", "signup");
        return "base";
    }

    @GetMapping("/reset-password")
    public String resetPassword(Model model) {
        backendService.setAuthPage(model, "Reset your password", "reset_password");
        return "base";
    }

    @GetMapping("/verify-otp")
    public String verifyOtp(Model model, RedirectAttributes redirectAttributes, HttpSession session) {
        if (session.getAttribute("email") == null) {
            backendService.setMessage(redirectAttributes, "info", "Can't access this page at the moment!");
            return "redirect:/";
        }
        backendService.setAuthPage(model, "OTP Verification", "verify_otp");
        return "base";
    }

    @GetMapping("/resend-otp")
    public String resendOtp(RedirectAttributes redirectAttributes, HttpSession session) {
        if (session.getAttribute("email") == null) {
            backendService.setMessage(redirectAttributes, "info", "Can't access this page at the moment!");
            return "redirect:/";
        }
        String email = session.getAttribute("email").toString();
        userService.sendOtp(email);
        backendService.setMessage(redirectAttributes, "success", "An OTP is sent to your email!");
        return "redirect:/verify-otp";
    }

    @GetMapping("/change-password")
    public String changePassword(Model model, RedirectAttributes redirectAttributes, HttpSession session) {
        Object requestType = session.getAttribute("requestType");
        if (requestType == null || !requestType.toString().equals("resetPassword")) {
            backendService.setMessage(redirectAttributes, "info", "Can't access this page at the moment!");
            return "redirect:/";
        }
        backendService.setAuthPage(model, "Change Password", "change_password");
        return "base";
    }

    @PostMapping("/register")
    public String register(@ModelAttribute User user, RedirectAttributes redirectAttributes, HttpSession session) {
        User registeredUser = userService.registerUser(user.getName(), user.getEmail(), user.getPassword());
        if (registeredUser == null) {
            backendService.setMessage(redirectAttributes, "info", "User with this email already exists!");
            return "redirect:/login";
        }
        backendService.setMessage(redirectAttributes, "success", "An OTP has been sent to your email, verify to complete the registration!");
        session.setAttribute("email", registeredUser.getEmail());
        session.setAttribute("requestType", "register");
        return "redirect:/verify-otp";
    }

    @PostMapping("/login")
    public String login(@ModelAttribute User user, RedirectAttributes redirectAttributes, HttpServletResponse response, HttpSession session) throws Exception {
        User authenticated = userService.authenticate(user.getEmail(), user.getPassword());
        if (authenticated == null) {
            backendService.setMessage(redirectAttributes, "error", "Invalid authentication credentials!");
            return "redirect:/login";
        }
        if (!authenticated.isActive()) {
            backendService.setMessage(redirectAttributes, "info", "Email verification pending!");
            userService.sendOtp(authenticated.getEmail());
            session.setAttribute("email", authenticated.getEmail());
            session.setAttribute("requestType", "register");
            return "redirect:/verify-otp";
        }
        Long userId = authenticated.getId();
        Cookie userCookie = new Cookie("user_id", EncryptionUtil.encrypt(userId.toString()));
        userCookie.setMaxAge(60 * 60 * 24);
        userCookie.setSecure(true);
        userCookie.setHttpOnly(true);
        response.addCookie(userCookie);
        backendService.setMessage(redirectAttributes, "success", "Signed in successfully!");
        return "redirect:/dashboard";
    }

    @PostMapping("/reset-password")
    public String resetPassword(@ModelAttribute User user, RedirectAttributes redirectAttributes, HttpSession session) {
        if (userService.getUserByEmail(user.getEmail()) == null) {
            backendService.setMessage(redirectAttributes, "error", "User with this email is not registered!");
            return "redirect:/reset-password";
        }

        Boolean success = userService.sendOtp(user.getEmail());
        if (!success) {
            backendService.setMessage(redirectAttributes, "error", "Something went wrong, please try again!");
        }
        backendService.setMessage(redirectAttributes,"success",  "An OTP has been sent to your email!");
        session.setAttribute("email", user.getEmail());
        session.setAttribute("requestType", "resetPassword");
        return "redirect:/verify-otp";
    }

    @PostMapping("/verify-otp")
    public String verifyOtp(@RequestParam String otp, RedirectAttributes redirectAttributes, HttpSession session) {
        String email = session.getAttribute("email").toString();
        String requestType = session.getAttribute("requestType").toString();
        Boolean success = userService.verifyOtp(email, Integer.parseInt(otp));
        if (!success) {
            backendService.setMessage(redirectAttributes, "error", "Invalid OTP! Please try resending the OTP!");
            return "redirect:/verify-otp";
        }
        session.setAttribute("success", true);
        if (requestType.equals("register")) {
            backendService.setMessage(redirectAttributes,  "success","OTP Verification Successful, Please login to continue!");
            return "redirect:/login";
        }
        backendService.setMessage(redirectAttributes, "success", "OTP Verification Successful!");
        return "redirect:/change-password";
    }

    @PostMapping("/change-password")
    public String changePassword(Model model, @RequestParam String password, @RequestParam String password2, RedirectAttributes redirectAttributes, HttpSession session) {
        String email = session.getAttribute("email").toString();
        String requestType = session.getAttribute("requestType").toString();
        Boolean success = (Boolean) session.getAttribute("success");
        if (!requestType.equals("resetPassword")) {
            backendService.setMessage(redirectAttributes, "info","Can't access this page at the moment!");
            return "redirect:/";
        }
        if (!success) {
            backendService.setMessage(redirectAttributes, "info", "Session expired, Please try again!");
            return "redirect:/forget-password";
        }
        if (!password.equals(password2)) {
            backendService.setAuthPage(model, "Change Password", "change_password");
            backendService.setMessage(model, "error", "Both password doesn't match!");
            return "base";
        }
        success = userService.changePassword(email, password);
        if (!success) {
            backendService.setMessage(redirectAttributes, "error","Something went wrong, Please try again!");
            return "redirect:/reset-password";
        }
        backendService.setMessage(redirectAttributes, "success", "Password reset successful, login to continue!");
        return "redirect:/login";
    }


}
