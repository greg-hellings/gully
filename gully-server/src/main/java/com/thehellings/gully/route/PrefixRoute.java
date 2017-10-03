package com.thehellings.gully.route;

import com.thehellings.gully.http.Verb;

/**
 * Represents a prefix route - that is, a route where the verbs match, and the path of the other one begins with the
 * path of the current segment.
 * <p>
 * This can be a very useful way to separate different portions of your application into logical containers of some
 * sort. For instance, you might have a public section of your site that is handled by one set of routes, and another
 * that is all hidden behind the route /admin/ that requires log in. You could model this by creating a prefix route
 * for /admin/ that sends all requests to a particular controller.
 * <p>
 * This is also a great way to re-use an existing codebase for a stand-alone element by prefixing it into one portion
 * of the code. That way, if you have one artifact that represents, for instance, a forum which you desire to place on
 * multiple sites, you can set that behind a prefix route /forum/ and hand off to the base
 * {@link io.undertow.server.HttpHandler} for the forum from there.
 * <p>
 * Take note that the prefix matching is done as a straight string comparison of the path segments of the requests, thus
 * could conflict with two non-'/' terminated strings if you expected "/foo/bar" and "/foo/barbaz" to be distinct.
 */
public class PrefixRoute extends Route {
	public PrefixRoute(Verb verb, String path) {
		super(verb, path);
	}

	@Override
	public RouteMatch matches(Object o) {
		if (! (o instanceof Route)) {
			return new RouteMatch(false);
		}
		Route other = (Route) o;
		final boolean matches = this.getVerb().matches(other.getVerb()) && other.getPath().startsWith(this.getPath());
		String remainder = null;
		if (matches) {
			remainder = other.getPath().substring(this.getPath().length());
		}
		return new RouteMatch(matches, remainder);
	}
}
