package com.thehellings.gully.config;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class DefaultEnvironmentTest {
	@Test
	public void testDefaultEnvironment() {
		DefaultEnvironment defaultEnvironment = new DefaultEnvironment("My Environment");
		assertFalse("Defaults to false", defaultEnvironment.isLikeDevelopment());
		assertFalse("Defaults to false", defaultEnvironment.isLikeProduction());
		assertTrue("Identity property", defaultEnvironment.equals(defaultEnvironment));
		assertFalse("Null doesn't match", defaultEnvironment.equals(null));
		assertFalse("Names don't match", defaultEnvironment.equals(new DefaultEnvironment("other")));
		assertTrue("Names match", defaultEnvironment.equals(new DefaultEnvironment("My Environment")));
		assertEquals("Empty properties", 0, defaultEnvironment.getVariables().size());
	}
}
