package com.thehellings.gully;

import com.thehellings.gully.http.Verb;
import com.thehellings.gully.route.Route;
import io.undertow.server.HttpHandler;

public interface Router {
	Route addRoute(Route route, HttpHandler handler);

	Route addRoute(String path, HttpHandler handler);

	Route addRoute(Verb verb, String path, HttpHandler handler);

	Route addExactRoute(Verb verb, String path, HttpHandler handler);

	Route addPrefixRoute(Verb verb, String path, HttpHandler handler);

	Route addPrefixRoute(String path, HttpHandler handler);

	Route addParameterizedRoute(Verb verb, String path, HttpHandler handler);

	Route addParameterizedRoute(String path, HttpHandler handler);
}
