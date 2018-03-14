/* $This file is distributed under the terms of the license in /doc/license.txt$ */

package edu.cornell.mannlib.orcidclient.testwebapp;

import static edu.cornell.mannlib.orcidclient.actions.ApiAction.AUTHENTICATE;

import java.io.IOException;
import java.net.URISyntaxException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import edu.cornell.mannlib.orcidclient.OrcidClientException;
import edu.cornell.mannlib.orcidclient.auth.AuthorizationStatus;

/**
 * TODO
 */
public class AuthenticationRequester extends OrcidActor {

	public AuthenticationRequester(HttpServletRequest req,
			HttpServletResponse resp) throws IOException {
		super(req, resp, AUTHENTICATE);
	}

	@Override
	protected void seekAuthorization() throws IOException {
		try {
			String returnUrl = occ
					.resolvePathWithWebapp("request?Authenticate=true");
			String authUrl = authManager.seekAuthorization(AUTHENTICATE,
					returnUrl);
			resp.sendRedirect(authUrl);
		} catch (OrcidClientException | URISyntaxException e) {
			showInternalError(e);
		}
	}

	@Override
	protected void performAction(AuthorizationStatus auth) {
		out.println("<html><head></head><body>");
		out.println("<h1>User profile:</h1>");
		out.println("<pre>" + auth.getAccessToken() + "</pre>");
		out.println("</body></html>");
	}
}
