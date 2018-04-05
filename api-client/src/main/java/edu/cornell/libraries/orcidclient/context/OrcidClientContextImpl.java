/* $This file is distributed under the terms of the license in /doc/license.txt$ */

package edu.cornell.libraries.orcidclient.context;

import static edu.cornell.libraries.orcidclient.context.OrcidClientContext.Setting.API_PLATFORM;
import static edu.cornell.libraries.orcidclient.context.OrcidClientContext.Setting.AUTHORIZED_API_BASE_URL;
import static edu.cornell.libraries.orcidclient.context.OrcidClientContext.Setting.CALLBACK_PATH;
import static edu.cornell.libraries.orcidclient.context.OrcidClientContext.Setting.OAUTH_AUTHORIZE_URL;
import static edu.cornell.libraries.orcidclient.context.OrcidClientContext.Setting.OAUTH_TOKEN_URL;
import static edu.cornell.libraries.orcidclient.context.OrcidClientContext.Setting.PUBLIC_API_BASE_URL;
import static edu.cornell.libraries.orcidclient.context.OrcidClientContext.Setting.WEBAPP_BASE_URL;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.client.utils.URIUtils;

import edu.cornell.libraries.orcidclient.OrcidClientException;
import edu.cornell.libraries.orcidclient.actions.OrcidApiClient;
import edu.cornell.libraries.orcidclient.auth.DefaultOrcidAuthorizationClient;
import edu.cornell.libraries.orcidclient.auth.OrcidAuthorizationClient;

/**
 * Read the supplied settings, validate them, adjust them as appropriate, and be
 * prepared to provide them when asked.
 */
public class OrcidClientContextImpl extends OrcidClientContext {
	private static final Log log = LogFactory
			.getLog(OrcidClientContextImpl.class);

	private final Map<Setting, String> settings;

	private String callbackUrl;

	public OrcidClientContextImpl(Map<Setting, String> settings)
			throws OrcidClientException {
		this.settings = new EnumMap<>(settings);

		adjustSettingsForPlatform();
		complainAboutMissingSettings();
		ensureWebappBaseEndsWithSlash();
		figureCallbackUrl();
	}

	private void adjustSettingsForPlatform() throws OrcidClientException {
		if (settings.containsKey(API_PLATFORM)) {
			try {
				String platformKey = settings.get(API_PLATFORM).toUpperCase();
				OrcidPlatformUrls platform = OrcidPlatformUrls
						.valueOf(platformKey);
				settings.putAll(platform.getUrls());
			} catch (IllegalArgumentException e) {
				throw new OrcidClientException(
						API_PLATFORM + " must be one of: "
								+ Arrays.toString(OrcidPlatformUrls.values()));
			}
		}
	}

	private void complainAboutMissingSettings() throws MissingSettingException {
		for (Setting s : Setting.values()) {
			if (!settings.containsKey(s)) {
				throw new MissingSettingException(s);
			}
		}
	}

	private void ensureWebappBaseEndsWithSlash() {
		String base = getSetting(WEBAPP_BASE_URL);
		if (!base.endsWith("/")) {
			settings.put(WEBAPP_BASE_URL, base + "/");
		}
	}

	private void figureCallbackUrl() throws OrcidClientException {
		try {
			callbackUrl = resolvePathWithWebapp(getSetting(CALLBACK_PATH))
					.toString();
		} catch (URISyntaxException e) {
			throw new OrcidClientException(String.format(
					"Failed to resolve the callback path: `%s` is `%s`, `%s` is `%s`",
					WEBAPP_BASE_URL, getSetting(WEBAPP_BASE_URL), CALLBACK_PATH,
					getSetting(CALLBACK_PATH)), e);
		}
	}

	@Override
	public String getSetting(Setting key) {
		if (settings.containsKey(key)) {
			return settings.get(key);
		} else {
			return "";
		}
	}

	@Override
	public String getCallbackUrl() {
		return callbackUrl;
	}

	@Override
	public String getAuthCodeRequestUrl() {
		return getSetting(OAUTH_AUTHORIZE_URL);
	}

	@Override
	public String getAccessTokenRequestUrl() {
		return getSetting(OAUTH_TOKEN_URL);
	}

	@Override
	public String getApiPublicUrl() {
		return getSetting(PUBLIC_API_BASE_URL);
	}

	@Override
	public String getApiMemberUrl() {
		return getSetting(AUTHORIZED_API_BASE_URL);
	}

	@Override
	public OrcidApiClient getApiClient(HttpServletRequest req) {
		return new OrcidApiClient(this, req);
	}

	@Override
	public OrcidAuthorizationClient getAuthorizationClient(
			HttpServletRequest req) {
		return new DefaultOrcidAuthorizationClient(this, req);
	}

	@Override
	public URI resolvePathWithWebapp(String path) throws URISyntaxException {
		return URIUtils.resolve(new URI(getSetting(WEBAPP_BASE_URL)), path);
	}

	@Override
	public String toString() {
		return "OrcidClientContextImpl[settings=" + settings + ", callbackUrl="
				+ callbackUrl + "]";
	}

	private static class MissingSettingException extends OrcidClientException {
		public MissingSettingException(Setting missingSetting) {
			super(toMessage(missingSetting));
		}

		private static String toMessage(Setting ms) {
			return "You must provide a value for '" + ms + "'";
		}
	}
}
