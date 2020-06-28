package com.mosioj.entrainements.service.pub;

import com.mosioj.entrainements.entities.PasswordResetRequest;
import com.mosioj.entrainements.entities.User;
import com.mosioj.entrainements.repositories.PasswordResetRequestRepositoy;
import com.mosioj.entrainements.repositories.UserRepository;
import com.mosioj.entrainements.service.response.ServiceResponse;
import com.mosioj.entrainements.utils.AppProperties;
import com.mosioj.entrainements.utils.EmailSender;
import com.mosioj.entrainements.utils.db.HibernateUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

@WebServlet("/public/service/reinit_mdp")
public class ReinitialisationMotDePasseService extends HttpServlet {

    private static final Logger logger = LogManager.getLogger(ReinitialisationMotDePasseService.class);
    private static final long serialVersionUID = -7130365362562193366L;

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {

        String email = request.getParameter("email");
        List<String> errors = new ArrayList<>();
        EmailSender.checkEmailValidity(email, true, errors);

        Optional<User> targetUser = UserRepository.getUser(email);
        if (!targetUser.isPresent()) {
            errors.add("Il semblerait que l'adresse email ne corresponde à aucun compte...");
        } else {
            if (PasswordResetRequestRepositoy.existsForUser(targetUser.get())) {
                errors.add("Il existe déjà une demande valide pour cette adresse email. Veuillez vérifier vos emails et vos courriers indésirables.");
            }
        }

        if (!errors.isEmpty()) {
            response.getOutputStream().print(ServiceResponse.ko(errors.get(0), request).asJSon(response));
            return;
        }

        // End of error checking, request is valid from there

        // Building the new request
        long token = new Random().nextLong();
        long userId = targetUser.map(User::getId).orElse(-1L);
        PasswordResetRequest resetRequest = PasswordResetRequest.of(userId, token);
        HibernateUtil.saveit(resetRequest);

        // Envoie du lien
        String url = AppProperties.getAProperty("base_url") + "/reset_mdp.jsp?token=" + token + "&user_id=" + userId;
        String emailMessage = "<html>" +
                              "<body>" +
                              "Cliquez <a href=\"" + url + "\">ici</a> pour réinitialiser votre mot de passe.<br/>" +
                              "Veuillez ignorer cette email si vous n'êtes pas à l'origine de cette demande." +
                              "</body>" +
                              "</html>";
        EmailSender.sendEmail(email,
                              "Demande de réinitialisation de mot de passe - " + AppProperties.getAProperty("domain"),
                              emailMessage);

        String message = "Demande de réinitialisation du mot de passe de " + email + " créée avec succès.";
        logger.info(message);
        response.getOutputStream().print(ServiceResponse.ok(message, request).asJSon(response));
    }

}
