package edu.cornell.library.orcidclient.testwebapp.actors;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jtwig.JtwigModel;

import edu.cornell.library.orcidclient.actions.ApiScope;

/**
 * Offer to do a client-based Authentication. Provide a list of available
 * scopes.
 */
public class AuthenticationClientOffer extends AbstractActor {
	public AuthenticationClientOffer(HttpServletRequest req,
			HttpServletResponse resp) {
		super(req, resp);
	}

	public void exec() throws IOException {
		render("/templates/authenticateClient.twig.html", //
				JtwigModel.newModel() //
						.with("scopes", ApiScope.values()));
	}

}
