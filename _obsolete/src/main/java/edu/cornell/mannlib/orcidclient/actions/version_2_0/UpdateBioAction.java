/* $This file is distributed under the terms of the license in /doc/license.txt$ */

package edu.cornell.mannlib.orcidclient.actions.version_2_0;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.cornell.mannlib.orcidclient.OrcidClientException;
import edu.cornell.mannlib.orcidclient.actions.version_1_0.Util;
import edu.cornell.mannlib.orcidclient.auth.AccessToken;
import edu.cornell.mannlib.orcidclient.context.OrcidClientContext;
import edu.cornell.mannlib.orcidclient.model.OrcidProfile;
import edu.cornell.mannlib.orcidclient.responses.message_1_2.OrcidMessage;
import edu.cornell.mannlib.orcidclient.responses.message_2_0.OrcidBiography;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.fluent.Content;
import org.apache.http.client.fluent.Request;
import org.apache.http.client.fluent.Response;
import org.apache.http.client.utils.URIUtils;
import org.apache.http.entity.ContentType;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

/**
 */
public class UpdateBioAction implements edu.cornell.mannlib.orcidclient.actions.UpdateBioAction {
	private static final Log log = LogFactory.getLog(UpdateBioAction.class);

	private final OrcidClientContext occ;

	public UpdateBioAction() {
		this.occ = OrcidClientContext.getInstance();
	}

	@Override
	public OrcidProfile execute(OrcidMessage profile, AccessToken accessToken)
			throws OrcidClientException {
		try {
			URI baseUri = new URI(occ.getApiMemberUrl());
			String requestUrl = URIUtils.resolve(baseUri, accessToken.getOrcid() + "/biography").toString();

			OrcidBiography bio = new OrcidBiography();
			bio.setContent(profile.getOrcidProfile().getOrcidBio().getBiography().getValue());

			ObjectMapper mapper = new ObjectMapper();
			String json = mapper.writeValueAsString(bio);

			Request request = Request
					.Put(requestUrl)
					.addHeader("Content-Type", "application/vnd.orcid+json")
					.addHeader(
							"Authorization",
							accessToken.getTokenType() + " "
									+ accessToken.getAccessToken())
					.bodyString(json,
							ContentType.APPLICATION_FORM_URLENCODED);
			Response response = request.execute();
			Content content = response.returnContent();
			String string = content.asString();
			log.debug("Content from UpdateBio was: " + string);

			ReadProfileAction readAction = new ReadProfileAction();
			return readAction.execute(accessToken);
		} catch (URISyntaxException e) {
			throw new OrcidClientException(
					"API_BASE_URL is not syntactically valid.", e);
		} catch (HttpResponseException e) {
			// Bad status code? Something funky.
			log.error("HttpResponse status code: " + e.getStatusCode());
			throw new OrcidClientException(
					"Failed to update bio. HTTP status code="
							+ e.getStatusCode(), e);
		} catch (IOException e) {
			throw new OrcidClientException("Failed to update bio", e);
		}
	}
}
