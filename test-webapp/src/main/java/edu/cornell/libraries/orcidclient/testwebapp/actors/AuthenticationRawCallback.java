package edu.cornell.libraries.orcidclient.testwebapp.actors;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jtwig.JtwigModel;

/**
 * Show the user what came back from the first phase of the "raw"
 * authentication.
 */
public class AuthenticationRawCallback extends AbstractActor {
	public static final String CALLBACK_STATE = "RawAuthenticationToken";

	public AuthenticationRawCallback(HttpServletRequest req,
			HttpServletResponse resp) {
		super(req, resp);
	}

	public void exec() throws IOException {
		render("/templates/authenticateRawCallback.twig.html",
				JtwigModel.newModel() //
						.with("callbackUrl", req.getRequestURL()) //
						.with("code", req.getParameter("code")) //
						.with("occ", occ) //
						.with("client_id", occ.getClientId()) //
						.with("client_secret", occ.getClientSecret()) //
						.with("mainPageUrl", occ.getWebappBaseUrl()));
	}

}
