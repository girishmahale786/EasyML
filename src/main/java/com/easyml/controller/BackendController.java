package com.easyml.controller;

import com.easyml.service.APIService;
import com.easyml.service.BackendService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Controller
public class BackendController {
    private final BackendService backendService;
    private final APIService apiService;

    public BackendController(BackendService backendService, APIService apiService) {
        this.backendService = backendService;
        this.apiService = apiService;
    }

    @GetMapping("/upload")
    public String upload(Model model) {
        return "upload";
    }

    @PostMapping("/upload")
    public String readCSV(Model model, @RequestParam("project_name") String projectName, @RequestParam("csv") MultipartFile csv, RedirectAttributes redirectAttributes) throws IOException {
        backendService.readCSV(csv, null);
        int[] dims = backendService.getDims();

        if (dims[0] <= 3 || dims[1] <= 3) {
            redirectAttributes.addFlashAttribute("errorMsg", "CSV doesn't have sufficient no. of rows and columns.");
            redirectAttributes.addFlashAttribute("backLink", "/upload");
            return "redirect:/error";
        }
        boolean success = apiService.createHistory(projectName, csv);
        if (!success) {
            redirectAttributes.addFlashAttribute("errorMsg", "CSV didn't uploaded to the database, try again.");
            redirectAttributes.addFlashAttribute("backLink", "/upload");
            return "redirect:/error";
        }
        return "redirect:/dashboard";
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) throws IOException {
        String csvUrl = "http://localhost:8000/static/datasets/iris-c8d03e6a40911ac31527.csv";
        backendService.readCSV(null, csvUrl);
        int[] dims = backendService.getDims();
        List<String> cols = backendService.getColumns();
        List<List<String>> rows = backendService.getRows();
        Map plot = apiService.getVisualization("hist", 4, "Gender", null);
        model.addAttribute("dims", dims);
        model.addAttribute("plot", plot);
        model.addAttribute("columns", cols);
        model.addAttribute("rows", rows);
        return "dashboard";
    }
}
