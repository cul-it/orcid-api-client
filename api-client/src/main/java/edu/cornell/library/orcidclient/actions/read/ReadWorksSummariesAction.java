package edu.cornell.library.orcidclient.actions.read;


import org.orcid.jaxb.model.record.summary_v2.Works;

import edu.cornell.library.orcidclient.auth.AccessToken;
import edu.cornell.library.orcidclient.context.OrcidClientContext;
import edu.cornell.library.orcidclient.exceptions.OrcidClientException;
import edu.cornell.library.orcidclient.http.HttpWrapper;

/**
 * Read the work summaries from an ORCID record.
 */
public class ReadWorksSummariesAction extends AbstractReadAction {
	public static final WorksSummaryEndpoint WORKS = new WorksSummaryEndpoint(
			"/works");

	public ReadWorksSummariesAction(OrcidClientContext context,
			HttpWrapper httpWrapper) {
		super(context, httpWrapper);
	}

	public Works readSummaries(AccessToken accessToken)
			throws OrcidClientException {
		return readElement(accessToken, WORKS);
	}

	public static class WorksSummaryEndpoint extends Endpoint<Works> {
		public WorksSummaryEndpoint(String path) {
			super(path, Works.class);
		}
	}

}
