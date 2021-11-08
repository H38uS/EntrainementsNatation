package com.mosioj.entrainements.repositories;

import com.mosioj.entrainements.entities.Coach;
import com.mosioj.entrainements.utils.db.HibernateUtil;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.query.Query;

import java.util.List;
import java.util.Optional;

public class CoachRepository {

    /**
     * @return Tous les entraineurs disponibles.
     */
    public static List<Coach> getCoach() {
        return HibernateUtil.doQueryFetch(s -> s.createQuery("FROM COACH ORDER BY name", Coach.class).list());
    }

    /**
     * @param name The coach name.
     * @return The coach object if found.
     */
    public static Optional<Coach> getCoachForName(String name) {
        if (StringUtils.isBlank(name))
            return Optional.empty();

        StringBuilder sb = new StringBuilder();
        sb.append("FROM COACH ");
        sb.append("WHERE name = :name ");

        return HibernateUtil.doQueryOptional(s -> {
            Query<Coach> query = s.createQuery(sb.toString(), Coach.class);
            query.setParameter("name", name);
            return query.uniqueResultOptional();
        });
    }

    /**
     * Creates a new coach for this club.
     *
     * @param coachName The coach name.
     */
    public static void addCoach(String coachName) {
        HibernateUtil.saveit(new Coach(coachName));
    }
}
