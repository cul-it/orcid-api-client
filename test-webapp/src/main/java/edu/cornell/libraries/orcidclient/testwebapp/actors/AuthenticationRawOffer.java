package edu.cornell.libraries.orcidclient.testwebapp.actors;

import static edu.cornell.libraries.orcidclient.context.OrcidClientContext.Setting.CLIENT_ID;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.http.client.utils.URIBuilder;
import org.jtwig.JtwigModel;

/**
 * Offer the user a chance to do a "raw" authentication.
 */
public class AuthenticationRawOffer extends AbstractActor {
	public static final String CALLBACK_STATE = "AuthenticationRawCallback";

	public AuthenticationRawOffer(HttpServletRequest req,
			HttpServletResponse resp) {
		super(req, resp);
	}

	public void exec()
			throws IOException, ServletException {
		try {
			URI requestUri = new URIBuilder(occ.getAuthCodeRequestUrl())
					.addParameter("client_id", occ.getSetting(CLIENT_ID))
					.addParameter("scope", "/authenticate")
					.addParameter("response_type", "code")
					.addParameter("redirect_uri", occ.getCallbackUrl())
					.addParameter("state", CALLBACK_STATE).build();
			
			render("/templates/authenticateRaw.twig.html", //
					JtwigModel.newModel() //
							.with("authRequestUrl", requestUri.toString()));
		} catch (URISyntaxException e) {
			throw new ServletException(e);
		}
	}
}
