package com.mosioj.entrainements.utils;

import java.io.UnsupportedEncodingException;
import java.text.MessageFormat;
import java.util.List;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.mosioj.entrainements.repositories.UserRepository;

public class EmailSender {

	private static final Logger logger = LogManager.getLogger(EmailSender.class);

	public static final Pattern VALID_EMAIL_ADDRESS_REGEX = Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$",
																			Pattern.CASE_INSENSITIVE);

	/**
	 * 
	 * @param email The email to check.
	 * @param shouldExist If it should raise an error if it is missing (true) or if it is already there (false)
	 * @param errors Appends an error if anything found.
	 */
	public static void checkEmailValidity(String email, boolean shouldExist, List<String> errors) {
		// The email
		Matcher matcher = VALID_EMAIL_ADDRESS_REGEX.matcher(email);
		if (!matcher.find()) {
			errors.add("L'adresse email ne semble pas être valide");
		} else {
			if (UserRepository.getUser(email).isPresent()) {
				if (!shouldExist) {
					errors.add("Cet email est déjà utilisé.");
				}
			} else {
				if (shouldExist) {
					errors.add("Cet email n'existe pas...");
				}
			}
		}
	}

	/**
	 * Sends out an email.
	 * 
	 * @param to The email address where to send the email.
	 * @param subject The email subject.
	 * @param htmlText The email body, html formated.
	 */
	public static void sendEmail(String to, String subject, String htmlText) {

		logger.info(MessageFormat.format("Sending email to {0}...", to));
		Properties properties = System.getProperties();
		properties.setProperty("mail.smtp.host", AppProperties.get().getProperty("host"));
		Session session = Session.getDefaultInstance(properties);

		try {
			MimeMessage message = new MimeMessage(session);

			message.setFrom(new InternetAddress(AppProperties.get().getProperty("from"), "Entrainements Natation"));
			message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
			message.setSubject(subject);
			message.setContent(htmlText, "text/html; charset=UTF-8");

			Transport.send(message);
			logger.info("Sent message successfully....");

		} catch (MessagingException | UnsupportedEncodingException mex) {
			mex.printStackTrace();
			logger.error(mex.getMessage());
		}
	}

}
