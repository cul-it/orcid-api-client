/* $This file is distributed under the terms of the license in /doc/license.txt$ */

package edu.cornell.libraries.orcidclient.context;

import static edu.cornell.libraries.orcidclient.context.OrcidClientContext.Constraint.OPTIONAL;
import static edu.cornell.libraries.orcidclient.context.OrcidClientContext.Constraint.REQUIRED;

import java.net.URISyntaxException;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import edu.cornell.libraries.orcidclient.OrcidClientException;
import edu.cornell.libraries.orcidclient.actions.OrcidApiClient;
import edu.cornell.libraries.orcidclient.auth.OrcidAuthorizationClient;
import edu.cornell.libraries.orcidclient.auth.OrcidAuthorizationClientContext;
import edu.cornell.libraries.orcidclient.responses.OrcidMessage;

/**
 * TODO
 */
public abstract class OrcidClientContext implements OrcidAuthorizationClientContext {
	private static final Log log = LogFactory.getLog(OrcidClientContext.class);

	enum Constraint {
		REQUIRED, OPTIONAL
	}

	public enum Setting {

		/**
		 * ID assigned by ORCID to the application.
		 */
		CLIENT_ID(REQUIRED),
		/**
		 * Secret code assigned by ORCID to the application.
		 */
		CLIENT_SECRET(REQUIRED),

		/**
		 * Environment - "public", "sandbox", or "custom".
		 */
		API_PLATFORM(REQUIRED),

		/**
		 * Root of the public API (requires no authorization). If API_PLATFORM
		 * is "custom", this is required.
		 */
		PUBLIC_API_BASE_URL(OPTIONAL),

		/**
		 * Root of the restricted API (requires authorization). If API_PLATFORM
		 * is "custom", this is required.
		 */
		AUTHORIZED_API_BASE_URL(OPTIONAL),

		/**
		 * URL to obtain an authorization code. This is sent to the browser as a
		 * redirect, so the user can log in at the ORCID site. If API_PLATFORM
		 * is "custom", this is required.
		 */
		OAUTH_AUTHORIZE_URL(OPTIONAL),

		/**
		 * URL to exchange the authorization code for an OAuth access token. If
		 * API_PLATFORM is "custom", this is required.
		 */
		OAUTH_TOKEN_URL(OPTIONAL),

		/**
		 * The base URL for contacting this webapp (including context path. Used
		 * when building the redirect URL for the browser.
		 */
		WEBAPP_BASE_URL(REQUIRED),

		/**
		 * Where should ORCID call back to during the auth dance? Path within
		 * this webapp.
		 */
		CALLBACK_PATH(REQUIRED);

		private Constraint constraint;

		Setting(Constraint constraint) {
			this.constraint = constraint;
		}

		public boolean isRequired() {
			return constraint == REQUIRED;
		}
	}

	// ----------------------------------------------------------------------
	// The factory
	// ----------------------------------------------------------------------

	private static volatile OrcidClientContext instance = new OrcidClientContextNotInitialized();

	public static synchronized void initialize(Map<Setting, String> settings)
			throws OrcidClientException {
		if (instance instanceof OrcidClientContextImpl) {
			throw new IllegalStateException("Already initialized: " + instance);
		} else {
			instance = new OrcidClientContextImpl(settings);
			log.debug("initialized: " + instance);
		}
	}

	public static OrcidClientContext getInstance() {
		return instance;

	}

	// ----------------------------------------------------------------------
	// The interface
	// ----------------------------------------------------------------------

	public abstract OrcidApiClient getApiClient(HttpServletRequest req);

	public abstract OrcidAuthorizationClient getAuthorizationClient(
			HttpServletRequest req);

	public abstract String getSetting(Setting setting);

	public abstract String getCallbackUrl();

	public abstract String getAuthCodeRequestUrl();

	public abstract String getAccessTokenRequestUrl();

	public abstract String getApiPublicUrl();

	public abstract String getApiMemberUrl();

	public abstract OrcidMessage unmarshall(String xml)
			throws OrcidClientException;

	public abstract String marshall(OrcidMessage message)
			throws OrcidClientException;

	public abstract String resolvePathWithWebapp(String path)
			throws URISyntaxException;

	// ----------------------------------------------------------------------
	// The empty implementation
	// ----------------------------------------------------------------------

	private static class OrcidClientContextNotInitialized
			extends OrcidClientContext {
		private static final String MESSAGE = "OrcidClientContext has not been initialized";

		@Override
		public OrcidApiClient getApiClient(HttpServletRequest req) {
			throw new IllegalStateException(MESSAGE);
		}

		@Override
		public OrcidAuthorizationClient getAuthorizationClient(
				HttpServletRequest req) {
			throw new IllegalStateException(MESSAGE);
		}

		@Override
		public String getSetting(Setting setting) {
			throw new IllegalStateException(MESSAGE);
		}

		@Override
		public String getCallbackUrl() {
			throw new IllegalStateException(MESSAGE);
		}

		@Override
		public String getAuthCodeRequestUrl() {
			throw new IllegalStateException(MESSAGE);
		}

		@Override
		public String getAccessTokenRequestUrl() {
			throw new IllegalStateException(MESSAGE);
		}

		@Override
		public String getApiPublicUrl() {
			throw new IllegalStateException(MESSAGE);
		}

		@Override
		public String getApiMemberUrl() {
			throw new IllegalStateException(MESSAGE);
		}

		@Override
		public OrcidMessage unmarshall(String xml) {
			throw new IllegalStateException(MESSAGE);
		}

		@Override
		public String marshall(OrcidMessage message) {
			throw new IllegalStateException(MESSAGE);
		}

		@Override
		public String resolvePathWithWebapp(String path) {
			throw new IllegalStateException(MESSAGE);
		}

	}

}