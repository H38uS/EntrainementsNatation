package com.mosioj.entrainements.service.secured;

import com.mosioj.entrainements.entities.SavedTraining;
import com.mosioj.entrainements.entities.Training;
import com.mosioj.entrainements.entities.User;
import com.mosioj.entrainements.repositories.EntrainementRepository;
import com.mosioj.entrainements.repositories.SavedTrainingRepository;
import com.mosioj.entrainements.service.AbstractService;
import com.mosioj.entrainements.service.response.ServiceResponse;
import com.mosioj.entrainements.utils.db.HibernateUtil;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@WebServlet("/protected/service/saved_training")
public class SavedTrainingService extends AbstractService {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        final List<Training> trainings = SavedTrainingRepository.getSavedTrainingsOf(getConnectedUser(request));
        trainings.forEach(Training::setSavedByCurrentUser);
        response.getOutputStream().print(ServiceResponse.ok(trainings, request).asJSon(response));
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        final String trainingIdParam = "trainingId";
        final String trainingParamValue = request.getParameter(trainingIdParam);
        try {
            getIntegerFromString(trainingParamValue).map(Integer::longValue)
                                                    .flatMap(EntrainementRepository::getById)
                                                    .map(t -> SavedTraining.save(getConnectedUser(request), t))
                                                    .ifPresent(HibernateUtil::saveit);
            response.getOutputStream().print(ServiceResponse.ok("OK", request).asJSon(response));
        } catch (Exception e) {
            response.getOutputStream().print(ServiceResponse.ko(e.getMessage(), request).asJSon(response));
        }
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws IOException {
        final String trainingIdParam = "trainingId";
        final String trainingParamValue = getParameterMapForPutAndDelete(request).get(trainingIdParam);
        try {
            final User connectedUser = getConnectedUser(request);
            getIntegerFromString(trainingParamValue).map(Integer::longValue)
                                                    .flatMap(EntrainementRepository::getById)
                                                    .flatMap(t -> SavedTrainingRepository.of(connectedUser, t))
                                                    .ifPresent(t -> {
                                                        t.setTraining(null); // obligatoire pour les caracteres spéciaux...
                                                        HibernateUtil.update(t);
                                                        HibernateUtil.deleteIt(t);
                                                    });
            response.getOutputStream().print(ServiceResponse.ok("OK", request).asJSon(response));
        } catch (Exception e) {
            response.getOutputStream().print(ServiceResponse.ko(e.getMessage(), request).asJSon(response));
        }
    }
}
