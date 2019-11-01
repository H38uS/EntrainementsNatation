package com.mosioj.entrainements.service;

import java.io.IOException;
import java.util.Optional;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.mosioj.entrainements.AbstractService;
import com.mosioj.entrainements.entities.Training;
import com.mosioj.entrainements.repositories.EntrainementRepository;
import com.mosioj.entrainements.service.response.ServiceResponse;

@WebServlet("/public/service/training")
public class TrainingService extends AbstractService {

	private static final long serialVersionUID = -495369726257473684L;

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		Optional<Long> id = getLongFromString(request.getParameter("id"));
		if (!id.isPresent()) {
			String message = "Le paramètre est manquant.";
			response.getOutputStream().print(new ServiceResponse(false, message, request).asJSon(response));
			return;
		}

		Optional<Training> t = EntrainementRepository.getById(id.get());
		if (!t.isPresent()) {
			String message = "L'entrainement n'existe pas.";
			response.getOutputStream().print(new ServiceResponse(false, message, request).asJSon(response));
			return;
		}

		response.getOutputStream().print(new ServiceResponse(true, t.get(), request).asJSon(response));
	}

}
