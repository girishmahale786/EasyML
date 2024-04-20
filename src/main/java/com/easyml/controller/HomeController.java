package com.easyml.controller;

import com.easyml.model.Enquiry;
import com.easyml.service.EnquiryService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class HomeController {

    private final EnquiryService service;

    public HomeController(EnquiryService service) {
        this.service = service;
    }

    @GetMapping("/")
    public String home(Model model) {
        return "home";
    }

    @GetMapping("/about")
    public String about(Model model) {
        return "about";
    }

    @GetMapping("/contact")
    public String contact(Model model) {
        model.addAttribute("EnquiryRequest", new Enquiry());
        return "contact";
    }

    @PostMapping("/enquiry")
    public String enquiry(@ModelAttribute Enquiry enquiry, Model model, RedirectAttributes redirectAttributes) {
        Enquiry savedEnquiry = service.saveEnquiry(enquiry.getName(), enquiry.getEmail(), enquiry.getEnquiryText());
        if (savedEnquiry == null) {
            redirectAttributes.addFlashAttribute("errorMsg", "Enquiry could not be submitted");
            redirectAttributes.addFlashAttribute("backLink", "/login");
            return "redirect:/error";
        } else {
            model.addAttribute("success", true);
        }
        return "contact";

    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        return "dashboard";
    }
}
