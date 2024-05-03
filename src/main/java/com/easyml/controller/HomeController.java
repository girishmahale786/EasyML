package com.easyml.controller;

import com.easyml.exception.EncryptionException;
import com.easyml.model.Enquiry;
import com.easyml.service.BackendService;
import com.easyml.service.EnquiryService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class HomeController {

    private final EnquiryService enquiryService;
    private final BackendService backendService;

    public HomeController(EnquiryService enquiryService, BackendService backendService) {
        this.enquiryService = enquiryService;
        this.backendService = backendService;
    }

    @GetMapping("/")
    public String home(Model model, @CookieValue(value = "user_id", required = false) String userId, RedirectAttributes redirectAttributes) {
        try {
            backendService.setLogin(model, userId);
        } catch (EncryptionException ee) {
            return "redirect:/error";
        }
        model.addAttribute("EnquiryRequest", new Enquiry());
        model.addAttribute("page", "home");
        return "base";
    }

    @GetMapping("/about")
    public String about(Model model, @CookieValue(value = "user_id", required = false) String userId) {
        try {
            backendService.setLogin(model, userId);
        } catch (EncryptionException ee) {
            return "redirect:/error";
        }
        backendService.setPage(model,"About", "about");
        return "base";
    }

    @PostMapping("/enquiry")
    public String enquiry(@ModelAttribute Enquiry enquiry, RedirectAttributes redirectAttributes) {
        Enquiry savedEnquiry = enquiryService.saveEnquiry(enquiry.getName(), enquiry.getEmail(), enquiry.getEnquiryText());
        if (savedEnquiry == null) {
            backendService.setMessage(redirectAttributes, "error", "Enquiry could not be submitted!");
            return "redirect:/";
        }
        backendService.setMessage(redirectAttributes, "success", "Your request has been received!");
        return "redirect:/";
    }
}
