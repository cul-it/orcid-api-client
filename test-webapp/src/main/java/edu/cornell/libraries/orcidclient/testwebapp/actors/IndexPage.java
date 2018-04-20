package edu.cornell.libraries.orcidclient.testwebapp.actors;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jtwig.JtwigModel;

import edu.cornell.libraries.orcidclient.OrcidClientException;

/**
 * Show the choices to the user.
 */
public class IndexPage extends AbstractActor {
	public IndexPage(HttpServletRequest req, HttpServletResponse resp) {
		super(req, resp);
	}

	public void exec() throws IOException, OrcidClientException {
		render("/templates/index.twig.html", //
				JtwigModel.newModel() //
						.with("var", "World"));
	}
}
