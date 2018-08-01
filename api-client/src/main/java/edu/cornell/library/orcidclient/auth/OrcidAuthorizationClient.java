package edu.cornell.library.orcidclient.auth;

import static edu.cornell.library.orcidclient.auth.OauthProgress.FailureCause.BAD_ACCESS_TOKEN;
import static edu.cornell.library.orcidclient.auth.OauthProgress.FailureCause.ERROR_STATUS;
import static edu.cornell.library.orcidclient.auth.OauthProgress.FailureCause.INVALID_STATE;
import static edu.cornell.library.orcidclient.auth.OauthProgress.FailureCause.NO_AUTH_CODE;
import static edu.cornell.library.orcidclient.auth.OauthProgress.FailureCause.UNKNOWN;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.client.utils.URIBuilder;

import edu.cornell.library.orcidclient.actions.ApiScope;
import edu.cornell.library.orcidclient.auth.OauthProgress.FailureDetails;
import edu.cornell.library.orcidclient.auth.OauthProgress.State;
import edu.cornell.library.orcidclient.exceptions.OrcidClientException;
import edu.cornell.library.orcidclient.http.HttpWrapper;
import edu.cornell.library.orcidclient.http.HttpWrapper.HttpStatusCodeException;
import edu.cornell.library.orcidclient.http.HttpWrapper.PostRequest;
import edu.cornell.library.orcidclient.util.ParameterMap;

/**
 * The tool that will help us walk through the 3-legged OAuth negotiation.
 * 
 * This is based on what has happened in this HTTP session, and what is
 * available from the persistent storage.
 * 
 * Each instance should be short-lived, tied to an HTTP session. The state of
 * the dance is kept in the progress cache. Long-term storage of access tokens
 * is handled by the AccessTokenCache.
 */
public class OrcidAuthorizationClient {
	private static final Log log = LogFactory
			.getLog(OrcidAuthorizationClient.class);

	/**
	 * Reflects the authorization status for a specified scope.
	 * 
	 * Note that we only try to get authorized if there is no AccessToken for
	 * this scope in the persistent storage.
	 */
	public enum AuthProcessResolution {
		/**
		 * We haven't tried to get authorized.
		 */
		NONE,

		/**
		 * We have an AccessToken.
		 */
		SUCCESS,

		/**
		 * The user denied authorization.
		 */
		DENIED,

		/**
		 * We tried to get authorization but encountered a system failure.
		 */
		FAILURE
	}

	private final OrcidAuthorizationClientContext context;
	private final OauthProgressCache cache;
	private final HttpWrapper httpWrapper;

	public OrcidAuthorizationClient(OrcidAuthorizationClientContext context,
			OauthProgressCache cache, HttpWrapper httpWrapper) {
		this.context = context;
		this.cache = cache;
		this.httpWrapper = httpWrapper;
	}

	/**
	 * Create a progress object to use in seeking authorization for this scope.
	 * Add it to the cache, so it can be used to track the progress of the
	 * authorization proceess.
	 * 
	 * @param successUrl
	 *            If the negotiation succeeds, redirect the browser to this URL.
	 * @param failureUrl
	 *            If the negotiation fails, redirect the browser to this URL.
	 * @param deniedUrl
	 *            If the user denies authorization, redirect the browser to this
	 *            URL.
	 * @throws OrcidClientException
	 */
	public OauthProgress createProgressObject(ApiScope scope, URI successUrl,
			URI failureUrl, URI deniedUrl) throws OrcidClientException {
		OauthProgress authProgress = new OauthProgress(scope, successUrl,
				failureUrl, deniedUrl);
		authProgress.addState(State.SEEKING_AUTHORIZATION);
		log.debug("createdProgressObject: " + authProgress);

		cache.store(authProgress);
		return authProgress;
	}

	/**
	 * Create a URL with appropriate parameters to seek authorization for this
	 * authorization process.
	 * 
	 * The URL can be sent to the browser as a re-direct to kick off the OAuth
	 * negotiation.
	 * 
	 * @throws OrcidClientException
	 */
	public String buildAuthorizationCall(OauthProgress progress)
			throws OrcidClientException {
		try {
			URI fullUri = new URIBuilder(context.getAuthCodeRequestUrl())
					.addParameter("client_id", context.getClientId())
					.addParameter("scope", progress.getScope().getScope())
					.addParameter("response_type", "code")
					.addParameter("redirect_uri", context.getCallbackUrl())
					.addParameter("state", progress.getId()).build();
			log.debug("fullUri=" + fullUri);
			return fullUri.toString();
		} catch (URISyntaxException e) {
			throw new OrcidClientException(
					"Failed to build the authorization URL for "
							+ progress.getScope(),
					e);
		}
	}

	/**
	 * Find out where we stand for this scope.
	 */
	public AuthProcessResolution getAuthProcessResolution(ApiScope scope)
			throws OrcidClientException {
		switch (cache.getByScope(scope).getState()) {
		case DENIED:
			return AuthProcessResolution.DENIED;
		case FAILURE:
			return AuthProcessResolution.FAILURE;
		case SUCCESS:
			return AuthProcessResolution.SUCCESS;
		default: // NONE, SEEKING_ACCESS_TOKEN, SEEKING_AUTHORIZATION
			return AuthProcessResolution.NONE;
		}
	}

	/**
	 * Get the AccessToken for this scope. It might have been obtained during
	 * this session, or it might be from the persistent storage.
	 * 
	 * @throws IllegalStateException
	 *             If the resolution for this scope is not SUCCESS.
	 */
	public AccessToken getAccessToken(ApiScope scope)
			throws IllegalStateException, OrcidClientException {
		OauthProgress progress = cache.getByScope(scope);
		if (progress.getState() != State.SUCCESS) {
			throw new IllegalStateException(
					"No access token available for scope: `" + scope + "`");
		} else {
			return progress.getAccessToken();
		}
	}

	/**
	 * Look in the cache for a progress indicator with this ID.
	 * 
	 * @returns the requested progress indicator, or null.
	 */
	public OauthProgress getProgressById(String id)
			throws OrcidClientException {
		return cache.getByID(id);
	}

	/**
	 * Update the authorization status to reflect this respons from our
	 * authorization request.
	 * 
	 * Return the redirect URL for success, denied, or failure, as appropriate.
	 * 
	 * @throws OrcidClientException
	 */
	public String processAuthorizationResponse(ParameterMap parameters)
			throws OrcidClientException {
		String state = parameters.getParameter("state");
		OauthProgress progress = getExistingAuthStatus(state);
		if (State.SEEKING_AUTHORIZATION != progress.getState()) {
			return fail(progress, new InvalidStateFailureDetails());
		}

		String error = parameters.getParameter("error");
		String errorDescription = parameters.getParameter("error_description");
		if ("access_denied".equals(error)) {
			return deny(progress, error, errorDescription);
		}

		if (error != null) {
			return fail(progress,
					new ErrorFailureDetails(error, errorDescription));
		}

		String code = parameters.getParameter("code");
		if (StringUtils.isEmpty(code)) {
			return fail(progress, new MissingAuthCodeFailureDetails());
		}

		progress.addCode(code);

		getAccessTokenFromAuthCode(progress);
		
		return progress.getRedirectUrl().toString();
	}

	private OauthProgress getExistingAuthStatus(String state)
			throws OrcidClientException {
		if (state == null || state.isEmpty()) {
			throw new OrcidClientException(
					"Request did not contain a 'state' parameter");
		}

		OauthProgress progress = getProgressById(state);
		if (progress == null) {
			throw new OrcidClientException(
					"Not seeking authorization for this state: " + state);
		}

		return progress;
	}

	private void getAccessTokenFromAuthCode(OauthProgress progress) {
		PostRequest postRequest = httpWrapper
				.createPostRequest(context.getAccessTokenRequestUrl())
				.addFormField("client_id", context.getClientId())
				.addFormField("client_secret", context.getClientSecret())
				.addFormField("grant_type", "authorization_code")
				.addFormField("code", progress.getAuthorizationCode())
				.addFormField("redirect_uri", context.getCallbackUrl())
				.addHeader("Accept", "application/json");

		try {
			String string = postRequest.execute().getContentString();

			try {
				log.debug("Json response: '" + string + "'");
				AccessToken accessToken = AccessToken.parse(string);
				progress.addAccessToken(accessToken);
			} catch (OrcidClientException e) {
				BadAccessTokenFailureDetails details = new BadAccessTokenFailureDetails(
						string);
				log.warn(details.describe() + " : " + progress, e);
				progress.addFailure(details);
			}
		} catch (HttpStatusCodeException e) {
			FailureDetails details = new UnknownFailureDetails(
					"Bad response code: " + e.getStatusCode());
			log.warn(details.describe() + " : " + progress);
			progress.addFailure(details);
		} catch (IOException e) {
			FailureDetails details = new UnknownFailureDetails(
					"Unknown failure: " + e);
			log.warn(details.describe() + " : " + progress);
			progress.addFailure(details);
		}

	}

	private String deny(OauthProgress progress, String error,
			String errorDescription) {
		progress.addState(State.DENIED);
		log.warn("User denied access, error='" + error + "', description='"
				+ errorDescription + "': " + progress);
		return progress.getRedirectUrl().toString();
	}

	private String fail(OauthProgress progress, FailureDetails details)
			throws OrcidClientException {
		progress.addFailure(details);
		log.warn(details.describe() + " : " + progress);
		storeProgressById(progress);
		return progress.getRedirectUrl().toString();
	}

	private void storeProgressById(OauthProgress progress)
			throws OrcidClientException {
		cache.store(progress);
	}

	// ----------------------------------------------------------------------
	// Failure details
	// ----------------------------------------------------------------------

	private static class InvalidStateFailureDetails extends FailureDetails {
		public InvalidStateFailureDetails() {
			super(INVALID_STATE);
		}

		@Override
		public String describe() {
			return ("Authorization response received, but not seeking authorization");
		}
	}

	private static class ErrorFailureDetails extends FailureDetails {
		private final String errorCode;
		private final String errorDescription;

		public ErrorFailureDetails(String errorCode, String errorDescription) {
			super(ERROR_STATUS);
			this.errorCode = errorCode;
			this.errorDescription = errorDescription;
		}

		@Override
		public String describe() {
			return ("Authorization response indicates error (error='"
					+ errorCode + "')(error_description='" + errorDescription
					+ "'): ");
		}
	}

	private static class MissingAuthCodeFailureDetails extends FailureDetails {
		public MissingAuthCodeFailureDetails() {
			super(NO_AUTH_CODE);
		}

		@Override
		public String describe() {
			return ("Authorization response contains no error, "
					+ "but also no auth code.");
		}
	}

	private static class BadAccessTokenFailureDetails extends FailureDetails {
		private final String json;

		public BadAccessTokenFailureDetails(String json) {
			super(BAD_ACCESS_TOKEN);
			this.json = json;
		}

		@Override
		public String describe() {
			return ("Request for access token yielded invalid JSON response (JSON='"
					+ json + "')");
		}
	}

	private static class UnknownFailureDetails extends FailureDetails {
		private final String message;

		public UnknownFailureDetails(String message) {
			super(UNKNOWN);
			this.message = message;
		}

		@Override
		public String describe() {
			return ("Bad authorization response: " + message);
		}
	}
}
