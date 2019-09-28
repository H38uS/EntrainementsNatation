package com.mosioj.entrainements.model;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Tries to get the size of a single block (not separated by two carriage return)
 * 
 * @author Jordan Mosio
 *
 */
public class TextBlockParser {

	private static final String START_WITH_A_NUMBER = "(\\d\\d+|\\dx)(.*\\r?\\n?)*";
	private static final String DOES_NOT_START_WITH_A_NUMBER = "\\D+(.*\\r?\\n?)*";

	private static final Logger logger = LogManager.getLogger(TextBlockParser.class);

	private final String text;
	private String remaining;

	/**
	 * Class constructor.
	 * 
	 * @param text The initial text.
	 */
	public TextBlockParser(String text) {
		this.text = text;
	}

	/**
	 * 
	 * @return The possible size of the training, based on the text.
	 */
	public int getBlockSize() {

		// Init et suppression des départs
		remaining = text.toLowerCase();
		int runningTotal = 0;

		logger.info("Starting block: " + remaining);
		while (containsANumberToParse()) {
			avoidParenthesis();
			moveToNextNumberStart();
			// Possibly, the last row is a parenthesis
			if (containsANumberToParse()) {
				runningTotal += checkAndReadSubPartIfFound(readNextNumber(Level.DEBUG), true);
			}
		}

		logger.info("Found in block: " + runningTotal);
		return runningTotal;
	}

	/**
	 * 
	 * @return True if the remaining part contains a number to parse.
	 */
	private boolean containsANumberToParse() {
		for (int i = 0; i < remaining.length(); i++) {
			char c = remaining.charAt(i);
			if (Character.isDigit(c)) {
				i++;
				if (i == remaining.length())
					return false;
				c = remaining.charAt(i);
				if (c == 'x' || Character.isDigit(c)) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Must be called just have read a number!
	 * 
	 * @param lastNumberRead The number just read.
	 * @return Either the current number if there is no subpart, or the current number times the subpart.
	 */
	private int checkAndReadSubPartIfFound(int lastNumberRead, boolean canContainDetails) {
		if (remaining.length() < 2) {
			return lastNumberRead;
		}
		char nextChar = remaining.charAt(0);
		remaining = remaining.substring(1);
		if (nextChar == 'x') {
			int total = readSubPart();
			logger.debug("SubPart with factor: " + (lastNumberRead * total));
			avoidParenthesis();
			return lastNumberRead * total;
		} else if (nextChar == 'm' && remaining.length() > 0 && remaining.charAt(0) != 'a') { // To filter out 'max'
			// On a probablement le détail derrière...
			if (containsNumberBeforeParenthesis('(')) {
				// Le bloc n'est pas forcément terminé... Il faut trouver la fin
				remaining = "";
				return (lastNumberRead % 25 == 0) ? lastNumberRead : 0;
			} else {
				// Il semblerait que le détail soit dans une parenthèse
				// On l'échappe
				avoidParenthesis();
			}
		} else {
			if (canContainDetails) {
				// Si la fin du bloc contient exactement le nombre qu'on vient de lire
				// On considère qu'il s'agit du détail
				if (checkForDetail(lastNumberRead)) {
					return lastNumberRead;
				}
			}

			// On est pas dans une sub part
			// On évite donc les parenthèses
			avoidParenthesis();
		}
		// Quand ce n'est pas un multiplier, on accepte uniquement les multiples de 25
		return lastNumberRead % 25 != 0 ? 0 : lastNumberRead;
	}

	/**
	 * 
	 * @param lastNumberRead The amount we want to check against the end of the block content.
	 * @return True if the end of the block is the detail of the last number.
	 */
	protected boolean checkForDetail(int lastNumberRead) {

		// On sauvegarde le texte restant
		String previousRemaining = new String(remaining);
		int total = 0;
		int nb = 0;
		while (containsANumberToParse()) {
			nb++;
			moveToNextNumberStart();
			total += readNextNumber(Level.TRACE);
			avoidParenthesis();
		}

		if (nb > 1 && total == lastNumberRead) {
			return true;
		}

		// Il semblerait que non...
		// On restore l'ancien reste
		remaining = previousRemaining;
		return false;
	}

	/**
	 * If there are parenthesis before the next number, skip the full parenthesis.
	 */
	private void avoidParenthesis() {
		if (remaining.contains("(") && !containsNumberBeforeParenthesis('(')) {
			remaining = remaining.substring(remaining.indexOf('('));
			if (!remaining.contains(")")) {
				logger.warn("Cannot find closing parenthesis... Remaining: " + remaining);
				return;
			}
			remaining = remaining.substring(remaining.indexOf(')'));
		}
	}

	/**
	 * Consumes char until we match the next number.
	 */
	private void moveToNextNumberStart() {
		while (!remaining.matches(START_WITH_A_NUMBER) && remaining.length() > 0) {
			logger.trace("Remaining: " + remaining);
			logger.trace("Matches: " + remaining.matches(START_WITH_A_NUMBER));
			remaining = remaining.substring(1);
		}
	}

	/**
	 * 
	 * @param level The log level.
	 * @return The next number read from remaining.
	 */
	private int readNextNumber(Level level) {
		StringBuilder sb = new StringBuilder();
		while (!remaining.matches(DOES_NOT_START_WITH_A_NUMBER) && remaining.length() > 0) {
			logger.trace("Remaining: " + remaining);
			logger.trace("Matches: " + remaining.matches(DOES_NOT_START_WITH_A_NUMBER));
			sb.append(remaining.charAt(0));
			remaining = remaining.substring(1);
		}
		if (sb.length() == 0)
			return 0;
		int factor = Integer.parseInt(sb.toString());
		logger.log(level, "Number: " + factor);
		return factor;
	}

	/**
	 * 
	 * @return True if and only if there is a number before the next parenthesis. False if there is no number.
	 */
	private boolean containsNumberBeforeParenthesis(char parenthesis) {
		for (int i = 0; i < remaining.length(); i++) {
			char c = remaining.charAt(i);
			if (c == parenthesis) {
				return false;
			}
			if (Character.isDigit(c)) {
				i++;
				if (i < remaining.length()) {
					char nextOne = remaining.charAt(i);
					if (nextOne == 'x' || Character.isDigit(nextOne)) {
						return true;
					}
				}
			}
		}
		return false;
	}

	/**
	 * 
	 * @return The size in meters of this subpart.
	 */
	private int readSubPart() {

		int runningTotal = 0;

		if (remaining.matches(START_WITH_A_NUMBER)) {
			runningTotal = readNextNumber(Level.DEBUG);
			logger.debug("Number start matched! Sub part returned: " + runningTotal);

			// Si la fin du bloc contient exactement le nombre qu'on vient de lire, on considère qu'il s'agit du détail
			// Cela va modifier le restant si besoin
			checkForDetail(runningTotal);

			return runningTotal;
		}

		// Remove spaces
		while (remaining.charAt(0) == ' ') {
			remaining = remaining.substring(1);
		}

		if (remaining.charAt(0) == '(') {
			int pos = remaining.indexOf(")");
			if (pos == -1) {
				logger.warn("Cannot find matching parenthesis... Remaining: " + remaining);
				return 0;
			}
			while (containsNumberBeforeParenthesis(')')) {
				moveToNextNumberStart();
				runningTotal += checkAndReadSubPartIfFound(readNextNumber(Level.DEBUG), false);
			}
			if (remaining.indexOf(")") > 0) {
				// Il est possible que checkAndReadSubPartIfFound ait consommé toute la chaine
				remaining = remaining.substring(remaining.indexOf(")"));
			}
			if (runningTotal > 0) {
				// Otherwise, it means the parenthesis is only some explanations
				logger.debug("Parenthesis mode... Sub part returned: " + runningTotal);
				return runningTotal;
			}
		}

		while (containsANumberToParse()) {
			moveToNextNumberStart();
			runningTotal += checkAndReadSubPartIfFound(readNextNumber(Level.DEBUG), false);
		}
		logger.debug("Sub part returned: " + runningTotal);
		return runningTotal;
	}
}
