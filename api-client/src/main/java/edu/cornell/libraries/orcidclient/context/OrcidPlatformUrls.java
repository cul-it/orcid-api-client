/* $This file is distributed under the terms of the license in /doc/license.txt$ */

package edu.cornell.libraries.orcidclient.context;

/**
 * When an ORCID platform is specified, it must define these four URLs.
 */
interface OrcidPlatformUrls {

	String getPublicUrl();

	String getMemberUrl();

	String getOAuthUrl();

	String getTokenUrl();

	/**
	 * The standard platforms are SANDBOX and PRODUCTION.
	 */
	enum StandardPlatform implements OrcidPlatformUrls {
		//
		SANDBOX("https://pub.sandbox.orcid.org/v2.1/",
				"https://api.sandbox.orcid.org/v2.1/",
				"https://sandbox.orcid.org/oauth/authorize",
				"https://sandbox.orcid.org/oauth/token"),
		//
		PRODUCTION("https://pub.orcid.org/v2.1/", "https://api.orcid.org/v2.1/",
				"https://orcid.org/oauth/authorize",
				"https://orcid.org/oauth/token");

		private StandardPlatform(String publicUrl, String memberUrl,
				String oauthUrl, String tokenUrl) {
			this.publicUrl = publicUrl;
			this.memberUrl = memberUrl;
			this.oauthUrl = oauthUrl;
			this.tokenUrl = tokenUrl;
		}

		String publicUrl;
		String memberUrl;
		String oauthUrl;
		String tokenUrl;

		@Override
		public String getPublicUrl() {
			return publicUrl;
		}

		@Override
		public String getMemberUrl() {
			return memberUrl;
		}

		@Override
		public String getOAuthUrl() {
			return oauthUrl;
		}

		@Override
		public String getTokenUrl() {
			return tokenUrl;
		}
	}

	/**
	 * If you want a custom platform, you will need to provide the four URLs.
	 */
	static class CustomPlatformUrls implements OrcidPlatformUrls {
		private final String publicUrl;
		private final String memberUrl;
		private final String oAuthUrl;
		private final String tokenUrl;

		public CustomPlatformUrls(String publicUrl, String memberUrl,
				String oAuthUrl, String tokenUrl) {
			this.publicUrl = publicUrl;
			this.memberUrl = memberUrl;
			this.oAuthUrl = oAuthUrl;
			this.tokenUrl = tokenUrl;
		}

		@Override
		public String getPublicUrl() {
			return publicUrl;
		}

		@Override
		public String getMemberUrl() {
			return memberUrl;
		}

		@Override
		public String getOAuthUrl() {
			return oAuthUrl;
		}

		@Override
		public String getTokenUrl() {
			return tokenUrl;
		}

	}

}
