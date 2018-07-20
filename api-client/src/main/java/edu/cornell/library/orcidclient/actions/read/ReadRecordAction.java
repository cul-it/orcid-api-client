package edu.cornell.library.orcidclient.actions.read;

import edu.cornell.library.orcidclient.auth.AccessToken;
import edu.cornell.library.orcidclient.context.OrcidClientContext;
import edu.cornell.library.orcidclient.exceptions.OrcidClientException;
import edu.cornell.library.orcidclient.http.HttpWrapper;
import edu.cornell.library.orcidclient.orcid_message_2_1.record.RecordElement;

/**
 * Reads the summary of an ORCID record.
 */
public class ReadRecordAction extends AbstractReadAction {
	public static final RecordEndpoint RECORD = new RecordEndpoint("/record");

	public ReadRecordAction(OrcidClientContext context,
			HttpWrapper httpWrapper) {
		super(context, httpWrapper);
	}

	public RecordElement read(AccessToken accessToken)
			throws OrcidClientException {
		return readElement(accessToken, RECORD);
	}

	public static class RecordEndpoint extends Endpoint<RecordElement> {

		protected RecordEndpoint(String path) {
			super(path, RecordElement.class);
		}
	}
}
