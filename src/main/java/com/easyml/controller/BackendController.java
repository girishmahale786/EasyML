package com.easyml.controller;

import com.easyml.service.BackendService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.io.IOException;

@Controller
public class BackendController {
    private final BackendService backendService;
    public BackendController(BackendService backendService) {
        this.backendService = backendService;
    }
    @GetMapping("/upload")
    public String upload(Model model) {
        return "upload";
    }

    @PostMapping("/upload")
    public String readCSV(Model model, @RequestParam("csv") MultipartFile csv){
        int[] dims = backendService.readCSV(csv);
        model.addAttribute("rows", dims[0]);
        model.addAttribute("cols", dims[1]);
        return "/upload";
    }
}
