package edu.cornell.library.orcidclient.auth;

import java.util.EnumMap;
import java.util.Map;

import edu.cornell.library.orcidclient.actions.ApiScope;
import edu.cornell.library.orcidclient.exceptions.OrcidClientException;

/**
 * The simplest possible implementation.
 */
public class StubAccessTokenCache implements AccessTokenCache {
	private Map<ApiScope, AccessToken> map = new EnumMap<>(ApiScope.class);

	@Override
	public void addAccessToken(AccessToken accessToken)
			throws OrcidClientException {
		map.put(accessToken.getScope(), accessToken);
	}

	@Override
	public AccessToken getToken(ApiScope scope) throws OrcidClientException {
		return map.get(scope);
	}

	@Override
	public void removeAccessToken(AccessToken accessToken)
			throws OrcidClientException {
		map.values().remove(accessToken);
	}

}
