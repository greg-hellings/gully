package com.thehellings.gully.route;

import com.thehellings.gully.http.Verb;
import org.junit.Test;

import static org.junit.Assert.assertFalse;

public class PrefixRouteTest {
	@Test
	public void testTrivialMatches() {
		PrefixRoute route = new PrefixRoute(Verb.ANY, "/any");
		assertFalse("Should not match null", route.matches(null).getMatching());
	}
}
