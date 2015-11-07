package com.thehellings.gully.route;

import com.thehellings.gully.http.Verb;

/**
 * Defines a route that evaluates equality only when an exact match is made.
 * <p>
 * An exact match is more strict than the checks in {@link Route}. In this instance, even the verbs of the two classes
 * must be an exact match. As such, it can be compared against any other type of Route object, and both the path and
 * the verb portions must match exactly. Thus, it would be inappropriate to create an instnace of this class with the
 * Verb.ANY value in most cases, as a user request would not come in asking for Verb.ANY. However, this behavior is not
 * prevented as there might be unforseen reasons to make such a call.
 */
public class ExactRoute extends Route {
	public ExactRoute(Verb verb, String path) {
		super(verb, path);
	}

	/**
	 * Checks for exact equality of the fields against the other object
	 *
	 * @param o Object against which to compare
	 * @return true iff the two objecst have identical fields, false otherwise
	 */
	@Override
	public RouteMatch matches(Object o) {
		if (! (o instanceof Route) || o == null) {
			return new RouteMatch(false);
		}
		Route other = (Route) o;
		if (other.getVerb() == this.getVerb() && this.getPath().equals(other.getPath())) {
			return new RouteMatch(true, "");
		}
		return new RouteMatch(false);
	}
}
