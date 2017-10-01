package com.thehellings.gully.config;

import com.thehellings.gully.handlers.Error404;
import io.undertow.server.HttpHandler;

/**
 * Defines configuration options for a new Gully server. Override to configure different defaults.
 *
 * <p>
 *     Rather than force configuration to be handled in external files, override this class in order to defined the
 *     set of defaults for your environment.
 * </p>
 */
public class Configuration {
	private boolean displayBacktraces = false;
	private Environment environment;
	private HttpHandler httpHandler;

	/**
	 * Specifies a base set of configurations.
	 *
	 * @param environment The environment specification
	 * @param httpHandler The base HttpHandler that will filter all requests
	 */
	public Configuration(final Environment environment, final HttpHandler httpHandler) {
		this.environment = environment;
		this.httpHandler = httpHandler;
	}

	/**
	 * Default handler will be set as the Error404 handler.
	 *
	 * @param environment The environment to configure for this server
	 */
	public Configuration(final Environment environment) {
		this(environment, new Error404());
	}

	/**
	 * Environment will be set to {@link DefaultEnvironment}
	 *
	 * @param httpHandler The base handler for this class
	 */
	public Configuration(final HttpHandler httpHandler) {
		this(new DefaultEnvironment("default"), httpHandler);
	}

	/**
	 * Tests if this Configuration's {@link Environment} equals another
	 *
	 * @param environment The Environment to compare against
	 * @return The result of {@link Environment#equals(Object)} on the two Environments
	 */
	public boolean isEnvironment(final Environment environment) {
		return this.environment.equals(environment);
	}

	public HttpHandler getBaseHandler() {
		return this.httpHandler;
	}

	/**
	 * Return true to start up an HTTP server, false to suppress. Default true.
	 *
	 * @return True if this configuration should serve HTTP
	 */
	public boolean isHttpServer() {
		return true;
	}

	/**
	 * Returns the HTTP port to serve, if applicable. Defaults to 8080.
	 *
	 * @return The integer number of the port to run on (between 1 and 65535)
	 */
	public int getHttpPort() {
		return 8080;
	}

	/**
	 * IP address to listen on. Defaults to blank (all)
	 *
	 * @return A string representation of the IP address to listen on, blank to listen on all IPs on the system
	 */
	public String getHttpHost() {
		return "";
	}

	/**
	 * The number of IO threads to start up (reasonable defaults range from 1 - 2*cores).
	 *
	 * <p>
	 *     Usees Java's Runtime to determine the number of available processors to the JVM and sets the number of IO
	 *     threads to this value, accordingly.
	 * </p>
	 *
	 * @return The number of IO threads to spawn in the base process
	 */
	public int getIoThreads() {
		return Runtime.getRuntime().availableProcessors();
	}

	/**
	 * The number of "worker" threads to start up.
	 *
	 * <p>
	 *     In Undertow, all calls are initially executed in the IO thread pool. If the handler is expecting to execute
	 *     a long-running process, such as database IO, external integration, intensive computation, etc then it should
	 *     pass the execution off to a handler thread. This number of threads could easily exceed the number of cores
	 *     available on a machine if the likelihood is threads waiting for IO, whereas it should not exceed the number
	 *     of cores if it is believed all the cores will be locking in processor-bound tasks.
	 * </p>
	 * <p>
	 *     Default value retunred is twice the number of available cores.
	 * </p>
	 *
	 * @return The number of worker threads to spawn in the Undertow process
	 */
	public int getWorkerThreads() {
		return 2 * Runtime.getRuntime().availableProcessors();
	}
}
