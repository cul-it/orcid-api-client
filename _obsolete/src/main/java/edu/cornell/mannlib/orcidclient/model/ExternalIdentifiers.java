package edu.cornell.mannlib.orcidclient.model;

import java.util.List;

public class ExternalIdentifiers {
    protected List<ExternalIdentifier> externalIdentifier;

    public List<ExternalIdentifier> getExternalIdentifier() { return externalIdentifier; }

    public void setExternalIdentifier(List<ExternalIdentifier> ids) { this.externalIdentifier = ids; }
}
