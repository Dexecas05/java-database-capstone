package com.project.back_end.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "admins")
public class Admin {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(unique = true) // Ensures usernames are unique
    private String username;

    @NotNull
    // In a real application, you'd use @JsonIgnore here to prevent the password from being serialized to JSON responses.
    private String password;

// Constructors

    // No-argument constructor (Required by JPA)
    public Admin() {
    }

    // Parameterized constructor (Optional, but useful for testing/initialization)
    public Admin(String username, String password) {
        this.username = username;
        this.password = password;
    }

// Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
