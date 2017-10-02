package com.thehellings.gully;

import com.thehellings.gully.config.Configuration;
import com.thehellings.gully.config.DefaultEnvironment;
import com.thehellings.gully.config.Environment;
import io.undertow.Undertow;
import io.undertow.server.HttpHandler;

/**
 * Primary entry-point for the Gully server.
 *
 * <p>
 *     This class creates an entirely new server object, according to the variables configured in the provided
 *     {@link Configuration} object or using reasonable defaults if none is provided.
 * </p>
 */
public class Server {
	private Configuration configuration;
	private Undertow undertow;

	public Server(final Configuration configuration) {
		this.configuration = configuration;
	}

	public Server(final Environment environment, final HttpHandler httpHandler) {
		this(new Configuration(environment, httpHandler));
	}

	public Server() {
		this(new Configuration(new DefaultEnvironment("Default")));
	}

	/**
	 * Configure, create, and start the Undertow server for this particular configuration.
	 */
	public void start() {
		Undertow.Builder builder = Undertow.builder();
		if (this.configuration.isHttpServer()) {
			builder.addHttpListener(this.configuration.getHttpPort(), this.configuration.getHttpHost());
		}
		// TODO: Implemnt AJP listeners
		// TODO: Implement WebSocket Listeners
		builder.setIoThreads(this.configuration.getIoThreads());
		builder.setWorkerThreads(this.configuration.getWorkerThreads());
		builder.setHandler(this.configuration.getBaseHandler());
		this.undertow = builder.build();
		this.undertow.start();
	}

	/**
	 * Attempt to stop the Undertow server for this configuration.
	 */
	public void stop() {
		this.undertow.stop();
	}

	/**
	 * Fetch the current configuration
	 *
	 * @return The configuration created when this server was initialized
	 */
	public Configuration getConfiguration() {
		return this.configuration;
	}
}
