package com.easyml.service;

import com.easyml.model.History;
import com.easyml.model.Project;
import com.easyml.repository.HistoryRepository;
import com.easyml.repository.ProjectRepository;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class ProjectService {
    private final ProjectRepository projectRepository;
    private final HistoryRepository historyRepository;
    private final APIService apiService;


    public ProjectService(ProjectRepository projectRepository, HistoryRepository historyRepository, APIService apiService) {
        this.projectRepository = projectRepository;
        this.historyRepository = historyRepository;
        this.apiService = apiService;
    }

    public Project getProject(Long projectId) {
        return projectRepository.findById(projectId).orElse(null);
    }

    public Project updateProject(Long id, String name, String description) {
        Project project = projectRepository.findById(id).orElse(null);
        if (project != null) {
            project.setName(name);
            project.setDescription(description);
            return projectRepository.save(project);
        }
        return null;
    }

    public void resetProject(Long id) {
        Project project = projectRepository.findById(id).orElse(null);
        if (project != null) {
            project.setEditDatasetUrl(project.getDatasetUrl());
            project.setFinalDatasetUrl(project.getDatasetUrl());
            List<History> histories = project.getHistories();
            historyRepository.deleteAllInBatch(histories);
            projectRepository.save(project);
            apiService.resetProject(id);
        }
    }

    public void deleteProject(Project project) {
        projectRepository.delete(project);
    }

}
