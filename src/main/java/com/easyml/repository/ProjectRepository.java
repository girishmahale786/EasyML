package com.easyml.repository;

import com.easyml.model.Project;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface ProjectRepository extends JpaRepository<Project, Long> {
    //update the project
    Optional<Project> updateProjectById(long id , Project project);


    Optional<Project> reset(Project project);

}
