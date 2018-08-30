package edu.cornell.library.orcidclient.testwebapp.actors;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jtwig.JtwigModel;
import org.orcid.jaxb.model.record_v2.Person;
import org.orcid.jaxb.model.record_v2.Record;

import edu.cornell.library.orcidclient.actions.OrcidActionClient;
import edu.cornell.library.orcidclient.auth.AccessToken;
import edu.cornell.library.orcidclient.exceptions.OrcidClientException;
import edu.cornell.library.orcidclient.util.PrettyToStringPrinter;

/**
 * Go read the summary of the ORCID record.
 */
public class ReadRecordRequest extends AbstractActor {
	private OrcidActionClient actions;

	public ReadRecordRequest(HttpServletRequest req, HttpServletResponse resp) {
		super(req, resp);
		actions = getActionClient();
	}

	public void exec() throws IOException, OrcidClientException {
		AccessToken token = getTokenByTokenId(req.getParameter("token"));
		Record record = actions.createReadRecordAction().read(token);

		render("/templates/readRecordResult.twig.html", //
				JtwigModel.newModel() //
						.with("recordString", (recordToString(record))));
	}

	private String recordToString(Record record) {
		String orcid = record.getOrcidIdentifier().getUri();

		Person person = record.getPerson();
		String givenNames = person.getName().getGivenNames().getContent();
		String familyName = person.getName().getFamilyName().getContent();
		List<String> peIDs = person.getExternalIdentifiers()
				.getExternalIdentifiers().stream()
				.map(peID -> peID.getUrl().getValue())
				.collect(Collectors.toList());

		String rawString = String.format( //
				"RecordElement[orcid-identifier=%s, name=%s %s, externalIDs=%s]", //
				orcid, familyName, givenNames, peIDs);
		return new PrettyToStringPrinter().format(rawString);
	}
}
