/* $This file is distributed under the terms of the license in /doc/license.txt$ */

package edu.cornell.mannlib.orcidclient.testwebapp;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import edu.cornell.mannlib.orcidclient.auth.AuthorizationStatus;
import edu.cornell.mannlib.orcidclient.context.OrcidClientContext;

/**
 * TODO
 */
public abstract class PublicOrcidActor {
	protected final HttpServletResponse resp;
	protected final PrintWriter out;
	protected final OrcidClientContext occ;

	protected PublicOrcidActor(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		this.resp = resp;
		this.out = resp.getWriter();
		resp.setContentType("text/html");

		this.occ = OrcidClientContext.getInstance();
	}

	public abstract void exec() throws IOException;

	protected void showInternalError(Exception e) {
		out.println("<html><head></head><body>");
		out.println("<h1>Internal Error</h1>");
		out.println("<pre>");
		e.printStackTrace(out);
		out.println("</pre>");
		out.println("</body></html>");
	}
}
