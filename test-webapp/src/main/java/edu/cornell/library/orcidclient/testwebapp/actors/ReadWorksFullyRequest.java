package edu.cornell.library.orcidclient.testwebapp.actors;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jtwig.JtwigModel;

import edu.cornell.library.orcidclient.actions.OrcidActionClient;
import edu.cornell.library.orcidclient.actions.read.ReadWorksAction.WorkDetailsEndpoint;
import edu.cornell.library.orcidclient.actions.read.ReadWorksAction;
import edu.cornell.library.orcidclient.auth.AccessToken;
import edu.cornell.library.orcidclient.exceptions.OrcidClientException;
import edu.cornell.library.orcidclient.orcid_message_2_1.activities.WorkGroup;
import edu.cornell.library.orcidclient.orcid_message_2_1.activities.WorksElement;
import edu.cornell.library.orcidclient.orcid_message_2_1.work.WorkSummaryElement;
import edu.cornell.library.orcidclient.util.OrcidXmlUtil;

/**
 * TODO
 */
public class ReadWorksFullyRequest extends AbstractActor {
	private OrcidActionClient actions;

	public ReadWorksFullyRequest(HttpServletRequest req,
			HttpServletResponse resp) {
		super(req, resp);
		actions = getActionClient();
	}

	public void exec() throws IOException, OrcidClientException {
		AccessToken token = getTokenByTokenId(req.getParameter("token"));
		ReadWorksAction action = actions.createReadWorksAction();
		String worksSummaryXml = action.readXml(token,
				ReadWorksAction.WORKS.getPath());

		WorksElement works = OrcidXmlUtil.unmarshall(worksSummaryXml,
				WorksElement.class);

		List<String> putCodes = getPutCodes(works);
		List<String> workXmls = getWorkDetailsXml(putCodes, token, action);

		render("/templates/readWorksFullyResult.twig.html", //
				JtwigModel.newModel() //
						.with("worksSummaryXml", worksSummaryXml) //
						.with("workXmls", workXmls));
	}

	private List<String> getPutCodes(WorksElement works) {
		List<String> putCodes = new ArrayList<>();
		for (WorkGroup workGroup : works.getGroup()) {
			for (WorkSummaryElement workSummary : workGroup.getWorkSummary()) {
				putCodes.add(String.valueOf(workSummary.getPutCode()));
			}
		}
		return putCodes;
	}

	private List<String> getWorkDetailsXml(List<String> putCodes,
			AccessToken token, ReadWorksAction action)
			throws OrcidClientException {
		List<String> xmls = new ArrayList<>();
		for (String putCode : putCodes) {
			String path = new WorkDetailsEndpoint(putCode).getPath();
			String workXml = action.readXml(token, path);
			xmls.add(workXml);
		}
		return xmls;
	}
}
