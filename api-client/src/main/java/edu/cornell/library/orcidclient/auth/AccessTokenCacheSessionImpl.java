package edu.cornell.library.orcidclient.auth;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpSession;

import edu.cornell.library.orcidclient.actions.ApiScope;
import edu.cornell.library.orcidclient.exceptions.OrcidClientException;

/**
 * A rudimentary implementation of the AccessTokenCache, where access tokens are
 * remembered only for the duration of an HTTP session.
 */
public class AccessTokenCacheSessionImpl implements AccessTokenCache {
	private static final String ATTRIBUTE_NAME = AccessTokenCacheSessionImpl.class
			.getName();

	public static AccessTokenCacheSessionImpl getInstance(HttpSession session) {
		Object attribute = session.getAttribute(ATTRIBUTE_NAME);
		if (!(attribute instanceof AccessTokenCacheSessionImpl)) {
			attribute = new AccessTokenCacheSessionImpl();
			session.setAttribute(ATTRIBUTE_NAME, attribute);
		}
		return (AccessTokenCacheSessionImpl) attribute;
	}

	private final Map<ApiScope, AccessToken> tokenMap = new HashMap<>();
	
	@Override
	public void addAccessToken(AccessToken accessToken)
			throws OrcidClientException {
		tokenMap.put(accessToken.getScope(), accessToken);
	}

	@Override
	public AccessToken getToken(ApiScope scope) throws OrcidClientException {
		return tokenMap.get(scope);
	}

	@Override
	public void removeAccessToken(AccessToken accessToken)
			throws OrcidClientException {
		tokenMap.values().remove(accessToken);
	}

}
