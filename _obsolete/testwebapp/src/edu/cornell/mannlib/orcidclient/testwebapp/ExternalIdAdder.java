/* $This file is distributed under the terms of the license in /doc/license.txt$ */

package edu.cornell.mannlib.orcidclient.testwebapp;

import static edu.cornell.mannlib.orcidclient.actions.ApiAction.ADD_EXTERNAL_ID;
import static edu.cornell.mannlib.orcidclient.responses.message_1_2.Visibility.PUBLIC;

import java.io.IOException;
import java.net.URISyntaxException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringEscapeUtils;

import edu.cornell.mannlib.orcidclient.OrcidClientException;
import edu.cornell.mannlib.orcidclient.actions.AddExternalIdAction;
import edu.cornell.mannlib.orcidclient.actions.ApiAction;
import edu.cornell.mannlib.orcidclient.auth.AuthorizationStatus;
import edu.cornell.mannlib.orcidclient.beans.ExternalId;
import edu.cornell.mannlib.orcidclient.responses.message_1_2.OrcidMessage;

/**
 * TODO
 */
public class ExternalIdAdder extends OrcidActor {

	public ExternalIdAdder(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		super(req, resp, ApiAction.ADD_EXTERNAL_ID);
	}

	@Override
	protected void seekAuthorization() throws IOException {
		try {
			String returnUrl = occ
					.resolvePathWithWebapp("request?AddExternalId=true");
			String authUrl = authManager.seekAuthorization(ADD_EXTERNAL_ID,
					returnUrl);
			resp.sendRedirect(authUrl);
		} catch (OrcidClientException | URISyntaxException e) {
			showInternalError(e);
		}
	}

	@Override
	protected void performAction(AuthorizationStatus auth) {
		String bogusUri = "http://bogus.uri.edu/" + System.currentTimeMillis();
		ExternalId externalId = new ExternalId().setCommonName("VIVO Cornell")
				.setReference(bogusUri).setUrl(bogusUri).setVisibility(PUBLIC);

		try {
			OrcidMessage message = new AddExternalIdAction().execute(
					externalId, auth.getAccessToken());
			String marshalled = occ.marshall(message);

			out.println("<html><head></head><body>");
			out.println("<h1>Added External ID</h1>");
			out.println("<h1>User profile:</h1>");
			out.println("<pre>" + StringEscapeUtils.escapeHtml(marshalled)
					+ "</pre>");
			out.println("</body></html>");
		} catch (OrcidClientException e) {
			showInternalError(e);
		}
	}

}
