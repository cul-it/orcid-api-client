/* $This file is distributed under the terms of the license in /doc/license.txt$ */

package edu.cornell.mannlib.orcidclient.testwebapp;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringEscapeUtils;

import edu.cornell.mannlib.orcidclient.OrcidClientException;
import edu.cornell.mannlib.orcidclient.actions.ReadPublicBioAction;
import edu.cornell.mannlib.orcidclient.responses.message_1_2.OrcidMessage;

/**
 * TODO
 */
public class PublicBioReader extends PublicOrcidActor {
	private static String orcid = "0000-0003-3479-6011";

	protected PublicBioReader(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		super(req, resp);
	}

	@Override
	public void exec() throws IOException {
		try {
			OrcidMessage message = new ReadPublicBioAction().execute(orcid);
			String marshalled = occ.marshall(message);

			out.println("<html><head></head><body>");
			out.println("<h1>Public Bio</h1>");
			out.println("<h1>User profile for " + orcid + "</h1>");
			out.println("<pre>" + StringEscapeUtils.escapeHtml(marshalled)
					+ "</pre>");
			out.println("</body></html>");
		} catch (OrcidClientException e) {
			showInternalError(e);
		}
	}

}
