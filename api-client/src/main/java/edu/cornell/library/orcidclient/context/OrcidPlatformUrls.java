package edu.cornell.library.orcidclient.context;

import static edu.cornell.library.orcidclient.context.OrcidClientContextImpl.Setting.AUTHORIZED_API_BASE_URL;
import static edu.cornell.library.orcidclient.context.OrcidClientContextImpl.Setting.OAUTH_AUTHORIZE_URL;
import static edu.cornell.library.orcidclient.context.OrcidClientContextImpl.Setting.OAUTH_TOKEN_URL;
import static edu.cornell.library.orcidclient.context.OrcidClientContextImpl.Setting.PUBLIC_API_BASE_URL;
import static edu.cornell.library.orcidclient.context.OrcidClientContextImpl.Setting.SITE_BASE_URL;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * When an ORCID platform is specified, it defines these four URLs.
 * 
 * A settings file can invoke one of these by name, with API_PLATFORM=SANDBOX,
 * for example, and not need to specify the four URLs.
 */
enum OrcidPlatformUrls {
	//
	SANDBOX("https://sandbox.orcid.org/", //
			"https://pub.sandbox.orcid.org/v2.1/",
			"https://api.sandbox.orcid.org/v2.1/",
			"https://sandbox.orcid.org/oauth/authorize",
			"https://sandbox.orcid.org/oauth/token"),
	//
	PRODUCTION("https://orcid.org/", //
			"https://pub.orcid.org/v2.1/", //
			"https://api.orcid.org/v2.1/", //
			"https://orcid.org/oauth/authorize",
			"https://orcid.org/oauth/token");

	private OrcidPlatformUrls(String siteUrl, String publicUrl,
			String memberUrl, String oauthUrl, String tokenUrl) {
		Map<String, String> map = new HashMap<>();
		map.put(SITE_BASE_URL, siteUrl);
		map.put(PUBLIC_API_BASE_URL, publicUrl);
		map.put(AUTHORIZED_API_BASE_URL, memberUrl);
		map.put(OAUTH_AUTHORIZE_URL, oauthUrl);
		map.put(OAUTH_TOKEN_URL, tokenUrl);
		this.urls = Collections.unmodifiableMap(map);
	}

	Map<String, String> urls;

	public Map<String, String> getUrls() {
		return urls;
	}
}
