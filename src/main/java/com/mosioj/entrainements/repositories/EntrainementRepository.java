package com.mosioj.entrainements.repositories;

import com.mosioj.entrainements.entities.Coach;
import com.mosioj.entrainements.entities.Training;
import com.mosioj.entrainements.entities.SearchCriteria;
import com.mosioj.entrainements.utils.db.HibernateUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.text.MessageFormat;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

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
     * @return Any training.
     */
    public static Optional<Training> getATraining() {
        return HibernateUtil.doQueryOptional(s -> s.createQuery("FROM TRAINING", Training.class)
                                                   .setMaxResults(1)
                                                   .uniqueResultOptional());
    }

    /**
     * @param criteria    This search criteria.
     * @param orderClause The order clause.
     * @param limit       The maximum number of rows that we are looking for.
     * @param firstRow    The first result to retrieve. Starts at 0.
     * @return Tous les entrainements qui font au moins cette longueur.
     */
    public static List<Training> getTrainings(SearchCriteria criteria, String orderClause, int limit, int firstRow) {

        final String message = "Getting trainings with options: {0} starting at {1}";
        logger.debug(MessageFormat.format(message, criteria, firstRow));

        String queryText = buildFromWhereOrder(criteria.shouldUseOrOperator(), orderClause);
        return HibernateUtil.doQueryFetch(s -> {
            Query<Training> query = s.createQuery(queryText, Training.class);
            bindParameters(criteria, query);
            query.setMaxResults(limit);
            query.setFirstResult(firstRow);
            return query.list();
        });
    }

    /**
     * @param criteria    This search criteria.
     * @param orderClause The order clause.
     * @return The total count for this query.
     */
    public static long getNbOfResults(SearchCriteria criteria, String orderClause) {
        return HibernateUtil.doQuerySingle(s -> {
            Query<Long> query = s.createQuery("select count(*) " +
                                              buildFromWhereOrder(criteria.shouldUseOrOperator(), orderClause),
                                              Long.class);
            bindParameters(criteria, query);
            return query.getSingleResult();
        });
    }

    /**
     * Binds where clause parameters.
     *
     * @param criteria This search criteria.
     * @param query    The produced query so far.
     */
    protected static void bindParameters(SearchCriteria criteria, Query<?> query) {
        query.setParameter("minSize", criteria.getMinimalSize());
        query.setParameter("maxSize", criteria.getMaximalSize());
        query.setParameter("from", criteria.getFromMonthInclusive());
        query.setParameter("to", criteria.getToMonthInclusive());
        query.setParameter("coach", criteria.getCoach());
        query.setParameter("day", criteria.getDayOfWeek());
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
               "  AND (coach = :coach or :coach is null)" +
               "  AND (DAYOFWEEK(date_seance) = :day or :day is null)" +
               "ORDER BY " + orderClause + ", createdAt desc";
    }

    /**
     * @return L'entrainement s'il existe.
     */
    public static Optional<Training> getById(Long id) {
        return HibernateUtil.doQueryOptional(s -> getById(id, s));
    }

    /**
     * @return L'entrainement s'il existe.
     */
    public static Optional<Training> getById(Long id, Session session) {
        return session.byId(Training.class).loadOptional(id);
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

    /**
     * Deletes the training corresponding to the given id if found.
     *
     * @param trainingId The identifier.
     */
    public static void deleteIt(long trainingId) {
        HibernateUtil.doSomeWork(s -> {
            Transaction t = s.beginTransaction();
            getById(trainingId, s).ifPresent(s::remove);
            t.commit();
        });
    }

}
