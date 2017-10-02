package com.thehellings.gully.handlers;

import com.thehellings.gully.http.ResponseCode;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;

public class Error404 implements HttpHandler {
	public void handleRequest(HttpServerExchange exchange) {
		exchange.setStatusCode(ResponseCode.ERROR_NOT_FOUND.getCode());
        exchange.getResponseSender().send("ERROR: Page not found");
	}
}
