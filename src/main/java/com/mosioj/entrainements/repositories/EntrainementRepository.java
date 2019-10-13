package com.mosioj.entrainements.repositories;

import java.text.MessageFormat;
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
	public static final int MAX_RESULT = 10;

	/**
	 * 
	 * @param minSize La taille minimale en mètres de l'entrainement.
	 * @param maxSize La taille maximale en mètres de l'entrainement.
	 * @param from The first month to include.
	 * @param to The last month to include.
	 * @param useOrForDates Whether we should match from and to, or from or to.
	 * @param orderClause The order clause.
	 * @param limit The maximum number of rows that we are looking for.
	 * @param firstRow The first result to retrieve. Starts at 0.
	 * @return Tous les entrainements qui font au moins cette longueur.
	 */
	public static List<Training> getTrainings(	int minSize,
												int maxSize,
												int from,
												int to,
												boolean useOrForDates,
												String orderClause,
												int limit,
												int firstRow) {

		logger.debug(MessageFormat.format(	"Getting trainings with options: [min:{0}, max:{1}, from:{2}, to:{3}] starting at {4}",
											minSize,
											maxSize,
											from,
											to,
											firstRow));

		String queryText = buildFromWhereOrder(useOrForDates, orderClause);
		Query<Training> query = HibernateUtil.getASession().createQuery(queryText, Training.class);
		bindParameters(minSize, maxSize, from, to, query);

		query.setMaxResults(limit);
		query.setFirstResult(firstRow);

		List<Training> list = query.list();
		list.forEach(Training::computeDateSeanceString);
		return list;
	}

	/**
	 * 
	 * @param minSize La taille minimale en mètres de l'entrainement.
	 * @param maxSize La taille maximale en mètres de l'entrainement.
	 * @param from The first month to include.
	 * @param to The last month to include.
	 * @param useOrForDates Whether we should match from and to, or from or to.
	 * @param orderClause The order clause.
	 * @return The total count for this query.
	 */
	public static long getNbOfResults(int minSize, int maxSize, int from, int to, boolean useOrForDates, String orderClause) {
		Query<Long> query = HibernateUtil.getASession()
											.createQuery("select count(*) " + buildFromWhereOrder(useOrForDates, orderClause),
											             Long.class);
		bindParameters(minSize, maxSize, from, to, query);
		return query.getSingleResult();
	}

	/**
	 * Binds where clause parameters.
	 * 
	 * @param minSize La taille minimale en mètres de l'entrainement.
	 * @param maxSize La taille maximale en mètres de l'entrainement.
	 * @param from The first month to include.
	 * @param to The last month to include.
	 * @param query The produced query so far.
	 */
	protected static void bindParameters(int minSize, int maxSize, int from, int to, Query<?> query) {
		query.setParameter("minSize", minSize);
		query.setParameter("maxSize", maxSize);
		query.setParameter("from", from);
		query.setParameter("to", to);
	}

	/**
	 * Used to put in common code between fetch and count.
	 * 
	 * @param useOrForDates Whether to use "OR" (true) or "AND" (false) operator.
	 * @param orderClause The order columns.
	 * @return The query text starting from the from part.
	 */
	private static String buildFromWhereOrder(boolean useOrForDates, String orderClause) {
		String operator = useOrForDates ? " OR " : " AND ";

		StringBuilder sb = new StringBuilder();
		sb.append("FROM TRAINING ");
		sb.append("WHERE size >= :minSize ");
		sb.append("  AND size <= :maxSize ");
		sb.append("  AND ( ");
		sb.append("           EXTRACT(MONTH FROM date_seance) >= :from ");
		sb.append("               ").append(operator);
		sb.append("           EXTRACT(MONTH FROM date_seance) <= :to ");
		sb.append("      ) ");
		sb.append("ORDER BY ").append(orderClause);
		return sb.toString();
	}

	/**
	 * 
	 * @return L'entrainement s'il existe.
	 */
	public static Optional<Training> getById(Long id) {
		IdentifierLoadAccess<Training> query = HibernateUtil.getASession().byId(Training.class);
		Optional<Training> training = query.loadOptional(id);
		training.ifPresent(Training::computeDateSeanceString);
		return training;
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
