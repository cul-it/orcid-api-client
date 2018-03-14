/* $This file is distributed under the terms of the license in /doc/license.txt$ */

package edu.cornell.mannlib.orcidclient.mockorcidapp;

import static javax.servlet.http.HttpServletResponse.SC_NOT_FOUND;

import java.io.IOException;
import java.util.Arrays;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import edu.cornell.mannlib.orcidclient.OrcidClientException;
import edu.cornell.mannlib.orcidclient.responses.message_1_2.OrcidMessage;

/**
 * Handle a request for the public biography.
 * 
 * If we have the profile they are asking for, return it as XML. Otherwise, send
 * a 404 Not Found.
 * 
 * TODO - It would be nice if we removed any visibility="limited" sections.
 */
public class PublicBioAction {
	private static final Log log = LogFactory.getLog(PublicBioAction.class);

	private final HttpServletRequest req;
	private final HttpServletResponse resp;

	/**
	 * Match something of the form /0000-0003-3479-6011/orcid-bio
	 */
	public static boolean matches(String pathInfo) {
		return extractOrcid(pathInfo) != null;
	}

	public PublicBioAction(HttpServletRequest req, HttpServletResponse resp) {
		this.req = req;
		this.resp = resp;
	}

	public void doGet() throws IOException, OrcidClientException {
		String orcid = extractOrcid(req.getPathInfo());
		log.debug("Request for public bio of " + orcid);

		if (OrcidProfiles.hasProfile(orcid)) {
			showSuccess(orcid);

		} else {
			showNoProfile(orcid);
		}
	}

	private void showSuccess(String orcid) throws IOException,
			OrcidClientException {
		OrcidMessage profile = OrcidProfiles.getProfile(orcid);
		resp.setContentType("application/vnd.orcid+xml");
		resp.getWriter().println(OrcidMessageUtils.marshall(profile));
	}

	private void showNoProfile(String orcid) throws IOException {
		log.debug("No profile for " + orcid);
		resp.sendError(SC_NOT_FOUND);
	}

	private static String extractOrcid(String pathInfo) {
		String[] parts = pathInfo.split("/");
		log.debug("parts is " + Arrays.toString(parts));
		if ((parts.length == 3) && ("orcid-bio".equals(parts[2]))) {
			return parts[1];
		} else {
			return null;
		}
	}
}
