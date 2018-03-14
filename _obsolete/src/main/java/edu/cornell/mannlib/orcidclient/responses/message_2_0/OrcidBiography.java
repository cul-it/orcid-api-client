package edu.cornell.mannlib.orcidclient.responses.message_2_0;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;

import java.util.Date;

@JsonIgnoreProperties(ignoreUnknown = true)
public class OrcidBiography {
    private OrcidDate createdDate;
    private OrcidDate lastModifiedDate;
    private String content;
    private String visibility;
    private String path;

    @JsonIgnore
    public OrcidDate getCreatedDate() {
        return createdDate;
    }

    @JsonSetter("created-date")
    public void setCreatedDate(OrcidDate pDate) {
        createdDate = pDate;
    }

    @JsonIgnore
    public OrcidDate getLastModifiedDate() {
        return lastModifiedDate;
    }

    @JsonSetter("last-modified-date")
    public void setLastModifiedDate(OrcidDate pDate) {
        lastModifiedDate = pDate;
    }

    @JsonGetter("content")
    public String getContent() {
        return content;
    }

    @JsonSetter("content")
    public void setContent(String pContent) {
        content = pContent;
    }

    @JsonGetter("visibility")
    public String getVisibility() {
        return visibility;
    }

    @JsonGetter("visibility")
    public void setVisibility(String pVisibility) {
        visibility = pVisibility;
    }

    @JsonIgnore
    public String getPath() {
        return path;
    }

    @JsonSetter("path")
    public void setPath(String pPath) {
        path = pPath;
    }
}
