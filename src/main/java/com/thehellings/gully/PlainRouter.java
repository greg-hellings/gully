package com.thehellings.gully;

import com.thehellings.gully.handlers.Error404;
import com.thehellings.gully.http.*;
import com.thehellings.gully.matchers.HandlerMatch;
import com.thehellings.gully.matchers.Matcher;
import com.thehellings.gully.matchers.DefaultMatcher;
import com.thehellings.gully.route.ExactRoute;
import com.thehellings.gully.route.ParameterizedRoute;
import com.thehellings.gully.route.PrefixRoute;
import com.thehellings.gully.route.Route;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.AttachmentKey;

import java.util.Map;

/**
 * Gives a basic router implementation. You can use this to route all manner of requests from anywhere within your
 * application. It is, itself, an HttpHandler, so you can use it anywhere that you might want to route something within
 * an Undertow application. You may also give it a default handler, which all requests which are otherwise un-answered
 * will be sent.
 * <p>
 * Because of the nested handler design of Undertow, it is not necessary for you to completely represent your full stack
 * of routing within a single PlainRouter. You could, for example, have a base router which routes between public and private
 * areas of your website, then another router which routes to specific pages within the public area of your network.
 * Meanwhile, people who access the private area might be routed through an authentication handler which then sends
 * properly authenticated requests on to a third router which routes between base pages in the private area.
 */
public class PlainRouter implements HttpHandler, Router {
	final public static AttachmentKey<RouteAttachment> ATTACHMENT_KEY = AttachmentKey.create(RouteAttachment.class);
	private HttpHandler defaultHandler;
	protected Matcher matcher;

	/**
	 * Creates a new router with the default handler, if the matcher returns empty and using the custom matcher provided
	 * @param defaultHandler
	 * @param matcher
	 */
	public PlainRouter(HttpHandler defaultHandler, Matcher matcher) {
		this.defaultHandler = defaultHandler;
		if (this.defaultHandler == null) {
			this.defaultHandler = new Error404();
		}
		this.matcher = matcher;
	}
	/**
	 * Creates a new router to route between different pages or segments of a site, using our default matcher algorithm
	 *
	 * @param defaultHandler The default handler which will be called when no matching templates are found
	 */
	public PlainRouter(HttpHandler defaultHandler) {
		this(defaultHandler, new DefaultMatcher());
	}

	/**
	 * Creates a new router to route between different pages or segments of a site.
	 * <p>
	 * Pages which are not handled by your requests will be sent back an HTTP 404 "Page Not Found" response code.
	 */
	public PlainRouter() {
		this(new Error404());
	}

	/**
	 * If you want to execute some code around the process of adding a route, this is the method to override.
	 * <p>
	 *     All calls to add*Route are shunted through this method, so if you want to subclass this class and change some
	 *     of its default behaviors, this is the method that you are going to need to override. Likewise, if you want
	 *     to add your own implementation of Route to this router, then you can use this method to execute it.
	 * </p>
	 *
	 * @param route The route to add to this class
	 * @param handler The object that will respond to requests on this route
	 */
	public Route addRoute(Route route, HttpHandler handler) {
		this.matcher.addRoute(route, handler);
		return route;
	}

	/**
	 * Adds a route that will be matched on any HTTP verb, but checks for an exact path match to the specified path.
	 * <p>
	 * If you are used to thinking in terms of PHP-style or WSGI-style applications, this is the method you will be most
	 * familiar with. It handles an exact path the way you are used to, with no regard for the HTTP Verb that was used,
	 * matching on any type of GET, POST, PUT, DELETE, etc. The specified handler will be called to responsd to the
	 * requested page.
	 *
	 * @param path Exact string path portion of the URL to match
	 * @param handler The object that will respond to the calls
	 * @see Route
	 */
	public Route addRoute(String path, HttpHandler handler) {
		return this.addRoute(Verb.ANY, path, handler);
	}

	/**
	 * Similar to {@link #addRoute(String, HttpHandler)} except it allows you to specify the exact verb that you want to
	 * match against.
	 * <p>
	 * Useres most familiar with a Servlet's style of doPost, doGet, etc might be most comfortable with this method, as
	 * it allows you to control specifically for the verb type of the incoming transaction.
	 *
	 * @param verb The verb to match against (see {@link Verb#matches(Object)}) the incoming request
	 * @param path The exact path portion of the URL to match
	 * @param handler The object that will respond to these calls
	 * @see Route
	 */
	public Route addRoute(Verb verb, String path, HttpHandler handler) {
		Route route = new Route(verb, path);
		return this.addRoute(route, handler);
	}

	/**
	 * Establishes an exact match path.
	 * <p>
	 * This class is of limited usefulness. Its only difference from the {@link #addRoute(Verb, String, HttpHandler)}
	 * method is that it ignores the "matching" definition of the {@link Verb#ANY} special value and forces a check for
	 * strict equality of the Verb as well, even if the specified Verb is Verb.ANY. But it is included here for the sake
	 * of completeness
	 *
	 * @param verb The HTTP Verb to match against exactly
	 * @param path The exact path portion of the URL to match
	 * @param handler The object that will respond to these calls
	 * @see ExactRoute
	 */
	public Route addExactRoute(Verb verb, String path, HttpHandler handler) {
		Route route = new ExactRoute(verb, path);
		return this.addRoute(route, handler);
	}

	/**
	 * Adds a prefix-matching route to the matcher
	 * <p>
	 * This will allow you to route calls based on a specific prefix route matching scheme. This can be extremely useful
	 * for breaking your site up into multiple nested Routers, or including an external application as a portion within
	 * this application. For instance, you might write a sweet Chat application that can be ported to all your gully
	 * applications, or you might want to create a single sub-router that handles portions of your site that are behind
	 * an authentication mechanism.
	 *
	 * @param verb The HTTP verb to match (see {@link Verb#matches(Object)})
	 * @param path The exact string prefix portion of the URL to match
	 * @param handler The object that will respond to these calls
	 */
	public Route addPrefixRoute(final Verb verb, final String path, final HttpHandler handler) {
		Route route = new PrefixRoute(verb, path);
		return this.addRoute(route, handler);
	}

	/**
	 * Same as {@link #addPrefixRoute(Verb, String, HttpHandler)}, but defaults Verb to {@link Verb#ANY}
	 *
	 * @param path
	 * @param handler
	 */
	public Route addPrefixRoute(final String path, final HttpHandler handler) {
		return this.addPrefixRoute(Verb.ANY, path, handler);
	}

	/**
	 * Adds a parameterized route to the matcher
	 * <p>
	 * This allows you to define parameterized routes. For instance, on a blog you might want to match all URL paths
	 * that fit the form of "/post/{some_id}". You can accomplish this through this method with the syntax you see to
	 * the left. A parameterized route may have multiple parameters in it, each of which will be accessible to your
	 * handler via the {@link HttpServerExchange#getAttachment(AttachmentKey)} method. Invoke it with the value of
	 * {@link #ATTACHMENT_KEY} to fetch out the parameters.
	 *
	 * @param verb The Verb to match against
	 * @param path The parameterized path portion of the URL
	 * @param handler The object that will respond to these calls
	 */
	public Route addParameterizedRoute(final Verb verb, final String path, final HttpHandler handler) {
		Route route = new ParameterizedRoute(verb, path);
		return this.addRoute(route, handler);
	}

	/**
	 * Same as {@link #addParameterizedRoute(Verb, String, HttpHandler)} with {@link Verb#ANY} set as verb
	 *
	 * @param path
	 * @param handler
	 */
	public Route addParameterizedRoute(final String path, final HttpHandler handler) {
		return this.addParameterizedRoute(Verb.ANY, path, handler);
	}

	public void handleRequest(HttpServerExchange exchange) throws Exception {
		this.handleRequest(exchange, true);
	}

	/**
	 * Finds the matching route and handler and, optionally, dispatches the request to that method.
	 * <p>
	 *     In order to facilitate simple extensions of this class which may desire building on the behavior of this
	 *     routing, this method is offered as the solution to permit wrapping calls to the Router and handler in any
	 *     way that the extender sees fit.
	 * </p>
	 * <p>
	 *     Invoking this method with "true" willl cause it to execute the handler that it locates as the default.
	 *     Otherwise, it will only attach the results it finds to the exchange and then exit cleanly. If you call the
	 *     public version of this method without the boolean, it will execute this method with the second argument being
	 *     set to "true". However, if you want to customize the behavior of this class, you can override the public
	 *     version of this method, invoke this protected method, and fetch the attachment results to operate on later.
	 * </p>
	 *
	 * @param exchange The exhcange object being handled currently
	 * @param execute True to execute the defined handler after attaching it, false to only attach it
	 * @throws Exception
	 */
	protected void handleRequest(HttpServerExchange exchange, boolean execute) throws Exception {
		final Verb verb = Verb.valueOf(exchange.getRequestMethod().toString());
		final String path = exchange.getRelativePath();
		final Route route = new Route(verb, path);
		final HandlerMatch handlerMatch = this.matcher.matchRoute(route);
		if (handlerMatch.getHandler() == null) {
			if (execute) {
				this.defaultHandler.handleRequest(exchange);
			} else {
				handlerMatch.setHandler(this.defaultHandler);
			}
		}
		exchange.putAttachment(PlainRouter.ATTACHMENT_KEY, new RouteAttachment(handlerMatch));
		if (execute) {
			final HttpHandler handler = handlerMatch.getHandler();
			if (handler != null) {
				final String remainder = handlerMatch.getRemainder();
				if (remainder != null) {
					exchange.setRelativePath(remainder);
				}
				handler.handleRequest(exchange);
			} else {
				this.defaultHandler.handleRequest(exchange);
			}
		}
	}

	public class RouteAttachment {
		private Map<String, String> parameters;
		private Route route;
		private HttpHandler handler;

		public RouteAttachment(final Map<String, String> parameters, final Route route, final HttpHandler handler) {
			this.parameters = parameters;
			this.route = route;
			this.handler = handler;
		}

		public RouteAttachment(final HandlerMatch match) {
			this(match.getParameters(), match.getRoute(), match.getHandler());
		}

		public Map<String, String> getParameters() {
			return this.parameters;
		}

		public Route getRoute() {
			return this.route;
		}

		public HttpHandler getHandler() {
			return this.handler;
		}
	}
}
