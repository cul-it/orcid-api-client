package edu.cornell.libraries.orcidclient.elements;

import edu.cornell.libraries.orcidclient.orcid_message_2_1.common.OrcidId;

/**
 * A conversational tool for building an Orcid ID
 */
public class OrcidIdBuilder {
	private OrcidId orcid;

	public OrcidIdBuilder(String path) {
		orcid = new OrcidId();
		orcid.setPath(path);
	}

	public OrcidId build() {
		return orcid;
	}
}
