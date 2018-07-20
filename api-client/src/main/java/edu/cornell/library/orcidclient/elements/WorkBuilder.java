package edu.cornell.library.orcidclient.elements;

import java.util.ArrayList;
import java.util.List;

import edu.cornell.library.orcidclient.orcid_message_2_1.common.CountryElement;
import edu.cornell.library.orcidclient.orcid_message_2_1.common.CreditName;
import edu.cornell.library.orcidclient.orcid_message_2_1.common.ExternalIds;
import edu.cornell.library.orcidclient.orcid_message_2_1.common.FuzzyDate;
import edu.cornell.library.orcidclient.orcid_message_2_1.common.LanguageCode;
import edu.cornell.library.orcidclient.orcid_message_2_1.work.Citation;
import edu.cornell.library.orcidclient.orcid_message_2_1.work.CitationType;
import edu.cornell.library.orcidclient.orcid_message_2_1.work.Contributor;
import edu.cornell.library.orcidclient.orcid_message_2_1.work.ContributorAttributes;
import edu.cornell.library.orcidclient.orcid_message_2_1.work.ContributorEmail;
import edu.cornell.library.orcidclient.orcid_message_2_1.work.ContributorRole;
import edu.cornell.library.orcidclient.orcid_message_2_1.work.ContributorSequence;
import edu.cornell.library.orcidclient.orcid_message_2_1.work.WorkContributors;
import edu.cornell.library.orcidclient.orcid_message_2_1.work.WorkElement;
import edu.cornell.library.orcidclient.orcid_message_2_1.work.WorkTitle;
import edu.cornell.library.orcidclient.orcid_message_2_1.work.WorkType;

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
	private LanguageCode languageCode;
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

	public WorkBuilder setLanguageCode(LanguageCode languageCode) {
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

	public WorkElement build() {
		WorkElement work = new WorkElement();
		work.setType(workType);
		work.setTitle(buildTitle());

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
			work.setJournalTitle(journalTitle);
		}
		if (citation != null) {
			work.setCitation(citation.build());
		}

		work.setContributors(new WorkContributors());
		for (ContributorBuilder contributor : contributors) {
			work.getContributors().getContributor().add(contributor.build());
		}

		work.setExternalIds(new ExternalIds());
		for (ExternalIdBuilder externalId : externalIds) {
			work.getExternalIds().getExternalId().add(externalId.build());
		}

		return work;
	}

	private WorkTitle buildTitle() {
		WorkTitle workTitle = new WorkTitle();
		workTitle.setTitle(title);
		if (subtitle != null) {
			workTitle.setSubtitle("An odyssey");
		}
		return workTitle;
	}

	private FuzzyDate buildPublicationDate() {
		return new FuzzyDateBuilder(publicationDate).build();
	}

	private CountryElement buildCountry() {
		CountryElement element = new CountryElement();
		element.setValue(country);
		return element;
	}

	// ----------------------------------------------------------------------
	// Helper classes
	// ----------------------------------------------------------------------

	public static class ContributorBuilder {
		private final ContributorRole contributorRole;
		private final ContributorSequence contributorSequence;
		private String creditName;
		private String contributorEmail;
		private OrcidIdBuilder orcidId;

		public ContributorBuilder(ContributorRole contributorRole,
				ContributorSequence contributorSequence) {
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

		public ContributorBuilder setOrcidId(OrcidIdBuilder orcidId) {
			this.orcidId = orcidId;
			return this;
		}

		public Contributor build() {
			ContributorAttributes attributes = new ContributorAttributes();
			attributes.setContributorRole(contributorRole);
			attributes.setContributorSequence(contributorSequence);

			CreditName credit = new CreditName();
			credit.setValue(creditName);

			ContributorEmail email = new ContributorEmail();
			email.setValue(contributorEmail);

			Contributor contributor = new Contributor();
			contributor.setContributorAttributes(attributes);
			contributor.setCreditName(credit);
			contributor.setContributorEmail(email);
			contributor.setContributorOrcid(orcidId.build());
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
			citation.setCitationType(type);
			citation.setCitationValue(value);
			return citation;
		}
	}
}
