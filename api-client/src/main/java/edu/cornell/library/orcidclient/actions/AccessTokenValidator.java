package edu.cornell.library.orcidclient.actions;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.client.utils.URIUtils;

import edu.cornell.library.orcidclient.auth.AccessToken;
import edu.cornell.library.orcidclient.context.OrcidClientContext;
import edu.cornell.library.orcidclient.exceptions.OrcidClientException;
import edu.cornell.library.orcidclient.http.HttpWrapper;
import edu.cornell.library.orcidclient.http.HttpWrapper.GetRequest;
import edu.cornell.library.orcidclient.http.HttpWrapper.HttpStatusCodeException;

/**
 * If this access token is valid for any scope, we should be able to do a simple
 * read operation against the public API.
 * 
 * We could go for any read, but the "/email" endpoint should return a vary
 * small response.
 */
public class AccessTokenValidator {
	private static final Log log = LogFactory
			.getLog(AccessTokenValidator.class);

	private final OrcidClientContext context;
	private final HttpWrapper httpWrapper;

	public AccessTokenValidator(OrcidClientContext context,
			HttpWrapper httpWrapper) {
		this.context = context;
		this.httpWrapper = httpWrapper;
	}

	public boolean isValid(AccessToken accessToken)
			throws OrcidClientException {
		try {
			URI baseUri = new URI(context.getApiPublicUrl());
			String requestUrl = URIUtils
					.resolve(baseUri, accessToken.getOrcid() + "/email")
					.toString();
			GetRequest request = httpWrapper.createGetRequest(requestUrl)
					.addHeader("Accept", "application/vnd.orcid+xml")
					.addHeader("Authorization", accessToken.toAuthHeader());

			request.execute();
			
			log.debug("Access token is still valid: " + accessToken);
			return true;
		} catch (HttpStatusCodeException e) {
			log.info(String.format(
					"Access token not valid: status code is %d, response is: %s",
					e.getStatusCode(), e.getResponseContent()));
			return false;
		} catch (URISyntaxException e) {
			throw new OrcidClientException(
					"API_BASE_URL is not syntactically valid.", e);
		} catch (IOException e) {
			throw new OrcidClientException("Failed to read profile.", e);
		}
	}

}
