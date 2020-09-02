package com.mosioj.entrainements.service;

import com.mosioj.entrainements.entities.User;
import com.mosioj.entrainements.filter.LoginFilter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
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

public abstract class AbstractService extends HttpServlet {

    private static final long serialVersionUID = 5697165385167093428L;
    private static final Logger logger = LogManager.getLogger(AbstractService.class);

    @Override
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response) throws ServletException, IOException {
        super.doGet(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response) throws ServletException, IOException {
        super.doPost(request, response);
    }

    @Override
    protected void doPut(HttpServletRequest request,
                         HttpServletResponse response) throws ServletException, IOException {
        super.doPut(request, response);
    }

    @Override
    protected void doDelete(HttpServletRequest request,
                            HttpServletResponse response) throws ServletException, IOException {
        super.doDelete(request, response);
    }

    /**
     * @param request The http request.
     * @return The connected user if any.
     */
    protected User getConnectedUser(HttpServletRequest request) {
        return (User) request.getAttribute(LoginFilter.PARAM_CONNECTED_USER);
    }

    /**
     * @param value The string value that should contain an integer.
     * @return An optional integer if the value is well formatted.
     */
    protected Optional<Integer> getIntegerFromString(String value) {
        try {
            return Optional.of(Integer.parseInt(value));
        } catch (NumberFormatException e) {
            return Optional.empty();
        }
    }

    /**
     * @param value The string value that should contain a long.
     * @return An optional integer if the value is well formatted.
     */
    protected Optional<Long> getLongFromString(String value) {
        try {
            return Optional.of(Long.parseLong(value));
        } catch (NumberFormatException e) {
            return Optional.empty();
        }
    }

    /**
     * @return The password hashed.
     */
    protected String hashPwd(String pwd, List<String> pwdErrors) {
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
     * @return The parameter map for PUT and DELETE request.
     */
    protected Map<String, String> fromRequestMapToSingleValueMap(HttpServletRequest request) {
        Map<String, String> res = new LinkedHashMap<>();
        final Map<String, String[]> initial = request.getParameterMap();
        initial.keySet().forEach(key -> {
            String[] values = initial.get(key);
            if (values != null && values.length > 0) {
                res.put(key, values[0]);
            }
        });
        return res;
    }

    /**
     * @param request The http request.
     * @return The parameter map for PUT and DELETE request.
     */
    protected Map<String, String> getParameterMapForPutAndDelete(HttpServletRequest request) {
        Map<String, String> queryPairs = new LinkedHashMap<>();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(request.getInputStream()))) {
            String query = br.readLine();
            String[] pairs = query.split("&");
            for (String pair : pairs) {
                int idx = pair.indexOf("=");
                queryPairs.put(URLDecoder.decode(pair.substring(0, idx), "UTF-8"),
                               URLDecoder.decode(pair.substring(idx + 1), "UTF-8"));
            }
            logger.info(query);
            logger.info("Service parameter pair: {}", queryPairs);
            return queryPairs;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return queryPairs;
    }
}
