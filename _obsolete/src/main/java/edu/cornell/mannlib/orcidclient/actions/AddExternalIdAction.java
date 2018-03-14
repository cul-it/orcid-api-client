package edu.cornell.mannlib.orcidclient.actions;

import edu.cornell.mannlib.orcidclient.OrcidClientException;
import edu.cornell.mannlib.orcidclient.auth.AccessToken;
import edu.cornell.mannlib.orcidclient.beans.ExternalId;
import edu.cornell.mannlib.orcidclient.model.OrcidProfile;

public interface AddExternalIdAction {
    OrcidProfile execute(ExternalId externalId, AccessToken accessToken)
            throws OrcidClientException;
}
