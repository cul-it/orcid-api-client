/* $This file is distributed under the terms of the license in /doc/license.txt$ */

package edu.cornell.libraries.orcidclient.testwebapp.actors;

import static org.jtwig.JtwigTemplate.classpathTemplate;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jtwig.JtwigModel;

import edu.cornell.libraries.orcidclient.actions.ApiScope;

/**
 * Offer to do a client-based Authentication. Provide a list of available
 * scopes.
 */
public class AuthenticationClientOffer extends AbstractActor {
	public AuthenticationClientOffer(HttpServletRequest req,
			HttpServletResponse resp) {
		super(req, resp);
	}

	@Override
	public void exec() throws IOException, ServletException {
		JtwigModel model = JtwigModel.newModel() //
				.with("scopes", ApiScope.values());

		String path = "/templates/authenticateClient.twig.html";
		ServletOutputStream outputStream = resp.getOutputStream();
		classpathTemplate(path).render(model, outputStream);
	}

}
