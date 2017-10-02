package com.thehellings.gully;

import com.thehellings.gully.handlers.Error404;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class ServerTest {
	@Test
	public void testDefaultServer() {
		Server server = new Server();

		assertTrue(server.getConfiguration().getBaseHandler().getClass() == Error404.class);
	}
}
