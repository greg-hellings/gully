package com.thehellings.gully.config;

import java.util.HashMap;
import java.util.Map;

/**
 * Defines a default implementation of the environment type with simple functionality.
 *
 * <p>
 *     The default implementation supports only the three basic pieces of functionality defined in the interface,
 *     allowing it to identify whether an environment is production/development-like and a basic set of variables, which
 *     are not populated by this class at all.
 * </p>
 */
public class DefaultEnvironment implements Environment {
	/**
	 * True if this environment should be treated as development-like
	 */
	protected boolean isDevelopmentLike;
	/**
	 * True if this environment should be treated as production-like
	 */
	protected boolean isProductionLike;
	/**
	 * The set of variables specific to this environment
	 */
	protected Map<String, Object> variables;
	/**
	 * A name for the environment
	 */
	protected String environemtnName;

	public DefaultEnvironment(final String environmentName, final boolean isDevelopmentLike, final boolean isProductionLike, final Map<String, Object> variables) {
		this.isDevelopmentLike = isDevelopmentLike;
		this.isProductionLike = isProductionLike;
		this.variables = variables;
		this.environemtnName = environmentName;
	}

	/**
	 * Creates a default, empty set of variables.
	 *
	 * @param environmentName
	 * @param isDevelopmentLike
	 * @param isProductionLike
	 */
	public DefaultEnvironment(final String environmentName, final boolean isDevelopmentLike, final boolean isProductionLike) {
		this(environmentName, isDevelopmentLike, isProductionLike, new HashMap<String, Object>());
	}

	/**
	 * Creates a default, empty environment which is neither dev-like nor production-like
	 * @param environmentName
	 */
	public DefaultEnvironment(final String environmentName) {
		this(environmentName, false, false);
	}

	/**
	 * Compares two environment NAMES
	 *
	 * <p>
	 *     Only compares the names of two environments to determine if they are equal. It will not compare the specific
	 *     variable names within them. You will need to either extend this class or implement your own Environemtn if you
	 *     wish to have additional, deeper checks on environment equality.
	 * </p>
	 *
	 * @param o Other object against which to compare this one
	 * @return True if this object has the same name as the other environment, false otherwise
	 */
	@Override
	public boolean equals(Object o) {
		if (o == this) {
			return true;
		}
		if (! (o instanceof DefaultEnvironment)) {
			return false;
		}
		DefaultEnvironment other = (DefaultEnvironment) o;
		return this.environemtnName.equals(other.environemtnName);
	}

	@Override
	public boolean isLikeProduction() {
		return this.isProductionLike;
	}

	@Override
	public boolean isLikeDevelopment() {
		return this.isDevelopmentLike;
	}

	@Override
	public Map<String, Object> getVariables() {
		return this.variables;
	}
}
