package edu.cornell.libraries.orcidclient.actions;

import edu.cornell.libraries.orcidclient.context.OrcidClientContext;
import edu.cornell.libraries.orcidclient.http.HttpWrapper;

/**
 * So far, just a facade for creating Action objects.
 */
public class OrcidActionClient {
	private final OrcidClientContext context;
	private final HttpWrapper httpWrapper;

	public OrcidActionClient(OrcidClientContext context,
			HttpWrapper httpWrapper) {
		this.context = context;
		this.httpWrapper = httpWrapper;
	}

	public ReadRecordAction createReadRecordAction() {
		return new ReadRecordAction(context, httpWrapper);
	}
	
	public ExternalIdsEditAction createEditExiternalIdsAction() {
		return new ExternalIdsEditAction(context, httpWrapper);
	}

	public WorksEditAction createEditWorksAction() {
		return new WorksEditAction(context, httpWrapper);
	}
	
}
