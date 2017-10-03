package com.thehellings.gully.css;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.thehellings.gully.config.Environment;
import com.thehellings.gully.config.DefaultEnvironment;

import java.io.File;
import java.io.IOException;

public class CSS implements HttpHandler {
	private File baseFile;
	private Environment environment;

	public CSS(final File file) {
		this(file, new DefaultEnvironment("default"));
	}

	public CSS(final File file, final Environment environment) {
		this.baseFile = file;
		this.environment = environment;
	}

	@Override
	public void handle(HttpExchange httpExchange) throws IOException {

	}
}
