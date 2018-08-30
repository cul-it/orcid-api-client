package edu.cornell.library.orcidclient.actions;

import org.orcid.jaxb.model.record_v2.PersonExternalIdentifier;

import edu.cornell.library.orcidclient.context.OrcidClientContext;
import edu.cornell.library.orcidclient.http.HttpWrapper;

/**
 * Perform ADD, UPDATE and REMOVE operations on Person External IDs.
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
		extends AbstractRecordElementEditAction<PersonExternalIdentifier> {

	public ExternalIdsEditAction(OrcidClientContext context,
			HttpWrapper httpWrapper) {
		super(context, httpWrapper,
				(id, putCode) -> id.setPutCode(Long.valueOf(putCode)));
	}

	@Override
	protected String getUrlPath() {
		return "external-identifiers";
	}
}
