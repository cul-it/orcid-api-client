/* $This file is distributed under the terms of the license in /doc/license.txt$ */

package edu.cornell.libraries.orcidclient.testwebapp.actors;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import edu.cornell.libraries.orcidclient.OrcidClientException;
import edu.cornell.libraries.orcidclient.context.OrcidClientContext;

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

	protected URI callbackUrl() throws OrcidClientException {
		try {
			return new URI(occ.getCallbackUrl());
		} catch (URISyntaxException e) {
			throw new OrcidClientException("Failed to form callback URL", e);
		}
	}

}
