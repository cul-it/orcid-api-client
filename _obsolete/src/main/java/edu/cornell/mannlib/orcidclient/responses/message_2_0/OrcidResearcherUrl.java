package edu.cornell.mannlib.orcidclient.responses.message_2_0;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSetter;

@JsonIgnoreProperties(ignoreUnknown = true)
public class OrcidResearcherUrl {
    private OrcidDate createdDate;
    private OrcidDate lastModifiedDate;
    // private OrcidSource source
    private String urlName;
    private OrcidString url;
    private String visibility;
    private String path;
    private Long putCode;
    private long displayIndex;

    @JsonGetter("created-date")
    public OrcidDate getCreatedDate() {
        return createdDate;
    }

    @JsonSetter("created-date")
    public void setCreatedDate(OrcidDate createdDate) {
        this.createdDate = createdDate;
    }

    @JsonGetter("last-modified-date")
    public OrcidDate getLastModifiedDate() {
        return lastModifiedDate;
    }

    @JsonSetter("last-modified-date")
    public void setLastModifiedDate(OrcidDate lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }

    @JsonGetter("url-name")
    public String getUrlName() {
        return urlName;
    }

    @JsonSetter("url-name")
    public void setUrlName(String urlName) {
        this.urlName = urlName;
    }

    @JsonGetter("url")
    public OrcidString getUrl() {
        return url;
    }

    @JsonSetter("url")
    public void setUrl(OrcidString url) {
        this.url = url;
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

    @JsonGetter("put-code")
    public Long getPutCode() {
        return putCode;
    }

    @JsonSetter("put-code")
    public void setPutCode(Long putCode) {
        this.putCode = putCode;
    }

    @JsonGetter("name")
    public long getDisplayIndex() {
        return displayIndex;
    }

    @JsonSetter("name")
    public void setDisplayIndex(long displayIndex) {
        this.displayIndex = displayIndex;
    }
}
