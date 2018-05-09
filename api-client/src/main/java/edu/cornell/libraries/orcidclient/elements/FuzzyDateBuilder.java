package edu.cornell.libraries.orcidclient.elements;

import edu.cornell.libraries.orcidclient.orcid_message_2_1.common.FuzzyDate;
import edu.cornell.libraries.orcidclient.orcid_message_2_1.common.FuzzyDate.Day;
import edu.cornell.libraries.orcidclient.orcid_message_2_1.common.FuzzyDate.Month;
import edu.cornell.libraries.orcidclient.orcid_message_2_1.common.FuzzyDate.Year;

/**
 * A conversational tool for building a FuzzyDate
 */
public class FuzzyDateBuilder {
	private int[] yearMonthDay;

	public FuzzyDateBuilder(int... yearMonthDay) {
		this.yearMonthDay = yearMonthDay;
	}

	public FuzzyDate build() {
		FuzzyDate fuzzy = new FuzzyDate();

		Year year = new Year();
		year.setValue(String.format("%04d", yearMonthDay[0]));
		fuzzy.setYear(year);

		if (yearMonthDay.length > 1) {
			Month month = new Month();
			month.setValue(String.format("%02d", yearMonthDay[1]));
			fuzzy.setMonth(month);
		}

		if (yearMonthDay.length > 2) {
			Day day = new Day();
			day.setValue(String.format("%02d", yearMonthDay[2]));
			fuzzy.setDay(day);
		}

		return fuzzy;
	}
}
