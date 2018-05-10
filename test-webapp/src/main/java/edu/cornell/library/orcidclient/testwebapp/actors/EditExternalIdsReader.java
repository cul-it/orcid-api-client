package edu.cornell.library.orcidclient.testwebapp.actors;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jtwig.JtwigModel;

import edu.cornell.library.orcidclient.OrcidClientException;
import edu.cornell.library.orcidclient.actions.OrcidActionClient;
import edu.cornell.library.orcidclient.auth.AccessToken;
import edu.cornell.library.orcidclient.orcid_message_2_1.common.ExternalId;
import edu.cornell.library.orcidclient.orcid_message_2_1.record.RecordElement;

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
		RecordElement record = actions.createReadRecordAction().read(token);
		List<ExternalId> externalIds = record.getPerson()
				.getExternalIdentifiers().getExternalIdentifier();

		render("/templates/editExternalIdsList.twig.html", //
				JtwigModel.newModel() //
						.with("token", token) //
						.with("externalIds", externalIds));
	}
}
