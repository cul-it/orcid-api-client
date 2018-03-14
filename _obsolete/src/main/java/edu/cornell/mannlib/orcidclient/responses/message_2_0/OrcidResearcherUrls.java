package edu.cornell.mannlib.orcidclient.responses.message_2_0;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSetter;

@JsonIgnoreProperties(ignoreUnknown = true)
public class OrcidResearcherUrls {
    private OrcidDate lastModifiedDate;
    private OrcidResearcherUrl[] researcherUrls;
    private String path;

    @JsonGetter("last-modified-date")
    public OrcidDate getLastModifiedDate() {
        return lastModifiedDate;
    }

    @JsonSetter("last-modified-date")
    public void setLastModifiedDate(OrcidDate lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }

    @JsonGetter("researcher-url")
    public OrcidResearcherUrl[] getResearcherUrls() {
        return researcherUrls;
    }

    @JsonSetter("researcher-url")
    public void setResearcherUrls(OrcidResearcherUrl[] researcherUrls) {
        this.researcherUrls = researcherUrls;
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
