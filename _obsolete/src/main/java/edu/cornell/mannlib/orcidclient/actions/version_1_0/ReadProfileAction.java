/* $This file is distributed under the terms of the license in /doc/license.txt$ */

package edu.cornell.mannlib.orcidclient.actions.version_1_0;

import edu.cornell.mannlib.orcidclient.OrcidClientException;
import edu.cornell.mannlib.orcidclient.auth.AccessToken;
import edu.cornell.mannlib.orcidclient.context.OrcidClientContext;
import edu.cornell.mannlib.orcidclient.context.OrcidClientContext.Setting;
import edu.cornell.mannlib.orcidclient.model.OrcidProfile;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.fluent.Content;
import org.apache.http.client.fluent.Request;
import org.apache.http.client.fluent.Response;
import org.apache.http.client.utils.URIUtils;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * <pre>
 * curl -H 'Content-Type: application/vdn.orcid+xml' 
 *      -H 'Authorization: Bearer f6d49570-c048-45a9-951f-a81ebb1fa543' 
 *      -X GET 'http://api.sandbox-1.orcid.org/v1.0.23/0000-0003-1495-7122/orcid-profile' 
 *      -L -i
 * </pre>
 */
public class ReadProfileAction implements edu.cornell.mannlib.orcidclient.actions.ReadProfileAction {
	private static final Log log = LogFactory.getLog(ReadProfileAction.class);

	private final OrcidClientContext occ;

	public ReadProfileAction() {
		this.occ = OrcidClientContext.getInstance();
	}

	@Override
	public OrcidProfile execute(AccessToken accessToken)
			throws OrcidClientException {
		try {
			URI baseUri = new URI(occ.getApiMemberUrl());
			String requestUrl = URIUtils.resolve(baseUri,
					accessToken.getOrcid() + "/orcid-profile").toString();
			Request request = Request
					.Get(requestUrl)
					.addHeader("Content-Type", "application/vdn.orcid+xml")
					.addHeader(
							"Authorization",
							accessToken.getTokenType() + " "
									+ accessToken.getAccessToken());
			Response response = request.execute();
			Content content = response.returnContent();
			String string = content.asString();
			return Util.toModel(occ.unmarshall(string));
		} catch (URISyntaxException e) {
			throw new OrcidClientException(
					"API_BASE_URL is not syntactically valid.", e);
		} catch (HttpResponseException e) {
			// Bad status code? Something funky.
			log.error("HttpResponse status code: " + e.getStatusCode());
			throw new OrcidClientException(
					"Failed to read profile. HTTP status code="
							+ e.getStatusCode(), e);
		} catch (IOException e) {
			throw new OrcidClientException("Failed to read profile", e);
		}
	}
}
