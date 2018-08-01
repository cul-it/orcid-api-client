package edu.cornell.library.orcidclient.auth;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.cornell.library.orcidclient.actions.ApiScope;
import edu.cornell.library.orcidclient.exceptions.OrcidClientException;

/**
 * Implement the cache with the usual map, plus a list that maintains history.
 */
public class StubOauthProgressCache implements OauthProgressCache {
	private final List<OauthProgress> list = new ArrayList<>();
	private final Map<ApiScope, OauthProgress> map = new HashMap<>();

	public void set(OauthProgress progress) {
		// Initialization - not part of the history.
		map.put(progress.getScope(), progress);
	}

	public List<OauthProgress> getList() {
		return new ArrayList<>(list);
	}

	@Override
	public void store(OauthProgress progress) throws OrcidClientException {
		list.add(progress.copy());
		map.put(progress.getScope(), progress);
	}

	@Override
	public OauthProgress getByID(String id) throws OrcidClientException {
		for (OauthProgress progress : map.values()) {
			if (progress.getId().equals(id)) {
				return progress;
			}
		}
		return null;
	}

	@Override
	public OauthProgress getByScope(ApiScope scope)
			throws OrcidClientException {
		return map.get(scope);
	}
}
