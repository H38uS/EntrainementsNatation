package com.mosioj.entrainements.repositories;

import java.util.List;

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
}
