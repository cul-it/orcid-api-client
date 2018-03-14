/* $This file is distributed under the terms of the license in /doc/license.txt$ */

package edu.cornell.mannlib.orcidclient.mockorcidapp;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Some code that is useful for multiple action classes.
 */
public abstract class AbstractAction {
	protected final HttpServletRequest req;
	protected final HttpSession session;
	protected final ServletContext ctx;
	protected final HttpServletResponse resp;
	protected final OrcidSessionStatus oss;
	protected final OrcidContextStatus ocs;

	protected AbstractAction(HttpServletRequest req, HttpServletResponse resp) {
		this.req = req;
		
		this.session = req.getSession();
		this.oss = OrcidSessionStatus.fetch(this.session);

		this.ctx = this.session.getServletContext();
		this.ocs = OrcidContextStatus.fetch(this.ctx);
		
		this.resp = resp;
	}

	protected String getRequiredParameter(String name) {
		String value = req.getParameter(name);
		if (value == null) {
			throw new IllegalStateException("Request has no parameter for '"
					+ name + "'");
		}
		return value;
	}

}
