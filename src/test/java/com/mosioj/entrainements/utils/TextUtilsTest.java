package com.mosioj.entrainements.utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TextUtilsTest {

    @Test
    public void shouldAutInsertLineBreaks() {

        final String source = "300 nl dos 200 batt (50 par côté) 100 4n  \n" +
                              "300 pull (2/3/4 temps enchaîné)  \n" +
                              "200 jbs spé 100 4n\n" +
                              "\n" +
                              "8x100 plaque palmes dép 1'20\n" +
                              "3 allure moyenne 1 vite\n" +
                              "\n" +
                              "100 souple \r\n" +
                              "\n\n" +
                              "3x\n" +
                              "200 nl pull plaque \n" +
                              "100 spé éduc \n" +
                              "2x50 max dép 1'300\n" +
                              "100 souple";

        final String expected = "300 nl dos 200 batt (50 par côté) 100 4n  \n" +
                                "300 pull (2/3/4 temps enchaîné)  \n" +
                                "200 jbs spé 100 4n\n" +
                                "\n" +
                                "8x100 plaque palmes dép 1'20  \n" +
                                "3 allure moyenne 1 vite\n" +
                                "\n" +
                                "100 souple \r\n" +
                                "\n\n" +
                                "3x  \n" +
                                "200 nl pull plaque  \n" +
                                "100 spé éduc  \n" +
                                "2x50 max dép 1'300  \n" +
                                "100 souple";

        assertEquals(expected, TextUtils.prepareForMarkDown(source));
    }

}