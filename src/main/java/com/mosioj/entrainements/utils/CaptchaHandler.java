package com.mosioj.entrainements.utils;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.text.MessageFormat;
import java.util.stream.Collectors;

import javax.net.ssl.HttpsURLConnection;
import javax.servlet.ServletException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.Gson;

public class CaptchaHandler {

	private static final Logger logger = LogManager.getLogger(CaptchaHandler.class);
	private static String SECRET_KEY;
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

		try (BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream()))) {
			String resp = br.lines().collect(Collectors.joining(" "));
			CaptchaAnswer answer = buildAnswerFromJSon(resp);
			boolean isSuccess = answer.isSuccess();
			logger.debug("Success ? " + isSuccess);
			return isSuccess;
		} catch (IOException e) {
			logger.error("Error while reading the anwers: " + e.getMessage());
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * 
	 * @param json The raw JSON string.
	 * @return The Answer as an object.
	 */
	public static CaptchaAnswer buildAnswerFromJSon(String json) {
		Gson gson = GsonFactory.getIt();
		logger.debug("Response: " + json);
		CaptchaAnswer answer = gson.fromJson(json, CaptchaAnswer.class);
		logger.debug("Computed answer: " + answer);
		return answer;
	}

	static {
		try {
			URL = new URL("https://www.google.com/recaptcha/api/siteverify");
			SECRET_KEY = AppProperties.get().getProperty("googleCaptchaSecretKey");
		} catch (MalformedURLException e) {
			logger.error("Error while resolving the creating Captcha URL: " + e.getMessage());
			e.printStackTrace();
		}
	}

}
