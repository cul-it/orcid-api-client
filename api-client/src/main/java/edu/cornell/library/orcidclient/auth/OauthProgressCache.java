package edu.cornell.library.orcidclient.auth;

import edu.cornell.library.orcidclient.actions.ApiScope;
import edu.cornell.library.orcidclient.exceptions.OrcidClientException;

/**
 * Hold the progress of the OAuth authentication dance(s).
 * 
 * The only meaningful implementation is based on an HTTP session. Persistent
 * storage of access tokens (the result of a successful dance) is handled by the
 * AccessTokenCache.
 */
public interface OauthProgressCache {
	/**
	 * Store this progress in the cache.
	 * 
	 * Replace any previous progress for the same scope, or with the same ID.
	 */
	void store(OauthProgress progress) throws OrcidClientException;

	/**
	 * Fetch the OauthProgress that has this ID.
	 * 
	 * @return The status, or null
	 */
	OauthProgress getByID(String id) throws OrcidClientException;

	/**
	 * Fetch the OauthProgress that has this scope.
	 * 
	 * @return The status, or null
	 */
	OauthProgress getByScope(ApiScope scope) throws OrcidClientException;
}
