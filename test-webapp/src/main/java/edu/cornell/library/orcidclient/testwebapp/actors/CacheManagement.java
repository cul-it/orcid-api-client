package edu.cornell.library.orcidclient.testwebapp.actors;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jtwig.JtwigModel;

import edu.cornell.library.orcidclient.exceptions.OrcidClientException;
import edu.cornell.library.orcidclient.testwebapp.support.AccessTokenCacheFileImpl;
import edu.cornell.library.orcidclient.util.PrettyToStringPrinter;

/**
 * Show and/or clear the access token cache.
 */
public class CacheManagement extends AbstractActor {

	public CacheManagement(HttpServletRequest req, HttpServletResponse resp) {
		super(req, resp);
	}

	public void exec() throws IOException, OrcidClientException {
		AccessTokenCacheFileImpl cache = AccessTokenCacheFileImpl.instance(req);
		String message = null;

		if (null != req.getParameter("clear")) {
			cache.clear();
			message = "Cleared the cache";
		}

		render("/templates/cacheManagement.twig.html", //
				JtwigModel.newModel() //
						.with("cache",
								new PrettyToStringPrinter().format(cache)) //
						.with("message", message));
	}

}
