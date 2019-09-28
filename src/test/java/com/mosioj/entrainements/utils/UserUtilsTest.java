package com.mosioj.entrainements.utils;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class UserUtilsTest {

	@Test
	public void testEncryption() {
		
		String source = "bococobo";
		String expected = "c9c451bf1edc2d53019d3b54b4b9a3c8061691c4";
		assertEquals(expected, UserUtils.hashPwd(source, null));
		
		source = "qT##AG^FNU&GPr>O9Wv'";
		expected = "ed6a733ab2be9797aab4b54c6a9538580306468a";
		assertEquals(expected, UserUtils.hashPwd(source, null));
	}

}
