package edu.cornell.library.orcidclient.testwebapp.actors;

import static edu.cornell.library.orcidclient.testwebapp.actors.CheckConnectionHandler.FailureOption.FAIL_ACCESS_TOKEN;
import static edu.cornell.library.orcidclient.testwebapp.actors.CheckConnectionHandler.FailureOption.FAIL_API_MEMBER;
import static edu.cornell.library.orcidclient.testwebapp.actors.CheckConnectionHandler.FailureOption.FAIL_API_PUBLIC;
import static edu.cornell.library.orcidclient.testwebapp.actors.CheckConnectionHandler.FailureOption.FAIL_AUTH_CODE;
import static edu.cornell.library.orcidclient.testwebapp.actors.CheckConnectionHandler.FailureOption.FAIL_SITE;
import static edu.cornell.library.orcidclient.testwebapp.actors.CheckConnectionHandler.FailureOption.SUCCEED;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jtwig.JtwigModel;

import edu.cornell.library.orcidclient.actions.OrcidActionClient;
import edu.cornell.library.orcidclient.auth.OauthProgressCacheImpl;
import edu.cornell.library.orcidclient.auth.OrcidAuthorizationClient;
import edu.cornell.library.orcidclient.context.OrcidClientContext;
import edu.cornell.library.orcidclient.exceptions.OrcidClientException;
import edu.cornell.library.orcidclient.http.BaseHttpWrapper;
import edu.cornell.library.orcidclient.testwebapp.support.AccessTokenCacheFileImpl;

/**
 * Offer the user a chance to test the client connections, with or without
 * mangling the URL for an intentional failure.
 */
public class CheckConnectionHandler extends AbstractActor {
	public enum FailureOption {
		SUCCEED, FAIL_SITE, FAIL_AUTH_CODE, FAIL_ACCESS_TOKEN, FAIL_API_PUBLIC, FAIL_API_MEMBER
	}

	private static final Log log = LogFactory
			.getLog(CheckConnectionHandler.class);

	public CheckConnectionHandler(HttpServletRequest req,
			HttpServletResponse resp) {
		super(req, resp);
	}

	public void exec() throws IOException {
		render("/templates/checkConnection.twig.html", //
				JtwigModel.newModel());
	}

	public void execSiteSucceed() throws IOException {
		boolean passed = checkSiteConnection(SUCCEED);
		render("/templates/checkConnection.twig.html", //
				JtwigModel.newModel().with("result", passed));
	}

	public void execSiteFail() throws IOException {
		boolean passed = checkSiteConnection(FAIL_SITE);
		render("/templates/checkConnection.twig.html", //
				JtwigModel.newModel().with("result", passed));
	}

	private boolean checkSiteConnection(FailureOption selectedFailure) {
		try {
			new OrcidActionClient( //
					new Context(selectedFailure), //
					new BaseHttpWrapper()) //
							.checkConnection();
			log.debug("Check connection: passed");
			return true;
		} catch (OrcidClientException e) {
			log.debug("Check connection: failed", e);
			return false;
		}
	}

	public void execOauthSucceed() throws IOException {
		boolean passed = checkAuthConnection(SUCCEED);
		render("/templates/checkConnection.twig.html", //
				JtwigModel.newModel().with("result", passed));
	}

	public void execFailAuthCode() throws IOException {
		boolean passed = checkAuthConnection(FAIL_AUTH_CODE);
		render("/templates/checkConnection.twig.html", //
				JtwigModel.newModel().with("result", passed));
	}

	public void execFailAccessToken() throws IOException {
		boolean passed = checkAuthConnection(FAIL_ACCESS_TOKEN);
		render("/templates/checkConnection.twig.html", //
				JtwigModel.newModel().with("result", passed));
	}

	private boolean checkAuthConnection(FailureOption selectedFailure) {
		try {
			new OrcidAuthorizationClient( //
					new Context(selectedFailure), //
					new OauthProgressCacheImpl(),
					AccessTokenCacheFileImpl.instance(req), //
					new BaseHttpWrapper()) //
							.checkConnection();
			log.debug("Check connection: passed");
			return true;
		} catch (OrcidClientException e) {
			log.debug("Check connection: failed", e);
			return false;
		}
	}

	public void execApiSucceed() throws IOException {
		boolean passed = checkApiConnection(SUCCEED);
		render("/templates/checkConnection.twig.html", //
				JtwigModel.newModel().with("result", passed));
	}

	public void execApiFailPublic() throws IOException {
		boolean passed = checkApiConnection(FAIL_API_PUBLIC);
		render("/templates/checkConnection.twig.html", //
				JtwigModel.newModel().with("result", passed));
	}

	public void execApiFailMember() throws IOException {
		boolean passed = checkApiConnection(FAIL_API_MEMBER);
		render("/templates/checkConnection.twig.html", //
				JtwigModel.newModel().with("result", passed));
	}

	private boolean checkApiConnection(FailureOption selectedFailure) {
		try {
			new OrcidActionClient( //
					new Context(selectedFailure), //
					new BaseHttpWrapper()) //
							.checkConnection();
			log.debug("Check connection: passed");
			return true;
		} catch (OrcidClientException e) {
			log.debug("Check connection: failed", e);
			return false;
		}
	}

	/**
	 * A context implementation that hands out bogus URLs as requested.
	 */
	private class Context extends OrcidClientContext {
		private FailureOption failureRequest;

		public Context(FailureOption failureRequest) {
			this.failureRequest = failureRequest;
		}

		@Override
		public String getSiteBaseUrl() {
			return mangleIfRequested(FAIL_SITE, occ.getSiteBaseUrl());
		}

		@Override
		public String getAuthCodeRequestUrl() {
			return mangleIfRequested(FAIL_AUTH_CODE,
					occ.getAuthCodeRequestUrl());
		}

		@Override
		public String getCallbackUrl() {
			return occ.getCallbackUrl(); // Never fails
		}

		@Override
		public String getAccessTokenRequestUrl() {
			return mangleIfRequested(FAIL_ACCESS_TOKEN,
					occ.getAccessTokenRequestUrl());
		}

		@Override
		public String getClientId() {
			return occ.getClientId();
		}

		@Override
		public String getClientSecret() {
			return occ.getClientSecret();
		}

		@Override
		public String getApiPublicUrl() {
			return mangleIfRequested(FAIL_API_PUBLIC, occ.getApiPublicUrl());
		}

		@Override
		public String getApiMemberUrl() {
			return mangleIfRequested(FAIL_API_MEMBER, occ.getApiMemberUrl());
		}

		@Override
		public String getWebappBaseUrl() {
			return occ.getWebappBaseUrl(); // Never fails.
		}

		@Override
		public URI resolvePathWithWebapp(String path)
				throws URISyntaxException {
			return occ.resolvePathWithWebapp(path); // Never fails.
		}

		/**
		 * Want a failure? Strip the trailing slash and add garbage characters
		 */
		private String mangleIfRequested(FailureOption option, String url) {
			if (option == failureRequest) {
				return url.replaceAll("\\w/", "XXX/");
			} else {
				return url;
			}
		}
	}
}
