/* $This file is distributed under the terms of the license in /doc/license.txt$ */

package edu.cornell.libraries.orcidclient.auth;

import edu.cornell.libraries.orcidclient.OrcidClientException;
import edu.cornell.libraries.orcidclient.actions.ApiScope;

/**
 * Hold that progress of the authentication dance(s).
 * 
 * Implementations: a session-based cache for VIVO integration, a
 * session-plus-database cache for Scholars integration, a session-plus-flatfile
 * cache for the test-webapp, a stub for unit tests.
 */
public interface AuthorizationStateProgressCache {
	/**
	 * Store this progress in the cache.
	 * 
	 * Replace any previous progress for the same scope, or with the same ID.
	 */
	void store(AuthorizationStateProgress progress) throws OrcidClientException;

	/**
	 * Fetch the AuthorizationStateProgree that has this ID.
	 * 
	 * @return The status, or null
	 */
	AuthorizationStateProgress getByID(String id) throws OrcidClientException;

	/**
	 * Fetch the AuthorizationStateProgree that has this scope.
	 * 
	 * @return The status, or null
	 */
	AuthorizationStateProgress getByScope(ApiScope scope) throws OrcidClientException;

	/**
	 * Remove any AuthorizationStateProgree that has this scope.
	 */
	void clearScopeProgress(ApiScope scope) throws OrcidClientException;
}
