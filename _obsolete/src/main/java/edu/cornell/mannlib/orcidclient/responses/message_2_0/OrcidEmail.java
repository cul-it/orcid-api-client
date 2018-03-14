package edu.cornell.mannlib.orcidclient.responses.message_2_0;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSetter;

@JsonIgnoreProperties(ignoreUnknown = true)
public class OrcidEmail {
    private OrcidDate createdDate;
    private OrcidDate lastModifiedDate;
    // private OrcidSource source;
    private String email;
    private String path;
    private String visibility;
    private boolean verified;
    private boolean primary;
    private Long putCode;

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

    @JsonGetter("email")
    public String getEmail() {
        return email;
    }

    @JsonSetter("email")
    public void setEmail(String email) {
        this.email = email;
    }

    @JsonGetter("path")
    public String getPath() {
        return path;
    }

    @JsonSetter("path")
    public void setPath(String path) {
        this.path = path;
    }

    @JsonGetter("visibility")
    public String getVisibility() {
        return visibility;
    }

    @JsonSetter("visibility")
    public void setVisibility(String visibility) {
        this.visibility = visibility;
    }

    @JsonGetter("verified")
    public boolean isVerified() {
        return verified;
    }

    @JsonSetter("verified")
    public void setVerified(boolean verified) {
        this.verified = verified;
    }

    @JsonGetter("primary")
    public boolean isPrimary() {
        return primary;
    }

    @JsonSetter("primary")
    public void setPrimary(boolean primary) {
        this.primary = primary;
    }

    @JsonGetter("put-code")
    public Long getPutCode() {
        return putCode;
    }

    @JsonSetter("put-code")
    public void setPutCode(Long putCode) {
        this.putCode = putCode;
    }
}
