package edu.cornell.mannlib.orcidclient.responses.message_2_0;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSetter;

@JsonIgnoreProperties(ignoreUnknown = true)
public class OrcidPerson {
    private OrcidDate lastModifiedDate;
    private OrcidName name;
    private OrcidOtherNames otherNames;
    private OrcidBiography biography;
    private OrcidResearcherUrls researcherUrls;
    private OrcidEmails emails;
    private OrcidAddresses addresses;
    private OrcidKeywords keywords;
    private OrcidExternalIdentifiers externalIdentifiers;

    @JsonGetter("last-modified-date")
    public OrcidDate getLastModifiedDate() {
        return lastModifiedDate;
    }

    @JsonSetter("last-modified-date")
    public void setLastModifiedDate(OrcidDate pDate) {
        lastModifiedDate = pDate;
    }

    @JsonGetter("name")
    public OrcidName getName() {
        return name;
    }

    @JsonSetter("name")
    public void setName(OrcidName pName) {
        name = pName;
    }

    @JsonGetter("other-names")
    public OrcidOtherNames getOtherNames() {
        return otherNames;
    }

    @JsonSetter("other-names")
    public void setOtherNames(OrcidOtherNames pNames) {
        otherNames = pNames;
    }

    @JsonGetter("biography")
    public OrcidBiography getBiography() {
        return biography;
    }

    @JsonSetter("biography")
    public void setBiography(OrcidBiography pBiography) {
        biography = pBiography;
    }

    @JsonGetter("researcher-urls")
    public OrcidResearcherUrls getResearcherUrls() {
        return researcherUrls;
    }

    @JsonSetter("researcher-urls")
    public void setResearcherUrls(OrcidResearcherUrls pResearcherUrls) {
        researcherUrls = pResearcherUrls;
    }

    @JsonGetter("emails")
    public OrcidEmails getEmails() { return emails; }

    @JsonSetter("emails")
    public void setEmails(OrcidEmails emails) { this.emails = emails; }

    @JsonGetter("addresses")
    public OrcidAddresses getAddresses() { return addresses; }

    @JsonSetter("addresses")
    public void setAddresses(OrcidAddresses addresses) { this.addresses = addresses; }

    @JsonGetter("keywords")
    public OrcidKeywords getKeywords() { return keywords; }

    @JsonSetter("keywords")
    public void setKeywords(OrcidKeywords keywords) { this.keywords = keywords; }

    @JsonGetter("external-identifiers")
    public OrcidExternalIdentifiers getExternalIdentifiers() { return externalIdentifiers; }

    @JsonSetter("external-identifiers")
    public void setExternalIdentifiers(OrcidExternalIdentifiers externalIdentifiers) { this.externalIdentifiers = externalIdentifiers; }
}
