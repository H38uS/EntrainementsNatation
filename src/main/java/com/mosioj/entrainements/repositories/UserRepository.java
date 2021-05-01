package com.mosioj.entrainements.repositories;

import com.mosioj.entrainements.entities.User;
import com.mosioj.entrainements.utils.db.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.util.List;
import java.util.Optional;

public class UserRepository {

    /**
     * @param email The user's email.
     * @return The corresponding user, if it exists.
     */
    public static Optional<User> getUser(String email) {
        return HibernateUtil.doQueryOptional(s -> getUser(email, s));
    }

    /**
     * @param email   The user's email.
     * @param session The hibernate session.
     * @return The corresponding user, if it exists.
     */
    public static Optional<User> getUser(String email, Session session) {
        Query<User> query = session.createQuery("FROM USERS WHERE email = :email ", User.class);
        query.setParameter("email", email);
        return query.uniqueResultOptional();
    }

    /**
     * @param id The user's id.
     * @return The corresponding user, if it exists.
     */
    public static Optional<User> getUser(long id) {

        StringBuilder sb = new StringBuilder();
        sb.append("FROM USERS ");
        sb.append("WHERE id = :id");

        return HibernateUtil.doQueryOptional(s -> {
            Query<User> query = s.createQuery(sb.toString(), User.class);
            query.setParameter("id", id);
            return query.uniqueResultOptional();
        });
    }

    /**
     * @return The list of users currently in the database.
     */
    public static List<User> getUsers() {
        return HibernateUtil.doQueryFetch(s -> s.createQuery("FROM USERS ORDER BY createdAt DESC", User.class).list());
    }

    /**
     * Deletes this user, if any.
     *
     * @param email The user's email.
     */
    public static void delete(String email) {
        HibernateUtil.doSomeWork(s -> {
            Transaction t = s.beginTransaction();
            getUser(email, s).ifPresent(s::delete);
            t.commit();
        });
    }
}
