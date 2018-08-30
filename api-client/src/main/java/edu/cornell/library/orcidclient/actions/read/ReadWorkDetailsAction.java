package edu.cornell.library.orcidclient.actions.read;

import org.orcid.jaxb.model.record_v2.Work;

import edu.cornell.library.orcidclient.auth.AccessToken;
import edu.cornell.library.orcidclient.context.OrcidClientContext;
import edu.cornell.library.orcidclient.exceptions.OrcidClientException;
import edu.cornell.library.orcidclient.http.HttpWrapper;

/**
 * Read a single work in full from an ORCID record.
 */
public class ReadWorkDetailsAction extends AbstractReadAction {
	public ReadWorkDetailsAction(OrcidClientContext context,
			HttpWrapper httpWrapper) {
		super(context, httpWrapper);
	}

	public Work readDetails(AccessToken accessToken, String putCode)
			throws OrcidClientException {
		return readElement(accessToken, new WorkDetailsEndpoint(putCode));
	}

	public static class WorkDetailsEndpoint extends Endpoint<Work> {
		public WorkDetailsEndpoint(String putCode) {
			super("/work/" + putCode, Work.class);
		}
	}

}
