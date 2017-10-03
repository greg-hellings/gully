package com.thehellings.gully;

import com.thehellings.gully.config.Configuration;
import com.thehellings.gully.http.Verb;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import org.apache.http.client.fluent.Request;
import org.apache.http.client.fluent.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Map;

import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

public class PlainRouterTest {
	private Server server;
	@Mock private HttpHandler handler;
	@Mock private HttpHandler error;
	@Mock private HttpHandler unreachable;
	private PlainRouter base;
	private PlainRouter child;
	private PlainRouter nullTest;
	private PlainRouter default404;

	@Before
	public void createServer() {
		MockitoAnnotations.initMocks(this);
		this.child = new PlainRouter(error);
		this.child.addExactRoute(Verb.GET, "/second", handler);
		this.nullTest = new PlainRouter(null);
		this.default404 = new PlainRouter();
		this.base = new PlainRouter(error);
		this.base.addPrefixRoute("/first", child);
		// Unreachable because Exact and ANY don't fit together
		this.base.addExactRoute(Verb.ANY, "/unreachable", this.unreachable);
		this.base.addRoute("/null", this.nullTest);
		this.base.addPrefixRoute("/default404", this.default404);
		this.base.addParameterizedRoute("/child/{path_param}/", new AttachmentVerifyHandler());

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

	@Test
	public void testDefault404IsCorrect() throws Exception {
		Response response = Request.Get("http://localhost:8080/default404")
				.execute();

		assertEquals(404, response.returnResponse().getStatusLine().getStatusCode());
	}

	@Test
	public void testPlainRoute() throws Exception {
		Response response = Request.Get("http://localhost:8080/null")
				.execute();

		assertEquals(404, response.returnResponse().getStatusLine().getStatusCode());
	}

	@Test
	public void testPathParameters() throws Exception {
		Request.Get("http://localhost:8080/child/subpath")
				.execute()
				.discardContent();
	}

	public static class AttachmentVerifyHandler implements HttpHandler {

		@Override
		public void handleRequest(HttpServerExchange httpServerExchange) throws Exception {
			PlainRouter.RouteAttachment attachment = httpServerExchange.getAttachment(PlainRouter.ATTACHMENT_KEY);
			Map<String, String> parameters = attachment.getParameters();
			assertEquals("subpath", parameters.get("path_param"));
		}
	}
}
