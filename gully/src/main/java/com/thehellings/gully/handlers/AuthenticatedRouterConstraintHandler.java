package com.thehellings.gully.handlers;

import com.thehellings.gully.PlainRouter;
import com.thehellings.gully.route.Route;
import io.undertow.security.handlers.AuthenticationCallHandler;
import io.undertow.security.handlers.AuthenticationConstraintHandler;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;

import java.util.HashMap;
import java.util.Map;

public class AuthenticatedRouterConstraintHandler extends AuthenticationConstraintHandler {
	private Map<Route, Boolean> required;

	public AuthenticatedRouterConstraintHandler(HttpHandler httpHandler) {
		super(httpHandler);
		this.required = new HashMap<Route, Boolean>();
	}

	public void setRequired(Route route, boolean require) {
		this.required.put(route, require);
	}

	@Override
	public boolean isAuthenticationRequired(HttpServerExchange exchange) {
		PlainRouter.RouteAttachment attachment = exchange.getAttachment(PlainRouter.ATTACHMENT_KEY);
		try {
			return this.required.get(attachment.getRoute());
		} catch(Exception exception) {
			return true;
		}
	}
}
