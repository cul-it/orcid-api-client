package edu.cornell.library.orcidclient.testwebapp.actors;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jtwig.JtwigModel;

/**
 * Offer to edit Exernal IDs
 */
public class EditExternalIdsOffer extends AbstractActor {
	public EditExternalIdsOffer(HttpServletRequest req,
			HttpServletResponse resp) {
		super(req, resp);
	}

	public void exec() throws IOException {
		render("/templates/editExternalIds.twig.html", //
				JtwigModel.newModel() //
						.with("tokens", getTokensFromCache()));
	}
}
