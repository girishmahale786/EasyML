package com.easyml.model;
import jakarta.persistence.*;

import java.util.Objects;

@Entity
@Table(name = "users") // Use your actual table name here
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false,unique = true)
    private String name;

    @Column(nullable = false,unique = true)
    private String email;
    @Column(nullable = false)
    private String password;


    @Column(nullable = false,columnDefinition = "boolean default false")
    private boolean isStaff;

    @Column(nullable = false,columnDefinition = "boolean default false")
    private boolean isSuperuser;

    @Column(nullable = false, columnDefinition = "boolean default true")
    private boolean isActive;


    // Constructors, getters, and setters

    // Constructors
    public User() {}

    public User(Long id, String email, String password, String name, boolean isStaff, boolean isSuperuser, boolean isActive) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.name = name;
        this.isStaff = isStaff;
        this.isSuperuser = isSuperuser;
        this.isActive = isActive;
    }

    public boolean isStaff() {
        return isStaff;
    }

    public boolean isSuperuser() {
        return isSuperuser;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setSuperuser(boolean superuser) {
        isSuperuser = superuser;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public void setStaff(boolean staff) {
        isStaff = staff;
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
                ", email='" + email + '\'' +
                ", name='" + name + '\'' +
                ", isStaff=" + isStaff +
                ", isSuperuser=" + isSuperuser +
                ", isActive=" + isActive +
                '}';
    }
}
