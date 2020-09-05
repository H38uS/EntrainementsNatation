package com.mosioj.entrainements.service.secured;

import com.mosioj.entrainements.model.SearchCriteria;
import com.mosioj.entrainements.repositories.CoachRepository;
import com.mosioj.entrainements.repositories.SearchCriteriaRepository;
import com.mosioj.entrainements.service.AbstractService;
import com.mosioj.entrainements.service.response.ServiceResponse;
import com.mosioj.entrainements.utils.db.HibernateUtil;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/protected/service/search_criteria")
public class SearchCriteriaService extends AbstractService {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        SearchCriteria criteria = SearchCriteriaRepository.of(getConnectedUser(request)).orElse(null);
        response.getOutputStream().print(ServiceResponse.ok(criteria, request).asJSon(response));
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

        // Saving it
        HibernateUtil.saveit(criteria);

        // Response
        response.getOutputStream().print(ServiceResponse.ok("OK", request).asJSon(response));
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws IOException {
        SearchCriteriaRepository.deleteTheOneOf(getConnectedUser(request));
        response.getOutputStream().print(ServiceResponse.ok("", request).asJSon(response));
    }
}
