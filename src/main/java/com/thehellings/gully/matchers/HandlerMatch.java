package com.thehellings.gully.matchers;

import com.thehellings.gully.route.Route;
import io.undertow.server.HttpHandler;
import java.util.Map;

public class HandlerMatch {
	private HttpHandler handler;
	private Map<String, String> parameters;
	private String remainder;
	private Route route;

	public HandlerMatch(final HttpHandler handler, final Route route) {
		this(handler, route, null);
	}

	public HandlerMatch(final HttpHandler handler, final Route route, final Map<String, String> parameters) {
		this(handler, route, parameters, null);
	}

	public HandlerMatch(final HttpHandler handler, final Route route, final Map<String, String> parameters, final String remainder) {
		this.handler = handler;
		this.parameters = parameters;
		this.remainder = remainder;
		this.route = route;
	}

	public HttpHandler getHandler() {
		return this.handler;
	}

	public void setHandler(final HttpHandler handler) {
		this.handler = handler;
	}

	public Map<String, String> getParameters() {
		return this.parameters;
	}

	public String getRemainder() {
		return this.remainder;
	}

	public Route getRoute() {
		return this.route;
	}
}
