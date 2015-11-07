package com.thehellings.gully;

import com.thehellings.gully.http.Verb;
import com.thehellings.gully.stubs.HttpServerExchangeStub;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.HttpString;
import junit.framework.TestCase;
import static org.mockito.Mockito.*;

public class PlainRouterTest extends TestCase {
	public void testPrefixPassesThrough() throws Exception {
		// Mocks
		HttpHandler handler = mock(HttpHandler.class);
		HttpHandler error = mock(HttpHandler.class);
		HttpServerExchange exchange = HttpServerExchangeStub.createHttpExchange();
		exchange.setRequestMethod(HttpString.tryFromString("GET"));
		exchange.setRequestPath("/first/second");
		exchange.setRelativePath("/first/second");
		// Objects
		PlainRouter base = new PlainRouter(error);
		PlainRouter child = new PlainRouter(error);
		child.addExactRoute(Verb.GET, "/second", handler);
		base.addPrefixRoute(Verb.ANY, "/first", child);
		// Test
		base.handleRequest(exchange);

		verify(handler, times(1)).handleRequest(exchange);
		verify(error, times(0)).handleRequest(exchange);
	}
}
