package edu.cornell.mannlib.orcidclient.responses.message_2_0;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSetter;

@JsonIgnoreProperties(ignoreUnknown = true)
public class OrcidString {
    private String value;
    private String visibility;

    @JsonGetter("value")
    public String getValue() {
        return value;
    }

    @JsonSetter("value")
    public void setValue(String pValue) {
        value = pValue;
    }

    @JsonIgnore
    public String getVisibility() {
        return visibility;
    }

    @JsonSetter("visibility")
    public void setVisibility(String pVisibility) {
        visibility = pVisibility;
    }
}
