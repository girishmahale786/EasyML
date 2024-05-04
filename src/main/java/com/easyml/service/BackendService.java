package com.easyml.service;

import com.easyml.exception.EncryptionException;
import com.easyml.exception.InvalidFileSize;
import com.easyml.exception.InvalidFileType;
import com.easyml.exception.PermissionDenied;
import com.easyml.model.History;
import com.easyml.model.Project;
import com.easyml.model.User;
import com.easyml.util.EncryptionUtil;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;

@Service
public class BackendService {

    public final APIService apiService;
    public final UserService userService;
    public final ProjectService projectService;
    public final HistoryService historyService;

    public BackendService(APIService apiService, UserService userService, ProjectService projectService, HistoryService historyService) {
        this.apiService = apiService;
        this.userService = userService;
        this.projectService = projectService;
        this.historyService = historyService;
    }

    public void readCsv(MultipartFile csv) throws InvalidFileType, InvalidFileSize {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(csv.getInputStream()));
            CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT);
            int rowCount = 0;
            int columnCount = 0;
            for (CSVRecord record : csvParser) {
                rowCount++;
                if (rowCount == 1) {
                    columnCount = record.size();
                }
            }
            csvParser.close();
            if (rowCount < 10 || columnCount < 2) {
                throw new InvalidFileSize();
            }
        } catch (Exception ioe) {
            throw new InvalidFileType();
        }
    }

    public void setMessage(Model model, String status, String message) {
        model.addAttribute("status", status);
        model.addAttribute("message", message);
    }

    public void setMessage(RedirectAttributes redirectAttributes, String status, String message) {
        redirectAttributes.addFlashAttribute("status", status);
        redirectAttributes.addFlashAttribute("message", message);
    }

    public void setPage(Model model, String pageTitle, String pageName) {
        model.addAttribute("title", pageTitle);
        model.addAttribute("page", pageName);
    }

    public void setAuthPage(Model model, String pageTitle, String pageName) {
        setPage(model, pageTitle, "auth_base");
        model.addAttribute("authPage", "auth");
        model.addAttribute("authContent", pageName);
    }

    public void setPath(Model model, String prev, String next) {
        model.addAttribute("prev", prev);
        model.addAttribute("next", next);
    }

    public void setLogin(Model model, @CookieValue(value = "user_id", required = false) String userId) throws EncryptionException {
        model.addAttribute("login", false);
        if (userId != null) {
            Long id = Long.valueOf(EncryptionUtil.decrypt(userId));
            User user = userService.getUser(id);
            model.addAttribute("user", user);
            model.addAttribute("login", true);
        }
    }

    public void validateLogin(Model model, @CookieValue(value = "user_id", required = false) String userId, RedirectAttributes redirectAttributes) throws PermissionDenied {
        if (userId == null) {
            throw new PermissionDenied().setPath("redirect:/login");
        }
        try {
            setLogin(model, userId);
        } catch (EncryptionException ee) {
            throw new PermissionDenied().setPath("redirect:/error");
        }
    }

    public Map<?, ?> preprocess(Long projectId, String option, String mode) {
        String preprocessing = String.format("%s-%s", option, mode);
        History history = new History();
        Project project = projectService.getProject(projectId);
        history.setProject(project);
        history.setPreprocessing(preprocessing);
        historyService.save(history);
        return apiService.getPreprocessing(projectId, option, mode);
    }

    public void applyPreprocess(Long projectId, String option, String mode, Model model, String pageTitle, String prev, String next) {
        model.addAttribute("data", preprocess(projectId, option, mode));
        model.addAttribute("previewTitle", pageTitle);
        model.addAttribute("projectId", projectId);
        setPage(model, pageTitle, "preview");
        setPath(model, prev, next);
    }

    public Map<?, ?> visualize(Long projectId, String plot, String x, String y, Boolean corr) {
        return apiService.getVisualization(projectId, plot, x, y, corr);
    }

    public void applyVisualize(Long projectId, String plot, String x, String y, Boolean corr, Model model, String pageTitle) {
        model.addAttribute("data", visualize(projectId, plot, x, y, corr));
        model.addAttribute("previewTitle", pageTitle);
        model.addAttribute("projectId", projectId);
        setPage(model, pageTitle, "plot");
    }
}
