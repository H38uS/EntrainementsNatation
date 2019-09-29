package com.mosioj.entrainements.service;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.mosioj.entrainements.entities.User;
import com.mosioj.entrainements.entities.UserRole;
import com.mosioj.entrainements.repositories.UserRepository;
import com.mosioj.entrainements.utils.CaptchaHandler;
import com.mosioj.entrainements.utils.HibernateUtil;
import com.mosioj.entrainements.utils.ServiceResponse;

@WebServlet("/public/service/creation_compte")
public class CreationCompteService extends HttpServlet {

	private static final long serialVersionUID = 2140508844182372665L;
	private static final Logger logger = LogManager.getLogger(CreationCompteService.class);
	private static final String HTTP_LOCALHOST_8080 = "http://localhost:8080";
	
	public static final Pattern VALID_EMAIL_ADDRESS_REGEX = Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);


	/**
	 * 
	 * @param email The user email.
	 * @param pwd Clear password.
	 * @param urlCalled The caller. Used to exclude localhost from the checks.
	 * @param captchaResponse The Google API captcha answer.
	 * @return The list of errors for those parameters.
	 */
	private List<String> checkParameters(String email, String pwd, String urlCalled, String captchaResponse) {

		List<String> errors = new ArrayList<>();
		
		// The email
		Matcher matcher = VALID_EMAIL_ADDRESS_REGEX .matcher(email);
		if (!matcher.find()) {
			errors.add("L'adresse email ne semble pas être valide");
		} else {
			// On vérifie si l'email n'existe pas déjà...
			UserRepository.getUser(email).ifPresent(u -> errors.add("Cet email est déjà utilisé."));
		}
		
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
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		try {
			// Do this so we can capture non-Latin chars
			request.setCharacterEncoding("UTF-8");
		} catch (UnsupportedEncodingException e1) {
			throw new ServletException(e1.getMessage());
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
			response.getOutputStream().print(new ServiceResponse(false, errors).asJSon(response));
			return;
		}

		// Les paramètres sont ok, on s'occupe de la requête
		// Password hash
		User user = new User(email, hashPwd);
		HibernateUtil.saveit(user);
		HibernateUtil.saveit(UserRole.getStandardRoleFor(user));
		request.getSession().invalidate();

		// Sending the response
		response.getOutputStream().print(new ServiceResponse(true, user).asJSon(response));
	}

	/**
	 * 
	 * @param pwd
	 * @param pwdErrors
	 * @return The password hashed.
	 */
	private String hashPwd(String pwd, List<String> pwdErrors) {
		StringBuffer hashPwd = new StringBuffer();
		try {
			MessageDigest md = MessageDigest.getInstance("SHA");
			md.update(pwd.getBytes());
			byte[] digest = md.digest();
			for (byte b : digest) {
				hashPwd.append(String.format("%02x", b & 0xff));
			}
		} catch (NoSuchAlgorithmException e) {
			pwdErrors.add("Echec du chiffrement du mot de passe. Erreur: " + e.getMessage());
		}
		return hashPwd.toString();
	}

}
