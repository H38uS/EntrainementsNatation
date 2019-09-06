package com.mosioj.entrainements.service;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.mosioj.entrainements.model.TrainingTextParser;
import com.mosioj.entrainements.utils.GsonFactory;
import com.mosioj.entrainements.utils.ServiceResponse;

@WebServlet("/public/service/trainingsize")
public class TrainingSizeService extends HttpServlet {

	private static final long serialVersionUID = -351300476801906574L;

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		String training = request.getParameter("training");
		if (training == null || training.trim().isEmpty()) {
			String jsonStr = GsonFactory.getIt().toJson(new ServiceResponse(false, "Aucune valeur trouvée en paramètre."));
			response.getOutputStream().print(jsonStr);
			return;
		}

		TrainingTextParser parser = new TrainingTextParser(training);
		if (!parser.isTextValid()) {
			String message = "Le text contient des caractères non-supportés.";
			String jsonStr = GsonFactory.getIt().toJson(new ServiceResponse(false, message));
			response.getOutputStream().print(jsonStr);
			return;
		}

		// Sending the response
		String size = parser.getTrainingSize() + "";
		String jsonStr = GsonFactory.getIt().toJson(new ServiceResponse(true, size));
		response.getOutputStream().print(jsonStr);
		
	}
}
