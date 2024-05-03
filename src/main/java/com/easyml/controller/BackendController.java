package com.easyml.controller;

import com.easyml.model.Project;
import com.easyml.model.User;
import com.easyml.service.APIService;
import com.easyml.service.BackendService;
import com.easyml.service.UserService;
import com.easyml.util.EncryptionUtil;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Map;

@Controller
public class BackendController {
    private final BackendService backendService;
    private final APIService apiService;
    private final UserService userService;


    public BackendController(BackendService backendService, APIService apiService, UserService userService) {
        this.backendService = backendService;
        this.apiService = apiService;
        this.userService = userService;
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model, @CookieValue(value = "user_id", required = false) String userId, RedirectAttributes redirectAttributes) throws Exception {
        if (userId == null) {
            userService.setFlashError(redirectAttributes, "Can't access this page. Sign in to continue...");
            return "redirect:/login";
        }
        userService.setUserLogin(model, userId);
        User user = (User) model.getAttribute("user");
        model.addAttribute("projects", user.getProjects());
        userService.setPage(model, "Dashboard", "dashboard");
        return "base";
    }

    @GetMapping("/create-project")
    public String createProject(Model model, @CookieValue(value = "user_id", required = false) String userId, RedirectAttributes redirectAttributes) {
        if (userId == null) {
            userService.setFlashError(redirectAttributes, "Can't access this page. Sign in to continue...");
            return "redirect:/login";
        }
        userService.setPage(model, "Create New Project", "create_project");
        return "base";
    }

    @PostMapping("/create-project")
    public String createProject(@ModelAttribute Project project, @RequestParam("csv") MultipartFile csv, @CookieValue(value = "user_id", required = false) String userId, RedirectAttributes redirectAttributes) throws Exception {
        if (userId == null) {
            userService.setFlashError(redirectAttributes, "Can't access this page. Sign in to continue...");
            return "redirect:/login";
        }
        Long id = Long.valueOf(EncryptionUtil.decrypt(userId));
        int[] dims = backendService.readCsv(csv);
        if (dims[0] <= 3 || dims[1] <= 3) {
            userService.setFlashError(redirectAttributes, "CSV doesn't have sufficient no. of rows and columns.");
            return "redirect:/create-project";
        }
        Project savedProject = apiService.createProject(id, project.getName(), project.getDescription(), csv);
        if (savedProject == null) {
            userService.setFlashError(redirectAttributes, "CSV didn't uploaded to the database, try again.");
            return "redirect:/create-project";
        }
        return "redirect:/preview/%d".formatted(savedProject.getId());
    }


    @GetMapping("/preview/{projectId}")
    public String preview(Model model, @PathVariable(value = "projectId") Long projectId, @CookieValue(value = "user_id", required = false) String userId, RedirectAttributes redirectAttributes) throws Exception {
        if (userId == null) {
            userService.setFlashError(redirectAttributes, "Can't access this page. Sign in to continue...");
            return "redirect:/login";
        }
        userService.setPage(model, "Dataset Preview", "preview");
        Long id = Long.valueOf(EncryptionUtil.decrypt(userId));
        User user = userService.getUser(id);
        List<Project> projects = user.getProjects();
        List<Long> projectIds = projects.stream().map(Project::getId).toList();
        if (!projectIds.contains(projectId)) {
            userService.setError(model, "Project with id %d does not exists!".formatted(projectId));
            return "base";
        }
        Map description = apiService.getDatasetDescription(projectId);
        model.addAttribute("data", description);
        model.addAttribute("previewTitle", "Dataset Preview");
        userService.setPath(model, "/dashboard", "/preprocess/%d".formatted(projectId));
        return "base";
    }

    @GetMapping("/preprocess/{projectId}")
    public String preprocess(Model model, @PathVariable(value = "projectId") Long projectId, @CookieValue(value = "user_id", required = false) String userId, RedirectAttributes redirectAttributes) throws Exception {
        if (userId == null) {
            userService.setFlashError(redirectAttributes, "Can't access this page. Sign in to continue...");
            return "redirect:/login";
        }
        userService.setPage(model, "Preprocessing", "preprocess");
        userService.setPath(model, "/preview/%d".formatted(projectId), "/visualize/%d".formatted(projectId));
        return "base";
    }

    @GetMapping("/preprocess/{projectId}/impute")
    public String impute(Model model, @PathVariable(value = "projectId") Long projectId, @CookieValue(value = "user_id", required = false) String userId, RedirectAttributes redirectAttributes) throws Exception {
        if (userId == null) {
            userService.setFlashError(redirectAttributes, "Can't access this page. Sign in to continue...");
            return "redirect:/login";
        }
        model.addAttribute("projectId", projectId);
        userService.setPage(model, "Data Imputation", "impute");
        userService.setPath(model, "/preprocess/%d".formatted(projectId), "/preprocess/%d/delete".formatted(projectId));
        return "base";
    }

    @GetMapping("/preprocess/{projectId}/impute/mean")
    public String imputeMean(Model model, @PathVariable(value = "projectId") Long projectId, @CookieValue(value = "user_id", required = false) String userId, RedirectAttributes redirectAttributes) throws Exception {
        if (userId == null) {
            userService.setFlashError(redirectAttributes, "Can't access this page. Sign in to continue...");
            return "redirect:/login";
        }
        String prev = "/preprocess/%d/impute".formatted(projectId);
        String next = "/preprocess/%d/delete".formatted(projectId);
        backendService.applyPreprocess(projectId, "remove_nulls", "mean", model, "Mean Imputation Results", prev, next);
        return "base";
    }

    @GetMapping("/preprocess/{projectId}/impute/mode")
    public String imputeMode(Model model, @PathVariable(value = "projectId") Long projectId, @CookieValue(value = "user_id", required = false) String userId, RedirectAttributes redirectAttributes) throws Exception {
        if (userId == null) {
            userService.setFlashError(redirectAttributes, "Can't access this page. Sign in to continue...");
            return "redirect:/login";
        }
        String prev = "/preprocess/%d/impute".formatted(projectId);
        String next = "/preprocess/%d/delete".formatted(projectId);
        backendService.applyPreprocess(projectId, "remove_nulls", "mode", model, "Mode Imputation Results", prev, next);
        return "base";
    }

    @GetMapping("/preprocess/{projectId}/impute/median")
    public String imputeMedian(Model model, @PathVariable(value = "projectId") Long projectId, @CookieValue(value = "user_id", required = false) String userId, RedirectAttributes redirectAttributes) throws Exception {
        if (userId == null) {
            userService.setFlashError(redirectAttributes, "Can't access this page. Sign in to continue...");
            return "redirect:/login";
        }
        String prev = "/preprocess/%d/impute".formatted(projectId);
        String next = "/preprocess/%d/delete".formatted(projectId);
        backendService.applyPreprocess(projectId, "remove_nulls", "median", model, "Median Imputation Results", prev, next);
        return "base";
    }

    @GetMapping("/preprocess/{projectId}/impute/bfill")
    public String imputeBfill(Model model, @PathVariable(value = "projectId") Long projectId, @CookieValue(value = "user_id", required = false) String userId, RedirectAttributes redirectAttributes) throws Exception {
        if (userId == null) {
            userService.setFlashError(redirectAttributes, "Can't access this page. Sign in to continue...");
            return "redirect:/login";
        }
        String prev = "/preprocess/%d/impute".formatted(projectId);
        String next = "/preprocess/%d/delete".formatted(projectId);
        backendService.applyPreprocess(projectId, "remove_nulls", "bfill", model, "Backward Fill Results", prev, next);
        return "base";
    }

    @GetMapping("/preprocess/{projectId}/impute/ffill")
    public String imputeFfill(Model model, @PathVariable(value = "projectId") Long projectId, @CookieValue(value = "user_id", required = false) String userId, RedirectAttributes redirectAttributes) throws Exception {
        if (userId == null) {
            userService.setFlashError(redirectAttributes, "Can't access this page. Sign in to continue...");
            return "redirect:/login";
        }
        String prev = "/preprocess/%d/impute".formatted(projectId);
        String next = "/preprocess/%d/delete".formatted(projectId);
        backendService.applyPreprocess(projectId, "remove_nulls", "ffill", model, "Forward Fill Results", prev, next);
        return "base";
    }

    @GetMapping("/preprocess/{projectId}/delete")
    public String delete(Model model, @PathVariable(value = "projectId") Long projectId, @CookieValue(value = "user_id", required = false) String userId, RedirectAttributes redirectAttributes) throws Exception {
        if (userId == null) {
            userService.setFlashError(redirectAttributes, "Can't access this page. Sign in to continue...");
            return "redirect:/login";
        }
        model.addAttribute("projectId", projectId);
        userService.setPage(model, "Delete Missing Data", "delete");
        userService.setPath(model, "/preprocess/%d".formatted(projectId), "/preprocess/%d/encode".formatted(projectId));
        return "base";
    }

    @GetMapping("/preprocess/{projectId}/delete/pairwise")
    public String deletePairwise(Model model, @PathVariable(value = "projectId") Long projectId, @CookieValue(value = "user_id", required = false) String userId, RedirectAttributes redirectAttributes) throws Exception {
        if (userId == null) {
            userService.setFlashError(redirectAttributes, "Can't access this page. Sign in to continue...");
            return "redirect:/login";
        }
        String prev = "/preprocess/%d/delete".formatted(projectId);
        String next = "/preprocess/%d/encode".formatted(projectId);
        backendService.applyPreprocess(projectId, "remove_nulls", "pairwise", model, "Pairwise Deletion Results", prev, next);
        return "base";
    }

    @GetMapping("/preprocess/{projectId}/delete/column")
    public String deleteColumn(Model model, @PathVariable(value = "projectId") Long projectId, @CookieValue(value = "user_id", required = false) String userId, RedirectAttributes redirectAttributes) throws Exception {
        if (userId == null) {
            userService.setFlashError(redirectAttributes, "Can't access this page. Sign in to continue...");
            return "redirect:/login";
        }
        String prev = "/preprocess/%d/delete".formatted(projectId);
        String next = "/preprocess/%d/encode".formatted(projectId);
        backendService.applyPreprocess(projectId, "remove_nulls", "column", model, "Drop Column Results", prev, next);
        return "base";
    }

    @GetMapping("/preprocess/{projectId}/delete/row")
    public String deleteRow(Model model, @PathVariable(value = "projectId") Long projectId, @CookieValue(value = "user_id", required = false) String userId, RedirectAttributes redirectAttributes) throws Exception {
        if (userId == null) {
            userService.setFlashError(redirectAttributes, "Can't access this page. Sign in to continue...");
            return "redirect:/login";
        }
        String prev = "/preprocess/%d/delete".formatted(projectId);
        String next = "/preprocess/%d/encode".formatted(projectId);
        backendService.applyPreprocess(projectId, "remove_nulls", "row", model, "Drop Row Results", prev, next);
        return "base";
    }

    @GetMapping("/preprocess/{projectId}/encode")
    public String encode(Model model, @PathVariable(value = "projectId") Long projectId, @CookieValue(value = "user_id", required = false) String userId, RedirectAttributes redirectAttributes) throws Exception {
        if (userId == null) {
            userService.setFlashError(redirectAttributes, "Can't access this page. Sign in to continue...");
            return "redirect:/login";
        }
        model.addAttribute("projectId", projectId);
        userService.setPage(model, "Data Encoding", "encode");
        userService.setPath(model, "/preprocess/%d".formatted(projectId), "/preprocess/%d/scale".formatted(projectId));
        return "base";
    }

    @GetMapping("/preprocess/{projectId}/encode/onehot")
    public String encodeOneHot(Model model, @PathVariable(value = "projectId") Long projectId, @CookieValue(value = "user_id", required = false) String userId, RedirectAttributes redirectAttributes) throws Exception {
        if (userId == null) {
            userService.setFlashError(redirectAttributes, "Can't access this page. Sign in to continue...");
            return "redirect:/login";
        }
        String prev = "/preprocess/%d/encode".formatted(projectId);
        String next = "/preprocess/%d/scale".formatted(projectId);
        backendService.applyPreprocess(projectId, "encode", "onehot", model, "One-Hot Encoding Results", prev, next);
        return "base";
    }

    @GetMapping("/preprocess/{projectId}/encode/label")
    public String encodeLabel(Model model, @PathVariable(value = "projectId") Long projectId, @CookieValue(value = "user_id", required = false) String userId, RedirectAttributes redirectAttributes) throws Exception {
        if (userId == null) {
            userService.setFlashError(redirectAttributes, "Can't access this page. Sign in to continue...");
            return "redirect:/login";
        }
        String prev = "/preprocess/%d/encode".formatted(projectId);
        String next = "/preprocess/%d/scale".formatted(projectId);
        backendService.applyPreprocess(projectId, "encode", "label", model, "Label Encoding Results", prev, next);
        return "base";
    }

    @GetMapping("/preprocess/{projectId}/scale")
    public String scale(Model model, @PathVariable(value = "projectId") Long projectId, @CookieValue(value = "user_id", required = false) String userId, RedirectAttributes redirectAttributes) throws Exception {
        if (userId == null) {
            userService.setFlashError(redirectAttributes, "Can't access this page. Sign in to continue...");
            return "redirect:/login";
        }
        model.addAttribute("projectId", projectId);
        userService.setPage(model, "Feature Scaling", "scale");
        userService.setPath(model, "/preprocess/%d".formatted(projectId), "/visualize/%d".formatted(projectId));
        return "base";
    }

    @GetMapping("/preprocess/{projectId}/scale/standard")
    public String scaleStandard(Model model, @PathVariable(value = "projectId") Long projectId, @CookieValue(value = "user_id", required = false) String userId, RedirectAttributes redirectAttributes) throws Exception {
        if (userId == null) {
            userService.setFlashError(redirectAttributes, "Can't access this page. Sign in to continue...");
            return "redirect:/login";
        }
        String prev = "/preprocess/%d/scale".formatted(projectId);
        String next = "/visualize/%d".formatted(projectId);
        backendService.applyPreprocess(projectId, "feature_scaling", "standard", model, "Standard Scaling Results", prev, next);
        return "base";
    }

    @GetMapping("/preprocess/{projectId}/scale/minmax")
    public String scaleMinMax(Model model, @PathVariable(value = "projectId") Long projectId, @CookieValue(value = "user_id", required = false) String userId, RedirectAttributes redirectAttributes) throws Exception {
        if (userId == null) {
            userService.setFlashError(redirectAttributes, "Can't access this page. Sign in to continue...");
            return "redirect:/login";
        }
        String prev = "/preprocess/%d/scale".formatted(projectId);
        String next = "/visualize/%d".formatted(projectId);
        backendService.applyPreprocess(projectId, "feature_scaling", "minmax", model, "MinMax Scaling Results", prev, next);
        return "base";
    }

    @GetMapping("/metrics")
    public String metrics(Model model, @CookieValue(value = "user_id", required = false) String userId) throws Exception {
        Map metrics = apiService.getMetrics(4L, "liner_regression");
        model.addAttribute("metrics", metrics);
        return "visualization";
    }
}
