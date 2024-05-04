package com.easyml.service;

import com.easyml.model.Project;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
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

import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
public class APIService {

    private final RestTemplate restTemplate = new RestTemplate();
    private final Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();

    @Value("${api.base}")
    private String apiBase;

    public Project createProject(Long userId, String projectName, String projectDescription, MultipartFile dataset) {
        if (userId == null || Objects.equals(projectName, "") || projectName == null) {
            return null;
        }
        String projectUrl = String.format("%s/project?user_id=%d&name=%s&description=%s", apiBase, userId, projectName, projectDescription);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        MultiValueMap<String, Object> requestBody = new LinkedMultiValueMap<>();
        requestBody.add("dataset", dataset.getResource());

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(requestBody, headers);
        ResponseEntity<String> response = restTemplate.postForEntity(projectUrl, requestEntity, String.class);
        return gson.fromJson(response.getBody(), Project.class);
    }

    public Map<?, ?> getDatasetDescription(Long projectId) {
        String descUrl = String.format("%s/describe?project_id=%d", apiBase, projectId);
        String jsonResponse = restTemplate.getForObject(descUrl, String.class);
        return gson.fromJson(jsonResponse, Map.class);
    }

    public Map<?, ?> getPreprocessing(Long projectId, String option, String mode) {
        String preprocessUrl = String.format("%s/preprocess/%s?project_id=%d&mode=%s", apiBase, option, projectId, mode);
        String jsonResponse = restTemplate.getForObject(preprocessUrl, String.class);
        return gson.fromJson(jsonResponse, Map.class);
    }

    public Map<?, ?> getVisualization(Long projectId, String plot, String x, String y, Boolean corr) {
        String plotUrl = String.format("%s/visualize/%s?project_id=%d", apiBase, plot, projectId);
        if (!x.isEmpty()) {
            plotUrl = String.format("%s&x=%s", plotUrl, x);
        }
        if (!y.isEmpty()) {
            plotUrl = String.format("%s&y=%s", plotUrl, y);
        }
        if (plot.equals("heatmap")) {
            plotUrl = String.format("%s&corr=%b", plotUrl, corr);
        }
        String jsonResponse = restTemplate.getForObject(plotUrl, String.class);
        return gson.fromJson(jsonResponse, Map.class);
    }

    public Map<?, ?> getMetrics(Long projectId, List<String> features, String target, String option) {
        StringBuilder feats = new StringBuilder();
        for (String f : features) {
            feats.append("&x=%s".formatted(f));
        }
        String metricsUrl = String.format("%s/metrics/%s?project_id=%d&y=%s%s", apiBase, option, projectId, target, feats);
        String jsonResponse = restTemplate.getForObject(metricsUrl, String.class);
        return gson.fromJson(jsonResponse, Map.class);
    }

    public void resetProject(Long projectId, Boolean delete) {
        String resetUrl = String.format("%s/reset_project?project_id=%d&delete=%b", apiBase, projectId, delete);
        restTemplate.getForObject(resetUrl, String.class);
    }

}

