package com.thehellings.gully.http;

/**
 * Represents the most common HTTP request verbs that might be used by a client.
 * <p>
 *     Each verb matches the name of the corresponding action in the HTTP protocol. There is one value that is added
 *     to the list of verbs, and that is ANY. This value semantically represents any of the other values for comparison's
 *     sake. To that end, the method {@link #matches(Object)} is provided which checks that the current value "matches"
 *     the other one. This is defined as either being identical to it in value, or either value being ANY.
 * </p>
 */
public enum Verb {
	GET,
	POST,
	PUT,
	DELETE,
	OPTIONS,
	HEAD,
	PATCH,
	ANY;

	private Verb() {
	}

	public boolean matches(Object other) {
		if(!(other instanceof Verb)) {
			return false;
		} else {
			Verb v = (Verb)other;
			return this == v || this == ANY || v == ANY;
		}
	}
}
