package edu.cornell.mannlib.orcidclient.responses.message_2_0;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSetter;

@JsonIgnoreProperties(ignoreUnknown = true)
public class OrcidName {
    private OrcidDate createdDate;
    private OrcidDate lastModifiedDate;
    private OrcidString givenNames;
    private OrcidString familyName;
    private OrcidString creditName;
    // private OrcidSource source;
    private String visibility;
    private String path;

    @JsonGetter("created-date")
    public OrcidDate getCreatedDate() {
        return createdDate;
    }

    @JsonSetter("created-date")
    public void setCreatedDate(OrcidDate pDate) {
        createdDate = pDate;
    }

    @JsonGetter("last-modified-date")
    public OrcidDate getLastModifiedDate() {
        return lastModifiedDate;
    }

    @JsonSetter("last-modified-date")
    public void setLastModifiedDate(OrcidDate pDate) {
        lastModifiedDate = pDate;
    }

    @JsonGetter("given-names")
    public OrcidString getGivenNames() {
        return givenNames;
    }

    @JsonSetter("given-names")
    public void setGivenNames(OrcidString pNames) {
        givenNames = pNames;
    }

    @JsonGetter("family-name")
    public OrcidString getFamilyName() {
        return familyName;
    }

    @JsonSetter("family-name")
    public void setFamilyName(OrcidString pNames) {
        familyName = pNames;
    }

    @JsonGetter("credit-name")
    public OrcidString getCreditName() {
        return creditName;
    }

    @JsonSetter("credit-name")
    public void setCreditName(OrcidString pNames) {
        creditName = pNames;
    }

    @JsonGetter("visibility")
    public String getVisibility() {
        return visibility;
    }

    @JsonSetter("visibility")
    public void setVisibility(String visibility) {
        this.visibility = visibility;
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
