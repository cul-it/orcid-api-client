/* $This file is distributed under the terms of the license in /doc/license.txt$ */

package edu.cornell.libraries.orcidclient.auth;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import edu.cornell.libraries.orcidclient.OrcidClientException;
import edu.cornell.libraries.orcidclient.actions.ApiScope;

/**
 * The access token granted at the end of the OAuth negotiation.
 *
 * Parse it into suitable fields, while retaining the original JSON string.
 * Interpret the "expires_in" field as being either short-term or long-term.
 * 
 * The original JSON string might look like this:
 * 
 * <pre>
 * {
 *   "access_token":"f5af9f51-07e6-4332-8f1a-c0c11c1e3728",
 *   "token_type":"bearer",
 *   "refresh_token":"f725f747-3a65-49f6-a231-3e8944ce464d",
 *   "expires_in":631138518,
 *   "scope":"/activities/update",
 *   "name":"Sofia Garcia",
 *   "orcid":"0000-0001-2345-6789"
 * }
 * </pre>
 */
public class AccessToken {
	static AccessToken parse(String jsonString) throws OrcidClientException {
		try {
			return new AccessToken(jsonString);
		} catch (IOException e) {
			throw new OrcidClientException("Failed to parse AccessToken", e);
		}
	}

	private final String jsonString;
	private final String token;
	private final String type;
	private final String refreshToken;
	private final long expiresIn;
	private final ApiScope scope;
	private final String name;
	private final String orcid;

	private AccessToken(String jsonString)
			throws IOException, OrcidClientException {
		this.jsonString = jsonString;

		Map<String, Object> map = new ObjectMapper().readValue(jsonString,
				new TypeReference<HashMap<String, Object>>() {
					// instantiate the abstract class
				});
		this.token = getJsonValue(map, "access_token");
		this.type = getJsonValue(map, "token_type");
		this.refreshToken = getJsonValue(map, "refresh_token");
		this.expiresIn = this.<Integer>getJsonValue(map, "expires_in");
		this.scope = ApiScope.parse(getJsonValue(map, "scope"));
		this.name = getJsonValue(map, "name");
		this.orcid = getJsonValue(map, "orcid");
	}

	@SuppressWarnings("unchecked")
	private <T> T getJsonValue(Map<String, Object> map, String key)
			throws OrcidClientException {
		if (!map.containsKey(key)) {
			throw new OrcidClientException("Can't parse AccessToken. "
					+ "JSON contains no value for '" + key + "'");
		} else {
			return (T) map.get(key);
		}
	}

	public String getJsonString() {
		return jsonString;
	}

	public String getToken() {
		return token;
	}

	public String getType() {
		return type;
	}

	public String getRefreshToken() {
		return refreshToken;
	}

	public long getExpiresIn() {
		return expiresIn;
	}

	public ApiScope getScope() {
		return scope;
	}

	public String getName() {
		return name;
	}

	public String getOrcid() {
		return orcid;
	}

	public boolean isShortTerm() {
		throw new RuntimeException("AccessToken.isShortTerm not implemented.");
	}

	@Override
	public String toString() {
		return String.format(
				"AccessToken[jsonString=%s, token=%s, type=%s, refreshToken=%s, expiresIn=%s, scope=%s, name=%s, orcid=%s]",
				jsonString, token, type, refreshToken, expiresIn, scope, name,
				orcid);
	}

}
