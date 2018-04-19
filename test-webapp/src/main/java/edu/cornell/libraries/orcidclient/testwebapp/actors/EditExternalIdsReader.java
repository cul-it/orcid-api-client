/* $This file is distributed under the terms of the license in /doc/license.txt$ */

package edu.cornell.libraries.orcidclient.testwebapp.actors;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jtwig.JtwigModel;

import edu.cornell.libraries.orcidclient.OrcidClientException;
import edu.cornell.libraries.orcidclient.actions.OrcidActionClient;
import edu.cornell.libraries.orcidclient.auth.AccessToken;
import edu.cornell.libraries.orcidclient.orcid_message_2_1.record.RecordElement;

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

	@Override
	public void exec()
			throws ServletException, IOException, OrcidClientException {
		AccessToken token = getTokenByTokenId(req.getParameter("token"));
		RecordElement record = actions.createReadRecordAction().read(token);

		render("/templates/editExternalIdsList.twig.html", //
				JtwigModel.newModel() //
						.with("token", token) //
						.with("externalIds",
								(record.getPerson().getExternalIdentifiers()
										.getExternalIdentifier())));
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
