package com.mosioj.entrainements.utils;

import static org.junit.Assert.*;

import org.junit.Test;

public class CaptchaHandlerTest {

	@Test
	public void test() {
		String response = "{   \"success\": true,   \"challenge_ts\": \"2019-09-29T09:21:16Z\",   \"hostname\": \"entrainements-natation.fr\" }";
		CaptchaAnswer answer = CaptchaHandler.buildAnswerFromJSon(response);

		assertEquals(true, answer.isSuccess());
		assertNull(answer.getErrorCodes());
		assertEquals("entrainements-natation.fr", answer.getHostname());
	}

}
