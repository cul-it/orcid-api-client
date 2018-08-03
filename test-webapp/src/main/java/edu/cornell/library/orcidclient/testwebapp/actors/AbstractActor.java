package edu.cornell.library.orcidclient.testwebapp.actors;

import static org.jtwig.JtwigTemplate.classpathTemplate;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jtwig.JtwigModel;
import org.jtwig.environment.EnvironmentConfiguration;
import org.jtwig.environment.EnvironmentConfigurationBuilder;

import edu.cornell.library.orcidclient.actions.OrcidActionClient;
import edu.cornell.library.orcidclient.auth.AccessToken;
import edu.cornell.library.orcidclient.auth.OauthProgressCacheImpl;
import edu.cornell.library.orcidclient.auth.OrcidAuthorizationClient;
import edu.cornell.library.orcidclient.context.OrcidClientContext;
import edu.cornell.library.orcidclient.exceptions.OrcidClientException;
import edu.cornell.library.orcidclient.http.BaseHttpWrapper;
import edu.cornell.library.orcidclient.testwebapp.support.AccessTokenCacheFileImpl;

/**
 * Methods that are in common among some or all Actors.
 */
public abstract class AbstractActor {
	protected final HttpServletRequest req;
	protected final HttpServletResponse resp;
	protected final OrcidClientContext occ;
	private final EnvironmentConfiguration env;

	public AbstractActor(HttpServletRequest req, HttpServletResponse resp) {
		this.req = req;
		this.resp = resp;
		this.occ = OrcidClientContext.getInstance();

		this.env = EnvironmentConfigurationBuilder.configuration().escape()
				.withInitialEngine("html").and().build();
	}

	protected void render(String path, JtwigModel model) throws IOException {
		classpathTemplate(path, env).render(
				model.with("mainPageUrl", occ.getWebappBaseUrl()),
				resp.getOutputStream());
	}

	protected URI callbackUrl() throws OrcidClientException {
		try {
			return new URI(occ.getCallbackUrl());
		} catch (URISyntaxException e) {
			throw new OrcidClientException("Failed to form callback URL", e);
		}
	}

	protected OrcidAuthorizationClient getAuthorizationClient()
			throws OrcidClientException {
		return new OrcidAuthorizationClient(occ,
				OauthProgressCacheImpl.instance(req),
				AccessTokenCacheFileImpl.instance(req), new BaseHttpWrapper());
	}

	protected OrcidActionClient getActionClient() {
		return new OrcidActionClient(occ, new BaseHttpWrapper());
	}

	protected List<AccessToken> getTokensFromCache() {
		try {
			return AccessTokenCacheFileImpl.instance(req).getAccessTokens();
		} catch (OrcidClientException e) {
			throw new RuntimeException(e);
		}
	}

	protected AccessToken getTokenByTokenId(String tokenId) {
		for (AccessToken t : getTokensFromCache()) {
			if (t.getToken().equals(tokenId)) {
				return t;
			}
		}
		return null;
	}
}
