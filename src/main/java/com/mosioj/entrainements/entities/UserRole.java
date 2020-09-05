package com.mosioj.entrainements.entities;

import com.google.gson.annotations.Expose;

import javax.persistence.*;

@Entity(name = "USER_ROLES")
public class UserRole {

    public static final String STANDARD_ROLE = "ROLE_USER";
    public static final String ADMIN_ROLE = "ROLE_ADMIN";
    public static final String MODIFICATION_ROLE = "ROLE_MODIF";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 50, unique = true)
    private String email;

    @Column(length = 50)
    @Expose
    private String role;

    /**
     * Generates a standard role for accessing the account.
     *
     * @param user     The user.
     * @param roleType The role to generate.
     * @return The role.
     */
    public static UserRole getARoleFor(User user, String roleType) {
        UserRole role = new UserRole();
        role.email = user.getEmail();
        role.role = roleType;
        return role;
    }

    /**
     * Generates a standard role for accessing the account.
     *
     * @param user The user.
     * @return The role.
     */
    public static UserRole getStandardRoleFor(User user) {
        return getARoleFor(user, STANDARD_ROLE);
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
     * @return the role
     */
    public String getRole() {
        return role;
    }

    /**
     * @param role the role to set
     */
    public void setRole(String role) {
        this.role = role;
    }
}
