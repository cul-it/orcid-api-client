package edu.cornell.library.orcidclient.elements;

import java.util.ArrayList;
import java.util.List;

import org.orcid.jaxb.model.common_v2.Contributor;
import org.orcid.jaxb.model.common_v2.ContributorAttributes;
import org.orcid.jaxb.model.common_v2.ContributorEmail;
import org.orcid.jaxb.model.common_v2.ContributorOrcid;
import org.orcid.jaxb.model.common_v2.ContributorRole;
import org.orcid.jaxb.model.common_v2.Country;
import org.orcid.jaxb.model.common_v2.CreditName;
import org.orcid.jaxb.model.common_v2.Iso3166Country;
import org.orcid.jaxb.model.common_v2.PublicationDate;
import org.orcid.jaxb.model.common_v2.Subtitle;
import org.orcid.jaxb.model.common_v2.Title;
import org.orcid.jaxb.model.record_v2.Citation;
import org.orcid.jaxb.model.record_v2.CitationType;
import org.orcid.jaxb.model.record_v2.ExternalIDs;
import org.orcid.jaxb.model.record_v2.SequenceType;
import org.orcid.jaxb.model.record_v2.Work;
import org.orcid.jaxb.model.record_v2.WorkContributors;
import org.orcid.jaxb.model.record_v2.WorkTitle;
import org.orcid.jaxb.model.record_v2.WorkType;

/**
 * A conversational tool for building a WorkElement. The only required fields
 * are type and title.
 */
public class WorkBuilder {
	private final WorkType workType;
	private String title;
	private String subtitle;
	private int[] publicationDate;
	private String shortDescription;
	private String languageCode;
	private String country;
	private String journalTitle;
	private CitationBuilder citation;
	private List<ExternalIdBuilder> externalIds = new ArrayList<>();
	private List<ContributorBuilder> contributors = new ArrayList<>();

	public WorkBuilder(WorkType workType, String title) {
		this.workType = workType;
		this.title = title;
	}

	public WorkBuilder setSubtitle(String subtitle) {
		this.subtitle = subtitle;
		return this;
	}

	public WorkBuilder setPublicationDate(int... yearMonthDay) {
		this.publicationDate = yearMonthDay;
		return this;
	}

	public WorkBuilder setShortDescription(String shortDescription) {
		this.shortDescription = shortDescription;
		return this;
	}

	public WorkBuilder setLanguageCode(String languageCode) {
		this.languageCode = languageCode;
		return this;
	}

	public WorkBuilder setCountry(String country) {
		this.country = country;
		return this;
	}

	public WorkBuilder setJournalTitle(String journalTitle) {
		this.journalTitle = journalTitle;
		return this;
	}

	public WorkBuilder setCitation(CitationBuilder citation) {
		this.citation = citation;
		return this;
	}

	public WorkBuilder addExternalId(ExternalIdBuilder externalId) {
		if (externalId != null) {
			this.externalIds.add(externalId);
		}
		return this;
	}

	public WorkBuilder addContributor(ContributorBuilder contributor) {
		if (contributor != null) {
			this.contributors.add(contributor);
		}
		return this;
	}

	public Work build() {
		Work work = new Work();
		work.setWorkType(workType);
		work.setWorkTitle(buildTitle());

		if (publicationDate != null) {
			work.setPublicationDate(buildPublicationDate());
		}
		if (shortDescription != null) {
			work.setShortDescription(shortDescription);
		}
		if (languageCode != null) {
			work.setLanguageCode(languageCode);
		}
		if (country != null) {
			work.setCountry(buildCountry());
		}
		if (journalTitle != null) {
			work.setJournalTitle(new Title(journalTitle));
		}
		if (citation != null) {
			work.setWorkCitation(citation.build());
		}

		work.setWorkContributors(new WorkContributors());
		for (ContributorBuilder contributor : contributors) {
			work.getWorkContributors().getContributor()
					.add(contributor.build());
		}

		work.setWorkExternalIdentifiers(new ExternalIDs());
		for (ExternalIdBuilder externalId : externalIds) {
			work.getWorkExternalIdentifiers().getExternalIdentifier()
					.add(externalId.build());
		}

		return work;
	}

	private WorkTitle buildTitle() {
		WorkTitle workTitle = new WorkTitle();
		workTitle.setTitle(new Title(title));
		if (subtitle != null) {
			workTitle.setSubtitle(new Subtitle(subtitle));
		}
		return workTitle;
	}

	private PublicationDate buildPublicationDate() {
		return new PublicationDateBuilder(publicationDate).build();
	}

	private Country buildCountry() {
		return new Country(Iso3166Country.fromValue(country));
	}

	// ----------------------------------------------------------------------
	// Helper classes
	// ----------------------------------------------------------------------

	public static class ContributorBuilder {
		private final ContributorRole contributorRole;
		private final SequenceType contributorSequence;
		private String creditName;
		private String contributorEmail;
		private String orcidId;

		public ContributorBuilder(ContributorRole contributorRole,
				SequenceType contributorSequence) {
			this.contributorRole = contributorRole;
			this.contributorSequence = contributorSequence;
		}

		public ContributorBuilder setCreditName(String creditName) {
			this.creditName = creditName;
			return this;
		}

		public ContributorBuilder setContributorEmail(String contributorEmail) {
			this.contributorEmail = contributorEmail;
			return this;
		}

		public ContributorBuilder setOrcidId(String orcidId) {
			this.orcidId = orcidId;
			return this;
		}

		public Contributor build() {
			ContributorAttributes attributes = new ContributorAttributes();
			attributes.setContributorRole(contributorRole);
			attributes.setContributorSequence(contributorSequence);

			Contributor contributor = new Contributor();
			contributor.setContributorAttributes(attributes);
			if (creditName != null) {
				contributor.setCreditName(new CreditName(creditName));
			}
			if (contributorEmail != null) {
				contributor.setContributorEmail(
						new ContributorEmail(contributorEmail));
			}
			if (orcidId != null) {
				ContributorOrcid contributorOrcid = new ContributorOrcid();
				contributorOrcid.setPath(orcidId);
				contributorOrcid.setHost("orcid.org");
				contributorOrcid.setUri("https://orcid.org/" + orcidId);
				contributor.setContributorOrcid(contributorOrcid);
			}
			return contributor;
		}
	}

	public static class CitationBuilder {
		private final CitationType type;
		private final String value;

		public CitationBuilder(CitationType type, String value) {
			this.type = type;
			this.value = value;
		}

		public Citation build() {
			Citation citation = new Citation();
			citation.setWorkCitationType(type);
			citation.setCitation(value);
			return citation;
		}
	}
}
