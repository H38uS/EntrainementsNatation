package com.mosioj.entrainements.service.secured;

import com.mosioj.entrainements.entities.SearchCriteria;
import com.mosioj.entrainements.entities.User;
import com.mosioj.entrainements.repositories.CoachRepository;
import com.mosioj.entrainements.repositories.SearchCriteriaRepository;
import com.mosioj.entrainements.service.AbstractService;
import com.mosioj.entrainements.service.response.ServiceResponse;
import com.mosioj.entrainements.utils.db.HibernateUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.Transaction;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/protected/service/search_criteria")
public class SearchCriteriaService extends AbstractService {

    /** Class logger */
    private static final Logger logger = LogManager.getLogger(SearchCriteriaService.class);

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        SearchCriteria criteria = SearchCriteriaRepository.of(getConnectedUser(request)).orElse(null);
        ServiceResponse.ok(criteria, request).sentItAsJson(response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {

        // Parameters
        Integer min = getIntegerFromString(request.getParameter("minsize")).orElse(null);
        Integer max = getIntegerFromString(request.getParameter("maxsize")).orElse(null);
        Integer from = getIntegerFromString(request.getParameter("from")).orElse(null);
        Integer to = getIntegerFromString(request.getParameter("to")).orElse(null);
        SearchCriteria criteria = SearchCriteria.build(min, max, from, to);
        CoachRepository.getCoachForName(request.getParameter("coach")).ifPresent(criteria::setCoach);
        getIntegerFromString(request.getParameter("day")).ifPresent(criteria::setDayOfWeek);

        User connectedUser = getConnectedUser(request);
        criteria.setUser(connectedUser);

        // Saving it
        logger.info("Sauvegarder des critères de recherche {} de {}.", criteria, connectedUser);
        HibernateUtil.doSomeWork(s -> {
            Transaction t = s.beginTransaction();
            s.saveOrUpdate(SearchCriteriaRepository.of(connectedUser, s).map(c -> c.merge(criteria)).orElse(criteria));
            t.commit();
        });

        // Response
        ServiceResponse.ok("OK", request).sentItAsJson(response);
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws IOException {
        SearchCriteriaRepository.deleteTheOneOf(getConnectedUser(request));
        ServiceResponse.ok("", request).sentItAsJson(response);
    }
}
