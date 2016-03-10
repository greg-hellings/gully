package com.thehellings.gully.matchers;

import com.thehellings.gully.route.Route;
import io.undertow.server.HttpHandler;

public interface Matcher {
	public void addRoute(Route route, HttpHandler handler);

	public HandlerMatch matchRoute(Route route);
}
