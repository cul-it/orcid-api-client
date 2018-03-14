package edu.cornell.mannlib.orcidclient.actions;

import edu.cornell.mannlib.orcidclient.OrcidClientException;
import edu.cornell.mannlib.orcidclient.model.OrcidProfile;

public interface ReadPublicBioAction {
    OrcidProfile execute(String orcid) throws OrcidClientException;
}
