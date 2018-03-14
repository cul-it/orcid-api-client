package edu.cornell.mannlib.orcidclient.responses.message_2_0;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSetter;

@JsonIgnoreProperties(ignoreUnknown = true)
public class OrcidOtherName {
    private OrcidDate createdDate;
    private OrcidDate lastModifiedDate;
//    private OrcidSource soruce;
    private String content;
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

    @JsonGetter("content")
    public String getContent() {
        return content;
    }

    @JsonSetter("content")
    public void setContent(String content) {
        this.content = content;
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

    @JsonGetter("display-index")
    public long getDisplayIndex() {
        return displayIndex;
    }

    @JsonSetter("display-index")
    public void setDisplayIndex(long displayIndex) {
        this.displayIndex = displayIndex;
    }
}
