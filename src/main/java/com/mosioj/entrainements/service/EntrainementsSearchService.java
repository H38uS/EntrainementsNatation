package com.mosioj.entrainements.service;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.mosioj.entrainements.entities.Training;
import com.mosioj.entrainements.repositories.EntrainementRepository;
import com.mosioj.entrainements.service.response.EntrainementServiceResponse;
import com.mosioj.entrainements.utils.ServiceResponse;

@WebServlet("/public/service/search")
public class EntrainementsSearchService extends HttpServlet {

	private static final long serialVersionUID = 8100248189287407082L;
	private static final Logger logger = LogManager.getLogger(EntrainementsSearchService.class);

	// TODO : dans la visu, une page recherche avancée et un accueil avec le dernier ajouté et un aléatoire
	// TODO : pouvoir filter sur une saison
	// TODO : pouvoir modifier/supprimer les entrainements
	// TODO : pouvoir sauvegarder les séances qui nous intéresse
	// TODO : pouvoir chercher des mots clés : genre 'palmes' ou 'plaque'

	// TODO : voir si on peut faire deux colonnes pour avoir des tuiles ? Et pas de trous

	/**
	 * 
	 * @param value
	 * @return An optional integer if the value is well formatted.
	 */
	private Optional<Integer> getIntegerFromString(String value) {
		try {
			return Optional.ofNullable(Integer.parseInt(value));
		} catch (NumberFormatException e) {
			return Optional.empty();
		}
	}

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		logger.info("Getting the trainings...");

		Optional<Integer> minSizeParam = getIntegerFromString(request.getParameter("minsize"));
		Optional<Integer> maxSizeParam = getIntegerFromString(request.getParameter("maxsize"));
		Optional<Integer> fromParam = getIntegerFromString(request.getParameter("from"));
		Optional<Integer> toParam = getIntegerFromString(request.getParameter("to"));
		Optional<Integer> pageParam = getIntegerFromString(request.getParameter("page"));

		int from = fromParam.orElse(1);
		Integer to = toParam.orElse(12);

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
		if (orderClause == null || orderClause.trim().isEmpty()) {
			orderClause = "date_seance desc";
		} else {
			String[] split = orderClause.split(" ");
			if (split.length != 2) {
				orderClause = "date_seance desc";
			} else {
				String column = split[0];
				String way = split[1];
				if ((!"asc".equals(way) && !"desc".equals(way)) || (!"date_seance".equals(column) && !"size".equals(column))) {
					orderClause = "date_seance desc";
				}
			}
		}

		// Getting the training
		Integer min = minSizeParam.orElse(0);
		Integer max = maxSizeParam.orElse(Integer.MAX_VALUE);
		List<Training> trainings = EntrainementRepository.getTrainings(	min,
																		max,
																		from,
																		to,
																		useOrOperator,
																		orderClause,
																		(pageParam.orElse(1) - 1) * EntrainementRepository.MAX_RESULT);


		// Sending the response
		long totalNbOfResults = EntrainementRepository.getNbOfResults(min, max, from, to, useOrOperator, orderClause);
		EntrainementServiceResponse resp = new EntrainementServiceResponse(trainings, totalNbOfResults);
		response.getOutputStream().print(new ServiceResponse(true, resp).asJSon(response));
	}
}
