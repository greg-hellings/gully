package com.thehellings.gully.route;

import com.thehellings.gully.PlainRouter;
import com.thehellings.gully.http.Verb;

/**
 * Defines a base route to be matched in the routing table.
 * <p>
 * In order for two routes to be considered equal, they must have identical path segments and either identical
 * or "matching" verbs. For more on the matching of verbs check out {@link Verb#matches(Object)}. Typically a user
 * will not directly create a Route, but will instead instantiate one from within their {@link PlainRouter}
 * object's addRoute and related methods.
 */
public class Route {
	private Verb verb;
	private String path;

	public Route(Verb verb, String path) {
		if (verb == null || path == null) {
			throw new IllegalArgumentException("Null value found in Route initializer.");
		}
		this.verb = verb;
		this.path = path;
	}

	/**
	 * Unlike {@link #matches(Object)}, the concept of "equals" requires exact equivalence.
	 *
	 * <p>
	 *     The {@link #matches(Object)} method does not do an exact comparison of equality between the two routes. There
	 *     are times when one route might match but the two might not be exactly equal. The most common case of this is
	 *     when one of the routes triggers on the Verb {@link Verb#ANY}. Another is when one route has a matching
	 *     pattern in its path while the other is a path literal.
	 * </p>
	 *
	 * @param o The object to be compared against
	 * @return True if the other Route is of the same type AND all the values match exactly
	 */
	@Override
	public boolean equals(Object o) {
		// Tests that the other object is possibly of the same type, and filters out null values as well
		if ( !(o instanceof Route))
			return false;
		// Test that the two types are exactly alike. If one is an exact match while the other is a parameterized route,
		// then they should not be considered equal
		if (this.getClass() != o.getClass())
			return false;
		Route other = (Route) o;
		return this.verb == other.verb && this.path.equals(other.path);
	}

	/**
	 * Checks for exact equality of the path portions, but only for matching (see {@link Verb#matches(Object)}) verbs.
	 *
	 * @param other Object against which to compare this route
	 * @return true if this matches the other, false otherwise
	 */
	public RouteMatch matches(Object other) {
		if (! (other instanceof Route)) {
			return new RouteMatch(false);
		}
		Route otherRoute = (Route) other;
		final boolean matches = this.verb.matches(otherRoute.verb) && this.path.equals(otherRoute.path);
		return new RouteMatch(matches, "");
	}

	public Verb getVerb() {
		return verb;
	}

	public void setVerb(Verb verb) {
		this.verb = verb;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}
}
