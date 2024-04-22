package com.easyml.model;

import jakarta.persistence.*;

@Entity
@Table(name = "history")
public class History {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(nullable = false)
    private String projectName;
    @Column(nullable = false)
    private byte[] dataset;
    @Column(nullable = false)
    private String workflow;

    public History() {

    }

    public History(long id, String projectName, byte[] dataset, String workflow) {
        this.id = id;
        this.projectName = projectName;
        this.dataset = dataset;
        this.workflow = workflow;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public byte[] getDataset() {
        return dataset;
    }

    public void setDataset(byte[] dataset) {
        this.dataset = dataset;
    }

    public String getWorkflow() {
        return workflow;
    }

    public void setWorkflow(String workflow) {
        this.workflow = workflow;
    }
}
