package com.mosioj.entrainements.utils;

import java.io.UnsupportedEncodingException;
import java.text.MessageFormat;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class EmailSender {

	private static final Logger logger = LogManager.getLogger(EmailSender.class);

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
