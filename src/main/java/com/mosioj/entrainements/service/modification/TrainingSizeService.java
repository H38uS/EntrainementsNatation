package com.mosioj.entrainements.service.modification;

import com.mosioj.entrainements.model.TrainingTextParser;
import com.mosioj.entrainements.service.response.ServiceResponse;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/modification/service/trainingsize")
public class TrainingSizeService extends HttpServlet {

    private static final long serialVersionUID = -351300476801906574L;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

        String training = request.getParameter("training");
        if (training == null || training.trim().isEmpty()) {
            ServiceResponse.ko("Aucune valeur trouvée en paramètre.", request).sentItAsJson(response);
            return;
        }

        // Sending the response
        TrainingTextParser parser = new TrainingTextParser(training);
        int size = parser.getTrainingSize();
        String sizeMessage = (size < 2000 || size > 6000 || size % 50 != 0) ? size + ". Cela semble bizarre..." : size +
                                                                                                                  "";
        ServiceResponse.ok(sizeMessage, request).sentItAsJson(response);
    }
}
