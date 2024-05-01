package com.easyml.model;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "history")
public class History {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(nullable = false)
    private String preprocessing;
    @Column(nullable = false)
    private long projectId;

    public History(long id, String preprocessing, long projectId) {
        this.id = id;
        this.preprocessing = preprocessing;
        this.projectId = projectId;
    }

    public History() {

    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getPreprocessing() {
        return preprocessing;
    }

    public void setPreprocessing(String preprocessing) {
        this.preprocessing = preprocessing;
    }

    public long getProjectId() {
        return projectId;
    }

    public void setProject_id(long projectId) {
        this.projectId = projectId;
    }
}
