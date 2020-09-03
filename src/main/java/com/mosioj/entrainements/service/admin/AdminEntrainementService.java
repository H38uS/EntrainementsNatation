package com.mosioj.entrainements.service.admin;

import com.mosioj.entrainements.repositories.EntrainementRepository;
import com.mosioj.entrainements.service.AbstractService;
import com.mosioj.entrainements.service.response.ServiceResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

@WebServlet("/admin/service/entrainement")
public class AdminEntrainementService extends AbstractService {

    private static final Logger logger = LogManager.getLogger(AdminEntrainementService.class);

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws IOException {

        // Récupération de l'entrainement
        Map<String, String> parameters = getParameterMapForPutAndDelete(request);
        final String idParamValue = parameters.get("id");
        logger.info("Demande de suppression de l'entrainement {}...", idParamValue);

        // Suppression s'il existe
        ServiceResponse<?> resp;
        try {
            getIntegerFromString(idParamValue).ifPresent(EntrainementRepository::deleteIt);
            resp = ServiceResponse.ok("L'entrainement a bien été supprimé.", request);
        } catch (Exception e) {
            logger.error(e);
            resp = ServiceResponse.ko("Impossible de le supprimer...", request);
        }

        // Writing the response
        response.getOutputStream().print(resp.asJSon(response));
    }

}
