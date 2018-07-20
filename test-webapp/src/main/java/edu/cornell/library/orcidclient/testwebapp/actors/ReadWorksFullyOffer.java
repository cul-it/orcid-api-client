package edu.cornell.library.orcidclient.testwebapp.actors;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jtwig.JtwigModel;

/**
 * Offer to read an ORCID record, focusing on the Works.
 */
public class ReadWorksFullyOffer extends AbstractActor {
	public ReadWorksFullyOffer(HttpServletRequest req,
			HttpServletResponse resp) {
		super(req, resp);
	}

	public void exec() throws IOException {
		render("/templates/readWorksFully.twig.html", //
				JtwigModel.newModel() //
						.with("tokens", getTokensFromCache()));
	}
}
