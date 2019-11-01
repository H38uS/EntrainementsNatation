package com.mosioj.entrainements;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServlet;

public abstract class AbstractService extends HttpServlet {

	private static final long serialVersionUID = 5697165385167093428L;

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
}
