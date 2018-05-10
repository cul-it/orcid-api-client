package edu.cornell.library.orcidclient.auth;

import java.net.URI;
import java.net.URISyntaxException;

import edu.cornell.library.orcidclient.actions.ApiScope;

/**
 * Keep track of where we are in the 3-legged OAuth dance.
 * 
 * NOTE: Instances are immutable, so if you make a change, you are creating a
 * new object. Be sure to treat it that way.
 */
public class AuthorizationStateProgress {
	public static final URI NO_URI = assignNoUri();

	private static URI assignNoUri() {
		try {
			return new URI("http://no.uri");
		} catch (URISyntaxException e) {
			throw new RuntimeException("Failed to create NO_URI", e);
		}
	}

	/**
	 * Where do we stand in the dance?
	 */
	public enum State {
		NONE, SEEKING_AUTHORIZATION, SEEKING_ACCESS_TOKEN, FAILURE, DENIED, SUCCESS
	}

	/**
	 * If State is FAILURE, what is the reason for failure?
	 */
	public enum FailureCause {
		NONE, INVALID_STATE, ERROR_STATUS, NO_AUTH_CODE, BAD_ACCESS_TOKEN, UNKNOWN
	}

	/**
	 * If State is FAILURE, what are the details (for the log, perhaps)? Extend
	 * this class with a describe() method that records any needed details.
	 */
	public abstract static class FailureDetails {
		public static final FailureDetails NO_FAILURE = new FailureDetails(
				FailureCause.NONE) {
			@Override
			public String describe() {
				return "No failure";
			}
		};

		protected final FailureCause cause;

		public FailureDetails(FailureCause cause) {
			this.cause = cause;
		}

		public FailureCause getCause() {
			return cause;
		}

		public abstract String describe();

		@Override
		public String toString() {
			return String.format("FailureDetails[cause=%s, describe()=%s]",
					cause, describe());
		}
	}

	// ----------------------------------------------------------------------
	// The factory
	// ----------------------------------------------------------------------

	public static AuthorizationStateProgress create(ApiScope scope,
			URI successUrl, URI failureUrl) {
		return new AuthorizationStateProgress(scope, successUrl, failureUrl);
	}

	public static AuthorizationStateProgress copy(
			AuthorizationStateProgress original) {
		return new AuthorizationStateProgress(original, null, null, null, null);
	}

	// ----------------------------------------------------------------------
	// The instance
	// ----------------------------------------------------------------------

	private final String id;
	private final State state;
	private final FailureDetails failureDetails;
	private final ApiScope scope;
	private final URI successUrl;
	private final URI failureUrl;
	private final AccessToken accessToken;
	private final String authorizationCode;

	/**
	 * Create a fresh progress record, with an arbitrary ID.
	 */
	private AuthorizationStateProgress(ApiScope scope, URI successUrl,
			URI failureUrl) {
		this.id = String.valueOf(this.hashCode());
		this.state = State.NONE;
		this.failureDetails = FailureDetails.NO_FAILURE;
		this.scope = scope;
		this.successUrl = successUrl;
		this.failureUrl = failureUrl;
		this.accessToken = AccessToken.NO_TOKEN;
		this.authorizationCode = "";
	}

	/**
	 * Make a copy of the original progress record, with alterations as
	 * provided.
	 */
	private AuthorizationStateProgress(AuthorizationStateProgress original,
			State state, FailureDetails failureDetails, AccessToken accessToken,
			String authorizationCode) {
		this.id = original.id;
		this.state = nonNull(state, original.state);
		this.failureDetails = nonNull(failureDetails, original.failureDetails);
		this.scope = original.scope;
		this.successUrl = original.successUrl;
		this.failureUrl = original.failureUrl;
		this.accessToken = nonNull(accessToken, original.accessToken);
		this.authorizationCode = nonNull(authorizationCode,
				original.authorizationCode);
	}

	private <T> T nonNull(T preferredValue, T defaultValue) {
		return (preferredValue == null) ? defaultValue : preferredValue;
	}

	public String getId() {
		return id;
	}

	public ApiScope getScope() {
		return scope;
	}

	public URI getSuccessUrl() {
		return successUrl;
	}

	public URI getFailureUrl() {
		return failureUrl;
	}

	public State getState() {
		return state;
	}

	public FailureCause getFailureCause() {
		return failureDetails.getCause();
	}

	public AccessToken getAccessToken() {
		return accessToken;
	}

	public String getAuthorizationCode() {
		return authorizationCode;
	}

	public AuthorizationStateProgress addState(State newState) {
		return new AuthorizationStateProgress(this, newState, null, null, null);
	}
	
	public AuthorizationStateProgress addFailure(FailureDetails details) {
		return new AuthorizationStateProgress(this, State.FAILURE, details,
				null, null);
	}

	public AuthorizationStateProgress addCode(String code) {
		return new AuthorizationStateProgress(this, State.SEEKING_ACCESS_TOKEN,
				null, null, code);
	}

	public AuthorizationStateProgress addAccessToken(AccessToken token) {
		return new AuthorizationStateProgress(this, State.SUCCESS, null, token,
				null);
	}

	@Override
	public String toString() {
		return String.format(
				"AuthorizationStateProgress[id=%s, state=%s, failureDetails=%s, scope=%s, successUrl=%s, "
						+ "failureUrl=%s, accessToken=%s, authorizationCode=%s]",
				id, state, failureDetails, scope, successUrl, failureUrl,
				accessToken, authorizationCode);
	}

}
