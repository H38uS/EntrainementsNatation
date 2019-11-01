package com.mosioj.entrainements.repositories;

import java.util.Optional;

import org.hibernate.query.Query;

import com.mosioj.entrainements.entities.PasswordResetRequest;
import com.mosioj.entrainements.entities.User;
import com.mosioj.entrainements.utils.db.HibernateUtil;

public class PasswordResetRequestRepositoy {

	/**
	 * 
	 * @param user The user we want to check against.
	 * @return True if this user has already a valid reset request.
	 */
	public static boolean existsForUser(User user) {

		boolean result = false;

		StringBuilder sb = new StringBuilder();
		sb.append("FROM PASSWORD_RESET_REQUEST ");
		sb.append("WHERE user_id = :id");

		Optional<PasswordResetRequest> res = HibernateUtil.doQueryOptional(s -> {
			Query<PasswordResetRequest> query = s.createQuery(sb.toString(), PasswordResetRequest.class);
			query.setParameter("id", user.getId());
			return query.uniqueResultOptional();
		});

		if (res.isPresent()) {
			return res.get().isValid();
		}

		return result;
	}

	/**
	 * 
	 * @param userId
	 * @param token
	 * @return The corresponding request, if found.
	 */
	public static Optional<PasswordResetRequest> getRequest(long userId, long token) {

		StringBuilder sb = new StringBuilder();
		sb.append(" FROM PASSWORD_RESET_REQUEST ");
		sb.append("WHERE user_id = :user_id ");
		sb.append("  AND token = :token ");

		return HibernateUtil.doQueryOptional(s -> {
			Query<PasswordResetRequest> query = s.createQuery(sb.toString(), PasswordResetRequest.class);
			query.setParameter("user_id", userId);
			query.setParameter("token", token);
			return query.uniqueResultOptional();
		});
	}
}
