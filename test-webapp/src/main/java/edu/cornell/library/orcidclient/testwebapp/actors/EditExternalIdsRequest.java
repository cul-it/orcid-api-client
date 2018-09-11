package edu.cornell.library.orcidclient.testwebapp.actors;

import static org.orcid.jaxb.model.record_v2.Relationship.PART_OF;
import static org.orcid.jaxb.model.record_v2.Relationship.SELF;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.jtwig.JtwigModel;
import org.orcid.jaxb.model.common_v2.Url;
import org.orcid.jaxb.model.record_v2.PersonExternalIdentifier;

import edu.cornell.library.orcidclient.actions.OrcidActionClient;
import edu.cornell.library.orcidclient.auth.AccessToken;
import edu.cornell.library.orcidclient.exceptions.OrcidClientException;

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
		String putCode = actions.createEditExternalIdsAction().add(token,
				populateExternalId());

		render("/templates/editExternalIdsResult.twig.html", //
				JtwigModel.newModel() //
						.with("putCode", putCode));
	}

	public void update() throws IOException, OrcidClientException {
		String putCode = req.getParameter("putCode");
		actions.createEditExternalIdsAction().update(token,
				populateExternalId(), putCode);

		render("/templates/editExternalIdsResult.twig.html", //
				JtwigModel.newModel() //
						.with("putCode", "No problems with update"));
	}

	public void remove() throws IOException, OrcidClientException {
		String putCode = req.getParameter("putCode");
		actions.createEditExternalIdsAction().remove(token, putCode);

		render("/templates/editExternalIdsResult.twig.html", //
				JtwigModel.newModel() //
						.with("putCode", "No problems with remove"));
	}

	private PersonExternalIdentifier populateExternalId() {
		PersonExternalIdentifier extId = new PersonExternalIdentifier();

		extId.setType(req.getParameter("type"));
		extId.setValue(req.getParameter("value"));

		String url = req.getParameter("url");
		if (StringUtils.isNotEmpty(url)) {
			extId.setUrl(new Url(url));
		}

		String relationship = req.getParameter("relationship");
		if (relationship.equalsIgnoreCase("part-of")) {
			extId.setRelationship(PART_OF);
		} else {
			extId.setRelationship(SELF);
		}

		return extId;
	}
}
