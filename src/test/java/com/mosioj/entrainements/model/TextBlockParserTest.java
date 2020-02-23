package com.mosioj.entrainements.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TextBlockParserTest {

	@Test
	public void testDetail() {
		String text = "400 nl pull\r\n"
						+ "25 vite 75 ampli\r\n"
						+ "25 ampli 25 vite 50 ampli\r\n"
						+ "50 ampli 25 vite 25 ampli\r\n"
						+ "75 ampli 25 vite";
		TextBlockParser parser = new TextBlockParser(text);
		assertEquals(400, parser.getBlockSize());
	}

	@Test
	public void testBlock() {
		String text = "2x 100 nl 100 dos 100 nl 50 br 100 nl 50 pap (25 éduc 25 nc)";
		TextBlockParser parser = new TextBlockParser(text);
		assertEquals(1000, parser.getBlockSize());
	}

}
