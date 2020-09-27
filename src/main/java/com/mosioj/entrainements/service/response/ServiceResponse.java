package com.mosioj.entrainements.service.response;

import com.google.gson.annotations.Expose;
import com.mosioj.entrainements.utils.GsonFactory;
import com.mosioj.entrainements.utils.UserUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;

public class ServiceResponse<T> {

    private static final Logger logger = LogManager.getLogger(ServiceResponse.class);

    @Expose
    private final String status;

    @Expose
    private final T message;

    @Expose
    private final boolean isAdmin;

    @Expose
    private final boolean canModify;

    @Expose
    private final boolean isLoggedIn;

    /**
     * Class constructor.
     *
     * @param isOK    Whether this call was successful.
     * @param message The message we want to send back.
     * @param request The http request being answered.
     */
    protected ServiceResponse(boolean isOK, T message, HttpServletRequest request) {
        status = isOK ? "OK" : "KO";
        this.message = message;
        this.canModify = UserUtils.canModify(request);
        this.isAdmin = UserUtils.isAdmin(request);
        this.isLoggedIn = UserUtils.isLoggedIn(request);
    }

    /**
     * @param message The message.
     * @param request The http request.
     * @return A new response for a successful action.
     */
    public static <T> ServiceResponse<T> ok(T message, HttpServletRequest request) {
        return new ServiceResponse<>(true, message, request);
    }

    /**
     * @param message The message.
     * @param request The http request.
     * @return A new response for a failed action.
     */
    public static <T> ServiceResponse<T> ko(T message, HttpServletRequest request) {
        return new ServiceResponse<>(false, message, request);
    }

    /**
     * @return True if the last call was successful.
     */
    public boolean isOK() {
        return "OK".equals(status);
    }

    /**
     * @return the message
     */
    public T getMessage() {
        return message;
    }

    /**
     * @return The JSon representation of this response.
     */
    private String asJSon(HttpServletResponse response) {
        String content = GsonFactory.getIt().toJson(this);
        try {
            content = new String(content.getBytes(StandardCharsets.UTF_8), response.getCharacterEncoding());
        } catch (UnsupportedEncodingException e) {
            logger.error(e.getMessage());
            e.printStackTrace();
        }
        return content;
    }

    /**
     * Sends this response to the client in JSon format.
     *
     * @param response The http servlet response interface.
     */
    public void sentItAsJson(HttpServletResponse response) throws IOException {
        response.getOutputStream().print(asJSon(response));
    }

    @Override
    public String toString() {
        return "PostServiceResponse [status=" + status + ", message=" + message + "]";
    }
}
