package com.thehellings.gully.http;

import com.thehellings.gully.route.Route;
import junit.framework.TestCase;

public class RouteTest extends TestCase {

	public void testMatches() throws Exception {
		Route getRoute = new Route(Verb.GET, "/some/path");
		Route postRoute = new Route(Verb.POST, "/some/path");
		Route otherPostRoute = new Route(Verb.POST, "/some/other/route");
		Route anyRoute = new Route(Verb.ANY, "/some/path");
		Route otherAnyRoute = new Route(Verb.ANY, "/some/other/route");
		assertTrue("Identity with GET", getRoute.matches(getRoute).getMatching());
		assertTrue("ANY matches GET", anyRoute.matches(getRoute).getMatching());
		assertTrue("ANY matches POST", anyRoute.matches(postRoute).getMatching());
		assertFalse("GET does not match POST", getRoute.matches(postRoute).getMatching());
		assertFalse("POST does not match GET", postRoute.matches(getRoute).getMatching());
		assertFalse("POST with different paths do not match", postRoute.matches(otherPostRoute).getMatching());
		assertFalse("ANY with different paths do not match", anyRoute.matches(otherAnyRoute).getMatching());
	}
}