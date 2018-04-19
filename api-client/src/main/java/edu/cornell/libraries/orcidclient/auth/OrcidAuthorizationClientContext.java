package edu.cornell.libraries.orcidclient.auth;

import edu.cornell.libraries.orcidclient.context.OrcidClientContext.Setting;

/**
 * TODO
 */
public interface OrcidAuthorizationClientContext {

	String getAuthCodeRequestUrl();

	String getCallbackUrl();

	String getAccessTokenRequestUrl();

	/**
	 * Get the value of a setting in the current OrcidClientContext.
	 * 
	 * Must return valid values for at least these settings: CLIENT_ID,
	 * CLIENT_SECRET, OAUTH_AUTHORIZE_URL, OAUTH_TOKEN_URL, WEBAPP_BASE_URL,
	 * CALLBACK_PATH.
	 */
	String getSetting(Setting key);

}
