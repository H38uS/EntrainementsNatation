package com.mosioj.entrainements.repositories;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.IdentifierLoadAccess;
import org.hibernate.query.Query;

import com.mosioj.entrainements.entities.Training;
import com.mosioj.entrainements.utils.HibernateUtil;

public class EntrainementRepository {

	private static final Logger logger = LogManager.getLogger(EntrainementRepository.class);

	/**
	 * 
	 * @param from Date (incluse) à partie de laquelle on récupère les entrainements.
	 * @param to Date (incluse) jusqu'à laquelle on récupère les entrainements.
	 * @return Tous les entrainements dans la période.
	 */
	public static List<Training> getRemplacements(Date from, Date to) {

		logger.debug("Getting values from " + from + " to " + to + "...");

		StringBuilder sb = new StringBuilder();
		sb.append("FROM TRAINING ");
		sb.append("WHERE date_seance >= :monthStart and date_seance <= :monthEnd ");
		sb.append("ORDER BY date_seance ");
		Query<Training> query = HibernateUtil.getASession().createQuery(sb.toString(), Training.class);

		query.setParameter("monthStart", from);
		query.setParameter("monthEnd", to);

		return query.list();
	}

	/**
	 * 
	 * @param minSize La taille minimale de l'entrainement.
	 * @return Tous les entrainements qui font au moins cette longueur.
	 */
	public static List<Training> getTrainings(int minSize) {
		
		logger.debug("Getting trainings longer than " + minSize + "...");
		
		StringBuilder sb = new StringBuilder();
		sb.append("FROM TRAINING ");
		sb.append("WHERE size >= :minSize ");
		sb.append("ORDER BY date_seance ");
		Query<Training> query = HibernateUtil.getASession().createQuery(sb.toString(), Training.class);
		
		query.setParameter("minSize", minSize);

		return query.list();
	}

	/**
	 * 
	 * @return L'entrainement s'il existe.
	 */
	public static Optional<Training> getById(Long id) {
		IdentifierLoadAccess<Training> query = HibernateUtil.getASession().byId(Training.class);
		return query.loadOptional(id);
	}

	/**
	 * 
	 * @param trainingText The training text to check.
	 * @return True if this training already exists.
	 */
	public static boolean exists(String trainingText) {
		
		StringBuilder sb = new StringBuilder();
		sb.append("FROM TRAINING ");
		sb.append("WHERE REPLACE(text, ' ', '') = :text ");

		Query<Training> query = HibernateUtil.getASession().createQuery(sb.toString(), Training.class);
		query.setParameter("text", trainingText.replaceAll(" ", ""));
		
		return query.list().size() > 0;
	}

}
