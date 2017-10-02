package com.thehellings.gully.config;

import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class ConfigurationTest {
	@Test
	public void testConfigurationEnvironmentPassthru() {
		Configuration configuration = new Configuration(new DefaultEnvironment("Test"));
		assertTrue("Passes through to underlying", configuration.isEnvironment(new DefaultEnvironment("Test")));
	}
}
