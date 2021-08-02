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

@WebServlet("/public/service/randomtraining")
public class RandomTrainingService extends AbstractService {

    private static final long serialVersionUID = -495369726257473684L;

    @Override
    protected void serviceGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

        Optional<Training> t = EntrainementRepository.getARandomTraining();
        if (!t.isPresent()) {
            String message = "Aucun entrainement n'a été trouvé...";
            ServiceResponse.ko(message, request).sentItAsJson(response);
            return;
        }

        ServiceResponse.ok(t.get(), request).sentItAsJson(response);
    }

}
