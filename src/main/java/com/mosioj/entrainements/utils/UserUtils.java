package com.mosioj.entrainements.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

public class UserUtils {

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
}
