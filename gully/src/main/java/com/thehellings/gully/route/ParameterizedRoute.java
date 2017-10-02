package com.thehellings.gully.route;

import com.thehellings.gully.http.Verb;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ParameterizedRoute extends Route {
	private List<Part> routeParts;
	public ParameterizedRoute(Verb verb, String route) {
		super(verb, route);
		if (route.charAt(0) == '/') {
			route = route.substring(1);
		}
		String[] parts = route.split("/");
		this.routeParts = new ArrayList<Part>(parts.length);
		for (String part : parts) {
			this.routeParts.add(new Part(part));
		}
	}

	@Override
	public RouteMatch matches(Object o) {
		if (! (o instanceof Route) ) {
			return new RouteMatch(false);
		}
		Route route = (Route) o;
		// Check verb, right off the bat
		if (!this.getVerb().matches(route.getVerb())) {
			return new RouteMatch(false);
		}
		String path = route.getPath();
		if (path.charAt(0) == '/') {
			path = path.substring(1);
		}
		String[] parts = path.split("/");
		// We don't handle prefixes
		if (parts.length > this.routeParts.size()) {
			return new RouteMatch(false);
		}
		Map <String, String> parameters = new HashMap<>();
		// Check component parts
		int i;
		for (i = 0; i < parts.length; ++i) {
			Part template = this.routeParts.get(i);
			if (!template.equals(parts[i])) {
				return new RouteMatch(false);
			}
			if (template.store()) {
				parameters.put(template.getName(), parts[i]);
			}
		}
		// Allows us to have multiple trailing segments of the URL which are missing ONLY IF all of them are parameters.
		// Continues searching for matches when there are more segments that _could_ be matched but which are not
		// present in the path being compared against
		for (; i < this.routeParts.size(); ++i) {
			Part template = this.routeParts.get(i);
			// Empty string segments are not to be matched, except by a parameter, thus if we encounter any path segments
			// at this point that do not match the empty string, then we have a match bust
			if (!template.equals("")) {
				return new RouteMatch(false);
			}
			parameters.put(template.getName(), "");
		}
		return new RouteMatch(true, parameters, "");
	}

	private class Part {
		private boolean isString;
		private String segment;

		/**
		 * Pass this the entire string portion of a path to be handled.
		 * <p>
		 * If the segment is wrapped with curly braces (that is, if it looks like {somename}) then the segment will
		 * match every value passed into it, including null values. In that case, the values should be retained and
		 * passed as a {@link java.util.Map} into the handler. Otherwise, the value should just be matched.
		 *
		 * @param portion literal path segment or name of segment match
		 */
		public Part(String portion) {
			if (portion.startsWith("{") && portion.endsWith("}")) {
				this.isString = false;
				this.segment = portion.substring(1, portion.length() - 1);
			} else {
				this.isString = true;
				this.segment = portion;
			}
		}

		@Override
		public boolean equals(Object o) {
			if (this.isString && o instanceof String && this.segment.equals(o)) {
				return true;
			} else if (!this.isString) {
				return true;
			}

			return false;
		}

		/**
		 * Use this to fetch back the name of the string, if you're finding that the value should be saved for
		 * later usages.
		 *
		 * @return Name of this segment to savee in pathing data
		 */
		public String getName() {
			return this.segment;
		}

		/**
		 * Indicates whether this value should be stored for sending to the handlers or not.
		 *
		 * @return true if the value should be stored, false otherwise
		 */
		public boolean store() {
			return !this.isString;
		}
	}
}
