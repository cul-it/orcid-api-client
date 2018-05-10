package edu.cornell.library.orcidclient.auth;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.cornell.library.orcidclient.OrcidClientException;
import edu.cornell.library.orcidclient.actions.ApiScope;

/**
 * Implement the cache with the usual map, plus a list that maintains history.
 */
public class StubAuthorizationStateProgressCache
		implements AuthorizationStateProgressCache {
	private final List<AuthorizationStateProgress> list = new ArrayList<>();
	private final Map<ApiScope, AuthorizationStateProgress> map = new HashMap<>();

	public void set(AuthorizationStateProgress progress) {
		// Initialization - not part of the history.
		map.put(progress.getScope(), progress);
	}

	public List<AuthorizationStateProgress> getList() {
		return new ArrayList<>(list);
	}

	@Override
	public void store(AuthorizationStateProgress progress)
			throws OrcidClientException {
		list.add(progress);
		map.put(progress.getScope(), progress);
	}

	@Override
	public AuthorizationStateProgress getByID(String id)
			throws OrcidClientException {
		for (AuthorizationStateProgress progress : map.values()) {
			if (progress.getId().equals(id)) {
				return progress;
			}
		}
		return null;
	}

	@Override
	public AuthorizationStateProgress getByScope(ApiScope scope)
			throws OrcidClientException {
		return map.get(scope);
	}

	@Override
	public void clearScopeProgress(ApiScope scope) throws OrcidClientException {
		map.remove(scope);
	}

}
