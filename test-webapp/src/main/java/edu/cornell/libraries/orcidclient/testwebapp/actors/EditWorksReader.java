package edu.cornell.libraries.orcidclient.testwebapp.actors;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jtwig.JtwigModel;

import edu.cornell.libraries.orcidclient.OrcidClientException;
import edu.cornell.libraries.orcidclient.actions.OrcidActionClient;
import edu.cornell.libraries.orcidclient.auth.AccessToken;
import edu.cornell.libraries.orcidclient.orcid_message_2_1.activities.WorkGroup;
import edu.cornell.libraries.orcidclient.orcid_message_2_1.record.RecordElement;
import edu.cornell.libraries.orcidclient.orcid_message_2_1.work.WorkSummaryElement;

/**
 * Read the existing Works, in prep for editing.
 */
public class EditWorksReader extends AbstractActor {
	private OrcidActionClient actions;

	public EditWorksReader(HttpServletRequest req,
			HttpServletResponse resp) {
		super(req, resp);
		actions = getActionClient();
	}

	public void exec() throws IOException, OrcidClientException {
		AccessToken token = getTokenByTokenId(req.getParameter("token"));
		RecordElement record = actions.createReadRecordAction().read(token);

		List<WorkSummaryElement> works = new ArrayList<>();
		for (WorkGroup group : record.getActivitiesSummary().getWorks()
				.getGroup()) {
			works.addAll(group.getWorkSummary());
		}

		render("/templates/editWorksList.twig.html", //
				JtwigModel.newModel() //
						.with("token", token) //
						.with("works", works));
	}
}
