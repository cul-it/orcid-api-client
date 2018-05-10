package edu.cornell.library.orcidclient.auth;

/**
 * These are the settings that the Authorization client needs to know.
 */
public interface OrcidAuthorizationClientContext {

	String getAuthCodeRequestUrl();

	String getCallbackUrl();

	String getAccessTokenRequestUrl();

	String getClientId();

	String getClientSecret();

}
