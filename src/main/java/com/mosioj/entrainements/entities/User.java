package com.mosioj.entrainements.entities;

import com.google.gson.annotations.Expose;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Entity(name = "USERS")
public class User implements Serializable {

    private static final long serialVersionUID = 7850514070863185385L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Expose
    private Long id;

    @Column(length = 50, unique = true)
    @Expose
    private String email;

    @Column(length = 50)
    private String name;

    @Column(length = 300)
    private String password;

    @OneToMany(fetch = FetchType.EAGER)
    @JoinColumn(name = "email", referencedColumnName = "email")
    @Expose
    private Set<UserRole> roles;

    @OneToMany(mappedBy = "byUser", cascade = CascadeType.ALL)
    private List<SavedTraining> savedTrainings;

    @Column(updatable = false)
    @CreationTimestamp
    @Expose
    private LocalDateTime createdAt;

    @Column()
    @UpdateTimestamp
    private LocalDateTime updatedAt;

    protected User() {
    }

    public User(String email, String password) {
        this.email = email;
        this.password = password;
    }

    /**
     * @return the id
     */
    public Long getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * @return the email
     */
    public String getEmail() {
        return email;
    }

    /**
     * @param email the email to set
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the createdAt
     */
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    /**
     * @param createdAt the createdAt to set
     */
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    /**
     * @return the updatedAt
     */
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    /**
     * @param updatedAt the updatedAt to set
     */
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    /**
     * @param password the password to set
     */
    public User setPassword(String password) {
        this.password = password;
        return this;
    }

    /**
     * @return The user's hashed password.
     */
    public String getPassword() {
        return password;
    }

    /**
     * @return the roles
     */
    public Set<UserRole> getRoles() {
        return roles;
    }

    /**
     * @param roles the roles to set
     */
    public void setRoles(Set<UserRole> roles) {
        this.roles = roles;
    }

    /**
     * Adds a new role.
     *
     * @param role The role.
     */
    public void addRole(UserRole role) {
        roles.add(role);
    }

    @Override
    public String toString() {
        return email;
    }
}
