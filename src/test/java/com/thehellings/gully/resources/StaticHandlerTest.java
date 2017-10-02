package com.thehellings.gully.resources;

import com.thehellings.gully.PlainRouter;
import com.thehellings.gully.Server;
import com.thehellings.gully.config.Configuration;
import org.apache.http.client.fluent.Request;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;

import static org.junit.Assert.assertEquals;

public class StaticHandlerTest {
	private Server server;
	private PlainRouter router;

	@Before
	public void before() throws  Exception {
		router = new PlainRouter();
		server = new Server(new Configuration(router));
		server.start();
		Thread.sleep(1000);
	}

	@After
	public void after() throws Exception {
		server.stop();
		Thread.sleep(1000);
	}

	@Test
	public void testStringConstructor() throws Exception {
		String path = StaticHandlerTest.class.getClassLoader().getResource("static/empty.txt").getPath();
		router.addPrefixRoute("/static", new StaticHandler(path));

		assertEquals("Empty file", "", Request.Get("http://localhost:8080/static")
				.execute()
				.returnContent()
				.asString());
	}

	@Test
	public void testFileConstructor() throws Exception {
		File path = new File(StaticHandlerTest.class.getClassLoader().getResource("static").getFile());
		router.addPrefixRoute("/file", new StaticHandler(path));

		assertEquals("Hello file", "Hello world!", Request.Get("http://localhost:8080/file/hello.txt")
				.execute()
				.returnContent()
				.asString());
	}
}
