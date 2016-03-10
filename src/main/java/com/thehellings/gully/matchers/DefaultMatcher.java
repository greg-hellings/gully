package com.thehellings.gully.matchers;

import com.thehellings.gully.route.RouteMatch;
import com.thehellings.gully.route.Route;
import io.undertow.server.HttpHandler;

import java.util.LinkedList;
import java.util.List;

/**
 * This is the default route matcher. Since there is the possibility of overlap and ordering foo, we implement a single
 * list, and the first entry added to the routes that matches the called route is the one which will be returned. If no
 * route in our selection matches the specified route, then a null handler will be returned. It is up to the caller to
 * determine what to do when no route within this set matches the given route.
 */
public class DefaultMatcher implements Matcher {
	private List<Pair> routes;
	public DefaultMatcher() {
		this.routes = new LinkedList<Pair>();
	}

	/**
	 * Adds the route to the current list of routes. This maintains an ordered set of routes based on the order they
	 * were inserted, and the first one that matches (in the case that multiple match) will be the one returned.
	 *
	 * @param route The Route to add to the list
	 * @param handler The handler that will respond to calls to this Route
	 */
	public void addRoute(Route route, HttpHandler handler) {
		this.routes.add(new Pair(route, handler));
	}

	/**
	 * Returns a HandlerMatch object that contains any parameterized route information and the handler that is assigned
	 * to the matched route. However, if no route is found, it will return null for both properties.
	 *
	 * @param route The route against which to match
	 * @return A handler match with route parameters, if found, or with null handlers if no route was found
	 */
	public HandlerMatch matchRoute(Route route) {
		for (Pair pair : routes) {
			RouteMatch routeMatch = pair.getRoute().matches(route);
			if (routeMatch.getMatching()) {
				return new HandlerMatch(pair.getHandler(), pair.getRoute(), routeMatch.getAttachment(), routeMatch.getRemainder());
			}
		}
		return new HandlerMatch(null, null);
	}

	private class Pair {
		private Route route;
		private HttpHandler handler;

		public Pair(final Route route, final HttpHandler handler) {
			this.route = route;
			this.handler = handler;
		}

		@Override
		public boolean equals(Object o) {
			if (o instanceof Pair) {
				Pair p = (Pair) o;
				return this.route.matches(p.route).getMatching() && (this.handler == p.handler);
			}
			if (o instanceof Route) {
				Route r = (Route) o;
				return this.route.matches(r).getMatching();
			}
			return false;
		}

		public Route getRoute() {
			return this.route;
		}

		public HttpHandler getHandler() {
			return this.handler;
		}
	}
}
