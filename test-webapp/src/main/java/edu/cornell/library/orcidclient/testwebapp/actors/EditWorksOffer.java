package edu.cornell.library.orcidclient.testwebapp.actors;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jtwig.JtwigModel;

/**
 * Offer to edit Works
 */
public class EditWorksOffer extends AbstractActor {
	public EditWorksOffer(HttpServletRequest req,
			HttpServletResponse resp) {
		super(req, resp);
	}

	public void exec() throws IOException {
		render("/templates/editWorks.twig.html", //
				JtwigModel.newModel() //
						.with("tokens", getTokensFromCache()));
	}
}
