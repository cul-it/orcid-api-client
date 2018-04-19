/* $This file is distributed under the terms of the license in /doc/license.txt$ */

package edu.cornell.libraries.orcidclient.testwebapp.actors;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.jtwig.JtwigModel;

import edu.cornell.libraries.orcidclient.OrcidClientException;
import edu.cornell.libraries.orcidclient.actions.OrcidActionClient;
import edu.cornell.libraries.orcidclient.auth.AccessToken;
import edu.cornell.libraries.orcidclient.orcid_message_2_1.common.RelationshipType;
import edu.cornell.libraries.orcidclient.orcid_message_2_1.personexternalidentifier.ExternalIdentifierElement;

/**
 * Do it. Add, Remove or Update an External ID.
 */
public class EditExternalIdsRequest extends AbstractActor {
	private OrcidActionClient actions;

	public EditExternalIdsRequest(HttpServletRequest req,
			HttpServletResponse resp) {
		super(req, resp);
		actions = getActionClient();
	}

	@Override
	public void exec()
			throws ServletException, IOException, OrcidClientException {
		AccessToken token = getTokenByTokenId(req.getParameter("token"));
		ExternalIdentifierElement extId = populateExternalId();		

		String putCode = actions.createEditExiternalIdsAction().add(token, extId);

		render("/templates/editExternalIdsResult.twig.html", //
				JtwigModel.newModel() //
						.with("putCode", putCode));
	}

	private ExternalIdentifierElement populateExternalId() {
		ExternalIdentifierElement extId = new ExternalIdentifierElement();
		
		extId.setExternalIdType(req.getParameter("type"));
		extId.setExternalIdValue(req.getParameter("value"));
		
		String url = req.getParameter("url");
		if (StringUtils.isNotEmpty(url)) {
			extId.setExternalIdUrl(url);
		}
		
		String relationship = req.getParameter("relationship");
		if (StringUtils.isNotEmpty(relationship)) {
			RelationshipType rel = (relationship.equalsIgnoreCase("self"))
					? RelationshipType.SELF
					: RelationshipType.PART_OF;
			extId.setExternalIdRelationship(rel);
		}
		return extId;
	}

	private AccessToken getTokenByTokenId(String tokenId) {
		for (AccessToken t : getTokensFromCache()) {
			if (t.getToken().equals(tokenId)) {
				return t;
			}
		}
		return null;
	}
}
