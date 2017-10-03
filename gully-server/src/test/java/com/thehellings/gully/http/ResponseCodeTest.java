package com.thehellings.gully.http;

import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

public class ResponseCodeTest {
	@Test
	public void derpTest() {
		assertArrayEquals(ResponseCode.values(), ResponseCode.values());
	}

	@Test
	public void valueOfTest() {
		assertEquals(ResponseCode.ERROR_NOT_FOUND, ResponseCode.valueOf("ERROR_NOT_FOUND"));
	}
}
