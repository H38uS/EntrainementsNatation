package com.mosioj.entrainements.utils;

import com.mosioj.entrainements.entities.UserRole;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import javax.servlet.http.HttpServletRequest;

public class UserUtils {

    /** Password encoder. */
    private static final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    private UserUtils() {
        // Utils class
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

    /**
     * @param pwd The raw password text.
     * @return The password hashed.
     */
    public static String hashPwd(String pwd) {
        return encoder.encode(pwd);
    }
}
