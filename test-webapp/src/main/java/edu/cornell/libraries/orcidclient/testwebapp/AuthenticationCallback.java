/* $This file is distributed under the terms of the license in /doc/license.txt$ */

package edu.cornell.libraries.orcidclient.testwebapp;

import static edu.cornell.libraries.orcidclient.context.OrcidClientContext.Setting.CLIENT_ID;
import static edu.cornell.libraries.orcidclient.context.OrcidClientContext.Setting.CLIENT_SECRET;
import static org.jtwig.JtwigTemplate.classpathTemplate;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jtwig.JtwigModel;

import edu.cornell.libraries.orcidclient.context.OrcidClientContext;

/**
 * TODO
 */
public class AuthenticationCallback {
	public static final String CALLBACK_STATE = "RawAuthenticationToken";
	private final HttpServletRequest req;
	private final HttpServletResponse resp;

	public AuthenticationCallback(HttpServletRequest req,
			HttpServletResponse resp) {
		this.req = req;
		this.resp = resp;
	}

	public void exec() throws ServletException, IOException {
		OrcidClientContext occ = OrcidClientContext.getInstance();
		String code = req.getParameter("code");
		JtwigModel model = JtwigModel.newModel()
				.with("callbackUrl", req.getRequestURL()) //
				.with("code", code) //
				.with("occ", occ) //
				.with("client_id", occ.getSetting(CLIENT_ID)) //
				.with("client_secret", occ.getSetting(CLIENT_SECRET));

		String path = "/templates/authenticateCallback.twig.html";
		ServletOutputStream outputStream = resp.getOutputStream();
		classpathTemplate(path).render(model, outputStream);
	}

}
