package com.mosioj.entrainements.service.modification;

import com.mosioj.entrainements.AbstractService;
import com.mosioj.entrainements.entities.Coach;
import com.mosioj.entrainements.entities.Training;
import com.mosioj.entrainements.entities.User;
import com.mosioj.entrainements.filter.LoginFilter;
import com.mosioj.entrainements.repositories.CoachRepository;
import com.mosioj.entrainements.repositories.EntrainementRepository;
import com.mosioj.entrainements.service.response.ServiceResponse;
import com.mosioj.entrainements.utils.date.DateUtils;
import com.mosioj.entrainements.utils.db.HibernateUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@WebServlet("/modification/service/entrainement")
public class EntrainementService extends AbstractService {

    private static final long serialVersionUID = 4998905930020112256L;
    private static final Logger logger = LogManager.getLogger(EntrainementService.class);

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {

        // Parameters
        String trainingParam = request.getParameter("training");
        Optional<Integer> sizeParam = getIntegerFromString(request.getParameter("size"));
        String dateParam = request.getParameter("trainingdate");
        String coachParam = request.getParameter("coach");
        String poolsizeParam = request.getParameter("poolsize");

        List<String> errors = checkParameter(trainingParam, dateParam, sizeParam.orElse(null), true);
        if (!errors.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            sb.append("Des erreurs ont été trouvées dans le formulaire. Veuilez les corriger.");
            sb.append("<ul class=\"pb-0 mb-0\">");
            for (String error : errors) {
                sb.append("<li>").append(error).append("</li>");
            }
            sb.append("</ul>");
            response.getOutputStream().print(new ServiceResponse(false, sb.toString(), request).asJSon(response));
            return;
        }

        Optional<LocalDate> date = DateUtils.getAsDate(dateParam);
        date.map(d -> new Training(trainingParam.trim(), d)).ifPresent(training -> {
            // Building the entity, and saving it
            sizeParam.ifPresent(training::setSize);
            CoachRepository.getCoachForName(coachParam).ifPresent(training::setCoach);
            if (!StringUtils.isBlank(poolsizeParam)) {
                training.setIsLongCourse("long".equals(poolsizeParam));
            }
            training.setCreatedBy((User) request.getAttribute(LoginFilter.PARAM_CONNECTED_USER));
            HibernateUtil.saveit(training);
        });

        response.getOutputStream()
                .print(new ServiceResponse(true, "L'entrainement a bien été ajouté.", request).asJSon(response));
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws IOException {

        // Parameters
        Map<String, String> parameters = getParameterMapForPutAndDelete(request);
        Optional<Long> idParam = getLongFromString(parameters.get("id"));
        String trainingParam = parameters.get("training");
        Optional<Integer> sizeParam = getIntegerFromString(parameters.get("size"));
        String dateParam = parameters.get("trainingdate");
        String coachParam = parameters.get("coach");
        String poolsizeParam = parameters.get("poolsize");

        Optional<Training> potentialTraining = Optional.empty();
        List<String> errors = checkParameter(trainingParam, dateParam, sizeParam.orElse(null), false);
        if (!idParam.isPresent()) {
            errors.add("L'entrainement n'existe pas.");
        } else {
            potentialTraining = EntrainementRepository.getById(idParam.get());
            if (!potentialTraining.isPresent()) {
                errors.add("L'entrainement n'existe pas.");
            }
        }

        if (!errors.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            sb.append("Des erreurs ont été trouvées dans le formulaire. Veuilez les corriger.");
            sb.append("<ul class=\"pb-0 mb-0\">");
            for (String error : errors) {
                sb.append("<li>").append(error).append("</li>");
            }
            sb.append("</ul>");
            response.getOutputStream().print(new ServiceResponse(false, sb.toString(), request).asJSon(response));
            return;
        }

        Optional<Coach> coach = CoachRepository.getCoachForName(coachParam);
        Optional<LocalDate> date = DateUtils.getAsDate(dateParam);
        potentialTraining.ifPresent(training -> {
            User user = (User) request.getAttribute(LoginFilter.PARAM_CONNECTED_USER);
            logger.info("Modification de l'entrainement " + training.getId() + " par " + user.getEmail() + "...");
            training.setText(trainingParam.trim());
            training.setSize(sizeParam.orElse(0));
            date.ifPresent(training::setDateSeance);
            coach.ifPresent(training::setCoach);
            training.setIsLongCourse("long".equals(poolsizeParam));
            training.setIsCourseSizeDefinedForSure(!StringUtils.isBlank(poolsizeParam));
            HibernateUtil.update(training);
            try {
                response.getOutputStream()
                        .print(new ServiceResponse(true,
                                                   "L'entrainement a bien été modifié.",
                                                   request).asJSon(response));
            } catch (IOException e) {
                e.printStackTrace();
                logger.error(e);
            }
        });
    }

    /**
     * Checks the mandatory parameters. Does not modify them.
     *
     * @param trainingParam The training text.
     * @param dateParam     The training occurrence.
     * @param shouldBeNew   True if we are trying to add a new training.
     * @return The list of errors found.
     */
    protected List<String> checkParameter(String trainingParam, String dateParam, Integer sizeParam, boolean shouldBeNew) {
        List<String> errors = new ArrayList<>();
        if (StringUtils.isBlank(trainingParam)) {
            errors.add("L'entrainement est vide...");
        }
        if (StringUtils.isBlank(dateParam)) {
            errors.add("La date de l'entrainement est manquante");
        } else {
            if (!dateParam.matches("\\d\\d\\d\\d-[01]\\d-[0-3]\\d")) {
                errors.add("Le format de la date doit être: yyyy-mm-jj (année, mois et jour).");
            }
        }
        if (sizeParam == null) {
            errors.add("La taille de l'entrainement est obligatoire.");
        } else {
            if (sizeParam <= 0) {
                errors.add("La taille de l'entrainement doit être un entier positif.");
            }
        }
        if (shouldBeNew && !StringUtils.isBlank(trainingParam) && EntrainementRepository.exists(trainingParam)) {
            errors.add("Il semblerait que cet entrainement existe déjà...");
        }
        return errors;
    }

}
