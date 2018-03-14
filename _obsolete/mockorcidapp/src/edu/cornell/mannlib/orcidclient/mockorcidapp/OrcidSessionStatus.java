/* $This file is distributed under the terms of the license in /doc/license.txt$ */

package edu.cornell.mannlib.orcidclient.mockorcidapp;

import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import edu.cornell.mannlib.orcidclient.responses.message_1_2.ScopePathType;

/**
 * Hold the orcid status for this login session to the mock app.
 */
public class OrcidSessionStatus {
	private static final Log log = LogFactory.getLog(OrcidSessionStatus.class);

	private static final String ATTRIBUTE_NAME = OrcidSessionStatus.class
			.getName();

	// ----------------------------------------------------------------------
	// The factory
	// ----------------------------------------------------------------------

	public static OrcidSessionStatus fetch(HttpSession session) {
		log.debug("session: " + session);
		Object o = session.getAttribute(ATTRIBUTE_NAME);
		if (o instanceof OrcidSessionStatus) {
			return (OrcidSessionStatus) o;
		} else {
			OrcidContextStatus ocs = OrcidContextStatus.fetch(session
					.getServletContext());
			OrcidSessionStatus oss = new OrcidSessionStatus(ocs);
			session.setAttribute(ATTRIBUTE_NAME, oss);
			return oss;
		}
	}

	// ----------------------------------------------------------------------
	// The instance
	// ----------------------------------------------------------------------

	private final OrcidContextStatus ocs;

	private String orcid;
	private AuthorizationData pendingAuthorization;

	public OrcidSessionStatus(OrcidContextStatus ocs) {
		this.ocs = ocs;
	}

	public boolean isLoggedIn() {
		return orcid != null;
	}

	public void setOrcid(String orcid) {
		this.orcid = orcid;
	}

	public boolean isAuthorizationPending() {
		return pendingAuthorization != null;
	}

	public AuthorizationData getPendingAuthorization() {
		return pendingAuthorization;
	}

	public void setPendingAuthorization(String redirectUri,
			ScopePathType scope, String state) {
		this.pendingAuthorization = new AuthorizationData(redirectUri, scope,
				state);
	}

	public void confirmPendingAuthorization(String authCode) {
		ocs.store(authCode, orcid, pendingAuthorization);
		clearPendingAuthorization();
	}

	public void clearPendingAuthorization() {
		this.pendingAuthorization = null;
	}

	@Override
	public String toString() {
		return "OrcidSessionStatus[orcid=" + orcid + ", pendingAuthorization="
				+ pendingAuthorization + "]";
	}

}
