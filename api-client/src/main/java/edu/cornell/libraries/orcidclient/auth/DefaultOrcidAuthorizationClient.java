/* $This file is distributed under the terms of the license in /doc/license.txt$ */

package edu.cornell.libraries.orcidclient.auth;

import static edu.cornell.libraries.orcidclient.auth.AuthorizationStateProgress.FailureCause.BAD_ACCESS_TOKEN;
import static edu.cornell.libraries.orcidclient.auth.AuthorizationStateProgress.FailureCause.ERROR_STATUS;
import static edu.cornell.libraries.orcidclient.auth.AuthorizationStateProgress.FailureCause.INVALID_STATE;
import static edu.cornell.libraries.orcidclient.auth.AuthorizationStateProgress.FailureCause.NO_AUTH_CODE;
import static edu.cornell.libraries.orcidclient.auth.AuthorizationStateProgress.State.SEEKING_AUTHORIZATION;
import static edu.cornell.libraries.orcidclient.context.OrcidClientContext.Setting.CLIENT_ID;
import static edu.cornell.libraries.orcidclient.context.OrcidClientContext.Setting.CLIENT_SECRET;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.NameValuePair;
import org.apache.http.client.fluent.Form;
import org.apache.http.client.fluent.Request;
import org.apache.http.client.utils.URIBuilder;

import edu.cornell.libraries.orcidclient.OrcidClientException;
import edu.cornell.libraries.orcidclient.actions.ApiScope;
import edu.cornell.libraries.orcidclient.auth.AuthorizationStateProgress.FailureDetails;

/**
 * TODO
 */
public class DefaultOrcidAuthorizationClient extends OrcidAuthorizationClient {
	private static final Log log = LogFactory
			.getLog(DefaultOrcidAuthorizationClient.class);

	private final OrcidAuthorizationClientContext context;
	private final HttpServletRequest req;
	private final AuthorizationStateProgressCache cache;

	public DefaultOrcidAuthorizationClient(
			OrcidAuthorizationClientContext context, HttpServletRequest req) {
		this.context = context;
		this.req = req;
		this.cache = AuthorizationStateProgressCache.getCache(context);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.cornell.libraries.orcidclient.auth.OrcidAuthorizationClient#
	 * resetProgress(edu.cornell.libraries.orcidclient.actions.ApiScope)
	 */
	@Override
	public void resetProgress(ApiScope scope) {
		// TODO Auto-generated method stub
		throw new RuntimeException(
				"DefaultOrcidAuthorizationClient.resetProgress() not implemented.");

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.cornell.libraries.orcidclient.auth.OrcidAuthorizationClient#
	 * getAuthProcessResolution(edu.cornell.libraries.orcidclient.actions.
	 * ApiScope)
	 */
	@Override
	public AuthProcessResolution getAuthProcessResolution(ApiScope scope) {
		// TODO Auto-generated method stub
		throw new RuntimeException(
				"DefaultOrcidAuthorizationClient.getAuthProcessResolution() not implemented.");

	}

	@Override
	public AuthorizationStateProgress createProgressObject(ApiScope scope,
			URI returnUrl) throws OrcidClientException {
		return createProgressObject(scope, returnUrl, returnUrl);
	}

	@Override
	public AuthorizationStateProgress createProgressObject(ApiScope scope,
			URI successUrl, URI failureUrl) throws OrcidClientException {
		AuthorizationStateProgress authProgress = AuthorizationStateProgress
				.create(scope, successUrl, failureUrl).addState(SEEKING_AUTHORIZATION);
		log.debug("createdProgressObject: " + authProgress);
		
		cache.store(authProgress);
		return authProgress;
	}

	@Override
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.cornell.libraries.orcidclient.auth.OrcidAuthorizationClient#
	 * getAccessToken(edu.cornell.libraries.orcidclient.actions.ApiScope)
	 */
	@Override
	public AccessToken getAccessToken(ApiScope scope)
			throws IllegalStateException {
		// TODO Auto-generated method stub
		throw new RuntimeException(
				"DefaultOrcidAuthorizationClient.getAccessToken() not implemented.");

	}

	public String displayProgress(ApiScope scope) {
		throw new RuntimeException(
				"DefaultOrcidAuthorizationClient.displayProgress not implemented.");

	}

	@Override
	public String processAuthorizationResponse(HttpServletRequest req)
			throws OrcidClientException {
		String state = req.getParameter("state");
		AuthorizationStateProgress progress = getExistingAuthStatus(state);
		if (SEEKING_AUTHORIZATION != progress.getState()) {
			return fail(progress, new InvalidStateFailureDetails());
		}

		String error = req.getParameter("error");
		String errorDescription = req.getParameter("error_description");
		if (error != null) {
			return fail(progress,
					new ErrorFailureDetails(error, errorDescription));
		}

		String code = req.getParameter("code");
		if (StringUtils.isEmpty(code)) {
			return fail(progress, new MissingAuthCodeFailureDetails());
		}

		progress = progress.addCode(code);
		storeProgressById(progress);
		
		progress = getAccessTokenFromAuthCode(progress);
		storeProgressById(progress);
		
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
		List<NameValuePair> form = Form.form()
				.add("client_id", context.getSetting(CLIENT_ID))
				.add("client_secret", context.getSetting(CLIENT_SECRET))
				.add("grant_type", "authorization_code")
				.add("code", progress.getAuthorizationCode())
				.add("redirect_uri", context.getCallbackUrl()).build();
		Request request = Request.Post(context.getAccessTokenRequestUrl())
				.addHeader("Accept", "application/json").bodyForm(form);

		String string = null;
		try {
			// TODO test bad response code: 400
			string = request.execute().returnContent().asString();
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

	private void storeProgressById(AuthorizationStateProgress progress) {
		cache.store(progress);
	}

	@Override
	public AuthorizationStateProgress getProgressById(String id) {
		return cache.getByID(id);
	}

	private String fail(AuthorizationStateProgress progress,
			FailureDetails details) {
		log.warn(details.describe() + " : " + progress);
		storeProgressById(progress.addFailure(details));
		return progress.getFailureUrl().toString();
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
