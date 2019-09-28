package com.mosioj.entrainements.service.modification;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.mosioj.entrainements.entities.Coach;
import com.mosioj.entrainements.entities.Training;
import com.mosioj.entrainements.repositories.CoachRepository;
import com.mosioj.entrainements.repositories.EntrainementRepository;
import com.mosioj.entrainements.utils.DateUtils;
import com.mosioj.entrainements.utils.HibernateUtil;
import com.mosioj.entrainements.utils.ServiceResponse;

@WebServlet("/modification/service/entrainement")
public class EntrainementService extends HttpServlet {

	private static final long serialVersionUID = 4998905930020112256L;

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		// Parameters
		String trainingParam = request.getParameter("training");
		String sizeParam = request.getParameter("size");
		String dateParam = request.getParameter("trainingdate");
		String coachParam = request.getParameter("coach");
		String poolsizeParam = request.getParameter("poolsize");

		List<String> errors = checkParameter(trainingParam, dateParam, sizeParam);
		if (!errors.isEmpty()) {
			StringBuilder sb = new StringBuilder();
			sb.append("Des erreurs ont été trouvées dans le formulaire. Veuilez les corriger.");
			sb.append("<ul class=\"pb-0 mb-0\">");
			for (String error : errors) {
				sb.append("<li>").append(error).append("</li>");
			}
			sb.append("</ul>");
			response.getOutputStream().print(new ServiceResponse(false, sb.toString()).asJSon(response));
			return;
		}
		
		Optional<Coach> coach = CoachRepository.getCoachForName(coachParam);
		Optional<Date> date = DateUtils.getAsDate(dateParam);

		// Building the entity, and saving it
		Training training = new Training(trainingParam.trim(), sizeParam, date.get(), coach, poolsizeParam);
		HibernateUtil.saveit(training);

		response.getOutputStream().print(new ServiceResponse(true, "L'entrainement a bien été ajouté.").asJSon(response));
	}

	/**
	 * Checks the mandatory parameters. Does not modify them.
	 * 
	 * @param trainingParam
	 * @param dateParam
	 * @return The list of errors found.
	 */
	protected List<String> checkParameter(String trainingParam, String dateParam, String sizeParam) {
		List<String> errors = new ArrayList<String>();
		if (trainingParam == null || trainingParam.trim().isEmpty()) {
			errors.add("L'entrainement est vide...");
		}
		if (dateParam == null || dateParam.trim().isEmpty()) {
			errors.add("La date de l'entrainement est manquante");
		} else {
			if (!dateParam.matches("\\d\\d\\d\\d\\-[01]\\d\\-[0-3]\\d")) {
				errors.add("Le format de la date doit être: yyyy-mm-jj (année, mois et jour).");
			}
		}
		if (sizeParam == null || sizeParam.trim().isEmpty()) {
			errors.add("La taille de l'entrainement est obligatoire.");
		} else {
			if (!sizeParam.matches("[1-9]\\d*")) {
				errors.add("La taille de l'entrainement doit être un entier positif.");
			}
		}
		if (EntrainementRepository.exists(trainingParam)) {
			errors.add("Il semblerait que cet entrainement existe déjà...");
		}
		return errors;
	}

}
