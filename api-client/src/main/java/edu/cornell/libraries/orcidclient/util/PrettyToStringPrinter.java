package edu.cornell.libraries.orcidclient.util;

import static edu.cornell.libraries.orcidclient.util.PrettyToStringPrinter.Scan.ScanState.IN_BRACES;
import static edu.cornell.libraries.orcidclient.util.PrettyToStringPrinter.Scan.ScanState.IN_BRACKETS;
import static edu.cornell.libraries.orcidclient.util.PrettyToStringPrinter.Scan.ScanState.OPEN;

import java.util.ArrayList;
import java.util.List;

/**
 * Take a hierarchical string, in a format that is customary for toString()
 * output, and insert line-feeds and indentations.
 */
public class PrettyToStringPrinter {
	public String format(Object o) {
		return format(0, String.valueOf(o));
	}

	public String format(String raw) {
		return format(0, raw);
	}
	
	private String format(int indent, String raw) {
		Scan scan = new Scan(raw);
		String formatted;
		if (scan.hasExposedCommas()) {
			formatted = splitByCommas(indent, raw, scan.getCommasAt());
		} else if (scan.hasExposedDelimiterPairs()) {
			formatted = splitByDelimiters(indent, raw,
					scan.getDelimiterPairs());
		} else {
			formatted = padding(indent) + raw;
		}
		return formatted;
	}

	private String splitByCommas(int indent, String raw,
			List<Integer> commasAt) {
		List<String> fragments = new ArrayList<>();
		int fromHere = 0;
		for (int commaAt : commasAt) {
			fragments.add(raw.substring(fromHere, commaAt).trim());
			fromHere = commaAt + 1;
		}
		fragments.add(raw.substring(fromHere).trim());

		String buffer = new String();
		for (int i = 0; i < fragments.size(); i++) {
			if (i > 0) {
				buffer += ",\n";
			}
			buffer += format(indent, fragments.get(i));
		}
		return buffer;
	}

	private String splitByDelimiters(int indent, String raw,
			List<DelimiterPair> delimiterPairs) {
		String buffer = "";
		String previousCloser = "";
		int soFar = 0;
		for (DelimiterPair pair : delimiterPairs) {
			String before = raw.substring(soFar, pair.openAt);
			String within = raw.substring(pair.openAt + pair.open.length(),
					pair.closeAt);
			buffer += format(indent, previousCloser + before + pair.open)
					+ '\n';
			buffer += format(indent + 1, within) + '\n';
			previousCloser = pair.close;
			soFar = pair.closeAt + pair.close.length();
		}
		String after = raw.substring(soFar);
		return buffer += format(indent, previousCloser + after);
	}

	private String padding(int indent) {
		if (indent == 0) {
			return "";
		} else {
			return String.format("%1$" + indent * 2 + "s", " ");
		}
	}

	static class Scan {
		private List<DelimiterPair> delimiterPairs = new ArrayList<>();
		private List<Integer> commasAt = new ArrayList<>();

		enum ScanState {
			OPEN, IN_BRACKETS, IN_BRACES
		}

		public Scan(String raw) {
			ScanState scanState = OPEN;
			int nestingLevel = 0;
			int openAt = -1;

			for (int at = 0; at < raw.length(); at++) {
				char thisChar = raw.charAt(at);
				switch (scanState) {
				case OPEN:
					switch (thisChar) {
					case ',':
						commasAt.add(at);
						break;
					case '[':
						openAt = at;
						nestingLevel += 1;
						scanState = IN_BRACKETS;
						break;
					case '{':
						openAt = at;
						nestingLevel += 1;
						scanState = IN_BRACES;
						break;
					default: // any other character
						break;
					}
					break;
				case IN_BRACKETS:
					switch (thisChar) {
					case '[':
						nestingLevel += 1;
						break;
					case ']':
						nestingLevel -= 1;
						if (nestingLevel == 0) {
							delimiterPairs.add(
									new DelimiterPair(openAt, "[", at, "]"));
							scanState = OPEN;
						}
						break;
					default: // any other character
						break;
					}
					break;
				default: // IN_BRACES
					switch (thisChar) {
					case '{':
						nestingLevel += 1;
						break;
					case '}':
						nestingLevel -= 1;
						if (nestingLevel == 0) {
							delimiterPairs.add(
									new DelimiterPair(openAt, "{", at, "}"));
							scanState = OPEN;
						}
						break;
					default: // any other character
						break;
					}
					break;
				}
			}
		}

		public boolean hasExposedCommas() {
			return !commasAt.isEmpty();
		}

		public boolean hasExposedDelimiterPairs() {
			return commasAt.isEmpty() && !delimiterPairs.isEmpty();
		}

		public List<Integer> getCommasAt() {
			return commasAt;
		}

		public List<DelimiterPair> getDelimiterPairs() {
			return delimiterPairs;
		}

		@Override
		public String toString() {
			return String.format("Scan[delimiterPairs=%s, commasAt=%s]",
					delimiterPairs, commasAt);
		}

	}

	private static class DelimiterPair {
		final int openAt;
		final String open;
		final int closeAt;
		final String close;

		public DelimiterPair(int openAt, String open, int closeAt,
				String close) {
			this.openAt = openAt;
			this.open = open;
			this.closeAt = closeAt;
			this.close = close;
		}

		@Override
		public String toString() {
			return String.format(
					"DelimiterPair[openAt=%s, '%s', closeAt=%s, '%s']", openAt,
					open, closeAt, close);
		}

	}
}
