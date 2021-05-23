package com.mosioj.entrainements.repositories;

import com.mosioj.entrainements.entities.PasswordResetRequest;
import com.mosioj.entrainements.entities.User;
import com.mosioj.entrainements.utils.db.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.query.Query;

import java.util.Optional;

public class PasswordResetRequestRepositoy {

    /**
     * @param user The user we want to check against.
     * @return True if this user has already a valid reset request.
     */
    public static boolean existsForUser(User user) {

        StringBuilder sb = new StringBuilder();
        sb.append("FROM PASSWORD_RESET_REQUEST ");
        sb.append("WHERE user_id = :id");

        Optional<PasswordResetRequest> res = HibernateUtil.doQueryOptional(s -> {
            Query<PasswordResetRequest> query = s.createQuery(sb.toString(), PasswordResetRequest.class);
            query.setParameter("id", user.getId());
            return query.uniqueResultOptional();
        });

        return res.map(PasswordResetRequest::isValid).orElse(false);
    }

    /**
     * @param userId The user ID
     * @param token  The token
     * @return True if found, false otherwise
     */
    public static boolean exists(long userId, long token) {

        StringBuilder sb = new StringBuilder();
        sb.append(" FROM PASSWORD_RESET_REQUEST ");
        sb.append("WHERE user_id = :user_id ");
        sb.append("  AND token = :token ");

        return HibernateUtil.doQueryOptional(s -> {
            Query<PasswordResetRequest> query = s.createQuery(sb.toString(), PasswordResetRequest.class);
            query.setParameter("user_id", userId);
            query.setParameter("token", token);
            return query.uniqueResultOptional();
        }).isPresent();
    }

    /**
     * @param s      The hibernate session.
     * @param userId The user ID
     * @param token  The token
     * @return The corresponding request, if found.
     */
    public static Optional<PasswordResetRequest> getRequest(Session s, long userId, long token) {
        String queryText = " FROM PASSWORD_RESET_REQUEST WHERE user_id = :user_id   AND token = :token ";
        Query<PasswordResetRequest> query = s.createQuery(queryText, PasswordResetRequest.class);
        query.setParameter("user_id", userId);
        query.setParameter("token", token);
        return query.uniqueResultOptional();
    }
}
