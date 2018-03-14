package edu.cornell.mannlib.orcidclient.model;

import java.util.List;

public class OrcidBio {
//    protected ExternalIdentifiers externalIdentifiers;

//    public ExternalIdentifiers getExternalIdentifiers() { return externalIdentifiers; }

//    public void setExternalIdentifiers(ExternalIdentifiers ids) { this.externalIdentifiers = ids; }

    protected List<ExternalIdentifier> externalIdentifiers;

    public List<ExternalIdentifier> getExternalIdentifiers() { return externalIdentifiers; }

    public void setExternalIdentifiers(List<ExternalIdentifier> ids) { this.externalIdentifiers = ids; }
}
