package edu.cornell.libraries.orcidclient.actions;

import static edu.cornell.libraries.orcidclient.context.OrcidClientContext.Setting.AUTHORIZED_API_BASE_URL;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.client.utils.URIUtils;

import edu.cornell.libraries.orcidclient.OrcidClientException;
import edu.cornell.libraries.orcidclient.auth.AccessToken;
import edu.cornell.libraries.orcidclient.context.OrcidClientContext;
import edu.cornell.libraries.orcidclient.http.HttpWrapper;
import edu.cornell.libraries.orcidclient.http.HttpWrapper.HttpResponse;
import edu.cornell.libraries.orcidclient.http.HttpWrapper.HttpStatusCodeException;
import edu.cornell.libraries.orcidclient.http.HttpWrapper.PostRequest;
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

	public void update(AccessToken accessToken, ExternalId externalId,
			String putCode) {
		throw new RuntimeException(
				"EditExternalIdsAction.update not implemented.");
	}

	public void remove(AccessToken accessToken, String putCode) {
		throw new RuntimeException(
				"EditExternalIdsAction.remove not implemented.");
	}
}

/**
 * BOGUS
 * 
 * ADD
 * 
 * <pre>
Method: POST
Content-type: application/vnd.orcid+xml or application/vnd.orcid+json
Authorization type: Bearer
Access token: Stored access token
Data: link to file or text of single employment item to add
URL: https://api.sandbox.orcid.org/v2.1/[ORCID iD]/employment
Example call: GitHub
 * </pre>
 * 
 * UPDATE
 * 
 * <pre>
  <?xml version="1.0" encoding="UTF-8"?> 
    <employment:employment put-code="739288" [...]>
    [...]         
  </employment:employment>
  
Method: PUT    <<<<< NOTE
  Content-type: application/vnd.orcid+xml or application/vnd.orcid+json
  Authorization type: Bearer
  Access token: Stored access token
  Data: link to file or text of affiliation to update
  URL: https://api.sandbox.orcid.org/v2.1/[ORCID iD]/employment/739288
  Example calls: GitHub
 * </pre>
 * 
 * DELETE
 * 
 * <pre>
Method: DELETE    <<<<< NOTE
  Content-type:  application/vnd.orcid+xml or application/vnd.orcid+json
  Authorization type:  Bearer
  Access Token: Stored access token 
  URL: https://api.sandbox.orcid.org/v2.1/[ORCID iD]/employment/739288
  Example call: GitHub
 * </pre>
 * 
 * PUT-CODE in LOCATION header
 * 
 * <pre>
2018-04-18 14:47:22,732 DEBUG [wire] http-outgoing-3 << "HTTP/1.1 201 Created[\r][\n]"
2018-04-18 14:47:22,732 DEBUG [wire] http-outgoing-3 << "Server: nginx/1.10.0[\r][\n]"
2018-04-18 14:47:22,732 DEBUG [wire] http-outgoing-3 << "Date: Wed, 18 Apr 2018 18:47:22 GMT[\r][\n]"
2018-04-18 14:47:22,732 DEBUG [wire] http-outgoing-3 << "Content-Type: application/vnd.orcid+xml; qs=5;charset=UTF-8[\r][\n]"
2018-04-18 14:47:22,732 DEBUG [wire] http-outgoing-3 << "Content-Length: 0[\r][\n]"
2018-04-18 14:47:22,732 DEBUG [wire] http-outgoing-3 << "Connection: keep-alive[\r][\n]"
2018-04-18 14:47:22,732 DEBUG [wire] http-outgoing-3 << "Access-Control-Allow-Origin: *[\r][\n]"
2018-04-18 14:47:22,732 DEBUG [wire] http-outgoing-3 << "Cache-Control: no-cache, no-store, max-age=0, must-revalidate[\r][\n]"
2018-04-18 14:47:22,732 DEBUG [wire] http-outgoing-3 << "Pragma: no-cache[\r][\n]"
2018-04-18 14:47:22,732 DEBUG [wire] http-outgoing-3 << "Expires: 0[\r][\n]"
2018-04-18 14:47:22,733 DEBUG [wire] http-outgoing-3 << "X-XSS-Protection: 1; mode=block[\r][\n]"
2018-04-18 14:47:22,733 DEBUG [wire] http-outgoing-3 << "X-Frame-Options: DENY[\r][\n]"
2018-04-18 14:47:22,733 DEBUG [wire] http-outgoing-3 << "X-Content-Type-Options: nosniff[\r][\n]"
2018-04-18 14:47:22,733 DEBUG [wire] http-outgoing-3 << "Location: http://api.sandbox.orcid.org/orcid-api-web/v2.1/0000-0003-0550-2950/external-identifiers/4848[\r][\n]"
 * </pre>
 */