package com.thehellings.gully;

import com.thehellings.gully.handlers.AuthenticatedRouterConstraintHandler;
import com.thehellings.gully.handlers.Error404;
import com.thehellings.gully.http.Verb;
import com.thehellings.gully.route.ExactRoute;
import com.thehellings.gully.route.ParameterizedRoute;
import com.thehellings.gully.route.PrefixRoute;
import com.thehellings.gully.route.Route;
import io.undertow.UndertowLogger;
import io.undertow.security.api.AuthenticationMechanism;
import io.undertow.security.api.AuthenticationMode;
import io.undertow.security.handlers.AuthenticationCallHandler;
import io.undertow.security.handlers.AuthenticationMechanismsHandler;
import io.undertow.security.handlers.SecurityInitialHandler;
import io.undertow.security.idm.IdentityManager;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import org.jboss.logging.Logger;

import java.util.List;

/**
 * This subclass of the PlainRouter adds in the option to allow authentication to individual routes.
 * <p>
 *     This class does not require authentication on all routes, although the default behavior if left unspecified will
 *     default to requiring authentication. In order to make authentication optional (e.g. if you want to permit a
 *     guest mode to certain routes), then be sure to specify "false" when adding the route to this Router.
 * </p>
 * <code>
 *     PlainRouter baseRouter = new PlainRouter();
 *     AuthenticatedRouter adminRouter = new AuthenticatedRouter();
 *     baseRouter.addExactRoute("/", new IndexHandler());
 *     baseRouter.addExactRoute("/about", new AboutHandler());
 *     baseRouter.addPrefixRoute("/posts", new PostHandler()); // Will not require login to the URL "/posts"
 *     baseRouter.addPrefixRoute("/admin", adminRouter);
 *     adminRouter.addExactPath("/posts", new AdminPostHandler()); // Will require login to the URL "/admin/posts"
 *     adminRouter.addExactPath("/users", new AdminUserHandler(), false); // Will allow unauthenticated access to "/admin/users"
 * </code>
 */
public class AuthenticatedRouter extends PlainRouter {
	final private static Logger log = Logger.getLogger(AuthenticatedRouter.class);
	private HttpHandler startChain;
	private AuthenticatedRouterConstraintHandler authenticationConstraintHandler;

	/**
	 * Create a router with the defined mechanisms and identity management.
	 *
	 * @param mechanisms The list of mechanisms to attempt authentication against
	 * @param identityManager The object that will check if a user is already logged in
	 * @see SecurityInitialHandler
	 * @see AuthenticationMechanismsHandler
	 * @see AuthenticationCallHandler
	 */
	public AuthenticatedRouter(List<AuthenticationMechanism> mechanisms, IdentityManager identityManager) {
		this(mechanisms, identityManager, new Error404());
	}

	/**
	 * Same as the other constructor, but also allows you to set a default error handler for when a route does not match
	 * at all.
	 * <p>
	 *     Use the default handler to establish chaining handlers as much of Undertow typically will do, or to define a
	 *     router-wdie error page, such as a 404 response or the like
	 * </p>
	 *
	 * @param mechanisms The list of mechanisms to attempt authentication against
	 * @param identityManager The object that will check if a user is already logged in
	 * @param errorHandler The default handler to be called when no other handler is discovered
	 */
	public AuthenticatedRouter(List<AuthenticationMechanism> mechanisms, IdentityManager identityManager, HttpHandler errorHandler) {
		super(errorHandler);
		AuthenticationCallHandler authenticationCallHandler = new AuthenticationCallHandler(new PassThruHandler(errorHandler));
		this.authenticationConstraintHandler = new AuthenticatedRouterConstraintHandler(authenticationCallHandler);
		AuthenticationMechanismsHandler authenticationMechanismsHandler = new AuthenticationMechanismsHandler(authenticationConstraintHandler, mechanisms);
		this.startChain = new SecurityInitialHandler(AuthenticationMode.PRO_ACTIVE, identityManager, authenticationMechanismsHandler);
	}

	@Override
	public void handleRequest(HttpServerExchange exchange) throws Exception {
		this.handleRequest(exchange, false); // Find the matching router and add the attachment
		UndertowLogger.REQUEST_LOGGER.info("Routed authenticated request");
		this.startChain.handleRequest(exchange); // Begin the march up the execution chain
		UndertowLogger.REQUEST_LOGGER.info("Dispatched authenticated request");
	}

	@Override
	public Route addRoute(Route route, HttpHandler handler) {
		UndertowLogger.ROOT_LOGGER.info("Adding authenticated route: " + route.getPath());
		return this.addRoute(route, handler, true);
	}

	/**
	 * Adds a route to this router, either requiring or offering optional authentication.
	 *
	 * @param route The route to add to this class
	 * @param handler The object that will respond to requests on this route
	 * @param requireAuthentication True to require authentication, false to leave it optional
	 * @return The Route object that wraps the other values
	 */
	public Route addRoute(Route route, HttpHandler handler, boolean requireAuthentication) {
		this.authenticationConstraintHandler.setRequired(route, requireAuthentication);
		return super.addRoute(route, handler);
	}

	/**
	 * @see PlainRouter#addRoute(String, HttpHandler)
	 * @param path Same as the parent method
	 * @param handler Same as the parent method
	 * @param requireAuthentication True if authentication should be required, false if it is optional
	 * @return The Route object that wraps the other values
	 */
	public Route addRoute(String path, HttpHandler handler, boolean requireAuthentication) {
		Route route = new Route(Verb.ANY, path);
		return this.addRoute(route, handler, requireAuthentication);
	}

	/**
	 * @param verb The HTTP {@link Verb} to match
	 * @param path The path to match
	 * @param handler The handler to invoke on a matching request
	 * @param requireAuthentication True if authentication should be required, false if it is optional
	 * @return The Route object wrapping the other values
	 * @see #addRoute(Verb, String, HttpHandler)
	 */
	public Route addRoute(Verb verb, String path, HttpHandler handler, boolean requireAuthentication) {
		Route route = new Route(verb, path);
		return this.addRoute(route, handler, requireAuthentication);
	}

	/**
	 *
	 * @param verb The HTTP {@link Verb} to match
	 * @param path The path to match
	 * @param handler The handler to invoke on a matching request
	 * @param requireAuthentication True if authentication should be required, false if it is optional
	 * @return The Route object wrapping the other values
	 * @see #addExactRoute(Verb, String, HttpHandler)
	 */
	public Route addExactRoute(Verb verb, String path, HttpHandler handler, boolean requireAuthentication) {
		Route route = new ExactRoute(verb, path);
		return this.addRoute(route, handler, requireAuthentication);
	}

	/**
	 *
	 * @param verb The HTTP {@link Verb} to match
	 * @param path The path to match
	 * @param handler The handler to invoke on a matching request
	 * @param requireAuthentication True if authentication should be required, false if it is optional
	 * @return The Route object wrapping the other values
	 * @see #addPrefixRoute(Verb, String, HttpHandler)
	 */
	public Route addPrefixRoute(Verb verb, String path, HttpHandler handler, boolean requireAuthentication) {
		Route route = new PrefixRoute(verb, path);
		return this.addRoute(route, handler, requireAuthentication);
	}

	/**
	 * Matches the {@link Verb#ANY} method
	 *
	 * @param path The path to match
	 * @param handler The handler to invoke on a matching request
	 * @param requireAuthentication True if authentication should be required, false if it is optional
	 * @return The Route object that wraps the other values
	 * @see #addPrefixRoute(Verb, String, HttpHandler, boolean)
	 */
	public Route addPrefixRoute(String path, HttpHandler handler, boolean requireAuthentication) {
		Route route = new PrefixRoute(Verb.ANY, path);
		return this.addRoute(route, handler, requireAuthentication);
	}

	/**
	 *
	 * @param verb The HTTP {@link Verb} to match
	 * @param path The path to match
	 * @param handler The handler to invoke on a matching request
	 * @param requireAuthentication True if authentication should be required, false if it is optional
	 * @return The Route object that wraps the other values
	 * @see #addParameterizedRoute(Verb, String, HttpHandler)
	 */
	public Route addParameterizedRoute(Verb verb, String path, HttpHandler handler, boolean requireAuthentication) {
		Route route = new ParameterizedRoute(verb, path);
		return this.addRoute(route, handler, requireAuthentication);
	}

	/**
	 * Matches the {@link Verb#ANY} method
	 *
	 * @param path The path to match
	 * @param handler The handler to invoke on a matching request
	 * @param requireAuthentication True if authentication should be required, false if it is optional
	 * @return The Route object that wraps the other values
	 * @see #addParameterizedRoute(Verb, String, HttpHandler)
	 */
	public Route addParameterizedRoute(String path, HttpHandler handler, boolean requireAuthentication) {
		Route route = new ParameterizedRoute(Verb.ANY, path);
		return this.addRoute(route, handler, requireAuthentication);
	}

	private class PassThruHandler implements HttpHandler {
		private HttpHandler errorHandler;
		public PassThruHandler(final HttpHandler handler) {
			this.errorHandler = handler;
		}

		@Override
		public void handleRequest(final HttpServerExchange httpServerExchange) throws Exception {
			final PlainRouter.RouteAttachment attachment = httpServerExchange.getAttachment(PlainRouter.ATTACHMENT_KEY);
			HttpHandler handler = attachment.getHandler();
			UndertowLogger.REQUEST_LOGGER.info("Found authenticated route.", handler, null);
			if (handler == null) {
				this.errorHandler.handleRequest(httpServerExchange);
			} else {
				handler.handleRequest(httpServerExchange);
			}
		}
	}
}
