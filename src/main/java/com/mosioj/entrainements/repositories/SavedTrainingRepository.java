package com.mosioj.entrainements.repositories;

import com.mosioj.entrainements.entities.SavedTraining;
import com.mosioj.entrainements.entities.Training;
import com.mosioj.entrainements.entities.User;
import com.mosioj.entrainements.utils.db.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class SavedTrainingRepository {

    private SavedTrainingRepository() {
        // Forbidden.
    }

    /**
     * @param user The user.
     * @return The list of training saved by the given user.
     */
    public static List<Training> getSavedTrainingsOf(User user) {
        final String queryText = "FROM SAVED_TRAINING " +
                                 "WHERE userId = :user " +
                                 "ORDER BY createdAt";
        return HibernateUtil.doQueryFetch(s -> {
            Query<SavedTraining> query = s.createQuery(queryText, SavedTraining.class);
            query.setParameter("user", user);
            return query.list().stream().map(SavedTraining::getTraining).collect(Collectors.toList());
        });
    }

    /**
     * @param user     The user.
     * @param training The training.
     * @return The optional saved training if an instance exists.
     */
    public static Optional<SavedTraining> of(User user, Training training) {
        return HibernateUtil.doQueryOptional(s -> of(user, training, s));
    }

    /**
     * Deletes this training from the saved items of this user, if any.
     *
     * @param user     The user.
     * @param training The training.
     */
    public static void delete(User user, Training training) {
        HibernateUtil.doSomeWork(s -> {
            Transaction t = s.beginTransaction();
            of(user, training, s).ifPresent(s::delete);
            t.commit();
        });
    }

    /**
     * @param user     The user.
     * @param training The training.
     * @param session  The session.
     * @return The optional saved training if an instance exists.
     */
    public static Optional<SavedTraining> of(User user, Training training, Session session) {
        final String queryText = "FROM SAVED_TRAINING " +
                                 "WHERE userId = :user " +
                                 "  AND training = :training";
        Query<SavedTraining> query = session.createQuery(queryText, SavedTraining.class);
        query.setParameter("user", user);
        query.setParameter("training", training);
        return query.uniqueResultOptional();
    }
}
