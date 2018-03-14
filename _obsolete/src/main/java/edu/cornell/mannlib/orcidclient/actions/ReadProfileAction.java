package edu.cornell.mannlib.orcidclient.actions;

import edu.cornell.mannlib.orcidclient.OrcidClientException;
import edu.cornell.mannlib.orcidclient.auth.AccessToken;
import edu.cornell.mannlib.orcidclient.model.OrcidProfile;

public interface ReadProfileAction {
    OrcidProfile execute(AccessToken accessToken)
            throws OrcidClientException;
}
