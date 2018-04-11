/* $This file is distributed under the terms of the license in /doc/license.txt$ */

package edu.cornell.libraries.orcidclient.testwebapp.actors;

import static edu.cornell.libraries.orcidclient.context.OrcidClientContext.Setting.WEBAPP_BASE_URL;
import static org.jtwig.JtwigTemplate.classpathTemplate;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jtwig.JtwigModel;

import edu.cornell.libraries.orcidclient.OrcidClientException;
import edu.cornell.libraries.orcidclient.auth.OrcidAuthorizationClient;
import edu.cornell.libraries.orcidclient.context.OrcidClientContext;
import edu.cornell.libraries.orcidclient.http.BaseHttpPostRequester;
import edu.cornell.libraries.orcidclient.testwebapp.support.WebappCache;

/**
 * TODO
 */
public abstract class AbstractActor {
	protected final HttpServletRequest req;
	protected final HttpServletResponse resp;
	protected final OrcidClientContext occ;

	public AbstractActor(HttpServletRequest req, HttpServletResponse resp) {
		this.req = req;
		this.resp = resp;
		this.occ = OrcidClientContext.getInstance();
	}

	public abstract void exec()
			throws ServletException, IOException, OrcidClientException;

	protected void render(String path, JtwigModel model) throws IOException {
		classpathTemplate(path).render(
				model.with("mainPageUrl", occ.getSetting(WEBAPP_BASE_URL)),
				resp.getOutputStream());
	}

	protected URI callbackUrl() throws OrcidClientException {
		try {
			return new URI(occ.getCallbackUrl());
		} catch (URISyntaxException e) {
			throw new OrcidClientException("Failed to form callback URL", e);
		}
	}

	/**
	 * Could be in the constructor, but not every Actor wants one.
	 */
	protected OrcidAuthorizationClient getAuthorizationClient()
			throws OrcidClientException {
		return new OrcidAuthorizationClient(occ, WebappCache.getCache(),
				new BaseHttpPostRequester());
	}
}
