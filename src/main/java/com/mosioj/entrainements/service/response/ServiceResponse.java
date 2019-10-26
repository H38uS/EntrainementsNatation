package com.mosioj.entrainements.service.response;

import java.io.UnsupportedEncodingException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.annotations.Expose;
import com.mosioj.entrainements.utils.GsonFactory;
import com.mosioj.entrainements.utils.UserUtils;

public class ServiceResponse {

	private static final Logger logger = LogManager.getLogger(ServiceResponse.class);

	@Expose
	private final String status;

	@Expose
	private final Object message;

	@Expose
	private final boolean isAdmin;

	@Expose
	private final boolean canModify;

	/**
	 * Class constructor.
	 * 
	 * @param isOK
	 * @param message
	 * @param request The http request being answered.
	 */
	public ServiceResponse(boolean isOK, Object message, HttpServletRequest request) {
		status = isOK ? "OK" : "KO";
		this.message = message;
		this.canModify = UserUtils.canModify(request);
		this.isAdmin = UserUtils.isAdmin(request);
	}

	/**
	 * @return the status
	 */
	public String getStatus() {
		return status;
	}

	/**
	 * @return the message
	 */
	public Object getMessage() {
		return message;
	}

	/**
	 * 
	 * @param response
	 * @return The JSon representation of this response.
	 */
	public String asJSon(HttpServletResponse response) {
		String content = GsonFactory.getIt().toJson(this);
		try {
			content = new String(content.getBytes("UTF-8"), response.getCharacterEncoding());
		} catch (UnsupportedEncodingException e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}
		return content;
	}

	@Override
	public String toString() {
		return "PostServiceResponse [status=" + status + ", message=" + message + "]";
	}
}
