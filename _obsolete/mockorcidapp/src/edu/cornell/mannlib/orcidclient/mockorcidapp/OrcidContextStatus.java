/* $This file is distributed under the terms of the license in /doc/license.txt$ */

package edu.cornell.mannlib.orcidclient.mockorcidapp;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletContext;

import edu.cornell.mannlib.orcidclient.auth.AccessToken;

/**
 * A global store of AuthorizationData.
 * 
 * When an AuthorizationData is complete, it goes beyond the realm of a single
 * session. The app that uses is may not possess the same session cookie as the
 * user who approved of it.
 * 
 * By the time an AuthorizationData gets here, it must have an authCode.
 */
public class OrcidContextStatus {
	// ----------------------------------------------------------------------
	// The factory
	// ----------------------------------------------------------------------

	private static final String ATTRIBUTE_NAME = OrcidContextStatus.class
			.getName();

	public static OrcidContextStatus fetch(ServletContext ctx) {
		Object o = ctx.getAttribute(ATTRIBUTE_NAME);
		if (o instanceof OrcidContextStatus) {
			return (OrcidContextStatus) o;
		} else {
			OrcidContextStatus ocs = new OrcidContextStatus();
			ctx.setAttribute(ATTRIBUTE_NAME, ocs);
			return ocs;
		}
	}

	// ----------------------------------------------------------------------
	// The instance
	// ----------------------------------------------------------------------

	private final Map<String, AuthorizationData> map = new HashMap<>();

	public void store(String authCode, String orcid, AuthorizationData oad) {
		oad.setAuthCode(authCode);
		oad.setOrcid(orcid);
		map.put(authCode, oad);
	}

	public AuthorizationData lookupByAuthCode(String authCode) {
		return map.get(authCode);
	}

	public AuthorizationData lookupByAccessToken(String accessToken) {
		for (AuthorizationData auth: map.values()) {
			AccessToken at = auth.getAccessToken();
			if ((at != null) && (accessToken.equals(at.getAccessToken()))) {
				return auth;
			}
		}
		return null;
	}
	@Override
	public String toString() {
		return "OrcidContextStatus[map=" + map + "]";
	}

}
