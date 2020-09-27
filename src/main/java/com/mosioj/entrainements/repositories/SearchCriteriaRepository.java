package com.mosioj.entrainements.repositories;

import com.mosioj.entrainements.entities.User;
import com.mosioj.entrainements.entities.SearchCriteria;
import com.mosioj.entrainements.utils.db.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.util.Optional;

public class SearchCriteriaRepository {

    /**
     * @param user The user.
     * @return The search criteria for this user, if any was saved.
     */
    public static Optional<SearchCriteria> of(User user) {
        return HibernateUtil.doQueryOptional(s -> of(user, s));
    }

    /**
     * @param user The user.
     * @return The search criteria for this user, if any was saved.
     */
    public static Optional<SearchCriteria> of(User user, Session session) {
        Query<SearchCriteria> query = session.createQuery("from SEARCH_CRITERIA where savedBy = :user",
                                                          SearchCriteria.class);
        query.setParameter("user", user);
        return query.uniqueResultOptional();
    }

    /**
     * Deletes a previously saved criteria.
     *
     * @param user The user.
     */
    public static void deleteTheOneOf(User user) {
        HibernateUtil.doSomeWork(s -> {
            Transaction t = s.beginTransaction();
            of(user, s).ifPresent(s::delete);
            t.commit();
        });
    }
}
