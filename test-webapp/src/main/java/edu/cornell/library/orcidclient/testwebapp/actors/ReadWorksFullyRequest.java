package edu.cornell.library.orcidclient.testwebapp.actors;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jtwig.JtwigModel;
import org.orcid.jaxb.model.record.summary_v2.WorkGroup;
import org.orcid.jaxb.model.record.summary_v2.WorkSummary;
import org.orcid.jaxb.model.record.summary_v2.Works;

import edu.cornell.library.orcidclient.actions.OrcidActionClient;
import edu.cornell.library.orcidclient.actions.read.ReadWorkDetailsAction;
import edu.cornell.library.orcidclient.actions.read.ReadWorkDetailsAction.WorkDetailsEndpoint;
import edu.cornell.library.orcidclient.actions.read.ReadWorksSummariesAction;
import edu.cornell.library.orcidclient.auth.AccessToken;
import edu.cornell.library.orcidclient.exceptions.OrcidClientException;
import edu.cornell.library.orcidclient.util.OrcidXmlUtil;

/**
 * Read the full details of the Works on an ORCID record.
 * 
 * Start by getting the summaries, from which we can get the put codes. For each
 * put code, read the full details.
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
		String worksSummaryXml = actions.createReadWorksSummariesAction()
				.readXml(token, ReadWorksSummariesAction.WORKS.getPath());

		Works works = OrcidXmlUtil.unmarshall(worksSummaryXml, Works.class);

		List<String> putCodes = getPutCodes(works);
		List<String> workXmls = getWorkDetailsXml(putCodes, token);

		render("/templates/readWorksFullyResult.twig.html", //
				JtwigModel.newModel() //
						.with("worksSummaryXml", worksSummaryXml) //
						.with("workXmls", workXmls));
	}

	private List<String> getPutCodes(Works works) {
		List<String> putCodes = new ArrayList<>();
		for (WorkGroup workGroup : works.getWorkGroup()) {
			for (WorkSummary workSummary : workGroup.getWorkSummary()) {
				putCodes.add(String.valueOf(workSummary.getPutCode()));
			}
		}
		return putCodes;
	}

	private List<String> getWorkDetailsXml(List<String> putCodes,
			AccessToken token) throws OrcidClientException {
		ReadWorkDetailsAction action = actions.createReadWorkDetailsAction();
		List<String> xmls = new ArrayList<>();
		for (String putCode : putCodes) {
			String path = new WorkDetailsEndpoint(putCode).getPath();
			String workXml = action.readXml(token, path);
			xmls.add(workXml);
		}
		return xmls;
	}
}
