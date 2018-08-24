package edu.cornell.library.orcidclient.context;

import static edu.cornell.library.orcidclient.context.OrcidClientContextImpl.Setting.API_PLATFORM;
import static edu.cornell.library.orcidclient.context.OrcidClientContextImpl.Setting.AUTHORIZED_API_BASE_URL;
import static edu.cornell.library.orcidclient.context.OrcidClientContextImpl.Setting.CALLBACK_PATH;
import static edu.cornell.library.orcidclient.context.OrcidClientContextImpl.Setting.CLIENT_ID;
import static edu.cornell.library.orcidclient.context.OrcidClientContextImpl.Setting.CLIENT_SECRET;
import static edu.cornell.library.orcidclient.context.OrcidClientContextImpl.Setting.OAUTH_AUTHORIZE_URL;
import static edu.cornell.library.orcidclient.context.OrcidClientContextImpl.Setting.OAUTH_TOKEN_URL;
import static edu.cornell.library.orcidclient.context.OrcidClientContextImpl.Setting.PUBLIC_API_BASE_URL;
import static edu.cornell.library.orcidclient.context.OrcidClientContextImpl.Setting.SITE_BASE_URL;
import static edu.cornell.library.orcidclient.context.OrcidClientContextImpl.Setting.WEBAPP_BASE_URL;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.client.utils.URIUtils;

import edu.cornell.library.orcidclient.exceptions.OrcidClientException;

/**
 * Read the supplied settings, validate them, adjust them as appropriate, and be
 * prepared to provide them when asked.
 */
public class OrcidClientContextImpl extends OrcidClientContext {
	private static final Log log = LogFactory
			.getLog(OrcidClientContextImpl.class);

	public interface Setting {
		/**
		 * ID assigned by ORCID to the application.
		 */
		String CLIENT_ID = "CLIENT_ID";

		/**
		 * Secret code assigned by ORCID to the application.
		 */
		String CLIENT_SECRET = "CLIENT_SECRET";

		/**
		 * Environment - "production" or "sandbox".
		 */
		String API_PLATFORM = "API_PLATFORM";

		/**
		 * Root of the website where users may view ORCID records. If
		 * API_PLATFORM is "custom", this is required.
		 */
		String SITE_BASE_URL = "SITE_BASE_URL";

		/**
		 * Root of the public API (requires no authorization). If API_PLATFORM
		 * is "custom", this is required.
		 */
		String PUBLIC_API_BASE_URL = "PUBLIC_API_BASE_URL";

		/**
		 * Root of the restricted API (requires authorization). If API_PLATFORM
		 * is "custom", this is required.
		 */
		String AUTHORIZED_API_BASE_URL = "AUTHORIZED_API_BASE_URL";

		/**
		 * URL to obtain an authorization code. This is sent to the browser as a
		 * redirect, so the user can log in at the ORCID site. If API_PLATFORM
		 * is "custom", this is required.
		 */
		String OAUTH_AUTHORIZE_URL = "OAUTH_AUTHORIZE_URL";

		/**
		 * URL to exchange the authorization code for an OAuth access token. If
		 * API_PLATFORM is "custom", this is required.
		 */
		String OAUTH_TOKEN_URL = "OAUTH_TOKEN_URL";

		/**
		 * The base URL for contacting this webapp (including context path. Used
		 * when building the redirect URL for the browser.
		 */
		String WEBAPP_BASE_URL = "WEBAPP_BASE_URL";

		/**
		 * Where should ORCID call back to during the auth dance? Path within
		 * this webapp.
		 */
		String CALLBACK_PATH = "CALLBACK_PATH";

		String[] ALL_SETTINGS = new String[] { API_PLATFORM, CLIENT_ID, CLIENT_SECRET,
				SITE_BASE_URL, PUBLIC_API_BASE_URL, AUTHORIZED_API_BASE_URL,
				OAUTH_AUTHORIZE_URL, OAUTH_TOKEN_URL, WEBAPP_BASE_URL,
				CALLBACK_PATH };
		String[] REQUIRED_SETTINGS = new String[] { CLIENT_ID, CLIENT_SECRET,
				SITE_BASE_URL, PUBLIC_API_BASE_URL, AUTHORIZED_API_BASE_URL,
				OAUTH_AUTHORIZE_URL, OAUTH_TOKEN_URL, WEBAPP_BASE_URL,
				CALLBACK_PATH };
	}

	private final Map<String, String> settings;

	private String callbackUrl;

	public OrcidClientContextImpl(Map<String, String> settings)
			throws OrcidClientException {
		Objects.requireNonNull(settings, "'settings' may not be null.");
		this.settings = new HashMap<>();
		this.settings.putAll(settings);

		adjustSettingsForPlatform();
		complainAboutMissingSettings();
		ensureBaseUrlsEndWithSlash();
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
		for (String s : Setting.REQUIRED_SETTINGS) {
			if (!settings.containsKey(s)) {
				throw new MissingSettingException(s);
			}
		}
	}

	private void ensureBaseUrlsEndWithSlash() {
		ensureSettingEndsWithSlash(PUBLIC_API_BASE_URL);
		ensureSettingEndsWithSlash(AUTHORIZED_API_BASE_URL);
		ensureSettingEndsWithSlash(WEBAPP_BASE_URL);
	}

	private void ensureSettingEndsWithSlash(String key) {
		String base = getSetting(key);
		if (!base.endsWith("/")) {
			settings.put(key, base + "/");
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

	String getSetting(String key) {
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
	public String getSiteBaseUrl() {
		return getSetting(SITE_BASE_URL);
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
	public String getClientId() {
		return getSetting(CLIENT_ID);
	}

	@Override
	public String getClientSecret() {
		return getSetting(CLIENT_SECRET);
	}

	@Override
	public String getWebappBaseUrl() {
		return getSetting(WEBAPP_BASE_URL);
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

	static class MissingSettingException extends OrcidClientException {
		public MissingSettingException(String missingSetting) {
			super(toMessage(missingSetting));
		}

		private static String toMessage(String ms) {
			return "You must provide a value for '" + ms + "'";
		}
	}
}
