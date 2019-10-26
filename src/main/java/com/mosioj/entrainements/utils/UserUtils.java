package com.mosioj.entrainements.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.mosioj.entrainements.entities.UserRole;

public class UserUtils {
	
	private UserUtils() {
		// Utils class
	}

	/**
	 * 
	 * @param pwd
	 * @param pwdErrors
	 * @return The hashed password.
	 */
	public static String hashPwd(String pwd, List<String> pwdErrors) {
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
	 * @return True if the user is an ADMIN.
	 */
	public static boolean isAdmin(HttpServletRequest request) {
		return request.isUserInRole(UserRole.ADMIN_ROLE);
	}

	/**
	 * 
	 * @param request
	 * @return True if the user can modify content.
	 */
	public static boolean canModify(HttpServletRequest request) {
		return isAdmin(request) || request.isUserInRole(UserRole.MODIFICATION_ROLE);
	}
}
