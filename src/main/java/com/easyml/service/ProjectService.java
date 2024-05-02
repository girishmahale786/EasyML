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
    public void updateProject(Project project) {
        projectRepository.updateProjectById(project.getId(), project);
    }

    public void deleteProject(Project project) {
        projectRepository.delete(project);
    }
    public void resetProject(Project project) {
        projectRepository.reset(project);
    }

}
