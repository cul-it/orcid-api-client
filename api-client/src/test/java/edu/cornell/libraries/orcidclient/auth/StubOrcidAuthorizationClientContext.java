/* $This file is distributed under the terms of the license in /doc/license.txt$ */

package edu.cornell.libraries.orcidclient.auth;

import java.util.EnumMap;
import java.util.Map;

import edu.cornell.libraries.orcidclient.context.OrcidClientContext.Setting;

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
	private Map<Setting, String> settings = new EnumMap<>(Setting.class);

	public void setAuthCodeRequestUrl(String authCodeRequestUrl) {
		this.authCodeRequestUrl = authCodeRequestUrl;
	}

	public void setAccessTokenRequestUrl(String accessTokenRequestUrl) {
		this.accessTokenRequestUrl = accessTokenRequestUrl;
	}

	public void setCallbackUrl(String callbackUrl) {
		this.callbackUrl = callbackUrl;
	}
	
	public void setSetting(Setting key, String value) {
		settings.put(key, value);
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
	public String getSetting(Setting key) {
		return settings.get(key);
	}

}
