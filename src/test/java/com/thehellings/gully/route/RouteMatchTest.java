package com.thehellings.gully.route;

import org.junit.Test;

import java.util.HashMap;

import static org.junit.Assert.assertNull;

public class RouteMatchTest {
	@Test
	public void testDefaultConstructors() {
		RouteMatch match = new RouteMatch(false, new HashMap<>());
		assertNull("Default is null", match.getRemainder());
	}
}
