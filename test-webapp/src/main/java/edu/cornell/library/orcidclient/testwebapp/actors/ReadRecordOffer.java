package edu.cornell.library.orcidclient.testwebapp.actors;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jtwig.JtwigModel;

/**
 * Offer to read an ORCID record
 */
public class ReadRecordOffer extends AbstractActor {
	public ReadRecordOffer(HttpServletRequest req, HttpServletResponse resp) {
		super(req, resp);
	}

	public void exec() throws IOException {
		render("/templates/readRecord.twig.html", //
				JtwigModel.newModel() //
						.with("tokens", getTokensFromCache()));
	}
}
