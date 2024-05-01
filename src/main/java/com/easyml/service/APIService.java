package com.easyml.service;

import com.easyml.model.History;
import com.easyml.model.Project;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;
import java.util.Objects;

@Service
public class APIService {

    private final RestTemplate restTemplate = new RestTemplate();
    private final Gson gson = new Gson();
    private final ProjectService projectService;
    private final HistoryService historyService;

    @Value("${api.base}")
    private String apiBase;

    public APIService(ProjectService projectService, HistoryService historyService) {
        this.projectService = projectService;
        this.historyService = historyService;
    }

    public Project createProject(Long userId, String projectName, MultipartFile dataset) {
        if (userId == null || Objects.equals(projectName, "") || projectName == null) {
            return null;
        }
        String projectUrl = String.format("%s/project?user_id=%d&name=%s", apiBase, userId, projectName);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        MultiValueMap<String, Object> requestBody = new LinkedMultiValueMap<>();
        requestBody.add("dataset", dataset.getResource());

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(requestBody, headers);
        ResponseEntity<String> response = restTemplate.postForEntity(projectUrl, requestEntity, String.class);
        return gson.fromJson(response.getBody(), Project.class);
    }

    public Map getVisualization(String plot, Long projectId, String x, String y) {
        String plotUrl = String.format("%s/visualize/%s?project_id=%d", apiBase, plot, projectId);
        if (x != null) {
            plotUrl = String.format("%s&x=%s", plotUrl, x);
        }
        if (y != null) {
            plotUrl = String.format("%s&y=%s", plotUrl, y);
        }
        String jsonResponse = restTemplate.getForObject(plotUrl, String.class);
        return gson.fromJson(jsonResponse, Map.class);
    }


    public Map getPreprocessing(String option, Long projectId, String mode) {
        String preprocessing = String.format("%s-%s", option, mode);
        History history = new History();
        Project project = projectService.getProject(projectId);
        history.setProject(project);
        history.setPreprocessing(preprocessing);
        historyService.save(history);
        String preprocessUrl = String.format("%s/preprocess/%s?project_id=%d&mode=%s", apiBase, option, projectId, mode);
        String jsonResponse = restTemplate.getForObject(preprocessUrl, String.class);
        return gson.fromJson(jsonResponse, Map.class);
    }

    public Map getMetrics(Long projectId, String option) {
        String metricsUrl = String.format("%s/metrics/%s?project_id=%d", apiBase, option, projectId);
        String jsonResponse = restTemplate.getForObject(metricsUrl, String.class);
        return gson.fromJson(jsonResponse, Map.class);
    }
}

