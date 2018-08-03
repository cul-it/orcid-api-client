package edu.cornell.library.orcidclient.auth;

import java.util.EnumMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import edu.cornell.library.orcidclient.actions.ApiScope;
import edu.cornell.library.orcidclient.exceptions.OrcidClientException;

/**
 * A progress cache tied to an HTTP Session
 * 
 * This is the only sensible implementation strategy, since the progress being
 * tracked is based on a series of HTTP requests.
 * 
 * Since multiple HTTP requests may come simultaneously, this must be
 * thread-safe.
 */
public class OauthProgressCacheImpl implements OauthProgressCache {
	private static final Log log = LogFactory
			.getLog(OauthProgressCacheImpl.class);

	private static final String SESSION_ATTRIBUTE_KEY = OauthProgressCacheImpl.class
			.getName();

	// ----------------------------------------------------------------------
	// Factory
	// ----------------------------------------------------------------------

	public static synchronized OauthProgressCacheImpl instance(
			HttpServletRequest req) {
		return instance(req.getSession());
	}

	public static synchronized OauthProgressCacheImpl instance(
			HttpSession session) {
		Object attribute = session.getAttribute(SESSION_ATTRIBUTE_KEY);
		if (attribute instanceof OauthProgressCacheImpl) {
			return (OauthProgressCacheImpl) attribute;
		} else {
			OauthProgressCacheImpl cache = new OauthProgressCacheImpl();
			session.setAttribute(SESSION_ATTRIBUTE_KEY, cache);
			return cache;
		}
	}

	// ----------------------------------------------------------------------
	// The instance
	// ----------------------------------------------------------------------

	private final Map<ApiScope, OauthProgress> map = new EnumMap<>(
			ApiScope.class);

	@Override
	public void store(OauthProgress progress) throws OrcidClientException {
		OauthProgress previous = map.put(progress.getScope(), progress);
		log.debug("Stored: " + progress + ", previously was: " + previous);
	}

	@Override
	public OauthProgress getByID(String id) throws OrcidClientException {
		for (OauthProgress progress : map.values()) {
			if (progress.getId().equals(id)) {
				log.debug("Found by ID: " + progress);
				return progress;
			}
		}
		log.debug("Nothing found for ID: " + id);
		return null;
	}

	@Override
	public OauthProgress getByScope(ApiScope scope)
			throws OrcidClientException {
		OauthProgress progress = map.get(scope);
		log.debug("Found for scope: " + scope + ", " + progress);
		return progress;
	}
}
