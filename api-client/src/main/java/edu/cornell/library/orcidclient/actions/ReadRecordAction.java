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
import edu.cornell.library.orcidclient.http.HttpWrapper.HttpResponse;
import edu.cornell.library.orcidclient.http.HttpWrapper.HttpStatusCodeException;
import edu.cornell.library.orcidclient.orcid_message_2_1.record.RecordElement;
import edu.cornell.library.orcidclient.util.OrcidXmlUtil;

/**
 * Reads the summary of an ORCID record.
 */
public class ReadRecordAction {
	private static final Log log = LogFactory.getLog(ReadRecordAction.class);

	private final OrcidClientContext context;
	private final HttpWrapper httpWrapper;

	public ReadRecordAction(OrcidClientContext context,
			HttpWrapper httpWrapper) {
		this.context = context;
		this.httpWrapper = httpWrapper;
	}

	/**
	 * <pre>
	 * curl -H 'Content-Type: application/vnd.orcid+xml' 
	 *      -H 'Authorization: Bearer f6d49570-c048-45a9-951f-a81ebb1fa543' 
	 *      -X GET 'https://api.sandbox.orcid.org/v2.1/0000-0003-1495-7122/record' 
	 *      -L -i
	 * </pre>
	 */
	public RecordElement read(AccessToken accessToken)
			throws OrcidClientException {
		try {
			URI baseUri = new URI(context.getApiPublicUrl());
			String requestUrl = URIUtils
					.resolve(baseUri, accessToken.getOrcid() + "/record")
					.toString();
			GetRequest request = httpWrapper.createGetRequest(requestUrl)
					.addHeader("Accept", "application/vnd.orcid+xml")
					.addHeader("Authorization", accessToken.toAuthHeader());

			HttpResponse response = request.execute();
			String xml = response.getContentString();
			log.debug("Record summary: " + xml);

			return OrcidXmlUtil.unmarshall(xml, RecordElement.class);
		} catch (URISyntaxException e) {
			throw new OrcidClientException(
					"API_BASE_URL is not syntactically valid.", e);
		} catch (HttpStatusCodeException e) {
			throw new OrcidClientException("Failed to read profile.", e);
		} catch (IOException e) {
			throw new OrcidClientException("Failed to read profile.", e);
		}
	}

}
