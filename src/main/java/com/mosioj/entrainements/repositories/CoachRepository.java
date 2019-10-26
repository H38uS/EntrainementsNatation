package com.mosioj.entrainements.repositories;

import java.util.List;
import java.util.Optional;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.mosioj.entrainements.entities.Coach;
import com.mosioj.entrainements.utils.HibernateUtil;

public class CoachRepository {

	/**
	 * 
	 * @return Tous les entraineurs disponibles.
	 */
	public static List<Coach> getCoach() {

		StringBuilder sb = new StringBuilder();
		sb.append("FROM COACH ");
		sb.append("ORDER BY name ");

		try (Session session = HibernateUtil.getASession()) {
			Query<Coach> query = session.createQuery(sb.toString(), Coach.class);
			return query.list();
		}
	}

	/**
	 * 
	 * @param name The coach name.
	 * @return The coach object if found.
	 */
	public static Optional<Coach> getCoachForName(String name) {
		try (Session session = HibernateUtil.getASession()) {
			return Optional.ofNullable(session.find(Coach.class, name));
		}
	}
}
