package com.easyml.service;

import com.google.gson.Gson;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@Service
public class APIService {
    private final String apiBase = "http://127.0.0.1:8000";
    private final RestTemplate restTemplate = new RestTemplate();
    private final Gson gson = new Gson();

    public boolean createHistory(String projectName, MultipartFile dataset) {
        if (projectName.isEmpty()) {
            return false;
        }
        String createHistoryUrl = String.format("%s/history?project_name=%s", apiBase, projectName);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        MultiValueMap<String, Object> requestBody = new LinkedMultiValueMap<>();
        requestBody.add("dataset", dataset.getResource());

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(requestBody, headers);
        ResponseEntity<String> response = restTemplate.postForEntity(createHistoryUrl, requestEntity, String.class);

        return response.getStatusCode() == HttpStatus.OK;
    }

    public Map getVisualization(String plot, int history_id, String x, String y) {
        String plotUrl = String.format("%s/visualize/%s?history_id=%d", apiBase, plot, history_id);
        if (x != null) {
            plotUrl = String.format("%s&x=%s", plotUrl, x);
        }
        if (y != null) {
            plotUrl = String.format("%s&y=%s", plotUrl, y);
        }
        String jsonResponse = restTemplate.getForObject(plotUrl, String.class);
        return gson.fromJson(jsonResponse, Map.class);
    }

}

