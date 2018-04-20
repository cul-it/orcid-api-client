package edu.cornell.libraries.orcidclient.actions;

import static edu.cornell.libraries.orcidclient.context.OrcidClientContext.Setting.AUTHORIZED_API_BASE_URL;

import java.io.IOException;
import java.math.BigInteger;
import java.net.URI;
import java.net.URISyntaxException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.client.utils.URIUtils;

import edu.cornell.libraries.orcidclient.OrcidClientException;
import edu.cornell.libraries.orcidclient.auth.AccessToken;
import edu.cornell.libraries.orcidclient.context.OrcidClientContext;
import edu.cornell.libraries.orcidclient.http.HttpWrapper;
import edu.cornell.libraries.orcidclient.http.HttpWrapper.DeleteRequest;
import edu.cornell.libraries.orcidclient.http.HttpWrapper.HttpResponse;
import edu.cornell.libraries.orcidclient.http.HttpWrapper.HttpStatusCodeException;
import edu.cornell.libraries.orcidclient.http.HttpWrapper.PostRequest;
import edu.cornell.libraries.orcidclient.http.HttpWrapper.PutRequest;
import edu.cornell.libraries.orcidclient.orcid_message_2_1.common.ExternalId;
import edu.cornell.libraries.orcidclient.orcid_message_2_1.personexternalidentifier.ExternalIdentifierElement;
import edu.cornell.libraries.orcidclient.util.OrcidXmlUtil;

/**
 * Perform ADD, UPDATE and REMOVE operations on External IDs.
 * 
 * Note that the contents of an External ID are similar to:
 * 
 * <pre>
 *   url: http://scholars.cornell.edu/JimBlake
 *   type: Scholars@Cornell -- apparently for display only
 *   relationship: SELF -- required
 *   value: Jim Blake -- apparently for display only
 * </pre>
 */
public class EditExternalIdsAction {
	private static final Log log = LogFactory
			.getLog(EditExternalIdsAction.class);

	private final OrcidClientContext context;
	private final HttpWrapper httpWrapper;

	public EditExternalIdsAction(OrcidClientContext context,
			HttpWrapper httpWrapper) {
		this.context = context;
		this.httpWrapper = httpWrapper;
	}

	/**
	 * <pre>
	 * curl -i -H 'Content-type: application/vnd.orcid+xml' 
	 *   -H 'Authorization: Bearer dd91868d-d29a-475e-9acb-bd3fdf2f43f4' 
	 *   -d '@[FILE-PATH]/external_identifier.xml' 
	 *   -X POST 'https://api.sandbox.orcid.org/v2.0/0000-0002-9227-8514/external-identifiers
	 * </pre>
	 */
	public String add(AccessToken accessToken,
			ExternalIdentifierElement externalId) throws OrcidClientException {
		String xml = OrcidXmlUtil.marshall(externalId);
		try {
			URI baseUri = new URI(context.getApiMemberUrl());
			String requestUrl = URIUtils
					.resolve(baseUri,
							accessToken.getOrcid() + "/external-identifiers")
					.toString();

			PostRequest request = httpWrapper.createPostRequest(requestUrl)
					.addHeader("Content-Type", "application/vnd.orcid+xml")
					.addHeader("Authorization", accessToken.toAuthHeader())
					.setBodyString(xml);
			HttpResponse response = request.execute();
			String string = response.getContentString();
			log.debug("Content from AddExternalID was: " + string);

			return String.valueOf(response.getHeaderValues("Location"));
		} catch (URISyntaxException e) {
			throw new OrcidClientException(
					AUTHORIZED_API_BASE_URL + " is not syntactically valid.",
					e);
		} catch (HttpStatusCodeException e) {
			log.error("HttpResponse status code: " + e.getStatusCode());
			throw new OrcidClientException(
					"Failed to add external ID. HTTP status code="
							+ e.getStatusCode() + ", xml='" + xml + "'",
					e);
		} catch (IOException e) {
			throw new OrcidClientException("Failed to add external ID", e);
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
	public void update(AccessToken accessToken, ExternalId externalId,
			String putCode) throws OrcidClientException {
		externalId.setPutCode(new BigInteger(putCode));
		String xml = OrcidXmlUtil.marshall(externalId);
		try {
			URI baseUri = new URI(context.getApiMemberUrl());
			String requestUrl = URIUtils.resolve(baseUri,
					accessToken.getOrcid() + "/external-identifiers/" + putCode)
					.toString();

			PutRequest request = httpWrapper.createPutRequest(requestUrl)
					.addHeader("Content-Type", "application/vnd.orcid+xml")
					.addHeader("Authorization", accessToken.toAuthHeader())
					.setBodyString(xml);
			request.execute();
		} catch (URISyntaxException e) {
			throw new OrcidClientException(
					AUTHORIZED_API_BASE_URL + " is not syntactically valid.",
					e);
		} catch (HttpStatusCodeException e) {
			log.error("HttpResponse status code: " + e.getStatusCode());
			throw new OrcidClientException(
					"Failed to update external ID. HTTP status code="
							+ e.getStatusCode() + ", xml='" + xml + "'",
					e);
		} catch (IOException e) {
			throw new OrcidClientException("Failed to update external ID", e);
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
			URI baseUri = new URI(context.getApiMemberUrl());
			String requestUrl = URIUtils.resolve(baseUri,
					accessToken.getOrcid() + "/external-identifiers/" + putCode)
					.toString();

			DeleteRequest request = httpWrapper.createDeleteRequest(requestUrl)
					.addHeader("Content-Type", "application/vnd.orcid+xml")
					.addHeader("Authorization", accessToken.toAuthHeader());
			request.execute();
		} catch (URISyntaxException e) {
			throw new OrcidClientException(
					AUTHORIZED_API_BASE_URL + " is not syntactically valid.",
					e);
		} catch (HttpStatusCodeException e) {
			log.error("HttpResponse status code: " + e.getStatusCode());
			throw new OrcidClientException(
					"Failed to remove external ID. HTTP status code="
							+ e.getStatusCode(),
					e);
		} catch (IOException e) {
			throw new OrcidClientException("Failed to remove external ID", e);
		}
	}
}
