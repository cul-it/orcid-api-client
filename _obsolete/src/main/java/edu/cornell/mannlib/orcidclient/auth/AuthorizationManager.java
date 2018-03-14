/* $This file is distributed under the terms of the license in /doc/license.txt$ */

package edu.cornell.mannlib.orcidclient.auth;

import static edu.cornell.mannlib.orcidclient.context.OrcidClientContext.Setting.CLIENT_ID;
import static edu.cornell.mannlib.orcidclient.context.OrcidClientContext.Setting.CLIENT_SECRET;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.NameValuePair;
import org.apache.http.client.fluent.Form;
import org.apache.http.client.fluent.Request;
import org.apache.http.client.fluent.Response;
import org.apache.http.client.utils.URIBuilder;

import edu.cornell.mannlib.orcidclient.OrcidClientException;
import edu.cornell.mannlib.orcidclient.actions.ApiAction;
import edu.cornell.mannlib.orcidclient.auth.AuthorizationStatus.State;
import edu.cornell.mannlib.orcidclient.context.OrcidClientContext;

/**
 * TODO
 */
public class AuthorizationManager {
	private static final Log log = LogFactory
			.getLog(AuthorizationManager.class);

	private final OrcidClientContext context;
	private final HttpServletRequest req;
	private final AuthorizationCache cache;

	public AuthorizationManager(OrcidClientContext context,
			HttpServletRequest req) {
		this.context = context;
		this.req = req;
		this.cache = AuthorizationCache.getCache(req);

	}

	/**
	 * Write a URL that can be sent to the browser as a redirect, that will
	 * attempt to obtain authorization for this action.
	 * 
	 * Create an AuthorizationStatus object in the session, and include that in
	 * the URL, so we can track the progress.
	 */
	public String seekAuthorization(ApiAction action, String returnUrl)
			throws OrcidClientException {
		return seekAuthorization(action, returnUrl, returnUrl);
	}

	public String seekAuthorization(ApiAction action, String successUrl,
			String failureUrl) throws OrcidClientException {
		log.debug("seekAuthorization: action=" + action + ", successUrl="
				+ successUrl + ", failureUrl=" + failureUrl);
		AuthorizationStatus authStatus = cache.store(AuthorizationStatus
				.create(action, successUrl, failureUrl));
		try {
			URI fullUri = new URIBuilder(context.getAuthCodeRequestUrl())
					.addParameter("client_id", context.getSetting(CLIENT_ID))
					.addParameter("scope", action.getScope())
					.addParameter("response_type", "code")
					.addParameter("redirect_uri", context.getCallbackUrl())
					.addParameter("state", authStatus.getId()).build();
			log.debug("fullUri=" + fullUri);
			return fullUri.toString();
		} catch (URISyntaxException e) {
			throw new OrcidClientException(
					"Failed to build the authorization URL for " + action, e);
		}
	}

	/**
	 * We received a callback which should contain an authorization code. Use
	 * that code to request an access token. Update the AuthorizationStatus
	 * accordingly.
	 */
	public AuthorizationStatus processAuthorizationResponse()
			throws OrcidClientException {
		AuthorizationStatus auth = getExistingAuthStatus();

		if (!auth.isSeekingAuthorization()) {
			return recordInvalidState(auth);
		}

		if (isError()) {
			return recordError(auth);
		}

		if (isCodePresent()) {
			recordCode(auth);
		} else {
			return recordNoCode(auth);
		}

		return getAccessTokenFromAuthCode(auth);
	}

	private AuthorizationStatus getExistingAuthStatus()
			throws OrcidClientException {
		String token = req.getParameter("state");
		if (token == null || token.isEmpty()) {
			throw new OrcidClientException(
					"Request did not contain a 'state' parameter");
		}

		AuthorizationStatus auth = getAuthorizationStatus(token);
		if (auth.isNone()) {
			throw new OrcidClientException(
					"Not seeking authorization for this action: " + auth);
		}

		return auth;
	}

	private AuthorizationStatus recordInvalidState(AuthorizationStatus auth) {
		return auth.setFailure(AuthorizationStatus.ErrorCode.INVALID_STATE,
				auth.getState(), State.SEEKING_AUTHORIZATION);
	}

	private boolean isError() {
		return req.getParameter("error") != null;
	}

	private AuthorizationStatus recordError(AuthorizationStatus auth) {
		return auth.setFailure(req.getParameter("error"),
				req.getParameter("error_description"));
	}

	private boolean isCodePresent() {
		String code = req.getParameter("code");
		return (code != null) && (!code.isEmpty());
	}

	private AuthorizationStatus recordCode(AuthorizationStatus auth) {
		return auth.setSeekingAccessToken(req.getParameter("code"));
	}

	private AuthorizationStatus recordNoCode(AuthorizationStatus auth) {
		return auth.setFailure(AuthorizationStatus.ErrorCode.NO_AUTH_CODE);
	}

	private AuthorizationStatus getAccessTokenFromAuthCode(
			AuthorizationStatus auth) throws AccessTokenFormatException,
			OrcidClientException {
		List<NameValuePair> form = Form.form()
				.add("client_id", context.getSetting(CLIENT_ID))
				.add("client_secret", context.getSetting(CLIENT_SECRET))
				.add("grant_type", "authorization_code")
				.add("code", auth.getAuthorizationCode())
				.add("redirect_uri", context.getCallbackUrl()).build();
		Request request = Request.Post(context.getAccessTokenRequestUrl())
				.addHeader("Accept", "application/json").bodyForm(form);

		try {
			Response response = request.execute();
			String string = response.returnContent().asString();
			log.debug("Json response: '" + string + "'");
			AccessToken accessToken = new AccessToken(string);
			return auth.setSuccess(accessToken);
		} catch (IOException e) {
			return auth.setFailure("Request for access token failed.", e);
		}
	}

	public AuthorizationStatus getAuthorizationStatus(ApiAction action) {
		return cache.getStatus(action);
	}

	public AuthorizationStatus getAuthorizationStatus(String token) {
		return cache.getStatus(token);
	}

	public void clearStatus(ApiAction action) {
		cache.clearStatus(action);
	}

}
