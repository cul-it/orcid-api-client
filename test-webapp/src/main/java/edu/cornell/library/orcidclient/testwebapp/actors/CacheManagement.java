package edu.cornell.library.orcidclient.testwebapp.actors;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jtwig.JtwigModel;

import edu.cornell.library.orcidclient.actions.ApiScope;
import edu.cornell.library.orcidclient.exceptions.OrcidClientException;
import edu.cornell.library.orcidclient.testwebapp.support.WebappCache;

/**
 * TODO
 */
public class CacheManagement extends AbstractActor {

	public CacheManagement(HttpServletRequest req, HttpServletResponse resp) {
		super(req, resp);
	}

	public void exec() throws IOException, OrcidClientException {
		WebappCache cache = WebappCache.getCache();
		String message = null;

		if (null != req.getParameter("clear")) {
			for (ApiScope scope : ApiScope.values()) {
				cache.clearScopeProgress(scope);
			}
			message = "Cleared the cache";
		}

		render("/templates/cacheManagement.twig.html", //
				JtwigModel.newModel() //
						.with("cache", cache) //
						.with("message", message));
	}

}
