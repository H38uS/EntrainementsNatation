package com.mosioj.entrainements.repositories;

import java.util.List;
import java.util.Optional;

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
		Query<Coach> query = HibernateUtil.getASession().createQuery(sb.toString(), Coach.class);

		return query.list();
	}

	/**
	 * 
	 * @param name The coach name.
	 * @return The coach object if found.
	 */
	public static Optional<Coach> getCoachForName(String name) {
		return Optional.ofNullable(HibernateUtil.getASession().find(Coach.class, name));
	}
}
