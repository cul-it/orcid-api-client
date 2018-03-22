/* $This file is distributed under the terms of the license in /doc/license.txt$ */

package edu.cornell.libraries.orcidclient.auth;

import edu.cornell.libraries.orcidclient.actions.ApiScope;

/**
 * The tool that will help us walk through the 3-legged OAuth negotiation.
 * 
 * This is based on what has happened in this HTTP session, and what is
 * available from the persistent storage.
 */
public abstract class OrcidAuthorizationClient {
	/**
	 * Reflects the authorization status for a specified scope.
	 * 
	 * Note that we only try to get authorized if there is no AccessToken for
	 * this scope in the persistent storage.
	 */
	public enum AuthProcessResolution {
		/**
		 * We haven't tried to get authorized.
		 */
		NONE,

		/**
		 * We have an AccessToken.
		 */
		SUCCESS,

		/**
		 * The user denied authorization.
		 */
		DENIED,

		/**
		 * We tried to get authorization but encountered a system failure.
		 */
		FAILURE
	}

	/**
	 * Undo any progress that was made for this scope during this session.
	 */
	public abstract void resetProgress(ApiScope scope);

	/**
	 * Find out where we stand for this scope.
	 */
	public abstract AuthProcessResolution getAuthProcessResolution(
			ApiScope scope);

	/**
	 * Create a URL with appropriate parameters to seek authorization for this
	 * scope.
	 * 
	 * The URL can be sent to the browser as a re-direct to kick off the OAuth
	 * negotiation.
	 * 
	 * @param returnUrl
	 *            When the negotiation is complete, redirect the browser to this
	 *            URL.
	 */
	public abstract String buildAuthorizationCall(ApiScope scope,
			String returnUrl);

	/**
	 * Create a URL with appropriate parameters to seek authorization for this
	 * scope.
	 * 
	 * The URL can be sent to the browser as a re-direct to kick off the OAuth
	 * negotiation.
	 * 
	 * @param successUrl
	 *            If the negotiation succeeds, redirect the browser to this URL.
	 * @param failureUrl
	 *            If the negotiation fails, redirect the browser to this URL.
	 */
	public abstract String buildAuthorizationCall(ApiScope scope,
			String successUrl, String failureUrl);

	/**
	 * Get the AccessToken for this scope. It might have been obtained during
	 * this session, or it might be from the persistent storage.
	 * 
	 * @throws IllegalStateException
	 *             If the resolution for this scope is not SUCCESS.
	 */
	public abstract AccessToken getAccessToken(ApiScope scope)
			throws IllegalStateException;
}
