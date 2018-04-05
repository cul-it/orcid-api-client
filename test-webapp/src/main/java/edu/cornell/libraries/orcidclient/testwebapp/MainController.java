/* $This file is distributed under the terms of the license in /doc/license.txt$ */

package edu.cornell.libraries.orcidclient.testwebapp;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import edu.cornell.libraries.orcidclient.OrcidClientException;
import edu.cornell.libraries.orcidclient.testwebapp.actors.AuthenticationClientOffer;
import edu.cornell.libraries.orcidclient.testwebapp.actors.AuthenticationClientRequest;
import edu.cornell.libraries.orcidclient.testwebapp.actors.AuthenticationRawOffer;
import edu.cornell.libraries.orcidclient.testwebapp.actors.IndexPage;

/**
 * Present the index page, or react to selections from it.
 */
@WebServlet("/request/*")
public class MainController extends AbstractController {
	@Override
	public void init() throws ServletException {
		// Nothing to do (yet)
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		try {
			if (req.getParameter("RawAuthentication") != null) {
				new AuthenticationRawOffer(req, resp).exec();
			} else if (req.getParameter("ClientAuthentication") != null) {
				new AuthenticationClientOffer(req, resp).exec();
			} else if (req
					.getParameter("ClientAuthenticationRequest") != null) {
				new AuthenticationClientRequest(req, resp).exec();
			} else {
				new IndexPage(req, resp).exec();
			}
		} catch (OrcidClientException e) {
			e.printStackTrace();
		}
	}
}
