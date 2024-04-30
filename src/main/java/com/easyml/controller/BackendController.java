package com.easyml.controller;

import com.easyml.model.Project;
import com.easyml.model.User;
import com.easyml.service.APIService;
import com.easyml.service.BackendService;
import com.easyml.service.ProjectService;
import com.easyml.service.UserService;
import com.easyml.util.EncryptionUtil;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Map;

@Controller
public class BackendController {
    private final BackendService backendService;
    private final APIService apiService;
    private final UserService userService;
    private final ProjectService projectService;

    public BackendController(BackendService backendService, APIService apiService, UserService userService, ProjectService projectService) {
        this.backendService = backendService;
        this.apiService = apiService;
        this.userService = userService;
        this.projectService = projectService;
    }

    @GetMapping("/upload")
    public String upload(Model model, @CookieValue(value = "user_id", required = false) String userId, RedirectAttributes redirectAttributes) {
        if (userId == null) {
            userService.setFlashError(redirectAttributes, "Can't access this page. Sign in to continue...");
            return "redirect:/login";
        }
        return "upload";
    }

    @PostMapping("/upload")
    public String upload(Model model, @RequestParam("project_name") String projectName, @RequestParam("csv") MultipartFile csv, @CookieValue(value = "user_id", required = false) String userId, RedirectAttributes redirectAttributes) throws Exception {
        if (userId == null) {
            userService.setFlashError(redirectAttributes, "Can't access this page. Sign in to continue...");
            return "redirect:/login";
        }
        Long id = Long.valueOf(EncryptionUtil.decrypt(userId));
        backendService.readCSV(csv, null);
        int[] dims = backendService.getDims();

        if (dims[0] <= 3 || dims[1] <= 3) {
            userService.setError(model, "CSV doesn't have sufficient no. of rows and columns.");
            return "upload";
        }
        Project project = apiService.createProject(id, projectName, csv);
        if (project == null) {
            userService.setError(model, "CSV didn't uploaded to the database, try again.");
            return "upload";
        }
        return String.format("redirect:/preview?project_id=%d", project.getId());
//        Map preprocessed = apiService.getPreprocess("remove_nulls", 1L,"mean");
//        model.addAttribute("preprocessed", preprocessed);
//        return "visualization";

    }

    @GetMapping("/dashboard")
    public String dashboard(Model model, @CookieValue(value = "user_id", required = false) String userId, RedirectAttributes redirectAttributes) {
        if (userId == null) {
            userService.setFlashError(redirectAttributes, "Can't access this page. Sign in to continue...");
            return "redirect:/login";
        }
        return "dashboard";
    }


    @GetMapping("/preview")
    public String preview(Model model, @RequestParam(value = "project_id", required = false) Long projectId, @CookieValue(value = "user_id", required = false) String userId, RedirectAttributes redirectAttributes) throws Exception {
        if (userId == null) {
            userService.setFlashError(redirectAttributes, "Can't access this page. Sign in to continue...");
            return "redirect:/login";
        }
        Long id = Long.valueOf(EncryptionUtil.decrypt(userId));
        User user = userService.getUser(id);
        List<Project> projects = user.getProjects();
        List<Long> projectIds = projects.stream().map(Project::getId).toList();
        if (!projectIds.contains(projectId)) {
            userService.setError(model, String.format("Project with %d id does not exists!", projectId));
            return "preview";
        }
        Project project = projectService.getProject(projectId);
        backendService.readCSV(null, project.getDatasetUrl());
        int[] dims = backendService.getDims();
        List<String> cols = backendService.getColumns();
        List<List<String>> rows = backendService.getRows();
        model.addAttribute("dims", dims);
        model.addAttribute("columns", cols);
        model.addAttribute("rows", rows);
        return "preview";
    }

    @GetMapping("/projects")
    public String projects(Model model, @CookieValue(value = "user_id", required = false) String userId, RedirectAttributes redirectAttributes) throws Exception {
        if (userId == null) {
            userService.setFlashError(redirectAttributes, "Can't access this page. Sign in to continue...");
            return "redirect:/login";
        }
        userService.setUserLogin(model, userId);
        User user = (User) model.getAttribute("user");
        model.addAttribute("projects", user.getProjects());
        return "projects";
    }

    @GetMapping("/preprocess")
    public String preprocess(Model model, @CookieValue(value = "user_id", required = false) String userId) throws Exception {
        Map preprocessed = apiService.getPreprocess("remove_nulls", 1L,"mean");
        model.addAttribute("preprocessed", preprocessed);
        return "visualization";
    }
}
