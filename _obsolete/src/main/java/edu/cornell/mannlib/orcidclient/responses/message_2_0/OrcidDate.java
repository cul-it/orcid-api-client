package edu.cornell.mannlib.orcidclient.responses.message_2_0;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSetter;

import java.util.Date;

@JsonIgnoreProperties(ignoreUnknown = true)
public class OrcidDate {
    private Date date;

    @JsonGetter("value")
    public long getValue() {
        return date.getTime();
    }

    @JsonSetter("value")
    public void setValue(long value) {
        date = new Date(value);
    }

    public Date getDate() {
        return date;
    }
}
