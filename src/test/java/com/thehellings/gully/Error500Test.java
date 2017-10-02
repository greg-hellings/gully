package com.thehellings.gully;

import com.thehellings.gully.config.DefaultEnvironment;
import com.thehellings.gully.handlers.Error500;
import org.apache.http.client.fluent.Request;
import org.apache.http.client.fluent.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class Error500Test {
	private Server server;

	@Before
	public void before() throws Exception {
		server = new Server(new DefaultEnvironment("Environment"), new Error500());
		server.start();
		Thread.sleep(1000);
	}

	@After
	public void after() {
		server.stop();
	}

	@Test
	public void testError500ReturnsProperStatusCode() throws Exception {
		Response response = Request.Get("http://localhost:8080").execute();

		assertEquals(500, response.returnResponse().getStatusLine().getStatusCode());
	}
}
