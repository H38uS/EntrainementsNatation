package com.mosioj.entrainements.repositories;

import java.text.MessageFormat;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import com.mosioj.entrainements.entities.Coach;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.query.Query;

import com.mosioj.entrainements.entities.Training;
import com.mosioj.entrainements.utils.db.HibernateUtil;

public class EntrainementRepository {

    private static final Logger logger = LogManager.getLogger(EntrainementRepository.class);
    public static final int MAX_RESULT = 10;

    /**
     * Only fetches the first 10 results.
     *
     * @return The days for which there are multiple trainings with same size and same coach.
     */
    public static Set<LocalDate> getDoublons() {

        final String queryText = " SELECT t.* " +
                                 " FROM TRAINING t " +
                                 "WHERE t.id in (select max(o.id)" +
                                 "                from TRAINING o" +
                                 "               group by o.date_seance, o.coach, o.size" +
                                 "              having count(1) > 1)";

        return HibernateUtil.doQueryFetchAsSet(s -> {
            Query<Training> query = s.createNativeQuery(queryText, Training.class);
            query.setMaxResults(10);
            return query.stream().map(Training::getDateSeance).collect(Collectors.toSet());
        });
    }

    /**
     * @param date  The date of the training.
     * @param size  The size of the training.
     * @param coach The coach.
     * @return All the matches (could be an empty list). Unordered list.
     */
    public static List<Training> getTrainings(LocalDate date, int size, Coach coach) {
        final String queryText = "FROM TRAINING " +
                                 "WHERE size = :size " +
                                 "  AND date_seance = :date " +
                                 "  AND coach = :coach ";
        return HibernateUtil.doQueryFetch(s -> {
            Query<Training> query = s.createQuery(queryText, Training.class);
            query.setParameter("date", date);
            query.setParameter("size", size);
            query.setParameter("coach", coach);
            return query.list();
        });
    }

    /**
     * @param minSize       La taille minimale en mètres de l'entrainement.
     * @param maxSize       La taille maximale en mètres de l'entrainement.
     * @param from          The first month to include (1 is January).
     * @param to            The last month to include (1 is January).
     * @param useOrForDates Whether we should match from and to, or from or to.
     * @param orderClause   The order clause.
     * @param limit         The maximum number of rows that we are looking for.
     * @param firstRow      The first result to retrieve. Starts at 0.
     * @return Tous les entrainements qui font au moins cette longueur.
     */
    public static List<Training> getTrainings(int minSize,
                                              int maxSize,
                                              int from,
                                              int to,
                                              boolean useOrForDates,
                                              String orderClause,
                                              int limit,
                                              int firstRow) {

        logger.debug(MessageFormat.format(
                "Getting trainings with options: [min:{0}, max:{1}, from:{2}, to:{3}] starting at {4}",
                minSize,
                maxSize,
                from,
                to,
                firstRow));

        String queryText = buildFromWhereOrder(useOrForDates, orderClause);
        return HibernateUtil.doQueryFetch(s -> {
            Query<Training> query = s.createQuery(queryText, Training.class);
            bindParameters(minSize, maxSize, from, to, query);

            query.setMaxResults(limit);
            query.setFirstResult(firstRow);

            return query.list();
        });
    }

    /**
     * @param minSize       La taille minimale en mètres de l'entrainement.
     * @param maxSize       La taille maximale en mètres de l'entrainement.
     * @param from          The first month to include.
     * @param to            The last month to include.
     * @param useOrForDates Whether we should match from and to, or from or to.
     * @param orderClause   The order clause.
     * @return The total count for this query.
     */
    public static long getNbOfResults(int minSize,
                                      int maxSize,
                                      int from,
                                      int to,
                                      boolean useOrForDates,
                                      String orderClause) {
        return HibernateUtil.doQuerySingle(s -> {
            Query<Long> query = s.createQuery("select count(*) " + buildFromWhereOrder(useOrForDates, orderClause),
                                              Long.class);
            bindParameters(minSize, maxSize, from, to, query);
            return query.getSingleResult();
        });
    }

    /**
     * Binds where clause parameters.
     *
     * @param minSize La taille minimale en mètres de l'entrainement.
     * @param maxSize La taille maximale en mètres de l'entrainement.
     * @param from    The first month to include.
     * @param to      The last month to include.
     * @param query   The produced query so far.
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
     * @param orderClause   The order columns.
     * @return The query text starting from the from part.
     */
    private static String buildFromWhereOrder(boolean useOrForDates, String orderClause) {
        String operator = useOrForDates ? " OR " : " AND ";
        return "FROM TRAINING " +
               "WHERE size >= :minSize " +
               "  AND size <= :maxSize " +
               "  AND ( " +
               "           EXTRACT(MONTH FROM date_seance) >= :from " +
               "               " + operator +
               "           EXTRACT(MONTH FROM date_seance) <= :to " +
               "      ) " +
               "ORDER BY " + orderClause + ", createdAt desc";
    }

    /**
     * @return L'entrainement s'il existe.
     */
    public static Optional<Training> getById(Long id) {
        return HibernateUtil.doQueryOptional(s -> s.byId(Training.class).loadOptional(id));
    }

    /**
     * @param trainingText The training text to check.
     * @return True if this training already exists.
     */
    public static boolean exists(String trainingText) {

        StringBuilder sb = new StringBuilder();
        sb.append("FROM TRAINING ");
        sb.append("WHERE REPLACE(text, ' ', '') = :text ");

        return HibernateUtil.doQueryFetch(s -> {
            Query<Training> query = s.createQuery(sb.toString(), Training.class);
            query.setParameter("text", trainingText.replaceAll(" ", ""));
            return query.list();
        }).size() > 0;
    }

}
