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
 *     This class creatues an entirely new server object, according to the variables configured in the provided
 *     {@link Configuration} object or using reasonable defaults if none is provided.
 * </p>
 */
public class Server {
	private Configuration configuration;

	public Server(final Configuration configuration) {
		this.configuration = configuration;
	}

	public Server(final Environment environment, final HttpHandler httpHandler) {
		this(new Configuration(environment, httpHandler));
	}

	public Server() {
		this(new Configuration(new DefaultEnvironment("Default")));
	}

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
		builder.build().start();
	}
}
