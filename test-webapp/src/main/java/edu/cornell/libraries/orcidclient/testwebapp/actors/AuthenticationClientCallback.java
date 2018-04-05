/* $This file is distributed under the terms of the license in /doc/license.txt$ */

package edu.cornell.libraries.orcidclient.testwebapp.actors;

import static edu.cornell.libraries.orcidclient.context.OrcidClientContext.Setting.WEBAPP_BASE_URL;
import static org.jtwig.JtwigTemplate.classpathTemplate;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jtwig.JtwigModel;

import edu.cornell.libraries.orcidclient.OrcidClientException;
import edu.cornell.libraries.orcidclient.auth.AuthorizationStateProgress;
import edu.cornell.libraries.orcidclient.auth.OrcidAuthorizationClient;

/**
 * TODO
 */
public class AuthenticationClientCallback extends AbstractActor {

	public AuthenticationClientCallback(HttpServletRequest req,
			HttpServletResponse resp) {
		super(req, resp);
	}

	@Override
	public void exec()
			throws ServletException, IOException, OrcidClientException {
		OrcidAuthorizationClient auth = occ.getAuthorizationClient(req);
		String state = req.getParameter("state");

		AuthorizationStateProgress progressBefore = AuthorizationStateProgress.copy(
				auth.getProgressById(state));
		auth.processAuthorizationResponse(req);
		AuthorizationStateProgress progressAfter = AuthorizationStateProgress.copy(
				auth.getProgressById(state));

		JtwigModel model = JtwigModel.newModel()
				.with("progressBefore", progressBefore) //
				.with("progressAfter", progressAfter) //
				.with("mainPageUrl", occ.getSetting(WEBAPP_BASE_URL));

		String path = "/templates/authenticateClientCallback.twig.html";
		ServletOutputStream outputStream = resp.getOutputStream();
		classpathTemplate(path).render(model, outputStream);

	}

}
