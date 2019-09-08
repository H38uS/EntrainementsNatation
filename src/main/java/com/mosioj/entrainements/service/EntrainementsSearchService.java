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
import com.mosioj.entrainements.utils.GsonFactory;

@WebServlet("/public/service/search")
public class EntrainementsSearchService extends HttpServlet {

	private static final long serialVersionUID = 8100248189287407082L;
	private static final Logger logger = LogManager.getLogger(EntrainementsSearchService.class);

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

		// Getting the training
		Integer min = minSizeParam.orElse(0);
		Integer max = maxSizeParam.orElse(Integer.MAX_VALUE);
		List<Training> trainings = EntrainementRepository.getTrainings(min, max);

		// Sending the response
		String jsonStr = GsonFactory.getIt().toJson(trainings);
		response.getOutputStream().print(jsonStr);
	}
}
