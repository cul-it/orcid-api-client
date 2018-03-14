/* $This file is distributed under the terms of the license in /doc/license.txt$ */

package edu.cornell.mannlib.orcidclient.actions.version_2_0;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.cornell.mannlib.orcidclient.OrcidClientException;
import edu.cornell.mannlib.orcidclient.context.OrcidClientContext;
import edu.cornell.mannlib.orcidclient.model.OrcidProfile;
import edu.cornell.mannlib.orcidclient.responses.message_2_0.OrcidBiography;
import edu.cornell.mannlib.orcidclient.responses.message_2_0.OrcidPerson;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.client.utils.URIUtils;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * <pre>
 * curl -H "Accept: application/json"
 *      'http://pub.sandbox-1.orcid.org/v2.0/0000-0001-7857-2795/orcid-bio'
 *      -L -i
 * </pre>
 */
public class ReadPublicBioAction implements edu.cornell.mannlib.orcidclient.actions.ReadPublicBioAction {
	private static final Log log = LogFactory.getLog(ReadPublicBioAction.class);

	private final OrcidClientContext occ;

	public ReadPublicBioAction() {
		this.occ = OrcidClientContext.getInstance();
	}

	@Override
	public OrcidProfile execute(String orcid) throws OrcidClientException {
		try {
			URI baseUri = new URI(occ.getApiPublicUrl());
			String requestUrl = URIUtils.resolve(baseUri, orcid + "/person").toString();
			String json = Util.readJSON(requestUrl, null);
			if (json == null) {
				throw new OrcidClientException("Failed to read profile");
			}

			log.debug("Public Bio: " + json);

			ObjectMapper mapper = new ObjectMapper();

			OrcidPerson person = mapper.readValue(json, OrcidPerson.class);
			return Util.toModel(person);
		} catch (URISyntaxException e) {
			throw new OrcidClientException("API_BASE_URL is not syntactically valid.", e);
		} catch (JsonParseException e) {
			throw new OrcidClientException("Failed to read profile", e);
		} catch (JsonMappingException e) {
			throw new OrcidClientException("Failed to read profile", e);
		} catch (IOException e) {
			throw new OrcidClientException("Failed to read profile", e);
		}
	}
}
