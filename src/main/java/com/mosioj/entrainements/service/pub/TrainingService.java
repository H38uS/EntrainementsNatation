package com.mosioj.entrainements.service.pub;

import com.mosioj.entrainements.entities.Training;
import com.mosioj.entrainements.repositories.EntrainementRepository;
import com.mosioj.entrainements.service.AbstractService;
import com.mosioj.entrainements.service.response.ServiceResponse;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;

@WebServlet("/public/service/training")
public class TrainingService extends AbstractService {

    private static final long serialVersionUID = -495369726257473684L;

    @Override
    protected void serviceGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

        Optional<Long> id = getLongFromString(request.getParameter("id"));
        if (!id.isPresent()) {
            String message = "Le paramètre est manquant.";
            ServiceResponse.ko(message, request).sentItAsJson(response);
            return;
        }

        Optional<Training> t = EntrainementRepository.getById(id.get());
        if (!t.isPresent()) {
            String message = "L'entrainement n'existe pas.";
            ServiceResponse.ko(message, request).sentItAsJson(response);
            return;
        }

        ServiceResponse.ok(t.get(), request).sentItAsJson(response);
    }

}
