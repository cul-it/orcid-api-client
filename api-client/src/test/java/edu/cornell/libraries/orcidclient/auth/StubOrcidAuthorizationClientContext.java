package edu.cornell.libraries.orcidclient.auth;

/**
 * TODO
 */
public class StubOrcidAuthorizationClientContext
		implements OrcidAuthorizationClientContext {
	// ----------------------------------------------------------------------
	// Stub infrastructure
	// ----------------------------------------------------------------------

	private String authCodeRequestUrl;
	private String accessTokenRequestUrl;
	private String callbackUrl;
	private String clientId;
	private String clientSecret;

	public void setAuthCodeRequestUrl(String authCodeRequestUrl) {
		this.authCodeRequestUrl = authCodeRequestUrl;
	}

	public void setAccessTokenRequestUrl(String accessTokenRequestUrl) {
		this.accessTokenRequestUrl = accessTokenRequestUrl;
	}

	public void setCallbackUrl(String callbackUrl) {
		this.callbackUrl = callbackUrl;
	}

	public void setClientId(String clientId) {
		this.clientId = clientId;
	}

	public void setClientSecret(String clientSecret) {
		this.clientSecret = clientSecret;
	}

	// ----------------------------------------------------------------------
	// Stub methods
	// ----------------------------------------------------------------------

	@Override
	public String getAuthCodeRequestUrl() {
		return authCodeRequestUrl;
	}

	@Override
	public String getAccessTokenRequestUrl() {
		return accessTokenRequestUrl;
	}

	@Override
	public String getCallbackUrl() {
		return callbackUrl;
	}

	@Override
	public String getClientId() {
		return clientId;
	}

	@Override
	public String getClientSecret() {
		return clientSecret;
	}

}
