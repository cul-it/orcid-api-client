package edu.cornell.library.orcidclient.elements;

import org.orcid.jaxb.model.common_v2.Day;
import org.orcid.jaxb.model.common_v2.Month;
import org.orcid.jaxb.model.common_v2.PublicationDate;
import org.orcid.jaxb.model.common_v2.Year;

/**
 * A conversational tool for building a PublicationDate (refinement of
 * FuzzyDate)
 */
public class PublicationDateBuilder {
	private int[] yearMonthDay;

	public PublicationDateBuilder(int... yearMonthDay) {
		this.yearMonthDay = yearMonthDay;
	}

	public PublicationDate build() {
		PublicationDate pubDate = new PublicationDate();

		Year year = new Year();
		year.setValue(String.format("%04d", yearMonthDay[0]));
		pubDate.setYear(year);

		if (yearMonthDay.length > 1) {
			Month month = new Month();
			month.setValue(String.format("%02d", yearMonthDay[1]));
			pubDate.setMonth(month);
		}

		if (yearMonthDay.length > 2) {
			Day day = new Day();
			day.setValue(String.format("%02d", yearMonthDay[2]));
			pubDate.setDay(day);
		}

		return pubDate;
	}
}
