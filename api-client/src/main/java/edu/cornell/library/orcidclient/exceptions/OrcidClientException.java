package edu.cornell.library.orcidclient.exceptions;

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
