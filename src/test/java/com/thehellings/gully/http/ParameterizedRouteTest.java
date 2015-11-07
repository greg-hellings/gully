package com.thehellings.gully.http;

import com.thehellings.gully.route.ParameterizedRoute;
import com.thehellings.gully.route.Route;
import com.thehellings.gully.route.RouteMatch;
import junit.framework.TestCase;

public class ParameterizedRouteTest extends TestCase {

	public void testMatchesGetWithSingleParameter() throws Exception {
		ParameterizedRoute testing = new ParameterizedRoute(Verb.GET, "/path/{parameter}");
		Route yes1 = new Route(Verb.GET, "/path/1");
		Route yes2 = new Route(Verb.GET, "/path");
		Route no2 = new Route(Verb.POST, "/path/1");
		RouteMatch match = testing.matches(yes1);
		assertTrue("Matches properly for simple case", match.getMatching());
		assertEquals("Parameter is 1", "1", match.getAttachment().get("parameter"));
		assertTrue("Null segments afterwards are fine", testing.matches(yes2).getMatching());
		assertEquals("Parameter is empty", "", testing.matches(yes2).getAttachment().get("parameter"));
		assertFalse("Rejects failed match on verb", testing.matches(no2).getMatching());
	}

	public void testMatchesGetWithTwoParameters() throws Exception {
		ParameterizedRoute testing = new ParameterizedRoute(Verb.GET, "/path/{one}/{two}");
		Route yes1 = new Route(Verb.GET, "/path/1/2");
		Route yes2 = new Route(Verb.GET, "/path/1");
		Route yes3 = new Route(Verb.GET, "/path");
		Route no1 = new Route(Verb.POST, "/path/1/");
		Route no2 = new Route(Verb.GET, "/not/path/1");
		RouteMatch match;

		match = testing.matches(yes1);
		assertTrue("Matches properly for trivial case", match.getMatching());
		assertEquals("Parameter one is 1", "1", match.getAttachment().get("one"));
		assertEquals("Parameter two is 2", "2", match.getAttachment().get("two"));

		match = testing.matches(yes2);
		assertTrue("Matches with single missing parameter", match.getMatching());
		assertEquals("Parameter one is 1", "1", match.getAttachment().get("one"));
		assertEquals("Parameter two is empty", "", match.getAttachment().get("two"));

		match = testing.matches(yes3);
		assertTrue("Matches with both missing parameters", match.getMatching());
		assertEquals("Parameter one is null", "", match.getAttachment().get("one"));
		assertEquals("Parameter two is null", "", match.getAttachment().get("two"));

		match = testing.matches(no1);
		assertFalse("Does not match on verb mismatch", match.getMatching());

		match = testing.matches(no2);
		assertFalse("Does not match on prefix mismatch", match.getMatching());
	}

	public void testMatchesWithoutLeadingSlash() throws Exception {
		ParameterizedRoute testing = new ParameterizedRoute(Verb.GET, "path/{one}");
		Route yes1 = new Route(Verb.GET, "/path/1");
		Route yes2 = new Route(Verb.GET, "path/2");
		Route no1 = new Route(Verb.GET, "/no/path");
		RouteMatch match = testing.matches(yes1);
		assertTrue("Matches even without leading slash", match.getMatching());
		assertEquals("Parameter one is 1", "1", match.getAttachment().get("one"));
		match = testing.matches(yes2);
		assertTrue("Matches without leading slash on input route", match.getMatching());
		assertEquals("Paraemter one is 2", "2", match.getAttachment().get("one"));
		match = testing.matches(no1);
		assertFalse("Matching doesn't mess with non-matched components", match.getMatching());
	}
}