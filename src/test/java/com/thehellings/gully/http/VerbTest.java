package com.thehellings.gully.http;

import junit.framework.TestCase;

public class VerbTest extends TestCase {

	public void testMatches() throws Exception {
		assertTrue("GET matches GET", Verb.GET.matches(Verb.GET));
		assertTrue("POST matches POST", Verb.POST.matches(Verb.POST));
		assertTrue("ANY matches GET", Verb.ANY.matches(Verb.GET));
		assertTrue("GET matches ANY", Verb.GET.matches(Verb.ANY));
		assertFalse("Verb doesn't match non-Verbs", Verb.DELETE.matches("hi"));
	}
}