package edu.cornell.library.orcidclient.testwebapp.actors;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jtwig.JtwigModel;
import org.orcid.jaxb.model.record_v2.PersonExternalIdentifier;
import org.orcid.jaxb.model.record_v2.Record;

import edu.cornell.library.orcidclient.actions.OrcidActionClient;
import edu.cornell.library.orcidclient.auth.AccessToken;
import edu.cornell.library.orcidclient.exceptions.OrcidClientException;

/**
 * Read the existing External IDs, in prep for editing.
 */
public class EditExternalIdsReader extends AbstractActor {
	private OrcidActionClient actions;

	public EditExternalIdsReader(HttpServletRequest req,
			HttpServletResponse resp) {
		super(req, resp);
		actions = getActionClient();
	}

	public void exec() throws IOException, OrcidClientException {
		AccessToken token = getTokenByTokenId(req.getParameter("token"));
		Record record = actions.createReadRecordAction().read(token);
		List<PersonExternalIdentifier> externalIds = record.getPerson()
				.getExternalIdentifiers().getExternalIdentifiers();

		render("/templates/editExternalIdsList.twig.html", //
				JtwigModel.newModel() //
						.with("token", token) //
						.with("externalIds", externalIds));
	}
}
