package com.thehellings.gully.route;

import java.util.Map;

public class RouteMatch {
	private boolean matches;
	private Map<String, String> attachment;
	String remainder;

	public RouteMatch(final boolean matches, Map<String, String> attachment) {
		this(matches, attachment, null);
	}

	public RouteMatch(final boolean matches, final Map<String, String> attachment, final String remainder) {
		this.matches = matches;
		this.attachment = attachment;
		this.remainder = remainder;
	}

	public RouteMatch(boolean matches) {
		this(matches, null, null);
	}

	public RouteMatch(final boolean matches, final String remainder) {
		this(matches, null, remainder);
	}

	public boolean getMatching() {
		return this.matches;
	}

	public Map<String, String> getAttachment() {
		return this.attachment;
	}

	public String getRemainder() {
		return this.remainder;
	}
}
