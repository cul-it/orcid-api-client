package edu.cornell.library.orcidclient.actions.read;

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
import edu.cornell.library.orcidclient.util.OrcidXmlUtil;

/**
 * The basis for classes that read information from the ORCID record.
 * 
 * Different endpoints return different element types. To implement a typesafe
 * class that does reads, create a read() method that takes a subclass of
 * Endpoint as a parameter. Then, that endpoint can be passed to readElement()
 * in this class, and the result is typed correctly.
 */
public abstract class AbstractReadAction {
	private static final Log log = LogFactory.getLog(AbstractReadAction.class);

	private final OrcidClientContext context;
	private final HttpWrapper httpWrapper;

	public AbstractReadAction(OrcidClientContext context,
			HttpWrapper httpWrapper) {
		this.context = context;
		this.httpWrapper = httpWrapper;
	}

	protected <T> T readElement(AccessToken accessToken, Endpoint<T> endpoint)
			throws OrcidClientException {
		String xml = readXml(accessToken, endpoint.getPath());
		return OrcidXmlUtil.unmarshall(xml, endpoint.getResultClass());
	}

	/**
	 * <pre>
	 * curl -H 'Content-Type: application/vnd.orcid+xml' 
	 *      -H 'Authorization: Bearer f6d49570-c048-45a9-951f-a81ebb1fa543' 
	 *      -X GET 'https://api.sandbox.orcid.org/v2.1/0000-0003-1495-7122/record' 
	 *      -L -i
	 * </pre>
	 */
	public String readXml(AccessToken accessToken, String endpointPath)
			throws OrcidClientException {
		try {
			URI baseUri = new URI(context.getApiPublicUrl());
			String requestUrl = URIUtils
					.resolve(baseUri, accessToken.getOrcid() + endpointPath)
					.toString();
			GetRequest request = httpWrapper.createGetRequest(requestUrl)
					.addHeader("Accept", "application/vnd.orcid+xml")
					.addHeader("Authorization", accessToken.toAuthHeader());

			HttpResponse response = request.execute();
			String xml = response.getContentString();
			log.debug("Read action result: " + xml);
			return xml;
		} catch (URISyntaxException e) {
			throw new OrcidClientException(
					"API_BASE_URL is not syntactically valid.", e);
		} catch (HttpStatusCodeException e) {
			throw new OrcidClientException("Failed to read profile.", e);
		} catch (IOException e) {
			throw new OrcidClientException("Failed to read profile.", e);
		}
	}

	// ----------------------------------------------------------------------
	// Helper classes
	// ----------------------------------------------------------------------

	public abstract static class Endpoint<T> {
		private final String path;
		private final Class<T> resultClass;

		protected Endpoint(String path, Class<T> resultClass) {
			this.path = path;
			this.resultClass = resultClass;
		}

		public String getPath() {
			return path;
		}

		public Class<T> getResultClass() {
			return resultClass;
		}

	}

}
