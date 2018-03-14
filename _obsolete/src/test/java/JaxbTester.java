import edu.cornell.mannlib.orcidclient.OrcidClientException;
import edu.cornell.mannlib.orcidclient.context.OrcidClientContext;
import edu.cornell.mannlib.orcidclient.responses.message_1_2.OrcidMessage;


/* $This file is distributed under the terms of the license in /doc/license.txt$ */

/**
 * TODO
 */
public class JaxbTester {
	private static final String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n"
			+ "<orcid-message xmlns=\"http://www.orcid.org/ns/orcid\">\n"
			+ "    <message-version>1.0.23</message-version>\n"
			+ "    <orcid-profile type=\"user\">\n"
			+ "        <orcid>0000-0003-3479-6011</orcid>\n"
			+ "        <orcid-id>http://sandbox-1.orcid.org/0000-0003-3479-6011</orcid-id>\n"
			+ "        <orcid-preferences>\n"
			+ "            <locale>en</locale>\n"
			+ "        </orcid-preferences>\n"
			+ "        <orcid-history>\n"
			+ "            <creation-method>website</creation-method>\n"
			+ "            <submission-date>2014-01-06T18:14:32.859Z</submission-date>\n"
			+ "            <last-modified-date>2014-01-14T22:46:54.371Z</last-modified-date>\n"
			+ "            <claimed>true</claimed>\n"
			+ "        </orcid-history>\n"
			+ "        <orcid-bio>\n"
			+ "            <personal-details>\n"
			+ "                <given-names>Sandbox-client</given-names>\n"
			+ "                <family-name>Blake</family-name>\n"
			+ "                <other-names visibility=\"public\"/>\n"
			+ "            </personal-details>\n"
			+ "            <biography visibility=\"public\">A singer, a dancer, a middle-tier Java jockey</biography>\n"
			+ "            <researcher-urls visibility=\"public\"/>\n"
			+ "            <contact-details>\n"
			+ "                <address>\n"
			+ "                    <country visibility=\"limited\">US</country>\n"
			+ "                </address>\n"
			+ "            </contact-details>\n"
			+ "        </orcid-bio>\n"
			+ "        <orcid-activities/>\n"
			+ "    </orcid-profile>\n"
			+ "</orcid-message>\n";

	public JaxbTester() {
		try {
			OrcidMessage om = OrcidClientContext.getInstance().unmarshall(xml);
			System.out.println("Message is: " + om);
			System.out.println("Orcid ID = "+ om.getOrcidProfile().getOrcidIdentifier().getContent());
		} catch (OrcidClientException e) {
			e.getCause().printStackTrace();
		}
	}

	public static void main(String args[]) {
		JaxbTester jt = new JaxbTester();
	}
}