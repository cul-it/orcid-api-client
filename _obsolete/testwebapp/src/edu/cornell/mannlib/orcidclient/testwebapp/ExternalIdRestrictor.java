/* $This file is distributed under the terms of the license in /doc/license.txt$ */

package edu.cornell.mannlib.orcidclient.testwebapp;

import static edu.cornell.mannlib.orcidclient.actions.ApiAction.UPDATE_BIO;

import java.io.IOException;
import java.net.URISyntaxException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import edu.cornell.mannlib.orcidclient.OrcidClientException;
import edu.cornell.mannlib.orcidclient.actions.ReadProfileAction;
import edu.cornell.mannlib.orcidclient.actions.UpdateBioAction;
import edu.cornell.mannlib.orcidclient.auth.AuthorizationStatus;
import edu.cornell.mannlib.orcidclient.responses.message_1_2.OrcidMessage;
import edu.cornell.mannlib.orcidclient.responses.message_1_2.Visibility;

/**
 * TODO
 */
public class ExternalIdRestrictor extends OrcidActor {
	private static final Log log = LogFactory
			.getLog(ExternalIdRestrictor.class);
	
	public ExternalIdRestrictor(HttpServletRequest req,
			HttpServletResponse resp) throws IOException {
		super(req, resp, UPDATE_BIO);
	}

	@Override
	protected void seekAuthorization() throws IOException {
		try {
			String returnUrl = occ
					.resolvePathWithWebapp("request?RestrictExternalIds=true");
			String authUrl = authManager.seekAuthorization(action, returnUrl);
			resp.sendRedirect(authUrl);
		} catch (OrcidClientException | URISyntaxException e) {
			showInternalError(e);
		}
	}
	
	@Override
	protected void performAction(AuthorizationStatus auth) {
		try {
			OrcidMessage profile = new ReadProfileAction(occ).execute(auth
					.getAccessToken());

			profile.getOrcidProfile().getOrcidBio().getExternalIdentifiers()
					.setVisibility(Visibility.LIMITED);

			OrcidMessage newProfile = new UpdateBioAction().execute(profile,
					auth.getAccessToken());
			String marshalled = occ.marshall(newProfile);

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
