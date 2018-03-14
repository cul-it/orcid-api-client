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
 * Return the profile, including private parts.
 * 
 * For now, we don't care what the authorization header contains - only that it
 * exists.
 * 
 * TODO Check that the authorization header contains the applicable access code
 * for the "/orcid-profile/read-limited" scope.
 * 
 * <pre>
 *    GET /0000-0003-3479-6011/orcid-profile 
 *    Content-Type: application/vdn.orcid+xml
 *    Authorization: bearer 785e8e34-0f66-4c98-a138-c89bc8cb3886
 * </pre>
 */
public class GetProfileAction extends AbstractAction {
	private static final Log log = LogFactory.getLog(GetProfileAction.class);

	public static boolean matches(String pathInfo) {
		return extractOrcid(pathInfo) != null;
	}

	public GetProfileAction(HttpServletRequest req, HttpServletResponse resp) {
		super(req, resp);
	}

	public void doGet() throws IOException, OrcidClientException {
		String orcid = extractOrcid(req.getPathInfo());
		log.debug("Request for orcid-profile/read-limited of " + orcid);

		if (OrcidProfiles.hasProfile(orcid) && authHeaderIsOk()) {
			showSuccess(orcid);
		} else {
			showNoProfile(orcid);
		}
	}

	private boolean authHeaderIsOk() {
		return req.getHeader("Authorization") != null;
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
		if ((parts.length == 3) && ("orcid-profile".equals(parts[2]))) {
			return parts[1];
		} else {
			return null;
		}
	}

}
