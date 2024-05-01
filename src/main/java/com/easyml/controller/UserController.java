package com.easyml.controller;

import com.easyml.model.User;
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

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/login")
    public String login(Model model, @CookieValue(value = "user_id", required = false) String userId, RedirectAttributes redirectAttributes) {
        if (userId != null) {
            userService.setFlashError(redirectAttributes, "Signed in already, please proceed to dashboard.");
            return "redirect:/dashboard";
        }
        model.addAttribute("formObject", new User());
        userService.setAuthPage(model, "Login to your account", "login");
        return "base";
    }

    @GetMapping("/logout")
    public String logout(Model model, HttpServletResponse response, RedirectAttributes redirectAttributes) {
        Cookie deleteUser = new Cookie("user_id", null);
        deleteUser.setMaxAge(0);
        response.addCookie(deleteUser);
        userService.setFlashError(redirectAttributes, "Logged out successfully.");
        return "redirect:/login";
    }

    @GetMapping("/register")
    public String register(Model model, @CookieValue(value = "user_id", required = false) String userId, RedirectAttributes redirectAttributes) {
        if (userId != null) {
            userService.setFlashError(redirectAttributes, "Signed in already, please proceed to dashboard.");
            return "redirect:/dashboard";
        }
        model.addAttribute("formObject", new User());
        userService.setAuthPage(model, "Create new account", "signup");
        return "base";
    }

    @GetMapping("/reset-password")
    public String resetPassword(Model model) {
        userService.setAuthPage(model, "Reset your password", "reset_password");
        return "base";
    }

    @GetMapping("/verify-otp")
    public String verifyOtp(Model model, RedirectAttributes redirectAttributes, HttpSession session) {
        if (session.getAttribute("email") == null) {
            userService.setFlashError(redirectAttributes, "Can't access this page at the moment!");
            return "redirect:/";
        }
        userService.setAuthPage(model, "OTP Verification", "verify_otp");
        return "base";
    }

    @GetMapping("/resend-otp")
    public String resendOtp(RedirectAttributes redirectAttributes, HttpSession session) {
        if (session.getAttribute("email") == null) {
            userService.setFlashError(redirectAttributes, "Can't access this page at the moment!");
            return "redirect:/";
        }
        String email = session.getAttribute("email").toString();
        userService.sendOtp(email);
        userService.setFlashError(redirectAttributes, "An OTP is sent to your email!");
        return "redirect:/verify-otp";
    }

    @GetMapping("/change-password")
    public String changePassword(Model model, RedirectAttributes redirectAttributes, HttpSession session) {
        Object requestType = session.getAttribute("requestType");
        if (requestType == null || !requestType.toString().equals("resetPassword")) {
            userService.setFlashError(redirectAttributes, "Can't access this page at the moment!");
            return "redirect:/";
        }
        userService.setAuthPage(model, "Change Password", "change_password");
        return "base";
    }

    @PostMapping("/register")
    public String register(@ModelAttribute User user, RedirectAttributes redirectAttributes, HttpSession session) {
        User registeredUser = userService.registerUser(user.getName(), user.getEmail(), user.getPassword());
        if (registeredUser == null) {
            userService.setFlashError(redirectAttributes, "User with this email already exists!");
            return "redirect:/login";
        }
        userService.setFlashError(redirectAttributes, "An OTP has been sent to your email, verify to complete the registration!");
        session.setAttribute("email", registeredUser.getEmail());
        session.setAttribute("requestType", "register");
        return "redirect:/verify-otp";
    }

    @PostMapping("/login")
    public String login(@ModelAttribute User user, RedirectAttributes redirectAttributes, HttpServletResponse response, HttpSession session) throws Exception {
        User authenticated = userService.authenticate(user.getEmail(), user.getPassword());
        if (authenticated == null) {
            userService.setFlashError(redirectAttributes, "Invalid authentication credentials!");
            return "redirect:/login";
        }
        if (!authenticated.isActive()) {
            userService.setFlashError(redirectAttributes, "Email verification pending!");
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
        userService.setFlashError(redirectAttributes, "Signed in successfully!");
        return "redirect:/dashboard";
    }

    @PostMapping("/reset-password")
    public String resetPassword(@ModelAttribute User user, RedirectAttributes redirectAttributes, HttpSession session) {
        if (userService.getUserByEmail(user.getEmail()) == null){
            userService.setFlashError(redirectAttributes, "User with this email is not registered!");
            return "redirect:/reset-password";
        }

        Boolean success = userService.sendOtp(user.getEmail());
        if (!success) {
            userService.setFlashError(redirectAttributes, "Something went wrong, please try again!");
        }
        userService.setFlashError(redirectAttributes, "An OTP has been sent to your email!");
        session.setAttribute("email", user.getEmail());
        session.setAttribute("requestType", "resetPassword");
        return "redirect:/verify-otp";
    }

    @PostMapping("/verify-otp")
    public String verifyOtp(Model model, @RequestParam String otp, RedirectAttributes redirectAttributes, HttpSession session) {
        String email = session.getAttribute("email").toString();
        String requestType = session.getAttribute("requestType").toString();
        Boolean success = userService.verifyOtp(email, Integer.parseInt(otp));
        if (!success) {
            userService.setFlashError(redirectAttributes, "Invalid OTP! Please try resending the OTP!");
            return "redirect:/verify-otp";
        }
        session.setAttribute("success", true);
        if (requestType.equals("register")) {
            userService.setFlashError(redirectAttributes, "OTP Verification Successful, Please login to continue!");
            return "redirect:/login";
        }
        userService.setFlashError(redirectAttributes, "OTP Verification Successful!");
        return "redirect:/change-password";
    }

    @PostMapping("/change-password")
    public String changePassword(Model model, @RequestParam String password, @RequestParam String password2, RedirectAttributes redirectAttributes, HttpSession session) {
        String email = session.getAttribute("email").toString();
        String requestType = session.getAttribute("requestType").toString();
        Boolean success = (Boolean) session.getAttribute("success");
        if (!requestType.equals("resetPassword")) {
            userService.setFlashError(redirectAttributes, "Can't access this page at the moment!");
            return "redirect:/";
        }
        if (!success) {
            userService.setFlashError(redirectAttributes, "Session expired, Please try again!");
            return "redirect:/forget-password";
        }
        if (!password.equals(password2)) {
            userService.setAuthPage(model, "Change Password", "change_password");
            userService.setError(model, "Both password doesn't match!");
            return "base";
        }
        success = userService.changePassword(email, password);
        if (!success) {
            userService.setFlashError(redirectAttributes, "Something went wrong, Please try again!");
            return "redirect:/reset-password";
        }
        userService.setFlashError(redirectAttributes, "Password reset successful, login to continue!");
        return "redirect:/login";
    }


}
