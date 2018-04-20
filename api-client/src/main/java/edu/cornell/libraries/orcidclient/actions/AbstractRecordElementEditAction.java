package edu.cornell.libraries.orcidclient.actions;

import java.io.IOException;
import java.math.BigInteger;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import edu.cornell.libraries.orcidclient.OrcidClientException;
import edu.cornell.libraries.orcidclient.auth.AccessToken;
import edu.cornell.libraries.orcidclient.context.OrcidClientContext;
import edu.cornell.libraries.orcidclient.http.HttpWrapper;
import edu.cornell.libraries.orcidclient.http.HttpWrapper.DeleteRequest;
import edu.cornell.libraries.orcidclient.http.HttpWrapper.HttpResponse;
import edu.cornell.libraries.orcidclient.http.HttpWrapper.HttpStatusCodeException;
import edu.cornell.libraries.orcidclient.http.HttpWrapper.PostRequest;
import edu.cornell.libraries.orcidclient.http.HttpWrapper.PutRequest;
import edu.cornell.libraries.orcidclient.orcid_message_2_1.common.ElementSummary;
import edu.cornell.libraries.orcidclient.util.OrcidXmlUtil;

/**
 * Perform ADD, UPDATE and REMOVE operations on elements of the ORCID record.
 */
abstract class AbstractRecordElementEditAction<T extends ElementSummary> {
	private static final Log log = LogFactory
			.getLog(AbstractRecordElementEditAction.class);

	private final OrcidClientContext context;
	private final HttpWrapper httpWrapper;

	public AbstractRecordElementEditAction(OrcidClientContext context,
			HttpWrapper httpWrapper) {
		this.context = context;
		this.httpWrapper = httpWrapper;
	}

	protected abstract String getUrlPath();

	/**
	 * <pre>
	 * curl -i -H 'Content-type: application/vnd.orcid+xml' 
	 *   -H 'Authorization: Bearer dd91868d-d29a-475e-9acb-bd3fdf2f43f4' 
	 *   -d '@[FILE-PATH]/external_identifier.xml' 
	 *   -X POST 'https://api.sandbox.orcid.org/v2.0/0000-0002-9227-8514/external-identifiers
	 * </pre>
	 */
	public String add(AccessToken accessToken, T element)
			throws OrcidClientException {
		String xml = OrcidXmlUtil.marshall(element);
		try {
			String requestUrl = createRequestUrl(accessToken, getUrlPath());
			PostRequest request = httpWrapper.createPostRequest(requestUrl)
					.addHeader("Content-Type", "application/vnd.orcid+xml")
					.addHeader("Authorization", accessToken.toAuthHeader())
					.setBodyString(xml);
			HttpResponse response = request.execute();
			log.debug("Content from Add " + getUrlPath() + " was: "
					+ response.getContentString());

			return getPutCode(response);
		} catch (HttpStatusCodeException e) {
			log.error("HttpResponse status code: " + e.getStatusCode());
			throw new OrcidClientException(
					"Failed to add " + getUrlPath() + ". HTTP status code="
							+ e.getStatusCode() + ", xml='" + xml + "'",
					e);
		} catch (IOException e) {
			throw new OrcidClientException("Failed to add " + getUrlPath(), e);
		}
	}

	/**
	 * <pre>
	 * curl -i -H 'Content-type: application/vnd.orcid+xml' 
	 *   -H 'Authorization: Bearer dd91868d-d29a-475e-9acb-bd3fdf2f43f4' 
	 *   -d '@[FILE-PATH]/external_identifier.xml' 
	 *   -X PUT 'https://api.sandbox.orcid.org/v2.0/0000-0002-9227-8514/external-identifiers/[PUT-CODE]
	 * </pre>
	 */
	public void update(AccessToken accessToken, T element, String putCode)
			throws OrcidClientException {
		element.setPutCode(new BigInteger(putCode));
		String xml = OrcidXmlUtil.marshall(element);
		try {
			String requestUrl = createRequestUrl(accessToken, getUrlPath(),
					putCode);
			PutRequest request = httpWrapper.createPutRequest(requestUrl)
					.addHeader("Content-Type", "application/vnd.orcid+xml")
					.addHeader("Authorization", accessToken.toAuthHeader())
					.setBodyString(xml);
			request.execute();
		} catch (HttpStatusCodeException e) {
			log.error("HttpResponse status code: " + e.getStatusCode());
			throw new OrcidClientException(
					"Failed to update " + getUrlPath() + ". HTTP status code="
							+ e.getStatusCode() + ", xml='" + xml + "'",
					e);
		} catch (IOException e) {
			throw new OrcidClientException("Failed to update " + getUrlPath(),
					e);
		}
	}

	/**
	 * <pre>
	 * curl -i -H 'Content-type: application/vnd.orcid+xml' 
	 *   -H 'Authorization: Bearer dd91868d-d29a-475e-9acb-bd3fdf2f43f4' 
	 *   -d '@[FILE-PATH]/external_identifier.xml' 
	 *   -X PUT 'https://api.sandbox.orcid.org/v2.0/0000-0002-9227-8514/external-identifiers/[PUT-CODE]
	 * </pre>
	 */
	public void remove(AccessToken accessToken, String putCode)
			throws OrcidClientException {
		try {
			String requestUrl = createRequestUrl(accessToken, getUrlPath(),
					putCode);
			DeleteRequest request = httpWrapper.createDeleteRequest(requestUrl)
					.addHeader("Content-Type", "application/vnd.orcid+xml")
					.addHeader("Authorization", accessToken.toAuthHeader());
			request.execute();
		} catch (HttpStatusCodeException e) {
			log.error("HttpResponse status code: " + e.getStatusCode());
			throw new OrcidClientException("Failed to remove " + getUrlPath()
					+ ". HTTP status code=" + e.getStatusCode(), e);
		} catch (IOException e) {
			throw new OrcidClientException("Failed to remove " + getUrlPath(),
					e);
		}
	}

	/**
	 * Join the parts to the Member URI. Concatenate the parts with slashes.
	 */
	protected String createRequestUrl(AccessToken accessToken, String... parts)
			throws OrcidClientException {
		try {
			String url = context.getApiMemberUrl();
			url += accessToken.getOrcid();
			for (String part : parts) {
				url += "/" + part;
			}
			return new URI(url).toString();
		} catch (URISyntaxException e) {
			throw new OrcidClientException(
					"Failed to resolve the URI: " + parts, e);
		}
	}

	/**
	 * Find the put-code at the end of the Location header.
	 * 
	 * <pre>
	 * [http://api.sandbox.orcid.org/orcid-api-web/v2.1/0000-0003-0550-2950/external-identifiers/4867]
	 * </pre>
	 */
	private String getPutCode(HttpResponse response) throws IOException {
		List<String> values = response.getHeaderValues("Location");
		if (values == null || values.isEmpty()) {
			return "0";
		}

		String value = values.get(0);
		int lastSlash = value.lastIndexOf("/");
		if (lastSlash == -1) {
			return "0";
		}

		return value.substring(lastSlash + 1);
	}

}
