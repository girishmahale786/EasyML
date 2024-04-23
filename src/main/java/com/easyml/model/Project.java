package com.easyml.model;

import jakarta.persistence.*;

@Entity
@Table(name = "projects")
public class Project {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String name;
    @Column(nullable = false)
    private String datasetUrl;
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    public Project() {

    }

    public Project(Long id, String name, String datasetUrl) {
        this.id = id;
        this.name = name;
        this.datasetUrl = datasetUrl;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDatasetUrl() {
        return datasetUrl;
    }

    public void setDatasetUrl(String datasetUrl) {
        this.datasetUrl = datasetUrl;
    }

    @Override
    public String toString() {
        return "Project{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", datasetUrl='" + datasetUrl + '\'' +
                ", user=" + user +
                '}';
    }
}
