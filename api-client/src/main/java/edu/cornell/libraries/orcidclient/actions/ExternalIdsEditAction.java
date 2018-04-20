package edu.cornell.libraries.orcidclient.actions;

import edu.cornell.libraries.orcidclient.context.OrcidClientContext;
import edu.cornell.libraries.orcidclient.http.HttpWrapper;
import edu.cornell.libraries.orcidclient.orcid_message_2_1.personexternalidentifier.ExternalIdentifierElement;

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
public class ExternalIdsEditAction
		extends AbstractRecordElementEditAction<ExternalIdentifierElement> {

	public ExternalIdsEditAction(OrcidClientContext context,
			HttpWrapper httpWrapper) {
		super(context, httpWrapper);
	}

	@Override
	protected String getUrlPath() {
		return "external-identifiers";
	}
}
