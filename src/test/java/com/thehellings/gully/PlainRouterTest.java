package com.thehellings.gully;

import com.thehellings.gully.config.Configuration;
import com.thehellings.gully.http.Verb;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import org.apache.http.client.fluent.Request;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.*;

public class PlainRouterTest {
	private Server server;
	@Mock private HttpHandler handler;
	@Mock private HttpHandler error;
	@Mock private HttpHandler unreachable;
	private PlainRouter base;
	private PlainRouter child;

	@Before
	public void createServer() {
		MockitoAnnotations.initMocks(this);
		this.child = new PlainRouter(error);
		this.child.addExactRoute(Verb.GET, "/second", handler);
		this.base = new PlainRouter(error);
		this.base.addPrefixRoute(Verb.ANY, "/first", child);
		// Unreachable because Exact and ANY don't fit together
		this.base.addExactRoute(Verb.ANY, "/unreachable", this.unreachable);

		this.server = new Server(new Configuration(this.base));
		this.server.start();
	}

	@After
	public void teardownServer() throws Exception {
		this.server.stop();
		Thread.sleep(1000);
	}

	@Test
	public void testPrefixPassesThrough() throws Exception {
		// Client
		Request.Get("http://localhost:8080/first/second")
				.execute()
				.discardContent();

		verify(handler, times(1)).handleRequest(any(HttpServerExchange.class));
		verify(error, times(0)).handleRequest(any(HttpServerExchange.class));
	}

	@Test
	public void testExactRouteDoesNotMatchAny() throws Exception {
		Request.Get("http://localhost:8080/unreachable")
				.execute()
				.discardContent();

		verify(handler, times(0)).handleRequest(any(HttpServerExchange.class));
		verify(error, times(1)).handleRequest(any(HttpServerExchange.class));
		verify(unreachable, times(0)).handleRequest(any(HttpServerExchange.class));
	}
}
