/* $This file is distributed under the terms of the license in /doc/license.txt$ */

package edu.cornell.libraries.orcidclient.auth;

import static edu.cornell.libraries.orcidclient.auth.AuthorizationStateProgress.FailureCause.BAD_ACCESS_TOKEN;
import static edu.cornell.libraries.orcidclient.auth.AuthorizationStateProgress.FailureCause.ERROR_STATUS;
import static edu.cornell.libraries.orcidclient.auth.AuthorizationStateProgress.FailureCause.INVALID_STATE;
import static edu.cornell.libraries.orcidclient.auth.AuthorizationStateProgress.FailureCause.NO_AUTH_CODE;
import static edu.cornell.libraries.orcidclient.context.OrcidClientContext.Setting.CLIENT_ID;
import static edu.cornell.libraries.orcidclient.context.OrcidClientContext.Setting.CLIENT_SECRET;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.client.utils.URIBuilder;

import edu.cornell.libraries.orcidclient.OrcidClientException;
import edu.cornell.libraries.orcidclient.actions.ApiScope;
import edu.cornell.libraries.orcidclient.auth.AuthorizationStateProgress.FailureDetails;
import edu.cornell.libraries.orcidclient.auth.AuthorizationStateProgress.State;
import edu.cornell.libraries.orcidclient.http.HttpPostRequester;
import edu.cornell.libraries.orcidclient.http.HttpPostRequester.PostRequest;
import edu.cornell.libraries.orcidclient.util.ParameterMap;

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
	private final HttpPostRequester httpPoster;

	public OrcidAuthorizationClient(OrcidAuthorizationClientContext context,
			AuthorizationStateProgressCache cache,
			HttpPostRequester httpPoster) {
		this.context = context;
		this.cache = cache;
		this.httpPoster = httpPoster;
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
					.addParameter("client_id", context.getSetting(CLIENT_ID))
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
					"Not seeking authorization for this action: " + progress);
		}

		return progress;
	}

	private AuthorizationStateProgress getAccessTokenFromAuthCode(
			AuthorizationStateProgress progress) throws OrcidClientException {
		PostRequest postRequest = httpPoster
				.createPostRequest(context.getAccessTokenRequestUrl())
				.addFormField("client_id", context.getSetting(CLIENT_ID))
				.addFormField("client_secret",
						context.getSetting(CLIENT_SECRET))
				.addFormField("grant_type", "authorization_code")
				.addFormField("code", progress.getAuthorizationCode())
				.addFormField("redirect_uri", context.getCallbackUrl())
				.addHeader("Accept", "application/json");

		String string = null;
		try {
			// TODO test bad response code: 400
			string = postRequest.execute().getContentString();
			log.debug("Json response: '" + string + "'");
			AccessToken accessToken = AccessToken.parse(string);
			return progress.addAccessToken(accessToken);
		} catch (IOException e) {
			BadAccessTokenFailureDetails details = new BadAccessTokenFailureDetails(
					string);
			log.warn(details.describe() + " : " + progress, e);
			return progress.addFailure(details);
		}
	}

	private String fail(AuthorizationStateProgress progress,
			FailureDetails details) throws OrcidClientException {
		log.warn(details.describe() + " : " + progress);
		storeProgressById(progress.addFailure(details));
		return progress.getFailureUrl().toString();
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

}
