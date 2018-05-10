package edu.cornell.libraries.orcidclient.testwebapp.actors;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jtwig.JtwigModel;

/**
 * TODO
 */
public class CallbackFailed extends AbstractActor {
	private final String state;

	public CallbackFailed(HttpServletRequest req, HttpServletResponse resp,
			String state) {
		super(req, resp);
		this.state = state;
	}

	public void exec() throws IOException {
		render("/templates/callbackFailure.twig.html", JtwigModel.newModel() //
				.with("message", "Didn't recognize the callback") //
				.with("state", state) //
				.with("mainPageUrl", occ.getWebappBaseUrl()));
	}

}
