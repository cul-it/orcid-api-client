/* $This file is distributed under the terms of the license in /doc/license.txt$ */

package edu.cornell.libraries.orcidclient.testwebapp.actors;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import edu.cornell.libraries.orcidclient.OrcidClientException;
import edu.cornell.libraries.orcidclient.actions.ApiScope;
import edu.cornell.libraries.orcidclient.auth.AuthorizationStateProgress;
import edu.cornell.libraries.orcidclient.auth.OrcidAuthorizationClient;

/**
 * The user has asked for client-based authentication. Start the dance.
 */
public class AuthenticationClientRequest extends AbstractActor {
	private final OrcidAuthorizationClient authClient;

	public AuthenticationClientRequest(HttpServletRequest req,
			HttpServletResponse resp) {
		super(req, resp);
		this.authClient = occ.getAuthorizationClient(req);
	}

	@Override
	public void exec()
			throws IOException, ServletException, OrcidClientException {
		ApiScope scope = getScopeFromRequest();
		AuthorizationStateProgress progress = authClient
				.createProgressObject(scope, callbackUrl());

		resp.sendRedirect(authClient.buildAuthorizationCall(progress));
	}

	private ApiScope getScopeFromRequest() {
		return ApiScope.valueOf(req.getParameter("scope"));
	}

}
