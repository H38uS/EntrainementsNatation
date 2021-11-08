package com.mosioj.entrainements.service.admin;

import com.mosioj.entrainements.entities.Coach;
import com.mosioj.entrainements.repositories.CoachRepository;
import com.mosioj.entrainements.service.AbstractService;
import com.mosioj.entrainements.service.response.ServiceResponse;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/admin/service/coach")
public class AdminCoach extends AbstractService {

    private static final Logger logger = LogManager.getLogger(AdminCoach.class);

    @Override
    protected void servicePost(HttpServletRequest request, HttpServletResponse response) throws IOException {

        final String coachName = request.getParameter("coachName");
        if (StringUtils.isBlank(coachName)) {
            ServiceResponse.ko("Il faut un nom au coach...", request).sentItAsJson(response);
            return;
        }

        final String trimmedCoachName = coachName.trim();
        if (CoachRepository.getCoach().stream().map(Coach::getName).anyMatch(n -> n.equals(trimmedCoachName))) {
            ServiceResponse.ko("Ce coach existe déjà...", request).sentItAsJson(response);
            return;
        }

        CoachRepository.addCoach(trimmedCoachName);
        logger.info("Ajout du coach {} fait avec succès!", trimmedCoachName);
        ServiceResponse.ok("OK", request).sentItAsJson(response);
    }

}
