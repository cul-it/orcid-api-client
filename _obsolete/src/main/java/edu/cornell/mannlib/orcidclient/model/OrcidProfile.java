package edu.cornell.mannlib.orcidclient.model;

public class OrcidProfile {
    protected OrcidBio orcidBio;
    protected OrcidId orcidIdentifier;

    public OrcidBio getOrcidBio() { return orcidBio; }

    public OrcidId getOrcidIdentifier() { return orcidIdentifier; }

    public void setOrcidBio(OrcidBio bio) { this.orcidBio = bio; }

    public void setOrcidIdentifier(OrcidId id) { this.orcidIdentifier = id; }
}
