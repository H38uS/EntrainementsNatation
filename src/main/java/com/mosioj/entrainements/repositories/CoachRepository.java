package com.mosioj.entrainements.repositories;

import com.mosioj.entrainements.entities.Coach;
import com.mosioj.entrainements.utils.db.HibernateUtil;
import org.apache.commons.lang3.StringUtils;

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
        return HibernateUtil.doQueryOptional(s -> Optional.ofNullable(s.find(Coach.class, name)));
    }
}
