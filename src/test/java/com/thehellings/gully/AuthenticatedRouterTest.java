package com.thehellings.gully;

import com.thehellings.gully.http.Verb;
import com.thehellings.gully.stubs.HttpServerExchangeStub;
import io.undertow.security.api.AuthenticationMechanism;
import io.undertow.security.idm.Account;
import io.undertow.security.idm.IdentityManager;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.HttpString;
import junit.framework.TestCase;
import org.jboss.logging.Logger;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.*;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

public class AuthenticatedRouterTest extends TestCase {
	private List<AuthenticationMechanism> mechanisms;
	private @Mock IdentityManager identityManager;
	private final static Logger log = Logger.getLogger(AuthenticatedRouterTest.class);

	@Before
	public void setUp() {
		// Mock out authentication mechanisms
		this.mechanisms = new ArrayList<>(1);
		MockitoAnnotations.initMocks(this);
		AuthenticationMechanism mechanism = mock(AuthenticationMechanism.class);
		when(mechanism.authenticate(any(), any())).thenReturn(AuthenticationMechanism.AuthenticationMechanismOutcome.AUTHENTICATED);
		this.mechanisms.add(mechanism);
		// Mock out Identity Manager
		Principal principal = mock(Principal.class);
		when(principal.getName()).thenReturn("admin");
		Account account = mock(Account.class);
		when(account.getPrincipal()).thenReturn(principal);
		when(this.identityManager.verify(account)).thenReturn(account);
		when(this.identityManager.verify(eq("admin"), any())).thenReturn(account);
	}

	@Test
	public void testPassesExactRoute() throws Exception {
		// Mocks, first
		HttpHandler handler = mock(HttpHandler.class);
		HttpServerExchange exchange = HttpServerExchangeStub.createHttpExchange();
		exchange.setRequestMethod(HttpString.tryFromString("GET"));
		exchange.setRequestPath("/test");
		exchange.setRelativePath("/test");
//		when(exchange.getRequestMethod()).thenReturn(HttpString.tryFromString("GET"));
//		when(exchange.getRequestPath()).thenReturn("/test");
		HttpHandler error = mock(HttpHandler.class);
		// Then real objects
		AuthenticatedRouter router = new AuthenticatedRouter(this.mechanisms, this.identityManager, error);
		router.addExactRoute(Verb.GET, "/test", handler);
		try {
			router.handleRequest(exchange);
		} catch(Exception ex) {
			log.error("Error while running test.", ex);
		}

		verify(handler, times(1)).handleRequest(exchange);
		verify(error, times(0)).handleRequest(exchange);
	}

	@Test
	public void testPassesExactRouteThroughParent() throws Exception {
		HttpHandler handler = mock(HttpHandler.class);
		HttpHandler error = mock(HttpHandler.class);
		HttpServerExchange exchange = HttpServerExchangeStub.createHttpExchange();
		exchange.setRequestMethod(HttpString.tryFromString("POST"));
		exchange.setRequestPath("/test/inner");
		exchange.setRelativePath("/test/inner");
		// Test objects
		PlainRouter router = new PlainRouter(error);
		AuthenticatedRouter authRouter = new AuthenticatedRouter(this.mechanisms, this.identityManager, error);
		router.addPrefixRoute("/test", authRouter);
		authRouter.addExactRoute(Verb.POST, "/inner", handler);
		try {
			router.handleRequest(exchange);
		} catch(Exception ex) {
			log.error("Error while running test", ex);
		}

		verify(handler, times(1)).handleRequest(exchange);
		verify(error, times(0)).handleRequest(exchange);
	}
}
