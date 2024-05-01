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
    @Column(nullable = false)
    private String edit_dataset_url;
    @Column(nullable = false)
    private String final_dataset_url;
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    public Project() {

    }

    public Project(Long id, String name, String datasetUrl, String edit_dataset_url, String final_dataset_url, User user) {
        this.id = id;
        this.name = name;
        this.datasetUrl = datasetUrl;
        this.edit_dataset_url = edit_dataset_url;
        this.final_dataset_url = final_dataset_url;
        this.user = user;
    }

    public String getEdit_dataset_url() {
        return edit_dataset_url;
    }

    public void setEdit_dataset_url(String edit_dataset_url) {
        this.edit_dataset_url = edit_dataset_url;
    }

    public String getFinal_dataset_url() {
        return final_dataset_url;
    }

    public void setFinal_dataset_url(String final_dataset_url) {
        this.final_dataset_url = final_dataset_url;
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
