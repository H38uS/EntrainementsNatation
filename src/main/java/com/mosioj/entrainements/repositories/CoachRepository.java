package com.mosioj.entrainements.repositories;

import java.util.List;
import java.util.Optional;

import com.mosioj.entrainements.entities.Coach;
import com.mosioj.entrainements.utils.db.HibernateUtil;

public class CoachRepository {

	/**
	 * 
	 * @return Tous les entraineurs disponibles.
	 */
	public static List<Coach> getCoach() {

		StringBuilder sb = new StringBuilder();
		sb.append("FROM COACH ");
		sb.append("ORDER BY name ");

		return HibernateUtil.doQueryFetch(s -> s.createQuery(sb.toString(), Coach.class).list());
	}

	/**
	 * 
	 * @param name The coach name.
	 * @return The coach object if found.
	 */
	public static Optional<Coach> getCoachForName(String name) {
		return HibernateUtil.doQueryOptional(s -> Optional.ofNullable(s.find(Coach.class, name)));
	}
}
