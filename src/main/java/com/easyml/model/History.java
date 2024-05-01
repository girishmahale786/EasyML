package com.easyml.model;

import jakarta.persistence.*;

@Entity
@Table(name = "history")
public class History {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String preprocessing;

    @ManyToOne
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    public History() {

    }

    public History(Long id, String preprocessing, Project project) {
        this.id = id;
        this.preprocessing = preprocessing;
        this.project = project;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPreprocessing() {
        return preprocessing;
    }

    public void setPreprocessing(String preprocessing) {
        this.preprocessing = preprocessing;
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }
}
