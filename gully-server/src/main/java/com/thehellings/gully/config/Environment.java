package com.thehellings.gully.config;

import java.util.Map;

public interface Environment {
	/**
	 * A convenience method to help your code determine if this environment is "like" your production environment.
	 *
	 * <p>
	 *     How you use this and what it means to your code is entirely up to you. In practice this is only used inside
	 *     of gully to determine:
	 * <ul>
	 *     <li>Whether to suppress printing of stack traces in 500 exceptions</li>
	 * </ul>
	 *
	 * @return true if this environment is similar to production
	 */
	public boolean isLikeProduction();

	/**
	 * A convenience method to help your code determine if this environment is "like" your development environment.
	 *
	 * <p>
	 *     How you use this and what it means to your code is entirely up to you.
	 * </p>
	 *
	 * @return true if this environment is similar to production
	 */
	public boolean isLikeDevelopment();

	/**
	 * Retrieve a set of environment-specific variables.
	 *
	 * <p>
	 *     This allows a particular environment implementation to store and create or return variables specific to its
	 *     own configuration. These can be entirely used by the definer of the Environment and could be anything from
	 *     database connection information to styling themes to arbitrary data stores.
	 * </p>
	 *
	 * @return A map of environment-specific variables
	 */
	public Map<String, Object> getVariables();
}
