package com.thehellings.gully.resources;

import io.undertow.server.handlers.resource.FileResourceManager;
import java.io.File;

/**
 * This is a helper class that really only lives to serve the {@link StaticHandler} class.
 *
 * See the other class for information on how to work with this one.
 */
class StaticManager extends FileResourceManager {
	public StaticManager(String resources) {
		super(new File(resources), 0, true, false);
	}

	public StaticManager(File resources) {
		super(resources, 0, true, false);
	}
}
