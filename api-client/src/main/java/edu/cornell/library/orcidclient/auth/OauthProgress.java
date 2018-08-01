package edu.cornell.library.orcidclient.auth;

import static edu.cornell.library.orcidclient.auth.OauthProgress.State.FAILURE;
import static edu.cornell.library.orcidclient.auth.OauthProgress.State.SEEKING_ACCESS_TOKEN;

import java.net.URI;
import java.net.URISyntaxException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import edu.cornell.library.orcidclient.actions.ApiScope;

/**
 * Keep track of where we are in the 3-legged OAuth dance.
 */
public class OauthProgress {
	private static final Log log = LogFactory.getLog(OauthProgress.class);

	public static final URI NO_URI = null;

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
	 * If State is FAILURE, what are the details (for the log)? Extend this
	 * class with a describe() method that records any needed details.
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
	// The instance
	// ----------------------------------------------------------------------

	// Provide package access to facilitate unit tests.
	final String id;
	final ApiScope scope;
	final URI successUrl;
	final URI failureUrl;
	final URI deniedUrl;

	private State state;
	private FailureDetails failureDetails;
	private AccessToken accessToken;
	private String authorizationCode;

	/**
	 * Create a fresh progress record, with an arbitrary ID.
	 */
	public OauthProgress(ApiScope scope, URI successUrl, URI failureUrl,
			URI deniedUrl) {
		this.id = String.valueOf(this.hashCode());
		this.state = State.NONE;
		this.failureDetails = FailureDetails.NO_FAILURE;
		this.scope = scope;
		this.successUrl = successUrl;
		this.failureUrl = failureUrl;
		this.deniedUrl = deniedUrl;
		this.accessToken = AccessToken.NO_TOKEN;
		this.authorizationCode = "";
	}

	// Provide package access to facilitate unit tests.
	// Note: doesn't copy ID
	OauthProgress copy() {
		OauthProgress that = new OauthProgress(this.scope, this.successUrl,
				this.failureUrl, this.deniedUrl);
		that.state = this.state;
		that.failureDetails = this.failureDetails;
		that.accessToken = this.accessToken;
		that.authorizationCode = this.authorizationCode;
		return that;
	}

	public String getId() {
		return id;
	}

	public ApiScope getScope() {
		return scope;
	}

	public URI getRedirectUrl() {
		switch (state) {
		case SUCCESS:
			return successUrl;
		case DENIED:
			return deniedUrl;
		default: // FAILURE
			if (state != State.FAILURE) {
				log.warn("The OAuth progress dance is not complete; state = "
						+ state);
			}
			return failureUrl;
		}
	}

	public State getState() {
		return state;
	}

	public FailureCause getFailureCause() {
		return failureDetails.getCause();
	}

	public FailureDetails getFailureDetails() {
		return failureDetails;
	}

	public AccessToken getAccessToken() {
		return accessToken;
	}

	public String getAuthorizationCode() {
		return authorizationCode;
	}

	public OauthProgress addState(State newState) {
		state = newState;
		return this;
	}

	public OauthProgress addFailure(FailureDetails details) {
		state = FAILURE;
		failureDetails = details;
		return this;
	}

	public OauthProgress addCode(String code) {
		state = SEEKING_ACCESS_TOKEN;
		authorizationCode = code;
		return this;
	}

	public OauthProgress addAccessToken(AccessToken token) {
		state = State.SUCCESS;
		accessToken = token;
		return this;
	}

	@Override
	public String toString() {
		return String.format(
				"OauthProgress[id=%s, state=%s, failureDetails=%s, "
						+ "scope=%s, successUrl=%s, failureUrl=%s, deniedUrl=%s, "
						+ "accessToken=%s, authorizationCode=%s]",
				id, state, failureDetails, scope, successUrl, failureUrl,
				deniedUrl, accessToken, authorizationCode);
	}

}
