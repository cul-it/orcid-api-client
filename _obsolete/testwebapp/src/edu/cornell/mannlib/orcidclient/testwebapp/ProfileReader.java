/* $This file is distributed under the terms of the license in /doc/license.txt$ */

package edu.cornell.mannlib.orcidclient.testwebapp;

import static edu.cornell.mannlib.orcidclient.actions.ApiAction.READ_PROFILE;

import java.io.IOException;
import java.net.URISyntaxException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringEscapeUtils;

import edu.cornell.mannlib.orcidclient.OrcidClientException;
import edu.cornell.mannlib.orcidclient.actions.ApiAction;
import edu.cornell.mannlib.orcidclient.actions.ReadProfileAction;
import edu.cornell.mannlib.orcidclient.auth.AuthorizationStatus;
import edu.cornell.mannlib.orcidclient.responses.message_1_2.OrcidMessage;

/**
 * TODO
 */
public class ProfileReader extends OrcidActor {

	public ProfileReader(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		super(req, resp, ApiAction.READ_PROFILE);
	}

	@Override
	protected void seekAuthorization() throws IOException {
		try {
			String returnUrl = occ
					.resolvePathWithWebapp("request?ReadProfile=true");
			String authUrl = authManager.seekAuthorization(READ_PROFILE,
					returnUrl);
			resp.sendRedirect(authUrl);
		} catch (OrcidClientException | URISyntaxException e) {
			showInternalError(e);
		}
	}
	
	@Override
	protected void performAction(AuthorizationStatus auth) {
		try {
			OrcidMessage message = new ReadProfileAction(occ)
					.execute(auth.getAccessToken());
			String marshalled = occ.marshall(message);

			out.println("<html><head></head><body>");
			out.println("<h1>User profile:</h1>");
			out.println("<pre>" + StringEscapeUtils.escapeHtml(marshalled)
					+ "</pre>");
			out.println("</body></html>");
		} catch (OrcidClientException e) {
			showInternalError(e);
		}
	}

}
