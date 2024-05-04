package com.easyml.controller;

import com.easyml.exception.PermissionDenied;
import com.easyml.service.APIService;
import com.easyml.service.BackendService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Map;

@Controller
@RequestMapping("/visualize")
public class VisualizeController {
    private final BackendService backendService;
    private final APIService apiService;

    public VisualizeController(BackendService backendService, APIService apiService) {
        this.backendService = backendService;
        this.apiService = apiService;
    }

    @GetMapping("/{projectId}")
    public String visualize(Model model, @PathVariable(value = "projectId") Long projectId, @CookieValue(value = "user_id", required = false) String userId, RedirectAttributes redirectAttributes, HttpSession session) {
        try {
            backendService.validateLogin(model, userId, redirectAttributes);
        } catch (PermissionDenied pd) {
            backendService.setMessage(model, "info", pd.getMessage());
            return pd.getPath();
        }
        Map<?, ?> description = apiService.getDatasetDescription(projectId);
        session.setAttribute("columns", description.get("cols"));

        backendService.setPage(model, "Visualization", "visualize");
        backendService.setPath(model, "/preview/%d".formatted(projectId), "/model/%d".formatted(projectId));
        return "base";

    }

    @GetMapping("/{projectId}/line")
    public String linePlot(Model model, @PathVariable(value = "projectId") Long projectId, @RequestParam(value = "x", required = false) String x, @RequestParam(value = "y", required = false) String y, @CookieValue(value = "user_id", required = false) String userId, RedirectAttributes redirectAttributes, HttpSession session) {
        try {
            backendService.validateLogin(model, userId, redirectAttributes);
        } catch (PermissionDenied pd) {
            backendService.setMessage(model, "info", pd.getMessage());
            return pd.getPath();
        }
        model.addAttribute("columns", session.getAttribute("columns"));
        model.addAttribute("plot", "line");
        model.addAttribute("previewTitle", "Line Plot");
        backendService.setPage(model, "Line Plot", "plot");
        if (x != null || y != null)
            backendService.applyVisualize(projectId, "plot", x, y, false, model, "Line Plot");
        return "base";
    }

    @GetMapping("/{projectId}/bar")
    public String barPlot(Model model, @PathVariable(value = "projectId") Long projectId, @RequestParam(value = "x", required = false) String x, @RequestParam(value = "y", required = false) String y, @CookieValue(value = "user_id", required = false) String userId, RedirectAttributes redirectAttributes, HttpSession session) {
        try {
            backendService.validateLogin(model, userId, redirectAttributes);
        } catch (PermissionDenied pd) {
            backendService.setMessage(model, "info", pd.getMessage());
            return pd.getPath();
        }
        model.addAttribute("columns", session.getAttribute("columns"));
        model.addAttribute("plot", "bar");
        model.addAttribute("previewTitle", "Bar Plot");
        backendService.setPage(model, "Bar Plot", "plot");
        if (x != null || y != null)
            backendService.applyVisualize(projectId, "bar", x, y, false, model, "Bar Plot");
        return "base";
    }

    @GetMapping("/{projectId}/scatter")
    public String scatterPlot(Model model, @PathVariable(value = "projectId") Long projectId, @RequestParam(value = "x", required = false) String x, @RequestParam(value = "y", required = false) String y, @CookieValue(value = "user_id", required = false) String userId, RedirectAttributes redirectAttributes, HttpSession session) {
        try {
            backendService.validateLogin(model, userId, redirectAttributes);
        } catch (PermissionDenied pd) {
            backendService.setMessage(model, "info", pd.getMessage());
            return pd.getPath();
        }
        model.addAttribute("columns", session.getAttribute("columns"));
        model.addAttribute("plot", "scatter");
        model.addAttribute("previewTitle", "Scatter Plot");
        backendService.setPage(model, "Scatter Plot", "plot");
        if (x != null || y != null)
            backendService.applyVisualize(projectId, "scatter", x, y, false, model, "Scatter Plot");
        return "base";
    }

    @GetMapping("/{projectId}/box")
    public String boxPlot(Model model, @PathVariable(value = "projectId") Long projectId, @RequestParam(value = "x", required = false) String x, @RequestParam(value = "y", required = false) String y, @CookieValue(value = "user_id", required = false) String userId, RedirectAttributes redirectAttributes, HttpSession session) {
        try {
            backendService.validateLogin(model, userId, redirectAttributes);
        } catch (PermissionDenied pd) {
            backendService.setMessage(model, "info", pd.getMessage());
            return pd.getPath();
        }
        model.addAttribute("columns", session.getAttribute("columns"));
        model.addAttribute("plot", "box");
        model.addAttribute("previewTitle", "Box Plot");
        backendService.setPage(model, "Box Plot", "plot");
        if (x != null || y != null)
            backendService.applyVisualize(projectId, "boxplot", x, y, false, model, "Box Plot");
        return "base";
    }

    @GetMapping("/{projectId}/hist")
    public String histogram(Model model, @PathVariable(value = "projectId") Long projectId, @RequestParam(value = "x", required = false) String x, @RequestParam(value = "y", required = false) String y, @CookieValue(value = "user_id", required = false) String userId, RedirectAttributes redirectAttributes, HttpSession session) {
        try {
            backendService.validateLogin(model, userId, redirectAttributes);
        } catch (PermissionDenied pd) {
            backendService.setMessage(model, "info", pd.getMessage());
            return pd.getPath();
        }
        model.addAttribute("columns", session.getAttribute("columns"));
        model.addAttribute("plot", "hist");
        model.addAttribute("previewTitle", "Histogram");
        backendService.setPage(model, "Histogram", "plot");
        if (x != null || y != null)
            backendService.applyVisualize(projectId, "hist", x, y, false, model, "Histogram");
        return "base";
    }

    @GetMapping("/{projectId}/heatmap")
    public String heatmap(Model model, @PathVariable(value = "projectId") Long projectId, @RequestParam(value = "corr", required = false) Boolean corr, @CookieValue(value = "user_id", required = false) String userId, RedirectAttributes redirectAttributes, HttpSession session) {
        try {
            backendService.validateLogin(model, userId, redirectAttributes);
        } catch (PermissionDenied pd) {
            backendService.setMessage(model, "info", pd.getMessage());
            return pd.getPath();
        }
        model.addAttribute("columns", session.getAttribute("columns"));
        model.addAttribute("plot", "heatmap");
        model.addAttribute("previewTitle", "Heatmap");
        backendService.setPage(model, "Heatmap", "plot");
        backendService.applyVisualize(projectId, "heatmap", "", "", corr, model, "Heatmap");
        return "base";
    }
}
