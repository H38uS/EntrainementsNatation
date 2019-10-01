package com.mosioj.entrainements.utils;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.text.MessageFormat;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;
import javax.servlet.ServletException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.Gson;

public class CaptchaHandler {

	private static final Logger logger = LogManager.getLogger(CaptchaHandler.class);
	private static final String SECRET_KEY = "";
	private static URL URL = null;;

	/**
	 * 
	 * @param captchaResponse
	 * @return True if it passed the validation, false otherwise
	 * @throws ServletException
	 */
	public static boolean resolveIt(String captchaResponse) {

		if (URL == null) {
			return false;
		}

		HttpsURLConnection con = null;
		try {
			con = (HttpsURLConnection) URL.openConnection();
		} catch (IOException e) {
			logger.error("Error while opening the URL: " + e.getMessage());
			e.printStackTrace();
			return false;
		}

		// add request header
		try {
			con.setRequestMethod("POST");
			con.setDoOutput(true);
		} catch (ProtocolException e) {
			logger.error("Error while setting the protocol: " + e.getMessage());
			e.printStackTrace();
			return false;
		}

		String urlParameters = MessageFormat.format("secret={0}&response={1}", SECRET_KEY, captchaResponse);

		// Send post request
		try {
			DataOutputStream wr = new DataOutputStream(con.getOutputStream());
			wr.writeBytes(urlParameters);
			wr.flush();
			wr.close();
		} catch (IOException e) {
			logger.error("Error while writting parameters: " + e.getMessage());
			e.printStackTrace();
			return false;
		}

		try {
			con.getResponseCode();
		} catch (IOException e) {
			logger.error("Error while processing the request: " + e.getMessage());
			e.printStackTrace();
			return false;
		}

		try {
			Gson gson = GsonFactory.getIt();
			CaptchaAnswer answer = gson.fromJson(new InputStreamReader(con.getInputStream()), CaptchaAnswer.class);
			boolean isSuccess = answer.success;
			logger.debug("Success ? " + isSuccess);
			return isSuccess;
		} catch (IOException e) {
			logger.error("Error while reading the anwers: " + e.getMessage());
			e.printStackTrace();
			return false;
		}
	}

	static {
		try {
			URL = new URL("https://www.google.com/recaptcha/api/siteverify");
		} catch (MalformedURLException e) {
			logger.error("Error while resolving the creating Captcha URL: " + e.getMessage());
			e.printStackTrace();
		}
	}

	private class CaptchaAnswer {

		private boolean success;
		private String challenge_ts;
		private String hostname;
		private List<String> errorCodes;

		/**
		 * @return the success
		 */
		@SuppressWarnings("unused")
		public boolean isSuccess() {
			return success;
		}

		/**
		 * @param success the success to set
		 */
		@SuppressWarnings("unused")
		public void setSuccess(boolean success) {
			this.success = success;
		}

		/**
		 * @return the challenge_ts
		 */
		@SuppressWarnings("unused")
		public String getChallenge_ts() {
			return challenge_ts;
		}

		/**
		 * @param challenge_ts the challenge_ts to set
		 */
		@SuppressWarnings("unused")
		public void setChallenge_ts(String challenge_ts) {
			this.challenge_ts = challenge_ts;
		}

		/**
		 * @return the hostname
		 */
		@SuppressWarnings("unused")
		public String getHostname() {
			return hostname;
		}

		/**
		 * @param hostname the hostname to set
		 */
		@SuppressWarnings("unused")
		public void setHostname(String hostname) {
			this.hostname = hostname;
		}

		/**
		 * @return the errorCodes
		 */
		@SuppressWarnings("unused")
		public List<String> getErrorCodes() {
			return errorCodes;
		}

		/**
		 * @param errorCodes the errorCodes to set
		 */
		@SuppressWarnings("unused")
		public void setErrorCodes(List<String> errorCodes) {
			this.errorCodes = errorCodes;
		}
	}
}
