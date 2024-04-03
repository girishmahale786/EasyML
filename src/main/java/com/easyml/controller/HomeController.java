package com.easyml.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class HomeController {

    @GetMapping("/")
    public String home(Model model){
        return "home";
    }

    @GetMapping("/about")
    public String About(Model model){
        return "About";
    }
    @GetMapping("/contact")
    public String Contact(Model model){
        return "Contact";
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model){
        return "dashboard";
    }

}
