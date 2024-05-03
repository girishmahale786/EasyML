package com.easyml.model;

import com.google.gson.annotations.Expose;
import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "projects")
public class Project {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Expose
    private Long id;
    @Column(nullable = false)
    private String name;
    @Column(nullable = true)
    private String description;
    @Column(nullable = false)
    private String datasetUrl;
    @Column(nullable = false)
    private String editDatasetUrl;
    @Column(nullable = false)
    private String finalDatasetUrl;
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    @Expose(serialize = false, deserialize = false)
    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL)
    private List<History> histories;

    public Project() {

    }

    public Project(Long id, String name, String description, String datasetUrl, String editDatasetUrl, String finalDatasetUrl, User user, List<History> histories) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.datasetUrl = datasetUrl;
        this.editDatasetUrl = editDatasetUrl;
        this.finalDatasetUrl = finalDatasetUrl;
        this.user = user;
        this.histories = histories;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDatasetUrl() {
        return datasetUrl;
    }

    public void setDatasetUrl(String datasetUrl) {
        this.datasetUrl = datasetUrl;
    }

    public String getEditDatasetUrl() {
        return editDatasetUrl;
    }

    public void setEditDatasetUrl(String editDatasetUrl) {
        this.editDatasetUrl = editDatasetUrl;
    }

    public String getFinalDatasetUrl() {
        return finalDatasetUrl;
    }

    public void setFinalDatasetUrl(String finalDatasetUrl) {
        this.finalDatasetUrl = finalDatasetUrl;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public List<History> getHistories() {
        return histories;
    }

    public void setHistories(List<History> histories) {
        this.histories = histories;
    }

    @Override
    public String toString() {
        return "Project{" + "id=" + id + ", name='" + name + '\'' + ", datasetUrl='" + datasetUrl + '\'' + ", editDatasetUrl='" + editDatasetUrl + '\'' + ", finalDatasetUrl='" + finalDatasetUrl + '\'' + ", user=" + user + ", histories=" + histories + '}';
    }
}
