package edu.cornell.library.orcidclient.actions;

import edu.cornell.library.orcidclient.context.OrcidClientContext;
import edu.cornell.library.orcidclient.http.HttpWrapper;
import edu.cornell.library.orcidclient.orcid_message_2_1.work.WorkElement;

/**
 * Perform ADD, UPDATE and REMOVE operations on Works.
 * 
 * Add:
 * 
 * <pre>
 * curl -i 
 *    -H 'Content-type: application/vnd.orcid+xml' 
 *    -H 'Authorization: Bearer dd91868d-d29a-475e-9acb-bd3fdf2f43f4' 
 *    -d '@[FILE-PATH]/work.xml' 
 *    -X POST 'https://api.sandbox.orcid.org/v2.0/0000-0002-9227-8514/work'
 * </pre>
 */
public class WorksEditAction
		extends AbstractRecordElementEditAction<WorkElement> {

	public WorksEditAction(OrcidClientContext context,
			HttpWrapper httpWrapper) {
		super(context, httpWrapper);
	}

	@Override
	protected String getUrlPath() {
		return "work";
	}
}
