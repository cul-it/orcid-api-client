package edu.cornell.mannlib.orcidclient.responses.message_2_0;

import com.fasterxml.jackson.annotation.JsonProperty;

public class OrcidProfile {
    @JsonProperty("access_token")
    String accessToken;

    String name;
    String orcid;

    @JsonProperty("refresh_token")
    String refreshToken;
}
