package com.easyml.controller;

import com.easyml.model.Enquiry;
import com.easyml.service.EnquiryService;
import com.easyml.service.UserService;
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
    private final UserService userService;

    public HomeController(EnquiryService enquiryService, UserService userService) {
        this.enquiryService = enquiryService;
        this.userService = userService;
    }

    @GetMapping("/")
    public String home(Model model, @CookieValue(value = "user_id", required = false) String userId) throws Exception {
        userService.setUserLogin(model, userId);
        return "home";
    }

    @GetMapping("/about")
    public String about(Model model, @CookieValue(value = "user_id", required = false) String userId) throws Exception {
        userService.setUserLogin(model, userId);
        return "about";
    }

    @GetMapping("/contact")
    public String contact(Model model, @CookieValue(value = "user_id", required = false) String userId) throws Exception {
        userService.setUserLogin(model, userId);
        model.addAttribute("EnquiryRequest", new Enquiry());
        return "contact";
    }

    @PostMapping("/enquiry")
    public String enquiry(@ModelAttribute Enquiry enquiry, Model model, RedirectAttributes redirectAttributes) {
        Enquiry savedEnquiry = enquiryService.saveEnquiry(enquiry.getName(), enquiry.getEmail(), enquiry.getEnquiryText());
        if (savedEnquiry == null) {
            redirectAttributes.addFlashAttribute("errorMsg", "Enquiry could not be submitted");
            redirectAttributes.addFlashAttribute("backLink", "/login");
            return "redirect:/error";
        } else {
            model.addAttribute("success", true);
        }
        return "contact";
    }

}
