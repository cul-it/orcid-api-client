package edu.cornell.library.orcidclient.testwebapp.actors;

import static org.orcid.jaxb.model.common_v2.ContributorRole.AUTHOR;
import static org.orcid.jaxb.model.record_v2.CitationType.BIBTEX;
import static org.orcid.jaxb.model.record_v2.SequenceType.FIRST;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jtwig.JtwigModel;
import org.orcid.jaxb.model.record_v2.Work;
import org.orcid.jaxb.model.record_v2.WorkType;

import edu.cornell.library.orcidclient.actions.OrcidActionClient;
import edu.cornell.library.orcidclient.auth.AccessToken;
import edu.cornell.library.orcidclient.elements.ExternalIdBuilder;
import edu.cornell.library.orcidclient.elements.WorkBuilder;
import edu.cornell.library.orcidclient.elements.WorkBuilder.CitationBuilder;
import edu.cornell.library.orcidclient.elements.WorkBuilder.ContributorBuilder;
import edu.cornell.library.orcidclient.exceptions.OrcidClientException;

/**
 * Do it. Add, Remove or Update a Work.
 */
public class EditWorksRequest extends AbstractActor {
	private OrcidActionClient actions;
	private AccessToken token;

	public EditWorksRequest(HttpServletRequest req, HttpServletResponse resp) {
		super(req, resp);
		actions = getActionClient();
		token = getTokenByTokenId(req.getParameter("token"));
	}

	public void add() throws IOException, OrcidClientException {
		String putCode = actions.createEditWorksAction().add(token,
				populateWork());

		render("/templates/editWorksResult.twig.html", //
				JtwigModel.newModel() //
						.with("putCode", putCode));
	}

	public void update() throws IOException, OrcidClientException {
		String putCode = req.getParameter("putCode");
		actions.createEditWorksAction().update(token, populateWork(), putCode);

		render("/templates/editWorksResult.twig.html", //
				JtwigModel.newModel() //
						.with("putCode", "No problems with update"));
	}

	public void remove() throws IOException, OrcidClientException {
		String putCode = req.getParameter("putCode");
		actions.createEditWorksAction().remove(token, putCode);

		render("/templates/editWorksResult.twig.html", //
				JtwigModel.newModel() //
						.with("putCode", "No problems with remove"));
	}

	private Work populateWork() {
		String title = req.getParameter("title");
		int[] pubDate = parseDate(req.getParameter("publicationDate"));
		String idSuffix = req.getParameter("externalId");

		return new WorkBuilder(WorkType.JOURNAL_ARTICLE, title)
				.setPublicationDate(pubDate) //
				.setLanguageCode("EN") //
				.setCountry("US") //
				.setJournalTitle("My favorite journal") //
				.addExternalId(new ExternalIdBuilder() //
						.setType("other-id") //
						.setUrl("http://external/id/" + idSuffix) //
						.setValue("Link to me " + idSuffix)) //
				.build();
	}

	private int[] parseDate(String dateString) {
		String[] parts = dateString.split("-");
		int[] ints = new int[parts.length];
		for (int i = 0; i < parts.length; i++) {
			ints[i] = Integer.parseInt(parts[i]);
		}
		return ints;
	}

	/**
	 * This is how we would create a fully-populated work.
	 */
	@SuppressWarnings("unused")
	private Work generate() {
		return new WorkBuilder(WorkType.JOURNAL_ARTICLE, "The article title")
				.setSubtitle("An odyssey") //
				.setPublicationDate(1953, 7, 30) //
				.setShortDescription("A most excellent article.") //
				.setLanguageCode("EN") //
				.setCountry("AR") //
				.setJournalTitle("My favorite journal") //
				.setCitation(
						new CitationBuilder(BIBTEX, "Some BIBTEX citation")) //
				.addExternalId(new ExternalIdBuilder() //
						.setType("other-id") //
						.setUrl("http://external/id") //
						.setValue("Link to me")) //
				.addContributor(new ContributorBuilder(AUTHOR, FIRST) //
						.setCreditName("Joe Bagadonuts") //
						.setContributorEmail("joeBags@donuts.edu") //
						.setOrcidId("0000-0000-0000-0000")) //
				.build();
	}

}
