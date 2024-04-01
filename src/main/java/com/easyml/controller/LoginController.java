package com.easyml.controller;
import com.easyml.model.User;
import com.easyml.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class LoginController {

        private final UserService userService;
        public LoginController(UserService userService) {
                this.userService = userService;
        }

        @GetMapping("/login")
        public String Login(Model  model){
                model.addAttribute("LoginRequest", new User());
                return "login";
        }

        @GetMapping("/register")
        public String register(Model model) {
                model.addAttribute("RegisterRequest", new User());
                return "login";
        }

        @PostMapping("/register")

        public String register(@ModelAttribute User users) {
                System.out.println("RegisterRequest" + users);
                User registeredUser = userService.registerUser(users.getName(), users.getEmail(), users.getPassword());
                return registeredUser == null ? "redirect:/" : "redirect:/login";
        }



        @PostMapping("/login")
        public String Authenticate(@ModelAttribute User users,Model model) {
                System.out.println("LoginRequest" + users);
                User authenticated = userService.authenticate(users.getEmail(), users.getPassword());
                if (authenticated != null) {
                        model.addAttribute("userlogin", authenticated.getName());
                        return "redirect:/dashboard";
                } else {
                        return "error";
                }
        }


}
