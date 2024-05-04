package com.easyml.controller;

import com.easyml.exception.PermissionDenied;
import com.easyml.service.APIService;
import com.easyml.service.BackendService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/model")
public class ModelController {
    private final BackendService backendService;
    private final APIService apiService;

    public ModelController(BackendService backendService, APIService apiService) {
        this.backendService = backendService;
        this.apiService = apiService;
    }

    @GetMapping("/{projectId}")
    public String model(Model model, @PathVariable(value = "projectId") Long projectId, @CookieValue(value = "user_id", required = false) String userId, RedirectAttributes redirectAttributes, HttpSession session) {
        try {
            backendService.validateLogin(model, userId, redirectAttributes);
        } catch (PermissionDenied pd) {
            backendService.setMessage(model, "info", pd.getMessage());
            return pd.getPath();
        }
        Map<?, ?> description = apiService.getDatasetDescription(projectId);
        session.setAttribute("columns", description.get("cols"));
        backendService.setPage(model, "Model Selection", "model_type");
        backendService.setPath(model, "/preview/%d".formatted(projectId), "/model/%d".formatted(projectId));
        return "base";

    }

    @GetMapping("/{projectId}/regression")
    public String regression(Model model, @PathVariable(value = "projectId") Long projectId, @CookieValue(value = "user_id", required = false) String userId, RedirectAttributes redirectAttributes, HttpSession session) {
        try {
            backendService.validateLogin(model, userId, redirectAttributes);
        } catch (PermissionDenied pd) {
            backendService.setMessage(model, "info", pd.getMessage());
            return pd.getPath();
        }
        model.addAttribute("columns", session.getAttribute("columns"));
        backendService.setPage(model, "Regression Models", "regression");
        backendService.setPath(model, "/model/%d".formatted(projectId), "");
        return "base";
    }

    @GetMapping("/{projectId}/classification")
    public String classification(Model model, @PathVariable(value = "projectId") Long projectId, @CookieValue(value = "user_id", required = false) String userId, RedirectAttributes redirectAttributes, HttpSession session) {
        try {
            backendService.validateLogin(model, userId, redirectAttributes);
        } catch (PermissionDenied pd) {
            backendService.setMessage(model, "info", pd.getMessage());
            return pd.getPath();
        }
        model.addAttribute("columns", session.getAttribute("columns"));
        backendService.setPage(model, "Classification Models", "classification");
        backendService.setPath(model, "/model/%d".formatted(projectId), "");
        return "base";
    }

    @GetMapping("/{projectId}/results")
    public String results(Model model, @PathVariable(value = "projectId") Long projectId, @CookieValue(value = "user_id", required = false) String userId, RedirectAttributes redirectAttributes, HttpSession session) {
        try {
            backendService.validateLogin(model, userId, redirectAttributes);
        } catch (PermissionDenied pd) {
            backendService.setMessage(model, "info", pd.getMessage());
            return pd.getPath();
        }
        String pageTitle = session.getAttribute("previewTitle").toString();
        model.addAttribute("projectId", projectId);
        model.addAttribute("data", session.getAttribute("data"));
        model.addAttribute("previewTitle", pageTitle);
        backendService.setPage(model, pageTitle, "results");
        backendService.setPath(model, "/model/%d/%s".formatted(projectId, session.getAttribute("model")), session.getAttribute("model_path").toString());
        return "base";
    }

    @PostMapping("/{projectId}/results")
    public String results(@PathVariable(value = "projectId") Long projectId, @RequestParam List<String> features, @RequestParam String target, @RequestParam String modelType, HttpSession session) {
        String option = null;
        String pageTitle = null;
        String model = null;
        if (modelType.equals("linear")) {
            option = "liner_regression";
            pageTitle = "Linear Regression Results";
            model = "regression";
        } else if (modelType.equals("polynomial")) {
            option = "polynomial_regression";
            pageTitle = "Polynomial Regression Results";
            model = "regression";
        } else if (modelType.equals("lasso")) {
            option = "lasso_regression";
            pageTitle = "Lasso Regression Results";
            model = "regression";

        } else if (modelType.equals("ridge")) {
            option = "ridge_regression";
            pageTitle = "Ridge Regression Results";
            model = "regression";
        } else if (modelType.equals("knn")) {
            option = "k_nearest_neighbors";
            pageTitle = "K-Nearest Neighbors Results";
            model = "classification";
        } else if (modelType.equals("dtree")) {
            option = "decision_tree";
            pageTitle = "Decision Tree Results";
            model = "classification";
        } else if (modelType.equals("svc")) {
            option = "support_vector_machine";
            pageTitle = "Support Vector Classifier Results";
            model = "classification";
        } else if (modelType.equals("logistic")) {
            option = "logistic_regression";
            pageTitle = "Logistic Regression Results";
            model = "classification";
        }
        session.setAttribute("model", model);
        backendService.applyModel(projectId, features, target, option, pageTitle, session);
        return "redirect:/model/%d/results".formatted(projectId);
    }
}
