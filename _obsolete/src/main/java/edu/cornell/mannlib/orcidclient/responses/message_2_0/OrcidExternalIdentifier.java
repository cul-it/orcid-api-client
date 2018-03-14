package edu.cornell.mannlib.orcidclient.responses.message_2_0;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSetter;

@JsonIgnoreProperties(ignoreUnknown = true)
public class OrcidExternalIdentifier {
    private OrcidDate createdDate;
    private OrcidDate lastModifiedDate;
    // private OrcidSource source;
    private String extCommonName;
    private String extReference;
    private OrcidString extUrl;
    private String extRelationship;
    private String visibility;
    private String path;
    private Long putCode;
    private long displayIndex;

    @JsonIgnore
    public OrcidDate getCreatedDate() {
        return createdDate;
    }

    @JsonSetter("created-date")
    public void setCreatedDate(OrcidDate createdDate) {
        this.createdDate = createdDate;
    }

    @JsonIgnore
    public OrcidDate getLastModifiedDate() {
        return lastModifiedDate;
    }

    @JsonSetter("last-modified-date")
    public void setLastModifiedDate(OrcidDate lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }

    @JsonGetter("external-id-type")
    public String getExtCommonName() {
        return extCommonName;
    }

    @JsonSetter("external-id-type")
    public void setExtCommonName(String extCommonName) {
        this.extCommonName = extCommonName;
    }

    @JsonGetter("external-id-value")
    public String getExtReference() {
        return extReference;
    }

    @JsonSetter("external-id-value")
    public void setExtReference(String extReference) {
        this.extReference = extReference;
    }

    @JsonGetter("external-id-url")
    public OrcidString getExtUrl() {
        return extUrl;
    }

    @JsonSetter("external-id-url")
    public void setExtUrl(OrcidString extUrl) {
        this.extUrl = extUrl;
    }

    @JsonGetter("external-id-relationship")
    public String getExtRelationship() {
        return extRelationship;
    }

    @JsonSetter("external-id-relationship")
    public void setExtRelationship(String extRelationship) {
        this.extRelationship = extRelationship;
    }

    @JsonIgnore
    public String getVisibility() {
        return visibility;
    }

    @JsonSetter("visibility")
    public void setVisibility(String visibility) {
        this.visibility = visibility;
    }

    @JsonIgnore
    public String getPath() {
        return path;
    }

    @JsonSetter("path")
    public void setPath(String path) {
        this.path = path;
    }

    @JsonIgnore
    public Long getPutCode() {
        return putCode;
    }

    @JsonSetter("put-code")
    public void setPutCode(Long putCode) {
        this.putCode = putCode;
    }

    @JsonIgnore
    public long getDisplayIndex() {
        return displayIndex;
    }

    @JsonSetter("display-index")
    public void setDisplayIndex(long displayIndex) {
        this.displayIndex = displayIndex;
    }
}
