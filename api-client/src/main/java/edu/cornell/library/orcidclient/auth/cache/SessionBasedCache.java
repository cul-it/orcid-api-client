package edu.cornell.library.orcidclient.auth.cache;

import java.util.EnumMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import edu.cornell.library.orcidclient.OrcidClientException;
import edu.cornell.library.orcidclient.actions.ApiScope;
import edu.cornell.library.orcidclient.auth.AuthorizationStateProgress;
import edu.cornell.library.orcidclient.auth.AuthorizationStateProgressCache;

/**
 * A cache implementation that provides medium-length memory: the length of an
 * HTTP Session.
 */
public class SessionBasedCache implements AuthorizationStateProgressCache {
	private static final Log log = LogFactory.getLog(SessionBasedCache.class);

	private static final String SESSION_KEY = SessionBasedCache.class.getName();

	// ----------------------------------------------------------------------
	// Factory
	// ----------------------------------------------------------------------

	public static SessionBasedCache getCache(HttpServletRequest req) {
		return getCache(req.getSession());
	}

	public static SessionBasedCache getCache(HttpSession session) {
		Object attribute = session.getAttribute(SESSION_KEY);
		if (attribute instanceof SessionBasedCache) {
			return (SessionBasedCache) attribute;
		} else {
			SessionBasedCache cache = new SessionBasedCache();
			session.setAttribute(SESSION_KEY, cache);
			return cache;
		}
	}

	// ----------------------------------------------------------------------
	// The instance
	// ----------------------------------------------------------------------

	private final Map<ApiScope, AuthorizationStateProgress> map = new EnumMap<>(
			ApiScope.class);

	@Override
	public void store(AuthorizationStateProgress progress)
			throws OrcidClientException {
		AuthorizationStateProgress previous = map.put(progress.getScope(),
				progress);
		log.debug("Stored: " + progress + ", previously was: " + previous);
	}

	@Override
	public AuthorizationStateProgress getByID(String id)
			throws OrcidClientException {
		for (AuthorizationStateProgress progress : map.values()) {
			if (progress.getId().equals(id)) {
				log.debug("Found by ID: " + progress);
				return progress;
			}
		}
		log.debug("Nothing found for ID: " + id);
		return null;
	}

	@Override
	public AuthorizationStateProgress getByScope(ApiScope scope)
			throws OrcidClientException {
		AuthorizationStateProgress progress = map.get(scope);
		log.debug("Found for scope: " + scope + ", " + progress);
		return progress;
	}

	@Override
	public void clearScopeProgress(ApiScope scope) throws OrcidClientException {
		AuthorizationStateProgress previous = map.remove(scope);
		log.debug("Cleared scope: " + scope + ", previously was: " + previous);
	}

}
