package edu.cornell.libraries.orcidclient.context;

import java.net.URI;
import java.net.URISyntaxException;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import edu.cornell.libraries.orcidclient.actions.OrcidApiClient;
import edu.cornell.libraries.orcidclient.auth.OrcidAuthorizationClientContext;

/**
 * TODO
 */
public abstract class OrcidClientContext
		implements OrcidAuthorizationClientContext {
	private static final Log log = LogFactory.getLog(OrcidClientContext.class);

	// ----------------------------------------------------------------------
	// The factory
	// ----------------------------------------------------------------------

	private static volatile OrcidClientContext instance = new OrcidClientContextNotInitialized();

	public static synchronized void initialize(OrcidClientContext newInstance) {
		if (instance == null
				|| instance instanceof OrcidClientContextNotInitialized) {
			instance = newInstance;
			log.debug("initialized: " + instance);
		} else {
			throw new IllegalStateException("Already initialized: " + instance);
		}
	}

	public static OrcidClientContext getInstance() {
		return instance;
	}

	// ----------------------------------------------------------------------
	// The interface
	// ----------------------------------------------------------------------

	public abstract OrcidApiClient getApiClient(HttpServletRequest req);

	public abstract String getApiPublicUrl();

	public abstract String getApiMemberUrl();

	public abstract String getWebappBaseUrl();

	public abstract URI resolvePathWithWebapp(String path)
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
		public URI resolvePathWithWebapp(String path) {
			throw new IllegalStateException(MESSAGE);
		}

		@Override
		public String getClientId() {
			throw new IllegalStateException(MESSAGE);
		}

		@Override
		public String getClientSecret() {
			throw new IllegalStateException(MESSAGE);
		}

		@Override
		public String getWebappBaseUrl() {
			throw new IllegalStateException(MESSAGE);
		}
	}

}
