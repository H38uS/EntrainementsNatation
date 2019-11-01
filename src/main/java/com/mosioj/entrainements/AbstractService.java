package com.mosioj.entrainements;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URLDecoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class AbstractService extends HttpServlet {

	private static final long serialVersionUID = 5697165385167093428L;
	private static final Logger logger = LogManager.getLogger(AbstractService.class);

	/**
	 * 
	 * @param value
	 * @return An optional integer if the value is well formatted.
	 */
	protected Optional<Integer> getIntegerFromString(String value) {
		try {
			return Optional.ofNullable(Integer.parseInt(value));
		} catch (NumberFormatException e) {
			return Optional.empty();
		}
	}

	/**
	 * 
	 * @param value
	 * @return An optional integer if the value is well formatted.
	 */
	protected Optional<Long> getLongFromString(String value) {
		try {
			return Optional.ofNullable(Long.parseLong(value));
		} catch (NumberFormatException e) {
			return Optional.empty();
		}
	}

	/**
	 * 
	 * @param pwd
	 * @param pwdErrors
	 * @return The password hashed.
	 */
	protected String hashPwd(String pwd, List<String> pwdErrors) {
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

	/**
	 * 
	 * @param request
	 * @return The parameter map for PUT and DELETE request.
	 */
	protected Map<String, String> getParameterMapForPutAndDelete(HttpServletRequest request) {
		Map<String, String> queryPairs = new LinkedHashMap<String, String>();
		try (BufferedReader br = new BufferedReader(new InputStreamReader(request.getInputStream()))) {
			String query = br.readLine();
			String[] pairs = query.split("&");
			for (String pair : pairs) {
				int idx = pair.indexOf("=");
				queryPairs.put(URLDecoder.decode(pair.substring(0, idx), "UTF-8"),
								URLDecoder.decode(pair.substring(idx + 1), "UTF-8"));
			}
			logger.info(query);
			logger.info(queryPairs);
			return queryPairs;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return queryPairs;
	}
}
