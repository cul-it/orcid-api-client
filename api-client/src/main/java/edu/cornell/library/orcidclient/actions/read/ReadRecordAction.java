package edu.cornell.library.orcidclient.actions.read;

import org.orcid.jaxb.model.record_v2.Record;

import edu.cornell.library.orcidclient.auth.AccessToken;
import edu.cornell.library.orcidclient.context.OrcidClientContext;
import edu.cornell.library.orcidclient.exceptions.OrcidClientException;
import edu.cornell.library.orcidclient.http.HttpWrapper;

/**
 * Reads the summary of an ORCID record.
 */
public class ReadRecordAction extends AbstractReadAction {
	public static final RecordEndpoint RECORD = new RecordEndpoint("/record");

	public ReadRecordAction(OrcidClientContext context,
			HttpWrapper httpWrapper) {
		super(context, httpWrapper);
	}

	public Record read(AccessToken accessToken) throws OrcidClientException {
		return readElement(accessToken, RECORD);
	}

	public static class RecordEndpoint extends Endpoint<Record> {

		protected RecordEndpoint(String path) {
			super(path, Record.class);
		}
	}
}
