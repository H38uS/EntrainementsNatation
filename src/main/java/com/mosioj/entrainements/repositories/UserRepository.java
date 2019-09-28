package com.mosioj.entrainements.repositories;

import java.util.Optional;

import org.hibernate.query.Query;

import com.mosioj.entrainements.entities.User;
import com.mosioj.entrainements.utils.HibernateUtil;

public class UserRepository {

	/**
	 * 
	 * @param email
	 * @return The corresponding user, if it exists.
	 */
	public static Optional<User> getUser(String email) {

		StringBuilder sb = new StringBuilder();
		sb.append("FROM USERS ");
		sb.append("WHERE email = :email ");
		Query<User> query = HibernateUtil.getASession().createQuery(sb.toString(), User.class);

		query.setParameter("email", email);

		return query.uniqueResultOptional();
	}
}
