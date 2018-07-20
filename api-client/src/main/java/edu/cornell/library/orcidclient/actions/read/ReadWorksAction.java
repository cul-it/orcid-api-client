package edu.cornell.library.orcidclient.actions.read;

import edu.cornell.library.orcidclient.auth.AccessToken;
import edu.cornell.library.orcidclient.context.OrcidClientContext;
import edu.cornell.library.orcidclient.exceptions.OrcidClientException;
import edu.cornell.library.orcidclient.http.HttpWrapper;
import edu.cornell.library.orcidclient.orcid_message_2_1.activities.WorksElement;
import edu.cornell.library.orcidclient.orcid_message_2_1.work.WorkElement;

/**
 * TODO
 */
public class ReadWorksAction extends AbstractReadAction {
	public static final WorksSummaryEndpoint WORKS = new WorksSummaryEndpoint("/works");

	public ReadWorksAction(OrcidClientContext context,
			HttpWrapper httpWrapper) {
		super(context, httpWrapper);
	}

	public WorksElement readSummaries(AccessToken accessToken)
			throws OrcidClientException {
		return readElement(accessToken, WORKS);
	}

	public WorkElement readDetails(AccessToken accessToken, String putCode)
			throws OrcidClientException {
		return readElement(accessToken, new WorkDetailsEndpoint(putCode));
	}

	public static class WorksSummaryEndpoint extends Endpoint<WorksElement> {
		public WorksSummaryEndpoint(String path) {
			super(path, WorksElement.class);
		}
	}

	public static class WorkDetailsEndpoint extends Endpoint<WorkElement> {
		public WorkDetailsEndpoint(String putCode) {
			super("/work/" + putCode, WorkElement.class);
		}
	}

}
