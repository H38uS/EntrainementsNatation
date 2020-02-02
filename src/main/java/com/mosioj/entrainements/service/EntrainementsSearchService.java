package com.mosioj.entrainements.service;

import com.mosioj.entrainements.AbstractService;
import com.mosioj.entrainements.entities.Training;
import com.mosioj.entrainements.repositories.EntrainementRepository;
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

    // TODO : dans la visu, une page recherche avancée et un accueil avec le dernier ajouté et un aléatoire
    // TODO : pouvoir filter sur une saison ou un entraineur
    // TODO : pouvoir sauvegarder les séances qui nous intéresse
    // TODO : pouvoir chercher des mots clés : genre 'palmes' ou 'plaque'

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

        Optional<Integer> minSizeParam = getIntegerFromString(request.getParameter("minsize"));
        Optional<Integer> maxSizeParam = getIntegerFromString(request.getParameter("maxsize"));
        Optional<Integer> fromParam = getIntegerFromString(request.getParameter("from"));
        Optional<Integer> toParam = getIntegerFromString(request.getParameter("to"));
        Optional<Integer> pageParam = getIntegerFromString(request.getParameter("page"));
        Optional<Integer> limitParam = getIntegerFromString(request.getParameter("limite"));

        int from = fromParam.orElse(1);
        int to = toParam.orElse(12);
        int limit = limitParam.orElse(EntrainementRepository.MAX_RESULT);
        if (limit > EntrainementRepository.MAX_RESULT) {
            limit = EntrainementRepository.MAX_RESULT;
        }

        // Quand on sélectionne un seul champs, l'autre est ignoré
        if (fromParam.isPresent() && !toParam.isPresent()) {
            to = -1;
        }
        if (!fromParam.isPresent() && toParam.isPresent()) {
            from = 13;
        }
        boolean useOrOperator = from > to;

        // Tri
        String orderClause = request.getParameter("order");
        if (!isAValidSortText(orderClause)) {
            orderClause = "date_seance desc";
        }

        // Getting the training
        int min = minSizeParam.orElse(0);
        int max = maxSizeParam.orElse(Integer.MAX_VALUE);
        List<Training> trainings = EntrainementRepository.getTrainings(min,
                                                                       max,
                                                                       from,
                                                                       to,
                                                                       useOrOperator,
                                                                       orderClause,
                                                                       limit,
                                                                       (pageParam.orElse(1) - 1) *
                                                                       EntrainementRepository.MAX_RESULT);

        // Sending the response
        long totalNbOfResults = EntrainementRepository.getNbOfResults(min, max, from, to, useOrOperator, orderClause);
        EntrainementServiceResponse resp = new EntrainementServiceResponse(trainings, totalNbOfResults, limit);
        response.getOutputStream().print(new ServiceResponse(true, resp, request).asJSon(response));
    }
}
