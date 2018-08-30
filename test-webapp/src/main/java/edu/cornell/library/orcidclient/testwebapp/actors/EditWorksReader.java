package edu.cornell.library.orcidclient.testwebapp.actors;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jtwig.JtwigModel;
import org.orcid.jaxb.model.record.summary_v2.WorkGroup;
import org.orcid.jaxb.model.record.summary_v2.WorkSummary;
import org.orcid.jaxb.model.record_v2.Record;

import edu.cornell.library.orcidclient.actions.OrcidActionClient;
import edu.cornell.library.orcidclient.auth.AccessToken;
import edu.cornell.library.orcidclient.exceptions.OrcidClientException;

/**
 * Read the existing Works, in prep for editing.
 */
public class EditWorksReader extends AbstractActor {
	private OrcidActionClient actions;

	public EditWorksReader(HttpServletRequest req, HttpServletResponse resp) {
		super(req, resp);
		actions = getActionClient();
	}

	public void exec() throws IOException, OrcidClientException {
		AccessToken token = getTokenByTokenId(req.getParameter("token"));
		Record record = actions.createReadRecordAction().read(token);

		List<WorkSummary> works = new ArrayList<>();
		for (WorkGroup group : record.getActivitiesSummary().getWorks()
				.getWorkGroup()) {
			works.addAll(group.getWorkSummary());
		}

		render("/templates/editWorksList.twig.html", //
				JtwigModel.newModel() //
						.with("token", token) //
						.with("works", works));
	}
}
