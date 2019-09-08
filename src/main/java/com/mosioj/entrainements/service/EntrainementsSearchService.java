package com.mosioj.entrainements.service;

import java.io.IOException;
import java.util.List;

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

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		logger.info("Getting the trainings...");
		
		// Getting the training
		List<Training> trainings =  EntrainementRepository.getTrainings(2000);
		
		// Sending the response
		String jsonStr = GsonFactory.getIt().toJson(trainings);
		response.getOutputStream().print(jsonStr);
	}
}
