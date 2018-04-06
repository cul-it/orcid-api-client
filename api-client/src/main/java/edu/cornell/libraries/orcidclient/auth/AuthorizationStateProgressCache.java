/* $This file is distributed under the terms of the license in /doc/license.txt$ */

package edu.cornell.libraries.orcidclient.auth;

import java.util.HashMap;
import java.util.Map;

import edu.cornell.libraries.orcidclient.actions.ApiScope;

/**
 * TODO
 * 
 * THIS SHOULD BE AN INTERFACE, WITH A SESSION-BASED IMPLEMENTATION.
 */
public class AuthorizationStateProgressCache {
	
	// ----------------------------------------------------------------------
	// The factory
	// ----------------------------------------------------------------------

	private static volatile AuthorizationStateProgressCache instance;
	
	public static synchronized AuthorizationStateProgressCache getCache(
			OrcidAuthorizationClientContext context) {
		if (instance == null) {
			instance = new AuthorizationStateProgressCache(context);
		}
		return instance;
	}

	// ----------------------------------------------------------------------
	// The instance
	// ----------------------------------------------------------------------

	private final OrcidAuthorizationClientContext context;
	private final Map<String, AuthorizationStateProgress> progressMap = new HashMap<>();
	

	private AuthorizationStateProgressCache(
			OrcidAuthorizationClientContext context) {
		this.context = context;
	}

	public void store(AuthorizationStateProgress progress) {
		progressMap.put(progress.getId(), progress);
	}

	/**
	 * Get the requests progress value, or null.
	 */
	public AuthorizationStateProgress getByID(String id) {
		return progressMap.get(id);
	}

	/**
	 * @param scope
	 * @return
	 */
	public AuthorizationStateProgress getByScope(ApiScope scope) {
		throw new RuntimeException(
				"AuthorizationStateProgressCache.getByScope not implemented.");
	}

	/**
	 * @param scope
	 */
	public void clearScopeProgress(ApiScope scope) {
		throw new RuntimeException(
				"AuthorizationStateProgressCache.clearScopeProgress not implemented.");
	}

}
