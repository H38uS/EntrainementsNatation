package com.mosioj.entrainements.service;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.Transaction;

import com.mosioj.entrainements.AbstractService;
import com.mosioj.entrainements.entities.PasswordResetRequest;
import com.mosioj.entrainements.entities.User;
import com.mosioj.entrainements.repositories.PasswordResetRequestRepositoy;
import com.mosioj.entrainements.repositories.UserRepository;
import com.mosioj.entrainements.service.response.ServiceResponse;
import com.mosioj.entrainements.utils.db.HibernateUtil;

@WebServlet("/public/service/new_mdp_from_reinit")
public class ModificationMdpService extends AbstractService {

	private static final long serialVersionUID = -3591869732984074965L;
	private static final Logger logger = LogManager.getLogger(ModificationMdpService.class);

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		try {
			// Do this so we can capture non-Latin chars
			request.setCharacterEncoding("UTF-8");
		} catch (UnsupportedEncodingException e1) {
			throw new ServletException(e1.getMessage());
		}

		Optional<Long> userIdParam = getLongFromString(request.getParameter("user_id"));
		Optional<Long> tokenParam = getLongFromString(request.getParameter("token"));
		String pwd = request.getParameter("newpassword");
		String pwd2 = request.getParameter("newpassword2");

		// Checking form parameters
		if (!userIdParam.isPresent() || !tokenParam.isPresent()) {
			String message = "Il manque des paramètres...";
			response.getOutputStream().print(new ServiceResponse(false, message, request).asJSon(response));
			return;
		}
		if (StringUtils.isBlank(pwd) || StringUtils.isBlank(pwd2)) {
			String message = "L'un des deux mots de passe est manquant...";
			response.getOutputStream().print(new ServiceResponse(false, message, request).asJSon(response));
			return;
		}
		if (pwd.length() < 8) {
			String message = "Le mot de passe doit faire au moins 8 caractères...";
			response.getOutputStream().print(new ServiceResponse(false, message, request).asJSon(response));
			return;
		}
		if (!pwd.equals(pwd2)) {
			String message = "Les deux mots de passe ne correspondent pas.";
			response.getOutputStream().print(new ServiceResponse(false, message, request).asJSon(response));
			return;
		}
		List<String> errors = new ArrayList<>();
		String hash = hashPwd(pwd, errors);
		if (!errors.isEmpty()) {
			String message = "Erreur lors du chiffrement.";
			response.getOutputStream().print(new ServiceResponse(false, message, request).asJSon(response));
			return;
		}

		// On vérifie qu'on a bien une demande qui correspond...
		Long userId = userIdParam.get();
		Long token = tokenParam.get();
		Optional<PasswordResetRequest> potential = PasswordResetRequestRepositoy.getRequest(userId, token);
		if (!potential.isPresent()) {
			String message = "Aucune demande trouvée en paramètre.";
			response.getOutputStream().print(new ServiceResponse(false, message, request).asJSon(response));
			return;
		}
		// ... Et un user
		Optional<User> pUser = UserRepository.getUser(userId);
		if (!pUser.isPresent()) {
			String message = "L'utilisateur n'existe plus...";
			response.getOutputStream().print(new ServiceResponse(false, message, request).asJSon(response));
			return;
		}

		// Real parameters
		PasswordResetRequest resetRequest = potential.get();
		User user = pUser.get();

		try {
			// Update the user password and delete the request
			HibernateUtil.doSomeWork(s -> {
				Transaction t = s.beginTransaction();
				user.setPassword(hash);
				s.update(user);
				s.delete(resetRequest);
				t.commit();
			});
		} catch (RuntimeException e) {
			e.printStackTrace();
			logger.error(e);
			String message = "Une erreur est survenue. Veuillez essayer à nouveau.";
			response.getOutputStream().print(new ServiceResponse(false, message, request).asJSon(response));
			return;
		}

		String message = "Mot de passe modifié !";
		response.getOutputStream().print(new ServiceResponse(true, message, request).asJSon(response));
	}

}
