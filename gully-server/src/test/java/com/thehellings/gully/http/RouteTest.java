package com.thehellings.gully.http;

import com.thehellings.gully.route.ExactRoute;
import com.thehellings.gully.route.ParameterizedRoute;
import com.thehellings.gully.route.Route;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

public class RouteTest {

	@Test
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
		assertFalse("Does not match non-Routes", anyRoute.matches("notroute").getMatching());
		assertFalse("Does not match null", anyRoute.matches(null).getMatching());
	}

	@Test
	public void testEquals() {
		Route firstRoute = new Route(Verb.ANY, "/any");
		assertTrue("Equivalence is true", firstRoute.equals(new Route(Verb.ANY, "/any")));
		assertFalse("Doesn't match different subtypes", firstRoute.equals(new ExactRoute(Verb.ANY, "/any")));
		assertFalse("ANY != GET", firstRoute.equals(new Route(Verb.GET, "/any")));
		assertFalse("Nothing matches", firstRoute.equals(new Route(Verb.DELETE, "/delete")));

		ParameterizedRoute parameterizedRoute = new ParameterizedRoute(Verb.GET, "/{get}");
		assertTrue("Equivalence is true", parameterizedRoute.equals(new ParameterizedRoute(Verb.GET, "/{get}")));
		assertFalse("Param names don't match", parameterizedRoute.equals(new ParameterizedRoute(Verb.GET, "/{had}")));
		assertFalse("Doesn't match across types", parameterizedRoute.equals(new Route(Verb.GET, "/{get}")));
		assertFalse("Nulls don't match", parameterizedRoute.equals(null));
	}

	@Test(expected = IllegalArgumentException.class)
	public void testValidArguments() {
		new Route(null, "path");
	}

	@Test(expected = IllegalArgumentException.class)
	public void testValidPath() {
		new Route(Verb.GET, null);
	}

	@Test
	public void testSetPostCreate() {
		Route testRoute = new Route(Verb.GET, "/somepath");
		testRoute.setVerb(Verb.POST);
		testRoute.setPath("/secondpath");

		assertEquals(Verb.POST, testRoute.getVerb());
		assertEquals("/secondpath", testRoute.getPath());
	}
}