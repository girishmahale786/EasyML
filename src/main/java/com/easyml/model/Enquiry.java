package com.easyml.model;

import jakarta.persistence.*;


@Entity
@Table(name = "enquiry")
public class Enquiry {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String name;
    @Column(nullable = false, unique = true)
    private String email;
    @Column(nullable = false)
    private String enquiryText;

    public Enquiry(){

    }
    public Enquiry(Long id, String name, String email, String enquiryText) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.enquiryText = enquiryText;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getEnquiryText() {
        return enquiryText;
    }

    public void setEnquiryText(String enquiryText) {
        this.enquiryText = enquiryText;
    }
}
