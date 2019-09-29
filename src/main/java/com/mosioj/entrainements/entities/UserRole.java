package com.mosioj.entrainements.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity(name = "USER_ROLES")
public class UserRole {

	private static final String STANDARD_ROLE = "ROLE_USER";

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(length = 50, unique = true)
	private String email;

	@Column(length = 50)
	private String role;

	/**
	 * Generates a standard role for accessing the account.
	 * 
	 * @param user The user.
	 * @return The role.
	 */
	public static UserRole getStandardRoleFor(User user) {
		UserRole role = new UserRole();
		role.email = user.getEmail();
		role.role = STANDARD_ROLE;
		return role;
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
