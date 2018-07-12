package edu.cornell.library.orcidclient.testwebapp.actors;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.jtwig.JtwigModel;

import edu.cornell.library.orcidclient.actions.OrcidActionClient;
import edu.cornell.library.orcidclient.auth.AccessToken;
import edu.cornell.library.orcidclient.exceptions.OrcidClientException;
import edu.cornell.library.orcidclient.orcid_message_2_1.common.RelationshipType;
import edu.cornell.library.orcidclient.orcid_message_2_1.personexternalidentifier.ExternalIdentifierElement;

/**
 * Do it. Add, Remove or Update an External ID.
 */
public class EditExternalIdsRequest extends AbstractActor {
	private OrcidActionClient actions;
	private AccessToken token;

	public EditExternalIdsRequest(HttpServletRequest req,
			HttpServletResponse resp) {
		super(req, resp);
		actions = getActionClient();
		token = getTokenByTokenId(req.getParameter("token"));
	}

	public void add() throws IOException, OrcidClientException {
		String putCode = actions.createEditExiternalIdsAction().add(token,
				populateExternalId());

		render("/templates/editExternalIdsResult.twig.html", //
				JtwigModel.newModel() //
						.with("putCode", putCode));
	}

	public void update() throws IOException, OrcidClientException {
		String putCode = req.getParameter("putCode");
		actions.createEditExiternalIdsAction().update(token,
				populateExternalId(), putCode);

		render("/templates/editExternalIdsResult.twig.html", //
				JtwigModel.newModel() //
						.with("putCode", "No problems with update"));
	}

	public void remove() throws IOException, OrcidClientException {
		String putCode = req.getParameter("putCode");
		actions.createEditExiternalIdsAction().remove(token, putCode);

		render("/templates/editExternalIdsResult.twig.html", //
				JtwigModel.newModel() //
						.with("putCode", "No problems with remove"));
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
		if (relationship.equalsIgnoreCase("part-of")) {
			extId.setExternalIdRelationship(RelationshipType.PART_OF);
		} else {
			extId.setExternalIdRelationship(RelationshipType.SELF);
		}

		return extId;
	}
}
