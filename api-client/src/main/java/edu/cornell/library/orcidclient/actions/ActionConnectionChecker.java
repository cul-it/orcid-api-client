package edu.cornell.library.orcidclient.actions;

import java.io.IOException;

import edu.cornell.library.orcidclient.context.OrcidClientContext;
import edu.cornell.library.orcidclient.exceptions.OrcidClientException;
import edu.cornell.library.orcidclient.http.HttpWrapper;
import edu.cornell.library.orcidclient.http.HttpWrapper.HttpResponse;
import edu.cornell.library.orcidclient.http.HttpWrapper.HttpStatusCodeException;

/**
 * Run the simplest tests to confirm that the URLs in the context respond as we
 * expect.
 */
public class ActionConnectionChecker {
	private final OrcidClientContext context;
	private final HttpWrapper httpWrapper;

	public ActionConnectionChecker(OrcidClientContext context,
			HttpWrapper httpWrapper) {
		this.context = context;
		this.httpWrapper = httpWrapper;
	}

	public void check() throws OrcidClientException {
		checkSiteBaseUrl();
		checkApiPublicUrl();
		checkApiMemberUrl();
	}

	/**
	 * The site base URL should return 200 and will at least contain the word
	 * ORCID (in the title).
	 */
	private void checkSiteBaseUrl() throws OrcidClientException {
		String url = context.getSiteBaseUrl();
		try {
			HttpResponse response = httpWrapper.createGetRequest(url).execute();
			if (!response.getContentString().contains("ORCID")) {
				throw new OrcidClientException(
						"ORCID site points to the wrong location: '" + url
								+ "'");
			}
		} catch (HttpStatusCodeException | IOException e) {
			throw new OrcidClientException(
					"ORCID site is not available at '" + url + "'", e);
		}
	}

	/**
	 * The public search API should return 200 and consist of a <search:search>
	 * XML element.
	 */
	private void checkApiPublicUrl() throws OrcidClientException {
		String url = context.getApiPublicUrl();
		try {
			String requestUrl = url + "search/?q=bogus";
			HttpResponse response = httpWrapper.createGetRequest(requestUrl)
					.addHeader("Accept", "application/vnd.orcid+xml").execute();
			if (!response.getContentString().contains(":search")) {
				throw new OrcidClientException("ORCID public API at '" + url
						+ "' doesn't provide search results");
			}
		} catch (HttpStatusCodeException | IOException e) {
			throw new OrcidClientException(
					"ORCID public API is not available at '" + url + "'", e);
		}
	}

	/**
	 * Since we aren't providing an access token, the member API should return
	 * 403.
	 */
	private void checkApiMemberUrl() throws OrcidClientException {
		String url = context.getApiMemberUrl();
		try {
			String requestUrl = url + "0000-0000-0000-0000";
			httpWrapper.createPostRequest(requestUrl)
					.addHeader("Content-Type", "application/vnd.orcid+xml")
					.execute();
		} catch (HttpStatusCodeException e) {
			if (e.getStatusCode() != 403) {
				throw new OrcidClientException(
						"ORCID member API is not available at '" + url + "'",
						e);
			}
		} catch (IOException e) {
			throw new OrcidClientException(
					"ORCID member API is not available at '" + url + "'", e);
		}
	}

}
