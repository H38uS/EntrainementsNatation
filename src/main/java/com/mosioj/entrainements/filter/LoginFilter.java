package com.mosioj.entrainements.filter;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.Optional;
import java.util.TimeZone;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.mosioj.entrainements.entities.User;
import com.mosioj.entrainements.entities.UserRole;
import com.mosioj.entrainements.repositories.UserRepository;

/**
 * Initialize user information if he is logged in.
 * 
 * @author Jordan Mosio
 *
 */
@WebFilter("/*")
public class LoginFilter implements Filter {

	public static final String PARAM_CONNECTED_USER = "connected_user";

	/**
	 * Class logger.
	 */
	private static final Logger logger = LogManager.getLogger(LoginFilter.class);

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

		logger.trace("Do credential filtering...");
		TimeZone.setDefault( TimeZone.getTimeZone( "Europe/Paris" ) );

		HttpServletRequest httpServletRequest = (HttpServletRequest) request;
		HttpSession session = httpServletRequest.getSession();
		String name = httpServletRequest.getRemoteUser();
		logger.trace(MessageFormat.format("Name: {0} requesting URL: {1}", name, httpServletRequest.getRequestURL().toString()));
		if (name != null) {

			name = name.substring(0, 1).toUpperCase() + name.substring(1).toLowerCase();

			// Storing the Id if not stored yet
			User user = (User) session.getAttribute(PARAM_CONNECTED_USER);
			if (user == null) {
				Optional<User> userInDB = UserRepository.getUser(name);
				if (!userInDB.isPresent()) {
					logger.error("Impossible de trouver l'utilisateur qui vient de se connecter... => " + name);
				} else {
					// Storing the new one
					user = userInDB.get();
					session.setAttribute(PARAM_CONNECTED_USER, user);
				}
			}
			request.setAttribute(PARAM_CONNECTED_USER, user);
			request.setAttribute("IS_ADMIN", httpServletRequest.isUserInRole(UserRole.ADMIN_ROLE));
		}

		chain.doFilter(request, response);
	}

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		// Nothing to do
	}

	@Override
	public void destroy() {
		// Nothing to do
	}
}
