package edu.cornell.mannlib.orcidclient.responses.message_2_0;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSetter;

@JsonIgnoreProperties(ignoreUnknown = true)
public class OrcidExternalIdentifiers {
    private OrcidDate lastModifiedDate;
    private OrcidExternalIdentifier[] externalIdentifiers;
    private String path;

    @JsonGetter("last-modified-date")
    public OrcidDate getLastModifiedDate() {
        return lastModifiedDate;
    }

    @JsonSetter("last-modified-date")
    public void setLastModifiedDate(OrcidDate lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }

    @JsonGetter("external-identifier")
    public OrcidExternalIdentifier[] getExternalIdentifiers() {
        return externalIdentifiers;
    }

    @JsonSetter("external-identifier")
    public void setExternalIdentifiers(OrcidExternalIdentifier[] externalIdentifiers) {
        this.externalIdentifiers = externalIdentifiers;
    }

    @JsonGetter("path")
    public String getPath() {
        return path;
    }

    @JsonSetter("path")
    public void setPath(String path) {
        this.path = path;
    }
}
