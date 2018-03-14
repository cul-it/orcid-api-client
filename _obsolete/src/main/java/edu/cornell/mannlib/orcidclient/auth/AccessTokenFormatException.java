/* $This file is distributed under the terms of the license in /doc/license.txt$ */

package edu.cornell.mannlib.orcidclient.auth;

import edu.cornell.mannlib.orcidclient.OrcidClientException;

/**
 * Failed to parse an AccessToken from the JSON string.
 */
public class AccessTokenFormatException extends OrcidClientException {

	public AccessTokenFormatException(String message) {
		super(message);
	}

	public AccessTokenFormatException(String message, Throwable cause) {
		super(message, cause);
	}

}
