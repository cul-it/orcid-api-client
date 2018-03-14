/* $This file is distributed under the terms of the license in /doc/license.txt$ */

package edu.cornell.mannlib.orcidclient.mockorcidapp;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import edu.cornell.mannlib.orcidclient.OrcidClientException;

/**
 * TODO
 */
public class MockOrcidController extends HttpServlet {
	private static final Log log = LogFactory.getLog(MockOrcidController.class);


	@Override
	public void init() throws ServletException {
		try {
			OrcidProfiles.load(getServletContext());
		} catch (OrcidClientException | IOException e) {
			throw new ServletException("Failed to load the Orcid Profiles.", e);
		}
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		log.debug("Request URL is '" + req.getRequestURL() + "'");
		log.debug("Context path is '" + req.getContextPath() + "'");
		log.debug("Servlet path is '" + req.getServletPath() + "'");
		log.debug("Path info is '" + req.getPathInfo() + "'");
		try {
			String pathInfo = req.getPathInfo();
			if (PublicBioAction.matches(pathInfo)) {
				new PublicBioAction(req, resp).doGet();
			} else if (GetProfileAction.matches(pathInfo)) {
				new GetProfileAction(req, resp).doGet();
			} else if (OauthAuthorizeAction.matches(pathInfo)) {
				new OauthAuthorizeAction(req, resp).doGet();
			} else if (LoginAction.matches(pathInfo)) {
				new LoginAction(req, resp).doGet();
			} else if (AuthResponseAction.matches(pathInfo)) {
				new AuthResponseAction(req, resp).doGet();
			} else {
				new BadRequestAction(req, resp).doGet();
			}
		} catch (Exception e) {
			new BadRequestAction(req, resp).exception(e);
		}
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		try {
			String pathInfo = req.getPathInfo();
			if (OauthTokenAction.matches(pathInfo)) {
				new OauthTokenAction(req, resp).doPost();
			} else if (AddExternalIdAction.matches(pathInfo)) {
				new AddExternalIdAction(req, resp).doPost();
			} else {
				new BadRequestAction(req, resp).doPost();
			}
		} catch (Exception e) {
			new BadRequestAction(req, resp).exception(e);
		}
	}

}
