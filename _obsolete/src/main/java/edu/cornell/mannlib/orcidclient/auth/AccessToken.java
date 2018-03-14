/* $This file is distributed under the terms of the license in /doc/license.txt$ */

package edu.cornell.mannlib.orcidclient.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * The information associated with an ORCID Access Token. Immutable.
 */
public class AccessToken {
	public static final AccessToken NO_TOKEN = new AccessToken();

	private final String accessToken;
	private final String tokenType;
	private final Integer expiresIn;
	private final String scope;
	private final String orcid;
	private final String toString;

	private AccessToken() {
		this.accessToken = "NO_ACCESS_TOKEN";
		this.tokenType = "";
		this.expiresIn = 0;
		this.scope = "NO_SCOPE";
		this.orcid = "NOT_AUTHENTICATED";
		this.toString = "No AccessToken";
	}

	public AccessToken(String jsonString) throws AccessTokenFormatException {
		try {
			ObjectMapper mapper = new ObjectMapper();
			ObjectNode json = (ObjectNode) mapper.readTree(jsonString);

			this.accessToken = json.get("access_token").textValue();
			this.tokenType = json.get("token_type").textValue();
			this.expiresIn = json.get("expires_in").intValue();
			this.scope = json.get("scope").textValue();
			this.orcid = json.get("orcid").textValue();

			this.toString = jsonString;
		} catch (Exception e) {
			throw new AccessTokenFormatException(
					"Failed to parse the ORID Access Token. JSON is '"
							+ jsonString + "'");
		}
	}

	public String getAccessToken() {
		return accessToken;
	}

	public String getTokenType() {
		return tokenType;
	}

	public Integer getExpiresIn() {
		return expiresIn;
	}

	public String getScope() {
		return scope;
	}

	public String getOrcid() {
		return orcid;
	}

	public String toJson() {
		return toString;
	}

	@Override
	public String toString() {
		return toString;
	}
}
