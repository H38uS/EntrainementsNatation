package com.mosioj.entrainements.service.secured;

import com.mosioj.entrainements.entities.SavedTraining;
import com.mosioj.entrainements.entities.Training;
import com.mosioj.entrainements.entities.User;
import com.mosioj.entrainements.repositories.EntrainementRepository;
import com.mosioj.entrainements.repositories.SavedTrainingRepository;
import com.mosioj.entrainements.service.AbstractService;
import com.mosioj.entrainements.service.response.ServiceResponse;
import com.mosioj.entrainements.utils.db.HibernateUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@WebServlet("/protected/service/saved_training")
public class SavedTrainingService extends AbstractService {

    private static final Logger logger = LogManager.getLogger(SavedTrainingService.class);

    @Override
    protected void serviceGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        final List<Training> trainings = SavedTrainingRepository.getSavedTrainingsOf(getConnectedUser(request));
        trainings.forEach(Training::setSavedByCurrentUser);
        ServiceResponse.ok(trainings, request).sentItAsJson(response);
    }

    @Override
    protected void servicePost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        final String trainingIdParam = "trainingId";
        final String trainingParamValue = request.getParameter(trainingIdParam);
        try {
            getIntegerFromString(trainingParamValue).map(Integer::longValue)
                                                    .flatMap(EntrainementRepository::getById)
                                                    .map(t -> SavedTraining.of(getConnectedUser(request), t))
                                                    .ifPresent(HibernateUtil::saveit);
            ServiceResponse.ok("OK", request).sentItAsJson(response);
        } catch (Exception e) {
            ServiceResponse.ko(e.getMessage(), request).sentItAsJson(response);
        }
    }

    @Override
    protected void serviceDelete(HttpServletRequest request, HttpServletResponse response) throws IOException {
        final String trainingIdParam = "trainingId";
        final String trainingParamValue = getParameterMapForPutAndDelete(request).get(trainingIdParam);
        try {
            final User connectedUser = getConnectedUser(request);
            getIntegerFromString(trainingParamValue).map(Integer::longValue)
                                                    .flatMap(EntrainementRepository::getById)
                                                    .ifPresent(t -> SavedTrainingRepository.delete(connectedUser, t));
            ServiceResponse.ok("OK", request).sentItAsJson(response);
        } catch (Exception e) {
            logger.error(e);
            ServiceResponse.ko(e.getMessage(), request).sentItAsJson(response);
        }
    }
}
