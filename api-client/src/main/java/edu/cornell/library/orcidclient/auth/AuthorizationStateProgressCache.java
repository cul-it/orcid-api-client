package edu.cornell.library.orcidclient.auth;

import edu.cornell.library.orcidclient.actions.ApiScope;
import edu.cornell.library.orcidclient.exceptions.OrcidClientException;

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
	 * Fetch the AuthorizationStateProgress that has this ID.
	 * 
	 * @return The status, or null
	 */
	AuthorizationStateProgress getByID(String id) throws OrcidClientException;

	/**
	 * Fetch the AuthorizationStateProgress that has this scope.
	 * 
	 * @return The status, or null
	 */
	AuthorizationStateProgress getByScope(ApiScope scope)
			throws OrcidClientException;

	/**
	 * Remove any transient AuthorizationStateProgress objects with this scope
	 * from the cache.
	 * 
	 * This does not guarantee that the next corresponding call to getByScope()
	 * will return null, since persistent state may be revealed that had been
	 * masked by the transient objects.
	 */
	void clearScopeProgress(ApiScope scope) throws OrcidClientException;
}
