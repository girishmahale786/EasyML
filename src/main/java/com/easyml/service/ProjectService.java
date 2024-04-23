package com.easyml.service;

import com.easyml.model.Project;
import com.easyml.repository.ProjectRepository;
import org.springframework.stereotype.Service;


@Service
public class ProjectService {
    private final ProjectRepository projectRepository;

    public ProjectService(ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }

    public Project getProject(Long projectId) {
        return projectRepository.findById(projectId).orElse(null);
    }

}
