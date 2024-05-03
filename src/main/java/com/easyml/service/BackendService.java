package com.easyml.service;

import com.easyml.model.History;
import com.easyml.model.Project;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.web.multipart.MultipartFile;

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

    public int[] readCsv(MultipartFile csv) throws IOException {
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
        return new int[]{rowCount, columnCount};
    }

    public Map preprocess(Long projectId, String option, String mode) {
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
        userService.setPage(model, pageTitle, "preview");
        userService.setPath(model, prev, next);
    }
}
