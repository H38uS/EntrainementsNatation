package com.mosioj.entrainements.service.pub;

import com.mosioj.entrainements.entities.User;
import com.mosioj.entrainements.repositories.PasswordResetRequestRepositoy;
import com.mosioj.entrainements.repositories.UserRepository;
import com.mosioj.entrainements.service.AbstractService;
import com.mosioj.entrainements.service.response.ServiceResponse;
import com.mosioj.entrainements.utils.UserUtils;
import com.mosioj.entrainements.utils.db.HibernateUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.Transaction;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Optional;

@WebServlet("/public/service/new_mdp_from_reinit")
public class ModificationMdpService extends AbstractService {

    private static final long serialVersionUID = -3591869732984074965L;
    private static final Logger logger = LogManager.getLogger(ModificationMdpService.class);

    @Override
    protected void servicePost(HttpServletRequest request, HttpServletResponse response) throws IOException {

        try {
            // Do this so we can capture non-Latin chars
            request.setCharacterEncoding("UTF-8");
        } catch (UnsupportedEncodingException e1) {
            throw new IOException(e1.getMessage());
        }

        Optional<Long> userIdParam = getLongFromString(request.getParameter("user_id"));
        Optional<Long> tokenParam = getLongFromString(request.getParameter("token"));
        String pwd = request.getParameter("newpassword");
        String pwd2 = request.getParameter("newpassword2");

        // Checking form parameters
        if (!userIdParam.isPresent() || !tokenParam.isPresent()) {
            String message = "Il manque des paramètres...";
            ServiceResponse.ko(message, request).sentItAsJson(response);
            return;
        }
        if (StringUtils.isBlank(pwd) || StringUtils.isBlank(pwd2)) {
            String message = "L'un des deux mots de passe est manquant...";
            ServiceResponse.ko(message, request).sentItAsJson(response);
            return;
        }
        if (pwd.length() < 8) {
            String message = "Le mot de passe doit faire au moins 8 caractères...";
            ServiceResponse.ko(message, request).sentItAsJson(response);
            return;
        }
        if (!pwd.equals(pwd2)) {
            String message = "Les deux mots de passe ne correspondent pas.";
            ServiceResponse.ko(message, request).sentItAsJson(response);
            return;
        }

        // Creation du mot de passe
        String hash = UserUtils.hashPwd(pwd);

        // On vérifie qu'on a bien une demande qui correspond...
        long userId = userIdParam.get();
        long token = tokenParam.get();
        if (!PasswordResetRequestRepositoy.exists(userId, token)) {
            String message = "Aucune demande trouvée en paramètre.";
            ServiceResponse.ko(message, request).sentItAsJson(response);
            return;
        }
        // ... Et un user
        Optional<User> pUser = UserRepository.getUser(userId);
        if (!pUser.isPresent()) {
            String message = "L'utilisateur n'existe plus...";
            ServiceResponse.ko(message, request).sentItAsJson(response);
            return;
        }

        // Real parameters
        User user = pUser.get();

        try {
            // Update the user password and delete the request
            HibernateUtil.doSomeWork(s -> {
                Transaction t = s.beginTransaction();
                user.setPassword(hash);
                s.update(user);
                PasswordResetRequestRepositoy.getRequest(s, token, userId).ifPresent(s::delete);
                t.commit();
            });
        } catch (RuntimeException e) {
            e.printStackTrace();
            logger.error(e);
            String message = "Une erreur est survenue. Veuillez essayer à nouveau.";
            ServiceResponse.ko(message, request).sentItAsJson(response);
            return;
        }

        String message = "Mot de passe modifié !";
        ServiceResponse.ok(message, request).sentItAsJson(response);
    }

}
