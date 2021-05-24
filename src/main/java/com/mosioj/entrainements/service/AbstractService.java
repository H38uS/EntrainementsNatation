package com.mosioj.entrainements.service;

import com.mosioj.entrainements.entities.User;
import com.mosioj.entrainements.filter.LoginFilter;
import com.mosioj.entrainements.service.response.ServiceResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URLDecoder;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

public abstract class AbstractService extends HttpServlet {

    private static final long serialVersionUID = 5697165385167093428L;
    private static final Logger logger = LogManager.getLogger(AbstractService.class);

    /**
     * Internal service GET.
     *
     * @param request  The http request.
     * @param response The http response.
     * @throws Exception If any exception occurred.
     */
    protected void serviceGet(HttpServletRequest request, HttpServletResponse response) throws Exception {
        throw new ServletException("GET not implemented.");
    }

    /**
     * Internal service POST.
     *
     * @param request  The http request.
     * @param response The http response.
     * @throws Exception If any exception occurred.
     */
    protected void servicePost(HttpServletRequest request, HttpServletResponse response) throws Exception {
        throw new ServletException("POST not implemented.");
    }

    /**
     * Internal service POST.
     *
     * @param request  The http request.
     * @param response The http response.
     * @throws Exception If any exception occurred.
     */
    protected void servicePut(HttpServletRequest request, HttpServletResponse response) throws Exception {
        throw new ServletException("PUT not implemented.");
    }

    /**
     * Internal service DELETE.
     *
     * @param request  The http request.
     * @param response The http response.
     * @throws Exception If any exception occurred.
     */
    protected void serviceDelete(HttpServletRequest request, HttpServletResponse response) throws Exception {
        throw new ServletException("DELETE not implemented.");
    }

    @Override
    protected final void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            serviceGet(request, response);
        } catch (Exception e) {
            logger.error(e);
            ServiceResponse.ko("Une erreur est survenue : " + e.getMessage(), request).sentItAsJson(response);
        }
    }

    @Override
    protected final void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            servicePost(request, response);
        } catch (Exception e) {
            logger.error(e);
            ServiceResponse.ko("Une erreur est survenue : " + e.getMessage(), request).sentItAsJson(response);
        }
    }

    @Override
    protected final void doPut(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            servicePut(request, response);
        } catch (Exception e) {
            logger.error(e);
            ServiceResponse.ko("Une erreur est survenue : " + e.getMessage(), request).sentItAsJson(response);
        }
    }

    @Override
    protected final void doDelete(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            serviceDelete(request, response);
        } catch (Exception e) {
            logger.error(e);
            ServiceResponse.ko("Une erreur est survenue : " + e.getMessage(), request).sentItAsJson(response);
        }
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
