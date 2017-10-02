package com.thehellings.gully;

import com.thehellings.gully.config.Configuration;
import com.thehellings.gully.handlers.Error404;
import com.thehellings.gully.http.Verb;
import io.undertow.security.api.AuthenticationMechanism;
import io.undertow.security.idm.Account;
import io.undertow.security.idm.Credential;
import io.undertow.security.idm.IdentityManager;
import io.undertow.security.impl.BasicAuthenticationMechanism;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import org.apache.http.HttpHost;
import org.apache.http.client.fluent.Executor;
import org.apache.http.client.fluent.Request;
import org.apache.http.client.fluent.Response;
import org.jboss.logging.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

import java.security.Principal;
import java.util.Collections;
import java.util.List;

public class AuthenticatedRouterTest {
	private List<AuthenticationMechanism> mechanisms;
	private @Mock IdentityManager identityManager;
	private final static Logger log = Logger.getLogger(AuthenticatedRouterTest.class);
	private HttpHandler error;
	private HttpHandler handler;
	private Server server;

	private Executor executor;

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
		// Mock out authentication mechanisms
		this.mechanisms = Collections.<AuthenticationMechanism>singletonList(new BasicAuthenticationMechanism("Test"));
		// Mock out Identity Manager
		Principal principal = mock(Principal.class);
		when(principal.getName()).thenReturn("admin");
		Account account = mock(Account.class);
		when(account.getPrincipal()).thenReturn(principal);
		when(this.identityManager.verify(account)).thenReturn(account);
		when(this.identityManager.verify(eq("admin"), any())).thenReturn(account);
		when(this.identityManager.verify((Credential) any())).thenReturn(account);

		this.executor = Executor.newInstance()
				.auth(new HttpHost("localhost", 8080),"admin", "password")
				.authPreemptive(new HttpHost("localhost", 8080));

		this.error = mock(HttpHandler.class);
		this.handler = mock(HttpHandler.class);
	}

	@After
    public void tearDown() throws Exception {
	    this.server.stop();
	    Thread.sleep(1000);
    }

	@Test
	public void testPassesExactRoute() throws Exception {
        AuthenticatedRouter router = new AuthenticatedRouter(this.mechanisms, this.identityManager, error);
        router.addExactRoute(Verb.GET, "/authed", handler, true);
	    this.server = new Server(new Configuration(router));
	    this.server.start();
	    Thread.sleep(1000);
		// Execute the request
        this.executor.execute(Request.Get("http://localhost:8080/authed"))
                .discardContent();

		verify(handler, times(1)).handleRequest(Mockito.any(HttpServerExchange.class));
		verify(error, times(0)).handleRequest(Mockito.any(HttpServerExchange.class));
	}

	@Test
	public void testPassesExactRouteThroughParent() throws Exception {
        AuthenticatedRouter router = new AuthenticatedRouter(this.mechanisms, this.identityManager, error);
        router.addExactRoute(Verb.POST, "/authed", handler, true);
        PlainRouter plainRouter = new PlainRouter(error);
        plainRouter.addPrefixRoute(Verb.POST, "/plain", router);
	    this.server = new Server(new Configuration(plainRouter));
	    this.server.start();
	    Thread.sleep(1000);
	    // Execute the request
	    this.executor.execute(Request.Post("http://localhost:8080/plain/authed"))
                .discardContent();

		verify(handler, times(1)).handleRequest(Mockito.any(HttpServerExchange.class));
		verify(error, times(0)).handleRequest(Mockito.any(HttpServerExchange.class));
	}

	@Test
	public void test404WhenPathInvalid() throws Exception {
		AuthenticatedRouter router = new AuthenticatedRouter(this.mechanisms, this.identityManager);
		router.addRoute("/derp", handler, true);
		router.addRoute(Verb.POST, "/herp", handler, true);
		router.addPrefixRoute("/prefix1", handler, true);
		router.addPrefixRoute(Verb.GET, "/prefix2", handler, true);
		router.addParameterizedRoute("/param1/{path_param}", new PlainRouterTest.AttachmentVerifyHandler(), true);
		router.addParameterizedRoute(Verb.ANY,
				"/param2/{path_param}/second",
				new PlainRouterTest.AttachmentVerifyHandler(),
				true);
		this.server = new Server(new Configuration(router));
		this.server.start();
		Thread.sleep(1000);

		testGetResponse("/nope", 404);
		testGetResponse("/herp", 404);
		testGetResponse("/prefix1", 200);
		testGetResponse("/prefix2", 200);
		testGetResponse("/param1/subpath/", 200);
		testGetResponse("/param2/subpath/second", 200);

		// Correct verb
		Response response = this.executor.execute(Request.Post("http://localhost:8080/herp"));
		assertEquals(200, response.returnResponse().getStatusLine().getStatusCode());
	}

	private void testGetResponse(final String path, int expected) throws Exception {
		Response response = this.executor.execute(Request.Get("http://localhost:8080" + path));
		assertEquals(expected, response.returnResponse().getStatusLine().getStatusCode());
	}
}
