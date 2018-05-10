package edu.cornell.library.orcidclient.actions;

/**
 * The different scope for which you might need authorization.
 * 
 * For now, you can find the reference at
 * https://github.com/ORCID/ORCID-Source/blob/master/orcid-model/src/main/resources/record_2.1/README.md#scopes
 */
public enum ApiScope {
	AUTHENTICATE("/authenticate"),

	ACTIVITIES_UPDATE("/activities/update"),

	PERSON_UPDATE("/person/update"),

	READ_LIMITED("/read-limited"),

	READ_PUBLIC("/read-public"),

	WEBHOOK("/webhook");

	private final String scope;

	private ApiScope(String scope) {
		this.scope = scope;
	}

	public String getScope() {
		return scope;
	}

	public static ApiScope parse(String scope) {
		for (ApiScope value : values()) {
			if (value.getScope().equals(scope)) {
				return value;
			}
		}
		throw new IllegalArgumentException(
				"No ApiScope has scope of '" + scope + "'");
	}
}
