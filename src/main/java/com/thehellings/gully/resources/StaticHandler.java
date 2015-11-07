package com.thehellings.gully.resources;

import java.io.File;
import io.undertow.server.handlers.resource.ResourceHandler;
/**
 * Use this class to serve up static resources from a root on your filesystem.
 *
 * By default, it's going to pull resources from within your classpath resource folder. In development mode you
 * probably want to use this on the root of something like src/main/resources/somestaticpath. In production, all of
 * the resources there will be bundled up into your deployed jar.
 *
 * If you combine this with the provided Options class for dependency injection, you'll find that the resources are
 * available for instant edit, add, update, or removal in development and pull from the JAR environment in production
 * mode. You can add this class to your base router as such:
 *
 * <code>
 *     PathHandler handler = new PathHandler();
 *     handler.addPrefixPath("/static/js", new StaticHandler("path/to/js/files"));
 * </code>
 *
 * This class will serve up all files underneath of that path at the point /static/js within your webroot context,
 * however, it will not traverse symlinks.
 */
public class StaticHandler extends ResourceHandler {
	public StaticHandler(final String resources) {
		super(new StaticManager(resources));
	}

	public StaticHandler(final File resources) {
		super(new StaticManager(resources));
	}
}
