package com.easyml.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, unique = true)
    private String name;
    @Column(nullable = false, unique = true)
    private String email;
    @Column(nullable = false)
    private String password;
    @Column(nullable = false, columnDefinition = "boolean default false")
    private boolean isStaff;
    @Column(nullable = false, columnDefinition = "boolean default false")
    private boolean isSuperuser;
    @Column(nullable = false, columnDefinition = "boolean default true")
    private boolean isActive;
    @Column(nullable = true)
    private String otp;
    @Column(nullable = true)
    private LocalDateTime otpGeneratedTime;
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Project> projects;

    public User() {
    }

    public User(Long id, String name, String email, String password, boolean isStaff, boolean isActive, String otp, LocalDateTime otpGeneratedTime, boolean isSuperuser) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
        this.isStaff = isStaff;
        this.isActive = isActive;
        this.otp = otp;
        this.otpGeneratedTime = otpGeneratedTime;
        this.isSuperuser = isSuperuser;
    }

    public String getOtp() {
        return otp;
    }

    public void setOtp(String otp) {
        this.otp = otp;
    }

    public LocalDateTime getOtpGeneratedTime() {
        return otpGeneratedTime;
    }

    public void setOtpGeneratedTime(LocalDateTime otpGeneratedTime) {
        this.otpGeneratedTime = otpGeneratedTime;
    }

    public boolean isStaff() {
        return isStaff;
    }

    public void setStaff(boolean staff) {
        isStaff = staff;
    }

    public boolean isSuperuser() {
        return isSuperuser;
    }

    public void setSuperuser(boolean superuser) {
        isSuperuser = superuser;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    @Access(AccessType.PROPERTY)
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Project> getProjects() {
        return projects;
    }

    public void setProjects(List<Project> projects) {
        this.projects = projects;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return isStaff == user.isStaff && isSuperuser == user.isSuperuser && isActive == user.isActive && Objects.equals(id, user.id) && Objects.equals(email, user.email) && Objects.equals(password, user.password) && Objects.equals(name, user.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, email, password, name, isStaff, isSuperuser, isActive);
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", isStaff=" + isStaff +
                ", isSuperuser=" + isSuperuser +
                ", isActive=" + isActive +
                ", projects=" + projects +
                '}';
    }
}
