package com.mosioj.entrainements.utils;

import com.mosioj.entrainements.entities.UserRole;

import javax.servlet.http.HttpServletRequest;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

public class UserUtils {

    private UserUtils() {
        // Utils class
    }

    /**
     * @param pwd       The password.
     * @param pwdErrors The password errors.
     * @return The hashed password.
     */
    public static String hashPwd(String pwd, List<String> pwdErrors) {
        StringBuilder hashPwd = new StringBuilder();
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
     * @param request The http request.
     * @return True if the user is an ADMIN.
     */
    public static boolean isAdmin(HttpServletRequest request) {
        return request.isUserInRole(UserRole.ADMIN_ROLE);
    }

    /**
     * @param request The http request.
     * @return True if the user can modify content.
     */
    public static boolean canModify(HttpServletRequest request) {
        return isAdmin(request) || request.isUserInRole(UserRole.MODIFICATION_ROLE);
    }

    /**
     * @param request The http request.
     * @return True if the user is logged in.
     */
    public static boolean isLoggedIn(HttpServletRequest request) {
        return request.isUserInRole(UserRole.STANDARD_ROLE);
    }
}
