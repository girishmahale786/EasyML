package com.easyml.controller;

import com.easyml.exception.EncryptionException;
import com.easyml.exception.InvalidFileSize;
import com.easyml.exception.InvalidFileType;
import com.easyml.model.Project;
import com.easyml.model.User;
import com.easyml.service.APIService;
import com.easyml.service.BackendService;
import com.easyml.service.ProjectService;
import com.easyml.service.UserService;
import com.easyml.util.EncryptionUtil;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Map;

@Controller
public class ProjectController {
    private final BackendService backendService;
    private final APIService apiService;
    private final UserService userService;
    private final ProjectService projectService;


    public ProjectController(BackendService backendService, APIService apiService, UserService userService, ProjectService projectService) {
        this.backendService = backendService;
        this.apiService = apiService;
        this.userService = userService;
        this.projectService = projectService;
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model, @CookieValue(value = "user_id", required = false) String userId, RedirectAttributes redirectAttributes) {
        if (userId == null) {
            backendService.setMessage(redirectAttributes, "info", "Can't access this page. Sign in to continue...");
            return "redirect:/login";
        }
        try {
            backendService.setLogin(model, userId);
        } catch (EncryptionException ee) {
            return "redirect:/error";
        }
        User user = (User) model.getAttribute("user");
        if (user != null) {
            model.addAttribute("projects", user.getProjects());
        }
        try {
            backendService.setLogin(model, userId);
        } catch (EncryptionException ee) {
            return "redirect:/error";
        }
        backendService.setPage(model, "Dashboard", "dashboard");
        return "base";
    }

    @GetMapping("/create-project")
    public String createProject(Model model, @CookieValue(value = "user_id", required = false) String userId, RedirectAttributes redirectAttributes) {
        if (userId == null) {
            backendService.setMessage(redirectAttributes, "info", "Can't access this page. Sign in to continue...");
            return "redirect:/login";
        }
        try {
            backendService.setLogin(model, userId);
        } catch (EncryptionException ee) {
            return "redirect:/error";
        }
        backendService.setPage(model, "Create New Project", "create_project");
        return "base";
    }

    @PostMapping("/create-project")
    public String createProject(@ModelAttribute Project project, @RequestParam("csv") MultipartFile csv, @CookieValue(value = "user_id", required = false) String userId, RedirectAttributes redirectAttributes) {
        if (userId == null) {
            backendService.setMessage(redirectAttributes, "info", "Can't access this page. Sign in to continue...");
            return "redirect:/login";
        }
        try {
            Long id = Long.valueOf(EncryptionUtil.decrypt(userId));
            try {
                backendService.readCsv(csv);
                Project savedProject = apiService.createProject(id, project.getName(), project.getDescription(), csv);
                if (savedProject == null) {
                    backendService.setMessage(redirectAttributes, "error", "CSV didn't uploaded to the database, try again.");
                    return "redirect:/create-project";
                }
                return "redirect:/preview/%d".formatted(savedProject.getId());
            } catch (InvalidFileType | InvalidFileSize ift) {
                backendService.setMessage(redirectAttributes, "error", ift.getMessage());
                return "redirect:/create-project";
            }
        } catch (EncryptionException ee) {
            backendService.setMessage(redirectAttributes, "error", ee.getMessage());
            return "redirect:/create-project";
        }

    }

    @GetMapping("/projects/{projectId}")
    public String projectSettings(Model model, @PathVariable(value = "projectId") Long projectId, @CookieValue(value = "user_id", required = false) String userId, RedirectAttributes redirectAttributes) {
        if (userId == null) {
            backendService.setMessage(redirectAttributes, "info", "Can't access this page. Sign in to continue...");
            return "redirect:/login";
        }
        Project project = projectService.getProject(projectId);
        model.addAttribute("projectId", projectId);
        model.addAttribute("project", project);
        try {
            backendService.setLogin(model, userId);
        } catch (EncryptionException ee) {
            return "redirect:/error";
        }
        backendService.setPage(model, "Project Settings", "project_settings");
        return "base";
    }

    @PostMapping("/update-project/{projectId}")
    public String updateProject(@ModelAttribute Project project, @PathVariable(value = "projectId") Long projectId, @CookieValue(value = "user_id", required = false) String userId, RedirectAttributes redirectAttributes) {
        if (userId == null) {
            backendService.setMessage(redirectAttributes, "info", "Can't access this page. Sign in to continue...");
            return "redirect:/login";
        }
        projectService.updateProject(projectId, project.getName(), project.getDescription());
        backendService.setMessage(redirectAttributes, "success", "Project details updated successfully!");
        return "redirect:/projects/%d".formatted(projectId);
    }

    @GetMapping("/reset-project/{projectId}")
    public String resetProject(@PathVariable(value = "projectId") Long projectId, @CookieValue(value = "user_id", required = false) String userId, RedirectAttributes redirectAttributes) {
        if (userId == null) {
            backendService.setMessage(redirectAttributes, "info", "Can't access this page. Sign in to continue...");
            return "redirect:/login";
        }
        projectService.resetProject(projectId);
        backendService.setMessage(redirectAttributes, "success", "Project reset successfully!");
        return "redirect:/projects/%d".formatted(projectId);
    }

    @GetMapping("/delete-project/{projectId}")
    public String deleteProject(@PathVariable(value = "projectId") Long projectId, @CookieValue(value = "user_id", required = false) String userId, RedirectAttributes redirectAttributes) {
        if (userId == null) {
            backendService.setMessage(redirectAttributes, "info", "Can't access this page. Sign in to continue...");
            return "redirect:/login";
        }
        projectService.deleteProjectById(projectId);
        backendService.setMessage(redirectAttributes, "success", "Project deleted successfully!");
        return "redirect:/dashboard";
    }

    @GetMapping("/preview/{projectId}")
    public String preview(Model model, @PathVariable(value = "projectId") Long projectId, @CookieValue(value = "user_id", required = false) String userId, RedirectAttributes redirectAttributes, HttpSession session) {
        if (userId == null) {
            backendService.setMessage(redirectAttributes, "info", "Can't access this page. Sign in to continue...");
            return "redirect:/login";
        }
        backendService.setPage(model, "Dataset Preview", "preview");
        try {
            Long id = Long.valueOf(EncryptionUtil.decrypt(userId));
            User user = userService.getUser(id);
            List<Project> projects = user.getProjects();
            List<Long> projectIds = projects.stream().map(Project::getId).toList();
            if (!projectIds.contains(projectId)) {
                backendService.setMessage(model, "info", "Project with id %d does not exists!".formatted(projectId));
                return "base";
            }
        } catch (EncryptionException ee) {
            backendService.setMessage(redirectAttributes, "error", ee.getMessage());
            return "redirect:/dashboard";
        }

        Map<?, ?> description = apiService.getDatasetDescription(projectId);
        model.addAttribute("data", description);
        model.addAttribute("previewTitle", "Dataset Preview");
        session.setAttribute("columns", description.get("cols"));
        try {
            backendService.setLogin(model, userId);
        } catch (EncryptionException ee) {
            return "redirect:/error";
        }
        backendService.setPath(model, "/dashboard", "/preprocess/%d".formatted(projectId));
        return "base";
    }
}
