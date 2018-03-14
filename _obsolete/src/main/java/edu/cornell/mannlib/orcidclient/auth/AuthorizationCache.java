/* $This file is distributed under the terms of the license in /doc/license.txt$ */

package edu.cornell.mannlib.orcidclient.auth;

import static edu.cornell.mannlib.orcidclient.actions.ApiAction.NO_ACTION;

import java.util.EnumMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import edu.cornell.mannlib.orcidclient.actions.ApiAction;

/**
 * TODO
 */
public class AuthorizationCache {
	private static final Log log = LogFactory.getLog(AuthorizationCache.class);

	private static final String ATTRIBUTE_NAME = AuthorizationCache.class.getName();

	private static AuthorizationCache defaultCache = null;

	public static AuthorizationCache getCache(HttpServletRequest req) {
		if (req == null) {
			if (defaultCache == null) {
				AuthorizationCache cache = new AuthorizationCache();
				defaultCache = cache;
			}
			return defaultCache;
		} else {
			HttpSession session = req.getSession();
			Object o = session.getAttribute(ATTRIBUTE_NAME);
			if (o instanceof AuthorizationCache) {
				return (AuthorizationCache) o;
			} else {
				AuthorizationCache cache = new AuthorizationCache();
				session.setAttribute(ATTRIBUTE_NAME, cache);
				return cache;
			}
		}
	}

	// ----------------------------------------------------------------------
	// The instance
	// ----------------------------------------------------------------------

	Map<ApiAction, AuthorizationStatus> map = new EnumMap<>(ApiAction.class);

	public AuthorizationStatus store(AuthorizationStatus auth) {
		map.put(auth.getAction(), auth);
		return auth;
	}

	public AuthorizationStatus getStatus(ApiAction action) {
		AuthorizationStatus authStatus = map.get(action);
		if (authStatus == null) {
			authStatus = AuthorizationStatus.empty(action);
		}
		log.debug("getStatus for action: " + action + ", status=" + authStatus);
		return authStatus;
	}

	public AuthorizationStatus getStatus(String token) {
		for (AuthorizationStatus auth : map.values()) {
			if (auth.getId().equals(token)) {
				log.debug("getStatus for token: " + token + ", status=" + auth);
				return auth;
			}
		}
		log.debug("getStatus for token: " + token + ", status=NONE");
		return AuthorizationStatus.empty(NO_ACTION);
	}

	public void clearStatus(ApiAction action) {
		map.remove(action);
	}

}
