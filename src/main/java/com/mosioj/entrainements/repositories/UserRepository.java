package com.mosioj.entrainements.repositories;

import java.util.List;
import java.util.Optional;

import org.hibernate.Session;
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
		
		try (Session session = HibernateUtil.getASession()) {
			Query<User> query = session.createQuery(sb.toString(), User.class);
			query.setParameter("email", email);
			return query.uniqueResultOptional();
		}
	}

	/**
	 * 
	 * @param id The user's id.
	 * @return The corresponding user, if it exists.
	 */
	public static Optional<User> getUser(long id) {
		
		StringBuilder sb = new StringBuilder();
		sb.append("FROM USERS ");
		sb.append("WHERE id = :id");
		
		try (Session session = HibernateUtil.getASession()) {
			Query<User> query = session.createQuery(sb.toString(), User.class);
			query.setParameter("id", id);
			return query.uniqueResultOptional();
		}
	}

	/**
	 * 
	 * @return The list of users currently in the database.
	 */
	public static List<User> getUsers() {
		try (Session session = HibernateUtil.getASession()) {
			Query<User> query = session.createQuery("FROM USERS ORDER BY createdAt DESC", User.class);
			return query.list();
		}
	}
}
