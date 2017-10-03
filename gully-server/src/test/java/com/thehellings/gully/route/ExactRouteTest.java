package com.thehellings.gully.route;

import com.thehellings.gully.http.Verb;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ExactRouteTest {
	@Test
	public void testTrivialMatches() {
		ExactRoute route = new ExactRoute(Verb.GET, "/any");
		assertFalse("Should not match non-Routes", route.matches(null).getMatching());
	}

	@Test
	public void testAllPermutations() {
		ExactRoute route = new ExactRoute(Verb.POST, "/post");
		Route yes = new Route(Verb.POST, "/post");
		Route no1 = new Route(Verb.GET, "/post");
		Route no2 = new Route(Verb.POST, "/get");
		Route no3 = new Route(Verb.GET, "/get");

		assertTrue("Exact matches true", route.matches(yes).getMatching());
		assertFalse("Verb doesn't match", route.matches(no1).getMatching());
		assertFalse("Path doesn't match", route.matches(no2).getMatching());
		assertFalse("Neither matches", route.matches(no3).getMatching());
	}
}
