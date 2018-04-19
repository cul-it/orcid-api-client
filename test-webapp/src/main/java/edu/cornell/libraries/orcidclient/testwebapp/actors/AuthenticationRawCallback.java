package edu.cornell.libraries.orcidclient.testwebapp.actors;

import static edu.cornell.libraries.orcidclient.context.OrcidClientContext.Setting.CLIENT_ID;
import static edu.cornell.libraries.orcidclient.context.OrcidClientContext.Setting.CLIENT_SECRET;
import static edu.cornell.libraries.orcidclient.context.OrcidClientContext.Setting.WEBAPP_BASE_URL;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jtwig.JtwigModel;

import edu.cornell.libraries.orcidclient.OrcidClientException;

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

	@Override
	public void exec()
			throws ServletException, IOException, OrcidClientException {
		render("/templates/authenticateRawCallback.twig.html",
				JtwigModel.newModel() //
						.with("callbackUrl", req.getRequestURL()) //
						.with("code", req.getParameter("code")) //
						.with("occ", occ) //
						.with("client_id", occ.getSetting(CLIENT_ID)) //
						.with("client_secret", occ.getSetting(CLIENT_SECRET))
						.with("mainPageUrl", occ.getSetting(WEBAPP_BASE_URL)));
	}

}
