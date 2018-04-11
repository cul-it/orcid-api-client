/* $This file is distributed under the terms of the license in /doc/license.txt$ */

package edu.cornell.libraries.orcidclient.testwebapp.actors;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jtwig.JtwigModel;

import edu.cornell.libraries.orcidclient.OrcidClientException;
import edu.cornell.libraries.orcidclient.actions.ApiScope;
import edu.cornell.libraries.orcidclient.testwebapp.support.WebappCache;

/**
 * TODO
 */
public class CacheManagement extends AbstractActor {

	public CacheManagement(HttpServletRequest req, HttpServletResponse resp) {
		super(req, resp);
	}

	@Override
	public void exec()
			throws ServletException, IOException, OrcidClientException {
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
