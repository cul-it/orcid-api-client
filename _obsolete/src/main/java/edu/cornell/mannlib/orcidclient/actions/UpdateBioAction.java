package edu.cornell.mannlib.orcidclient.actions;

import edu.cornell.mannlib.orcidclient.OrcidClientException;
import edu.cornell.mannlib.orcidclient.auth.AccessToken;
import edu.cornell.mannlib.orcidclient.model.OrcidProfile;
import edu.cornell.mannlib.orcidclient.responses.message_1_2.OrcidMessage;

public interface UpdateBioAction {
    OrcidProfile execute(OrcidMessage profile, AccessToken accessToken)
            throws OrcidClientException;
}
