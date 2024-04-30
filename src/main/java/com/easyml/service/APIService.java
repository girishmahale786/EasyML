package com.easyml.service;

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

    @Value("${api.base}")
    private String apiBase;

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

    public Map getVisualization(String plot, Long project_id, String x, String y) {
        String plotUrl = String.format("%s/visualize/%s?project_id=%d", apiBase, plot, project_id);
        if (x != null) {
            plotUrl = String.format("%s&x=%s", plotUrl, x);
        }
        if (y != null) {
            plotUrl = String.format("%s&y=%s", plotUrl, y);
        }
        String jsonResponse = restTemplate.getForObject(plotUrl, String.class);
        return gson.fromJson(jsonResponse, Map.class);
    }

//    public Map getPreprocess(String , Long project_id, String x, String y) {
//        String plotUrl = String.format("%s/visualize/%s?project_id=%d", apiBase, plot, project_id);
//        if (x != null) {
//            plotUrl = String.format("%s&x=%s", plotUrl, x);
//        }
//        if (y != null) {
//            plotUrl = String.format("%s&y=%s", plotUrl, y);
//        }
//        String jsonResponse = restTemplate.getForObject(plotUrl, String.class);
//        return gson.fromJson(jsonResponse, Map.class);
//    }

    public Map getPreprocess(String option, Long project_id, String mode) {
        String preprocessUrl = String.format("%s/preprocess/%s?project_id=%d&mode=%s", apiBase, option, project_id, mode);
        String jsonResponse = restTemplate.getForObject(preprocessUrl, String.class);
        return gson.fromJson(jsonResponse, Map.class);
    }

}

