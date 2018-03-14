/* $This file is distributed under the terms of the license in /doc/license.txt$ */

package edu.cornell.mannlib.orcidclient.auth;

import static org.junit.Assert.*;

import org.junit.Test;

/**
 * TODO
 */
public class AccessTokenTest {
	private static final String VALID_JSON = "{" //
			+ "\"access_token\": \"value1\", " //
			+ "\"token_type\": \"typeA\", " //
			+ "\"expires_in\": 123, " //
			+ "\"scope\": \"mouthwash\", " //
			+ "\"orcid\": \"oid\"" //
			+ "}";

	private static final String EXTRA_OPENING_BRACE = "{{" //
			+ "\"access_token\": \"value1\", " //
			+ "\"token_type\": \"typeA\", " //
			+ "\"expires_in\": 123, " //
			+ "\"scope\": \"mouthwash\", " //
			+ "\"orcid\": \"oid\"" //
			+ "}";

	private static final String NO_TOKEN_TYPE = "{" //
			+ "\"access_token\": \"value1\", " //
			+ "\"expires_in\": 123, " //
			+ "\"scope\": \"mouthwash\", " //
			+ "\"orcid\": \"oid\"" //
			+ "}";

	private AccessToken token;

	@Test
	public void readAnAccessToken() throws AccessTokenFormatException {
		token = new AccessToken(VALID_JSON);
		assertEquals("value1", token.getAccessToken());
		assertEquals("typeA", token.getTokenType());
		assertEquals(123, (int) token.getExpiresIn());
		assertEquals("mouthwash", token.getScope());
		assertEquals("oid", token.getOrcid());
	}

	@Test(expected = AccessTokenFormatException.class)
	public void invalidJsonThrowsException() throws AccessTokenFormatException {
		token = new AccessToken(EXTRA_OPENING_BRACE);
	}

	@Test(expected = AccessTokenFormatException.class)
	public void missingTokenTypeThrowsException()
			throws AccessTokenFormatException {
		token = new AccessToken(NO_TOKEN_TYPE);
	}

}
