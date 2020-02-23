package com.mosioj.entrainements.utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class CaptchaHandlerTest {

    @Test
    public void test() {
        String response = "{   \"success\": true,   \"challenge_ts\": \"2019-09-29T09:21:16Z\",   \"hostname\": \"entrainements-natation.fr\" }";
        CaptchaAnswer answer = CaptchaHandler.buildAnswerFromJSon(response);

        assertTrue(answer.isSuccess());
        assertNull(answer.getErrorCodes());
        assertEquals("entrainements-natation.fr", answer.getHostname());
    }

}
