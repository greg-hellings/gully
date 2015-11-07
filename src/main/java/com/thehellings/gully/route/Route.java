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
	 * Checks for exact equality of the path portions, but only for matching (see {@link Verb#matches(Object)}) verbs.
	 *
	 * @param other Object against which to compare this route
	 * @return true if this matches the other, false otherwise
	 */
	public RouteMatch matches(Object other) {
		if (! (other instanceof Route) || other == null) {
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
