package edu.cornell.library.orcidclient.elements;

import static org.junit.Assert.assertEquals;
import static org.orcid.jaxb.model.common_v2.ContributorRole.AUTHOR;
import static org.orcid.jaxb.model.record_v2.CitationType.BIBTEX;
import static org.orcid.jaxb.model.record_v2.Relationship.SELF;
import static org.orcid.jaxb.model.record_v2.SequenceType.FIRST;
import static org.orcid.jaxb.model.record_v2.WorkType.JOURNAL_ARTICLE;

import java.io.IOException;

import org.junit.Test;
import org.orcid.jaxb.model.record_v2.Work;

import edu.cornell.library.orcidclient.elements.WorkBuilder.CitationBuilder;
import edu.cornell.library.orcidclient.elements.WorkBuilder.ContributorBuilder;
import edu.cornell.library.orcidclient.exceptions.OrcidClientException;
import edu.cornell.library.orcidclient.testing.AbstractTestClass;
import edu.cornell.library.orcidclient.util.OrcidXmlUtil;

/**
 * ORCID provides an example of a full-populated Work in an XML file, here:
 * https://github.com/ORCID/ORCID-Source/blob/master/orcid-model/src/main/resources/record_2.1/samples/write_sample/work-full-2.1.xml
 *
 * See whether we can build that XML using the WorkBuilder.
 * 
 * Exceptions:
 * 
 * WorkBuilder does not process "work:title/translated-title". It has been
 * removed from the file.
 * 
 * WorkBuilder does not process "work:url". It has been removed from the file.
 */
public class WorkBuilderTest extends AbstractTestClass {
	@Test
	public void constructWorkFull_2_1()
			throws IOException, OrcidClientException {
		Work expectedWork = OrcidXmlUtil.unmarshall(
				readXmlFromFile("work-full-2.1_abridged.xml"), Work.class);
		Work actualWork = buildFullWorkInstance();
		assertEquals(expectedWork, actualWork);
	}

	private String readXmlFromFile(String filename) throws IOException {
		return readAll(this.getClass().getResourceAsStream(filename));
	}

	private Work buildFullWorkInstance() {
		return new WorkBuilder(JOURNAL_ARTICLE, "Work Title") //
				.setSubtitle("Sub title") //
				.setJournalTitle("Journal Title") //
				.setShortDescription("Short description") //
				.setCitation(new CitationBuilder(BIBTEX, "\n"
						+ "			@article {ORCID2012,\n"
						+ "			title = \"ORCID: a system to uniquely identify researchers\",\n"
						+ "			journal = \"Leanred Publishing\",\n"
						+ "			year = \"2012\",\n"
						+ "			doi = \"doi:10.1087/20120404\"\n"
						+ "			}") //
				) //
				.addExternalId(new ExternalIdBuilder(SELF) //
						.setType("doi") //
						.setValue("10.1087/20120404") //
						.setUrl("https://doi.org/10.1087/20120404") //
				) //
				.addContributor(new ContributorBuilder(AUTHOR, FIRST) //
						.setOrcidId("0000-0001-5109-3700") //
						.setCreditName("Laure L. Haak") //
				) //
				.setLanguageCode("en") //
				.setCountry("US") //
				.setPublicationDate(new int[] { 2012, 10, 1 }).build();
	}
}
