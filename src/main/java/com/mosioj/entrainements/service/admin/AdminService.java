package com.mosioj.entrainements.service.admin;

import com.mosioj.entrainements.entities.User;
import com.mosioj.entrainements.entities.UserRole;
import com.mosioj.entrainements.repositories.UserRepository;
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
import java.util.Optional;

@WebServlet("/admin/service/admin")
public class AdminService extends AbstractService {

    private static final long serialVersionUID = -5812331498447194238L;
    private static final Logger logger = LogManager.getLogger(AdminService.class);

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

        // Getting the users
        List<User> users = UserRepository.getUsers();

        // Sending the response
        response.getOutputStream().print(ServiceResponse.ok(users, request).asJSon(response));
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {

        logger.trace("Paramère reçu : " + request.getParameter("userId"));
        Optional<Long> userIdParam = getLongFromString(request.getParameter("userId"));
        ServiceResponse<?> unknownResponse = ServiceResponse.ko( "Utilisateur inconnu", request);
        if (!userIdParam.isPresent()) {
            response.getOutputStream().print(unknownResponse.asJSon(response));
            return;
        }

        long userId = userIdParam.get();
        logger.info("Demande d'ajout du role modification pour l'identifiant " + userId);
        Optional<User> userParam = UserRepository.getUser(userId);
        if (!userParam.isPresent()) {
            response.getOutputStream().print(unknownResponse.asJSon(response));
            return;
        }

        User user = userParam.get();
        UserRole newRole = UserRole.getARoleFor(user, UserRole.MODIFICATION_ROLE);
        user.addRole(newRole);
        HibernateUtil.saveit(newRole);

        String message = UserRole.MODIFICATION_ROLE + " ajouté à " + user.getEmail();
        logger.info(message);
        response.getOutputStream().print(ServiceResponse.ok(message, request).asJSon(response));
    }


}
