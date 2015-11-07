package com.thehellings.gully.http;

import com.thehellings.gully.route.PrefixRoute;
import com.thehellings.gully.route.Route;
import junit.framework.TestCase;

public class PrefixRouteTest extends TestCase {

	public void testMatches() throws Exception {
		PrefixRoute getRoute = new PrefixRoute(Verb.GET, "/some/prefix");
		PrefixRoute postRoute = new PrefixRoute(Verb.POST, "/some/other/prefix");
		PrefixRoute anyRoute = new PrefixRoute(Verb.ANY, "/some/prefix");
		Route matchesOne = new Route(Verb.POST, "/some/other/prefix/extra/path");
		Route matchesTwo = new Route(Verb.GET, "/some/prefix/extra/path");
		Route matchesThree = new Route(Verb.POST, "/some/prefix");
		// one
		assertFalse("Double unmatched - GET", getRoute.matches(matchesOne).getMatching());
		assertTrue("POST matches POST with prefix", postRoute.matches(matchesOne).getMatching());
		assertFalse("ANY doesn't override path mismatch", anyRoute.matches(matchesOne).getMatching());
		// two
		assertTrue("GET matches GET", getRoute.matches(matchesTwo).getMatching());
		assertFalse("POST does not match GET", postRoute.matches(matchesTwo).getMatching());
		assertTrue("ANY matches GET", anyRoute.matches(matchesTwo).getMatching());
		// three
		assertFalse("Unmatched prefixes - GET", getRoute.matches(matchesThree).getMatching());
		assertFalse("Unmatched prefixes - POST", postRoute.matches(matchesThree).getMatching());
		assertTrue("Exact matched prefixes - ANY", anyRoute.matches(matchesThree).getMatching());
	}
}