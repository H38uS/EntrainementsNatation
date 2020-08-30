package com.mosioj.entrainements.service.pub;

import com.mosioj.entrainements.entities.Training;
import com.mosioj.entrainements.model.SearchCriteria;
import com.mosioj.entrainements.repositories.CoachRepository;
import com.mosioj.entrainements.repositories.EntrainementRepository;
import com.mosioj.entrainements.service.AbstractService;
import com.mosioj.entrainements.service.response.EntrainementServiceResponse;
import com.mosioj.entrainements.service.response.ServiceResponse;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;

@WebServlet("/public/service/search")
public class EntrainementsSearchService extends AbstractService {

    private static final long serialVersionUID = 8100248189287407082L;
    private static final Logger logger = LogManager.getLogger(EntrainementsSearchService.class);

    /**
     * All possible columns valid in an order clause.
     */
    private static final Set<String> VALID_COLUMN = new HashSet<>(Arrays.asList("date_seance", "size", "updatedAt"));

    /**
     * @param sortText The order by clause.
     * @return True if the sort text is can be passed to the engine
     */
    public static boolean isAValidSortText(String sortText) {
        if (StringUtils.isBlank(sortText)) {
            return false;
        }
        List<String> orderedIdentifiers = Arrays.asList(sortText.split(","));
        return orderedIdentifiers.stream()
                                 // keep only the identifier which are valid
                                 .map(EntrainementsSearchService::isAValidOrderedIdentifier)
                                 // they must all be valid
                                 // false if no identifier at all
                                 .reduce((running, elem) -> running && elem).orElse(false);
    }

    /**
     * @param orderedIdenfier The ordered identifier
     * @return True if this is something of the form "column (asc|desc)?"
     */
    private static boolean isAValidOrderedIdentifier(String orderedIdenfier) {
        if (StringUtils.isBlank(orderedIdenfier)) {
            return false;
        }
        String[] parts = orderedIdenfier.trim().split(" ");
        return parts.length < 3 &&
               parts.length > 0 &&
               VALID_COLUMN.contains(parts[0]) &&
               (parts.length == 1 || "asc".equals(parts[1]) || "desc".equals(parts[1]));
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

        logger.info("Getting the trainings...");

        // Building the search criteria
        Optional<Integer> minSizeParam = getIntegerFromString(request.getParameter("minsize"));
        Optional<Integer> maxSizeParam = getIntegerFromString(request.getParameter("maxsize"));
        Optional<Integer> fromParam = getIntegerFromString(request.getParameter("from"));
        Optional<Integer> toParam = getIntegerFromString(request.getParameter("to"));
        SearchCriteria criteria = SearchCriteria.build(minSizeParam.orElse(null),
                                                       maxSizeParam.orElse(null),
                                                       fromParam.orElse(null),
                                                       toParam.orElse(null));
        CoachRepository.getCoachForName(request.getParameter("coach")).ifPresent(criteria::setCoach);
        getIntegerFromString(request.getParameter("day")).ifPresent(criteria::setDayOfWeek);

        // Tri
        String orderClause = request.getParameter("order");
        if (!isAValidSortText(orderClause)) {
            orderClause = "date_seance desc";
        }

        // Pagination
        Optional<Integer> pageParam = getIntegerFromString(request.getParameter("page"));
        Optional<Integer> limitParam = getIntegerFromString(request.getParameter("limite"));
        final int firstRow = (pageParam.orElse(1) - 1) * EntrainementRepository.MAX_RESULT;
        int limit = limitParam.orElse(EntrainementRepository.MAX_RESULT);
        if (limit > EntrainementRepository.MAX_RESULT) {
            limit = EntrainementRepository.MAX_RESULT;
        }

        // Getting the training
        List<Training> trainings = EntrainementRepository.getTrainings(criteria, orderClause, limit, firstRow);

        // Sending the response
        long totalNbOfResults = EntrainementRepository.getNbOfResults(criteria, orderClause);
        EntrainementServiceResponse resp = new EntrainementServiceResponse(trainings, totalNbOfResults, limit);
        response.getOutputStream().print(ServiceResponse.ok(resp, request).asJSon(response));
    }
}
