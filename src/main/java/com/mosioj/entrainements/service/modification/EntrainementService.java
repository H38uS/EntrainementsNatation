package com.mosioj.entrainements.service.modification;

import com.mosioj.entrainements.entities.Coach;
import com.mosioj.entrainements.entities.Training;
import com.mosioj.entrainements.entities.User;
import com.mosioj.entrainements.repositories.CoachRepository;
import com.mosioj.entrainements.repositories.EntrainementRepository;
import com.mosioj.entrainements.service.AbstractService;
import com.mosioj.entrainements.service.response.ServiceResponse;
import com.mosioj.entrainements.utils.TextUtils;
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

    /** Success for new trainings. */
    private static final String OK_AJOUT = "L'entrainement a bien été ajouté.";

    /** Success for trainings modifications. */
    private static final String OK_MODIF = "L'entrainement a bien été modifié.";

    /** Failure with a potential conflict. */
    private static final String KO_POSSIBLE_EXIST = "Un entrainement existe le même jour, avec la même taille et le même entraineur. Etes-vous sûr de vouloir ajouter celui-ci ?";

    /**
     * @param parameters The map of parameter names and values for the current request.
     * @return A new training object. Can be invalid (no dates and so on).
     */
    public Training getTheTrainingFromParameters(Map<String, String> parameters) {

        String trainingText = parameters.get("training");
        Optional<Integer> sizeParam = getIntegerFromString(parameters.get("size"));
        Optional<LocalDate> date = DateUtils.getAsDate(parameters.get("trainingdate"));
        Optional<Coach> coach = CoachRepository.getCoachForName(parameters.get("coach"));
        String poolsizeParam = parameters.get("poolsize");

        Training training = new Training(trainingText, date.orElse(null)).withSize(sizeParam.orElse(-1))
                                                                         .withCoach(coach.orElse(null));
        training.setText(TextUtils.transformSmileyToCode(training.getText()));

        if (!StringUtils.isBlank(poolsizeParam)) {
            training.setIsLongCourse("long".equals(poolsizeParam));
        }
        return training;
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {

        final Map<String, String> parameters = fromRequestMapToSingleValueMap(request);
        Training training = getTheTrainingFromParameters(parameters);
        List<String> errors = checkParameter(training, true, "true".equalsIgnoreCase(parameters.get("force")));
        if (!errors.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            sb.append("Des erreurs ont été trouvées dans le formulaire. Veuilez les corriger.");
            sb.append("<ul class=\"pb-0 mb-0\">");
            for (String error : errors) {
                sb.append("<li>").append(error).append("</li>");
            }
            sb.append("</ul>");
            ServiceResponse.ko(sb.toString(), request).sentItAsJson(response);
            return;
        }

        // No errors
        training.setCreatedBy(getConnectedUser(request));
        HibernateUtil.saveit(training);
        ServiceResponse.ok(OK_AJOUT, request).sentItAsJson(response);
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws IOException {

        // Paramètres
        Map<String, String> parameters = getParameterMapForPutAndDelete(request);
        Training modifiedTraining = getTheTrainingFromParameters(parameters);
        Optional<Training> initialTraining = getLongFromString(parameters.get("id")).flatMap(EntrainementRepository::getById);

        // Vérifications
        List<String> errors = checkParameter(modifiedTraining, false, false);
        if (!initialTraining.isPresent()) {
            errors.add("L'entrainement n'existe pas.");
        }

        // Gestions des erreurs
        if (!errors.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            sb.append("Des erreurs ont été trouvées dans le formulaire. Veuilez les corriger.");
            sb.append("<ul class=\"pb-0 mb-0\">");
            for (String error : errors) {
                sb.append("<li>").append(error).append("</li>");
            }
            sb.append("</ul>");
            ServiceResponse.ko(sb.toString(), request).sentItAsJson(response);
            return;
        }

        initialTraining.ifPresent(training -> {

            // Only fields to modify
            modifiedTraining.setId(training.getId());
            modifiedTraining.setCreatedBy(training.getCreatedBy());
            modifiedTraining.setCreatedAt(training.getCreatedAt());

            User user = getConnectedUser(request);
            logger.info("Modification de l'entrainement " +
                        modifiedTraining.getId() +
                        " par " +
                        user.getEmail() +
                        "...");
            HibernateUtil.update(modifiedTraining);
            try {
                ServiceResponse.ok(OK_MODIF, request).sentItAsJson(response);
            } catch (IOException e) {
                logger.error(e);
            }
        });
    }

    /**
     * Checks the mandatory parameters. Does not modify them.
     *
     * @param training      The training to verify.
     * @param shouldBeNew   True if we are trying to add a new training.
     * @param forceExisting True if we don't check for potential duplication (== override).
     * @return The list of errors found.
     */
    protected List<String> checkParameter(Training training, boolean shouldBeNew, boolean forceExisting) {

        // Pre validation de remplissage
        List<String> errors = new ArrayList<>();
        if (StringUtils.isBlank(training.getText())) {
            errors.add("L'entrainement est vide...");
        }
        if (training.getDateSeance() == null) {
            final String message = "La date de l'entrainement est obligatoire, et doit être au format yyyy-mm-jj (année, mois et jour).";
            errors.add(message);
        }
        if (training.getSize() <= 0) {
            errors.add("La taille de l'entrainement est obligatoire et doit être un entier positif.");
        }

        // Si les champs obligatoires sont manquants ou incorrect, on arrête ici
        // Aussi le cas si on modifie un entrainement existant
        if (!shouldBeNew || !errors.isEmpty()) {
            return errors;
        }

        // For new trainings, we try to infer if it already exists...
        if (!StringUtils.isBlank(training.getText()) && EntrainementRepository.exists(training.getText())) {
            errors.add("Il semblerait que cet entrainement existe déjà...");
        } else if (!forceExisting) {

            // Vérification si une séance similaire existe pour le même jour
            // date et size existent forcément

            // 1: Si le coach est rempli
            // 2: On récupère tous les entrainements sur le même jour, avec la même taille et le même coach
            // 3: Si aucun trouvé : OK!
            // 4: Sinon, on ajoute une erreur
            training.getCoach()
                    .map(c -> EntrainementRepository.getTrainings(training.getDateSeance(), training.getSize(), c))
                    .filter(list -> !list.isEmpty())
                    .ifPresent(list -> errors.add(KO_POSSIBLE_EXIST));
        }

        return errors;
    }

}
