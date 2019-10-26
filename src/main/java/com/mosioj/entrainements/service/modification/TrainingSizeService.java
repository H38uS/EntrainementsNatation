package com.mosioj.entrainements.service.modification;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.mosioj.entrainements.model.TrainingTextParser;
import com.mosioj.entrainements.service.response.ServiceResponse;

@WebServlet("/modification/service/trainingsize")
public class TrainingSizeService extends HttpServlet {

	private static final long serialVersionUID = -351300476801906574L;

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		String training = request.getParameter("training");
		if (training == null || training.trim().isEmpty()) {
			ServiceResponse resp = new ServiceResponse(false, "Aucune valeur trouvée en paramètre.", request);
			response.getOutputStream().print(resp.asJSon(response));
			return;
		}

		// Sending the response
		TrainingTextParser parser = new TrainingTextParser(training);
		int size = parser.getTrainingSize();
		String sizeMessage = (size < 2000 || size > 6000 || size % 50 != 0) ? size + ". Cela semble bizarre..." : size + "";
		response.getOutputStream().print(new ServiceResponse(true, sizeMessage, request).asJSon(response));
	}
}
