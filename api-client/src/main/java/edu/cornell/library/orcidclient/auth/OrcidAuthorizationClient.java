package edu.cornell.library.orcidclient.auth;

import static edu.cornell.library.orcidclient.auth.AuthorizationStateProgress.FailureCause.BAD_ACCESS_TOKEN;
import static edu.cornell.library.orcidclient.auth.AuthorizationStateProgress.FailureCause.ERROR_STATUS;
import static edu.cornell.library.orcidclient.auth.AuthorizationStateProgress.FailureCause.INVALID_STATE;
import static edu.cornell.library.orcidclient.auth.AuthorizationStateProgress.FailureCause.NO_AUTH_CODE;
import static edu.cornell.library.orcidclient.auth.AuthorizationStateProgress.FailureCause.UNKNOWN;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.client.utils.URIBuilder;

import edu.cornell.library.orcidclient.OrcidClientException;
import edu.cornell.library.orcidclient.actions.ApiScope;
import edu.cornell.library.orcidclient.auth.AuthorizationStateProgress.FailureDetails;
import edu.cornell.library.orcidclient.auth.AuthorizationStateProgress.State;
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
 * Each instance should be short-lived. The persistent state is kept in the
 * cache, and the particular cache-instance may contain state that depends on
 * the HTTP request.
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
	private final AuthorizationStateProgressCache cache;
	private final HttpWrapper httpWrapper;

	public OrcidAuthorizationClient(OrcidAuthorizationClientContext context,
			AuthorizationStateProgressCache cache, HttpWrapper httpWrapper) {
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
	 * @throws OrcidClientException
	 */
	public AuthorizationStateProgress createProgressObject(ApiScope scope,
			URI successUrl, URI failureUrl) throws OrcidClientException {
		AuthorizationStateProgress authProgress = AuthorizationStateProgress
				.create(scope, successUrl, failureUrl)
				.addState(State.SEEKING_AUTHORIZATION);
		log.debug("createdProgressObject: " + authProgress);

		cache.store(authProgress);
		return authProgress;
	}

	/**
	 * Remove any unresolved progress for this scope.
	 * 
	 * Depending on the cache implementation, this could leave no progress for
	 * this scope, or could revert to the previously committed progress.
	 */
	public void resetProgress(ApiScope scope) throws OrcidClientException {
		cache.clearScopeProgress(scope);
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
	public String buildAuthorizationCall(AuthorizationStateProgress progress)
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
		AuthorizationStateProgress progress = cache.getByScope(scope);
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
	public AuthorizationStateProgress getProgressById(String id)
			throws OrcidClientException {
		return cache.getByID(id);
	}

	/**
	 * Update the authorization status to reflect this respons from our
	 * authorization request.
	 * 
	 * Return the redirect URL for either success or failure, as appropriate.
	 * 
	 * @throws OrcidClientException
	 */
	public String processAuthorizationResponse(ParameterMap parameters)
			throws OrcidClientException {
		String state = parameters.getParameter("state");
		AuthorizationStateProgress progress = getExistingAuthStatus(state);
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

		progress = progress.addCode(code);
		storeProgressById(progress);

		progress = getAccessTokenFromAuthCode(progress);
		storeProgressById(progress);
		if (State.SUCCESS != progress.getState()) {
			return progress.getFailureUrl().toString();
		}

		return progress.getSuccessUrl().toString();
	}

	private AuthorizationStateProgress getExistingAuthStatus(String state)
			throws OrcidClientException {
		if (state == null || state.isEmpty()) {
			throw new OrcidClientException(
					"Request did not contain a 'state' parameter");
		}

		AuthorizationStateProgress progress = getProgressById(state);
		if (progress == null) {
			throw new OrcidClientException(
					"Not seeking authorization for this state: " + state);
		}

		return progress;
	}

	private AuthorizationStateProgress getAccessTokenFromAuthCode(
			AuthorizationStateProgress progress) {
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
				return progress.addAccessToken(accessToken);
			} catch (OrcidClientException e) {
				BadAccessTokenFailureDetails details = new BadAccessTokenFailureDetails(
						string);
				log.warn(details.describe() + " : " + progress, e);
				return progress.addFailure(details);
			}
		} catch (HttpStatusCodeException e) {
			FailureDetails details = new UnknownFailureDetails(
					"Bad response code: " + e.getStatusCode());
			log.warn(details.describe() + " : " + progress);
			return progress.addFailure(details);
		} catch (IOException e) {
			FailureDetails details = new UnknownFailureDetails(
					"Unknown failure: " + e);
			log.warn(details.describe() + " : " + progress);
			return progress.addFailure(details);
		}

	}

	private String deny(AuthorizationStateProgress progress, String error,
			String errorDescription) throws OrcidClientException {
		AuthorizationStateProgress denied = progress.addState(State.DENIED);
		log.warn("User denied access, error='" + error + "', description='"
				+ errorDescription + "': " + denied);
		storeProgressById(denied);
		return denied.getFailureUrl().toString();
	}

	private String fail(AuthorizationStateProgress progress,
			FailureDetails details) throws OrcidClientException {
		AuthorizationStateProgress failed = progress.addFailure(details);
		log.warn(details.describe() + " : " + failed);
		storeProgressById(failed);
		return failed.getFailureUrl().toString();
	}

	private void storeProgressById(AuthorizationStateProgress progress)
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
