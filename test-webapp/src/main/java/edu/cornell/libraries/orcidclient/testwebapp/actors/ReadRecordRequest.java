package edu.cornell.libraries.orcidclient.testwebapp.actors;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jtwig.JtwigModel;

import edu.cornell.libraries.orcidclient.OrcidClientException;
import edu.cornell.libraries.orcidclient.actions.OrcidActionClient;
import edu.cornell.libraries.orcidclient.auth.AccessToken;
import edu.cornell.libraries.orcidclient.orcid_message_2_1.person.PersonElement;
import edu.cornell.libraries.orcidclient.orcid_message_2_1.record.RecordElement;
import edu.cornell.libraries.orcidclient.util.PrettyToStringPrinter;

/**
 * TODO
 */
public class ReadRecordRequest extends AbstractActor {
	private OrcidActionClient actions;

	public ReadRecordRequest(HttpServletRequest req, HttpServletResponse resp) {
		super(req, resp);
		actions = getActionClient();
	}

	public void exec() throws IOException, OrcidClientException {
		AccessToken token = getTokenByTokenId(req.getParameter("token"));
		RecordElement record = actions.createReadRecordAction().read(token);

		render("/templates/readRecordResult.twig.html", //
				JtwigModel.newModel() //
						.with("recordString", (recordToString(record))));
	}

	private Object recordToString(RecordElement record) {
		String orcid = record.getOrcidIdentifier().getUri();

		PersonElement person = record.getPerson();
		String givenNames = person.getName().getGivenNames().getValue();
		String familyName = person.getName().getFamilyName().getValue();
		List<String> eids = person.getExternalIdentifiers()
				.getExternalIdentifier().stream()
				.map(eid -> eid.getExternalIdUrl())
				.collect(Collectors.toList());

		String rawString = String.format( //
				"RecordElement[orcid-identifier=%s, name=%s %s, externalIDs=%s]", //
				orcid, familyName, givenNames, eids);
		return new PrettyToStringPrinter().format(rawString);
	}
}
