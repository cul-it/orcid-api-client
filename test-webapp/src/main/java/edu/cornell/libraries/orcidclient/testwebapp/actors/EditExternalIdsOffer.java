/* $This file is distributed under the terms of the license in /doc/license.txt$ */

package edu.cornell.libraries.orcidclient.testwebapp.actors;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jtwig.JtwigModel;

/**
 * Offer to edit Exernal IDs
 */
public class EditExternalIdsOffer extends AbstractActor {
	public EditExternalIdsOffer(HttpServletRequest req, HttpServletResponse resp) {
		super(req, resp);
	}

	@Override
	public void exec() throws IOException, ServletException {
		render("/templates/editExternalIds.twig.html", //
				JtwigModel.newModel() //
						.with("tokens", getTokensFromCache()));
	}
}
