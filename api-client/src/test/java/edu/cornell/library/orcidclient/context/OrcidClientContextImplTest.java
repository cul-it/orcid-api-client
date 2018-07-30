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
import static org.junit.Assert.assertEquals;

import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.junit.Test;

import edu.cornell.library.orcidclient.context.OrcidClientContextImpl.MissingSettingException;
import edu.cornell.library.orcidclient.context.OrcidClientContextImpl.Setting;
import edu.cornell.library.orcidclient.exceptions.OrcidClientException;
import edu.cornell.library.orcidclient.testing.AbstractTestClass;

/**
 * Exercise the functions of the OrcidClientContextImpl.
 * 
 * Use a helper class SettingsMap to initialize the maps in a conversational
 * style.
 */
public class OrcidClientContextImplTest extends AbstractTestClass {
	private final Map<Setting, String> SETTINGS_WITH_PLATFORM = Collections
			.unmodifiableMap(new SettingsMap() //
					.and(API_PLATFORM, "sandbox")
					.and(CLIENT_ID, "XXX-9X42XXYY3Z4ZZ1YZ")
					.and(CLIENT_SECRET, "cafebabe-0000-9999-d00d-b0ad1ceabeef")
					.and(CALLBACK_PATH, "test/callback")
					.and(WEBAPP_BASE_URL, "http://my.domain/myapp/"));

	private final Map<Setting, String> SETTINGS_WITHOUT_PLATFORM = Collections
			.unmodifiableMap(new SettingsMap() //
					.and(SITE_BASE_URL, "http://unittest.org/")
					.and(AUTHORIZED_API_BASE_URL,
							"http://api.unittest.org/v9.9/")
					.and(PUBLIC_API_BASE_URL, "http://pub.unittest.org/v9.9/")
					.and(OAUTH_AUTHORIZE_URL,
							"http://unittest.org/oauth/authorize")
					.and(OAUTH_TOKEN_URL, "http://unittest.org/oauth/token")
					.and(CLIENT_ID, "XXX-9X42XXYY3Z4ZZ1YZ")
					.and(CLIENT_SECRET, "cafebabe-0000-9999-d00d-b0ad1ceabeef")
					.and(CALLBACK_PATH, "test/callback")
					.and(WEBAPP_BASE_URL, "http://my.domain/myapp/"));

	private final Map<Setting, String> DONT_CARE = Collections.emptyMap();

	private OrcidClientContextImpl context;
	private Map<Setting, String> provided;
	private Map<Setting, String> expected;

	@Test
	public void successWithoutPlatform() throws OrcidClientException {
		provided = SETTINGS_WITHOUT_PLATFORM;
		expected = SETTINGS_WITHOUT_PLATFORM;

		assertExpectedSettings();
	}

	@Test
	public void successWithPlatform() throws OrcidClientException {
		provided = SETTINGS_WITH_PLATFORM;
		expected = settings(SETTINGS_WITH_PLATFORM)
				.and(SITE_BASE_URL, "https://sandbox.orcid.org/")
				.and(AUTHORIZED_API_BASE_URL,
						"https://api.sandbox.orcid.org/v2.1/")
				.and(PUBLIC_API_BASE_URL, "https://pub.sandbox.orcid.org/v2.1/")
				.and(OAUTH_AUTHORIZE_URL,
						"https://sandbox.orcid.org/oauth/authorize")
				.and(OAUTH_TOKEN_URL, "https://sandbox.orcid.org/oauth/token");

		assertExpectedSettings();
	}

	@Test
	public void platformSettingsOverrideExplicitSettings()
			throws OrcidClientException {
		provided = settings(SETTINGS_WITHOUT_PLATFORM).and(API_PLATFORM,
				"production");
		expected = settings(SETTINGS_WITHOUT_PLATFORM)
				.except(SITE_BASE_URL, "https://orcid.org/")
				.except(PUBLIC_API_BASE_URL, "https://pub.orcid.org/v2.1/")
				.except(OAUTH_TOKEN_URL, "https://orcid.org/oauth/token")
				.except(AUTHORIZED_API_BASE_URL, "https://api.orcid.org/v2.1/")
				.except(OAUTH_AUTHORIZE_URL,
						"https://orcid.org/oauth/authorize")
				.and(API_PLATFORM, "production");
		assertExpectedSettings();
	}

	@Test
	public void baseUrlsAreModifiedToIncludeTrailingSlash()
			throws OrcidClientException {
		provided = settings(SETTINGS_WITHOUT_PLATFORM)
				.except(PUBLIC_API_BASE_URL, "http://pub.unittest.org/v9.9")
				.except(AUTHORIZED_API_BASE_URL, "http://api.unittest.org/v9.9")
				.except(WEBAPP_BASE_URL, "http://my.domain/myapp");
		expected = SETTINGS_WITHOUT_PLATFORM;

		assertExpectedSettings();
	}

	@Test
	public void nullSettingsMap_throwsException() throws OrcidClientException {
		provided = null;
		expected = DONT_CARE;

		expectException(NullPointerException.class, "'settings'");
		assertExpectedSettings();
	}

	@Test
	public void missingRequireSetting_throwsException()
			throws OrcidClientException {
		provided = settings(SETTINGS_WITH_PLATFORM).butNot(CLIENT_ID);
		expected = DONT_CARE;

		expectException(MissingSettingException.class, CLIENT_ID.toString());
		assertExpectedSettings();
	}

	@Test
	public void invalidPlatform_throwsException() throws OrcidClientException {
		provided = settings(SETTINGS_WITH_PLATFORM).except(API_PLATFORM,
				"bogus");
		expected = DONT_CARE;

		expectException(OrcidClientException.class, API_PLATFORM.toString());
		assertExpectedSettings();
	}

	@Test
	public void syntacticallyInvalidCallbackCombination_throwsException()
			throws OrcidClientException {
		provided = settings(SETTINGS_WITHOUT_PLATFORM).except(WEBAPP_BASE_URL,
				"!http://my.domain/myapp/");
		expected = DONT_CARE;

		expectException(OrcidClientException.class, "Failed to resolve");
		assertExpectedSettings();
	}

	// ----------------------------------------------------------------------
	// Helper methods
	// ----------------------------------------------------------------------

	private void assertExpectedSettings() throws OrcidClientException {
		context = new OrcidClientContextImpl(provided);

		Map<Setting, String> actual = new HashMap<>();
		for (Setting key : Setting.values()) {
			String setting = context.getSetting(key);
			if (StringUtils.isNotEmpty(setting)) {
				actual.put(key, setting);
			}
		}

		assertEquals(expected, actual);
	}

	private SettingsMap settings(Map<Setting, String> initial) {
		return new SettingsMap(initial);
	}

	// ----------------------------------------------------------------------
	// Helper classes
	// ----------------------------------------------------------------------

	private static class SettingsMap extends EnumMap<Setting, String> {
		public SettingsMap() {
			super(Setting.class);
		}

		public SettingsMap(Map<Setting, String> initial) {
			super(initial);
		}

		public SettingsMap and(Setting key, String value) {
			put(key, value);
			return this;
		}

		public SettingsMap except(Setting key, String value) {
			put(key, value);
			return this;
		}

		public SettingsMap butNot(Setting key) {
			remove(key);
			return this;
		}
	}
}
