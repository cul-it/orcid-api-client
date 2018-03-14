/* $This file is distributed under the terms of the license in /doc/license.txt$ */

package edu.cornell.mannlib.orcidclient;

/**
 * The ORCID client code has detected a problem.
 */
public class OrcidClientException extends Exception {

	public OrcidClientException(String message) {
		super(message);
	}

	public OrcidClientException(String message, Throwable cause) {
		super(message, cause);
	}

}
