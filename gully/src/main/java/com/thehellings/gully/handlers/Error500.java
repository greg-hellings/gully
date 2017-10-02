package com.thehellings.gully.handlers;

import com.thehellings.gully.http.ResponseCode;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;

public class Error500 implements HttpHandler {
	public boolean displayBacktrace;

	public Error500() {
		this(false);
	}

	public Error500(boolean displayBacktrace) {
		this.displayBacktrace = displayBacktrace;
	}

	@Override
	public void handleRequest(HttpServerExchange exchange) {
		exchange.setStatusCode(ResponseCode.SERVER_ERROR.getCode());
		exchange.getResponseSender().send("Error occurred during excution.");
	}
}
