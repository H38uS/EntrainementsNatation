package com.mosioj.entrainements.entities;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import org.hibernate.annotations.CreationTimestamp;

@Entity(name = "PASSWORD_RESET_REQUEST")
public class PasswordResetRequest {

	/**
	 * The number of hours for which this request is valid.
	 */
	private static final int VALIDITY_PERIOD_IN_HOURS = 3;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "user_id")
	private Long userId;
	
	@Column
	private Long token;

	@Column(updatable = false)
	@CreationTimestamp
	private LocalDateTime createdAt;

	/** Default constructor for Hibernate */
	public PasswordResetRequest() {
		// Default constructor for Hibernate
	}

	/**
	 * A new request.
	 * 
	 * @param userId For this user.
	 * @param token The validity token.
	 */
	public static PasswordResetRequest of(long userId, long token) {
		PasswordResetRequest instance = new PasswordResetRequest();
		instance.userId = userId;
		instance.token = token;
		return instance;
	}

	/**
	 * 
	 * @return True if and only if this request is still valid.
	 */
	public boolean isValid() {
		if (userId == null || token == null || createdAt == null) {
			return false;
		}
		return LocalDateTime.now().isBefore(createdAt.plusHours(VALIDITY_PERIOD_IN_HOURS)); 
	}
}
