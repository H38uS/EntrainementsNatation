package com.mosioj.entrainements.service.pub;

import com.mosioj.entrainements.entities.User;
import com.mosioj.entrainements.entities.UserRole;
import com.mosioj.entrainements.repositories.UserRoleRepository;
import com.mosioj.entrainements.service.AbstractService;
import com.mosioj.entrainements.service.response.ServiceResponse;
import com.mosioj.entrainements.utils.CaptchaHandler;
import com.mosioj.entrainements.utils.EmailSender;
import com.mosioj.entrainements.utils.db.HibernateUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/public/service/creation_compte")
public class CreationCompteService extends AbstractService {

    private static final long serialVersionUID = 2140508844182372665L;
    private static final Logger logger = LogManager.getLogger(CreationCompteService.class);
    private static final String HTTP_LOCALHOST_8080 = "http://localhost:8080";

    /**
     * @param email           The user email.
     * @param pwd             Clear password.
     * @param urlCalled       The caller. Used to exclude localhost from the checks.
     * @param captchaResponse The Google API captcha answer.
     * @return The list of errors for those parameters.
     */
    private List<String> checkParameters(String email, String pwd, String urlCalled, String captchaResponse) {

        List<String> errors = new ArrayList<>();
        EmailSender.checkEmailValidity(email, false, errors);

        // The password
        if (pwd.length() < 8) {
            errors.add("Le mot de passe doit faire au moins 8 caractère. Un peu de sérieux tout de même !");
        }

        // Captcha
        boolean captchaOk = urlCalled.startsWith(HTTP_LOCALHOST_8080) || CaptchaHandler.resolveIt(captchaResponse);
        if (!captchaOk) {
            errors.add("Une erreur est survenue lors de la validation du Captcha. Veuillez ré-essayer.");
        }

        return errors;
    }

    @Override
    protected void servicePost(HttpServletRequest request, HttpServletResponse response) throws IOException {

        try {
            // Do this so we can capture non-Latin chars
            request.setCharacterEncoding("UTF-8");
        } catch (UnsupportedEncodingException e1) {
            throw new IOException(e1.getMessage());
        }

        String email = request.getParameter("j_username").trim();
        String pwd = request.getParameter("j_password");
        String captchaResponse = request.getParameter("g_recaptcha_response");
        String urlCalled = request.getRequestURL().toString();

        logger.debug(captchaResponse + " / " + urlCalled);

        // Vérification des paramètres...
        List<String> errors = checkParameters(email, pwd, urlCalled, captchaResponse);
        String hashPwd = hashPwd(pwd, errors);
        if (!errors.isEmpty()) {
            ServiceResponse.ko(errors, request).sentItAsJson(response);
            return;
        }

        // Les paramètres sont ok, on s'occupe de la requête
        // Password hash
        User user = new User(email, hashPwd);
        HibernateUtil.saveit(user);
        HibernateUtil.saveit(UserRole.getStandardRoleFor(user));
        request.getSession().invalidate();

        // Notification des admins
        String subject = "Nouvelle inscription - " + email;
        String message = "Une nouvelle personne vient de s'inscrire : " + email;
        UserRoleRepository.getUserRole(UserRole.ADMIN_ROLE)
                          .forEach(u -> EmailSender.sendEmail(u.getEmail(), subject, message));

        // Sending the response
        ServiceResponse.ok(user, request).sentItAsJson(response);
    }

}
