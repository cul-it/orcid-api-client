/* $This file is distributed under the terms of the license in /doc/license.txt$ */

package edu.cornell.mannlib.orcidclient.mockorcidapp;

import edu.cornell.mannlib.orcidclient.auth.AccessToken;
import edu.cornell.mannlib.orcidclient.auth.AccessTokenFormatException;
import edu.cornell.mannlib.orcidclient.responses.message_1_2.ScopePathType;

/**
 * Hold all of the info regarding an Authorization. Created when an app requests
 * authorization for a scope, and provides stateToken and redirectUri as a way
 * to communicate the results back to the app.
 * 
 * The user must log in to VIVO, so their orcid will be added. They must
 * authorize, so an authCode will be added. Finally, the app must trade the
 * authCode for an access token, which will be added.
 */
public class AuthorizationData {
	private final String redirectUri;
	private final ScopePathType scope;
	private final String state;

	private String orcid;
	private String authCode;
	private AccessToken accessToken;

	public AuthorizationData(String redirectUri, ScopePathType scope,
			String state) {
		this.redirectUri = redirectUri;
		this.scope = scope;
		this.state = state;
	}

	public String getAuthCode() {
		return authCode;
	}

	public void setAuthCode(String authCode) {
		this.authCode = authCode;
	}

	public AccessToken getAccessToken() {
		return accessToken;
	}

	public void createAccessToken() throws AccessTokenFormatException {
		String json = String
				.format("{ \n" //
						+ "   \"access_token\":\"%s\", \n" //
						+ "   \"token_type\":\"bearer\", \n" //
						+ "   \"expires_in\":628207503, \n" //
						+ "   \"scope\":\"%s\", \n" //
						+ "   \"orcid\":\"%s\" \n" //
						+ "}", //
						"ACCESS-" + authCode, scope.value(),
						orcid);

		accessToken = new AccessToken(json);
	}

	public String getOrcid() {
		return orcid;
	}

	public void setOrcid(String orcid) {
		this.orcid = orcid;
	}

	public String getRedirectUri() {
		return redirectUri;
	}

	public ScopePathType getScope() {
		return scope;
	}

	public String getState() {
		return state;
	}

	@Override
	public String toString() {
		return "AuthorizationData[redirectUri=" + redirectUri + ", scope="
				+ scope + ", state=" + state + ", orcid=" + orcid
				+ ", authCode=" + authCode + ", accessToken=" + accessToken
				+ "]";
	}

}