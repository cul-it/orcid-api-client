package edu.cornell.mannlib.orcidclient.model;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.cornell.mannlib.orcidclient.responses.message_2_0.OrcidBiography;
import edu.cornell.mannlib.orcidclient.responses.message_2_0.OrcidPerson;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.util.Date;

public class JsonModelTest {
    private final static String biographyJson = "{\n" +
            "  \"created-date\": {\n" +
            "    \"value\": 1502820654775\n" +
            "  },\n" +
            "  \"last-modified-date\": {\n" +
            "    \"value\": 1502820654775\n" +
            "  },\n" +
            "  \"content\": \"Testing biography for VIVO integrations.\",\n" +
            "  \"visibility\": \"PUBLIC\",\n" +
            "  \"path\": \"/0000-0002-0841-2240/biography\"\n" +
            "}";


    private final static String externalIdJson = "";

    private final static String personJson = "{\n" +
            "  \"last-modified-date\": {\n" +
            "    \"value\": 1502895485272\n" +
            "  },\n" +
            "  \"name\": {\n" +
            "    \"created-date\": {\n" +
            "      \"value\": 1502805285731\n" +
            "    },\n" +
            "    \"last-modified-date\": {\n" +
            "      \"value\": 1502896474476\n" +
            "    },\n" +
            "    \"given-names\": {\n" +
            "      \"value\": \"Graham X\"\n" +
            "    },\n" +
            "    \"family-name\": {\n" +
            "      \"value\": \"Triggs\"\n" +
            "    },\n" +
            "    \"credit-name\": {\n" +
            "      \"value\": \"GX Triggs\"\n" +
            "    },\n" +
            "    \"source\": null,\n" +
            "    \"visibility\": \"PUBLIC\",\n" +
            "    \"path\": \"0000-0002-0841-2240\"\n" +
            "  },\n" +
            "  \"other-names\": {\n" +
            "    \"last-modified-date\": null,\n" +
            "    \"other-name\": [\n" +
            "      {\n" +
            "        \"created-date\": {\n" +
            "          \"value\": 1502913463163\n" +
            "        },\n" +
            "        \"last-modified-date\": {\n" +
            "          \"value\": 1502913463164\n" +
            "        },\n" +
            "        \"source\": {\n" +
            "          \"source-orcid\": {\n" +
            "            \"uri\": \"http://sandbox.orcid.org/0000-0002-0841-2240\",\n" +
            "            \"path\": \"0000-0002-0841-2240\",\n" +
            "            \"host\": \"sandbox.orcid.org\"\n" +
            "          },\n" +
            "          \"source-client-id\": null,\n" +
            "          \"source-name\": {\n" +
            "            \"value\": \"GX Triggs\"\n" +
            "          }\n" +
            "        },\n" +
            "        \"content\": \"G Triggs\",\n" +
            "        \"visibility\": \"PUBLIC\",\n" +
            "        \"path\": \"/0000-0002-0841-2240/other-names/16420\",\n" +
            "        \"put-code\": 16420,\n" +
            "        \"display-index\": 2\n" +
            "      },\n" +
            "      {\n" +
            "        \"created-date\": {\n" +
            "          \"value\": 1502913463169\n" +
            "        },\n" +
            "        \"last-modified-date\": {\n" +
            "          \"value\": 1502913463169\n" +
            "        },\n" +
            "        \"source\": {\n" +
            "          \"source-orcid\": {\n" +
            "            \"uri\": \"http://sandbox.orcid.org/0000-0002-0841-2240\",\n" +
            "            \"path\": \"0000-0002-0841-2240\",\n" +
            "            \"host\": \"sandbox.orcid.org\"\n" +
            "          },\n" +
            "          \"source-client-id\": null,\n" +
            "          \"source-name\": {\n" +
            "            \"value\": \"GX Triggs\"\n" +
            "          }\n" +
            "        },\n" +
            "        \"content\": \"Trigger\",\n" +
            "        \"visibility\": \"PUBLIC\",\n" +
            "        \"path\": \"/0000-0002-0841-2240/other-names/16421\",\n" +
            "        \"put-code\": 16421,\n" +
            "        \"display-index\": 1\n" +
            "      }\n" +
            "    ],\n" +
            "    \"path\": \"/0000-0002-0841-2240/other-names\"\n" +
            "  },\n" +
            "  \"biography\": {\n" +
            "    \"created-date\": {\n" +
            "      \"value\": 1502820654775\n" +
            "    },\n" +
            "    \"last-modified-date\": {\n" +
            "      \"value\": 1502820654775\n" +
            "    },\n" +
            "    \"content\": \"Testing biography for VIVO integrations.\",\n" +
            "    \"visibility\": \"PUBLIC\",\n" +
            "    \"path\": \"/0000-0002-0841-2240/biography\"\n" +
            "  },\n" +
            "  \"researcher-urls\": {\n" +
            "    \"last-modified-date\": {\n" +
            "      \"value\": 1502895485272\n" +
            "    },\n" +
            "    \"researcher-url\": [\n" +
            "      {\n" +
            "        \"created-date\": {\n" +
            "          \"value\": 1502895485270\n" +
            "        },\n" +
            "        \"last-modified-date\": {\n" +
            "          \"value\": 1502895485271\n" +
            "        },\n" +
            "        \"source\": {\n" +
            "          \"source-orcid\": {\n" +
            "            \"uri\": \"http://sandbox.orcid.org/0000-0002-0841-2240\",\n" +
            "            \"path\": \"0000-0002-0841-2240\",\n" +
            "            \"host\": \"sandbox.orcid.org\"\n" +
            "          },\n" +
            "          \"source-client-id\": null,\n" +
            "          \"source-name\": {\n" +
            "            \"value\": \"GX Triggs\"\n" +
            "          }\n" +
            "        },\n" +
            "        \"url-name\": \"VIVO\",\n" +
            "        \"url\": {\n" +
            "          \"value\": \"http://vivoweb.org\"\n" +
            "        },\n" +
            "        \"visibility\": \"PUBLIC\",\n" +
            "        \"path\": \"/0000-0002-0841-2240/researcher-urls/42924\",\n" +
            "        \"put-code\": 42924,\n" +
            "        \"display-index\": 2\n" +
            "      },\n" +
            "      {\n" +
            "        \"created-date\": {\n" +
            "          \"value\": 1502895485272\n" +
            "        },\n" +
            "        \"last-modified-date\": {\n" +
            "          \"value\": 1502895485272\n" +
            "        },\n" +
            "        \"source\": {\n" +
            "          \"source-orcid\": {\n" +
            "            \"uri\": \"http://sandbox.orcid.org/0000-0002-0841-2240\",\n" +
            "            \"path\": \"0000-0002-0841-2240\",\n" +
            "            \"host\": \"sandbox.orcid.org\"\n" +
            "          },\n" +
            "          \"source-client-id\": null,\n" +
            "          \"source-name\": {\n" +
            "            \"value\": \"GX Triggs\"\n" +
            "          }\n" +
            "        },\n" +
            "        \"url-name\": \"DuraSpace\",\n" +
            "        \"url\": {\n" +
            "          \"value\": \"http://duraspace.org\"\n" +
            "        },\n" +
            "        \"visibility\": \"PUBLIC\",\n" +
            "        \"path\": \"/0000-0002-0841-2240/researcher-urls/42925\",\n" +
            "        \"put-code\": 42925,\n" +
            "        \"display-index\": 1\n" +
            "      }\n" +
            "    ],\n" +
            "    \"path\": \"/0000-0002-0841-2240/researcher-urls\"\n" +
            "  },\n" +
            "  \"emails\": {\n" +
            "    \"last-modified-date\": {\n" +
            "      \"value\": 1502820621653\n" +
            "    },\n" +
            "    \"email\": [\n" +
            "      {\n" +
            "        \"created-date\": {\n" +
            "          \"value\": 1502820606429\n" +
            "        },\n" +
            "        \"last-modified-date\": {\n" +
            "          \"value\": 1502820621653\n" +
            "        },\n" +
            "        \"source\": {\n" +
            "          \"source-orcid\": {\n" +
            "            \"uri\": \"http://sandbox.orcid.org/0000-0002-0841-2240\",\n" +
            "            \"path\": \"0000-0002-0841-2240\",\n" +
            "            \"host\": \"sandbox.orcid.org\"\n" +
            "          },\n" +
            "          \"source-client-id\": null,\n" +
            "          \"source-name\": {\n" +
            "            \"value\": \"GX Triggs\"\n" +
            "          }\n" +
            "        },\n" +
            "        \"email\": \"grahamtriggs@mailinator.com\",\n" +
            "        \"path\": null,\n" +
            "        \"visibility\": \"PUBLIC\",\n" +
            "        \"verified\": true,\n" +
            "        \"primary\": true,\n" +
            "        \"put-code\": null\n" +
            "      }\n" +
            "    ],\n" +
            "    \"path\": \"/0000-0002-0841-2240/email\"\n" +
            "  },\n" +
            "  \"addresses\": {\n" +
            "    \"last-modified-date\": {\n" +
            "      \"value\": 1502895448534\n" +
            "    },\n" +
            "    \"address\": [\n" +
            "      {\n" +
            "        \"created-date\": {\n" +
            "          \"value\": 1502895448531\n" +
            "        },\n" +
            "        \"last-modified-date\": {\n" +
            "          \"value\": 1502895448534\n" +
            "        },\n" +
            "        \"source\": {\n" +
            "          \"source-orcid\": {\n" +
            "            \"uri\": \"http://sandbox.orcid.org/0000-0002-0841-2240\",\n" +
            "            \"path\": \"0000-0002-0841-2240\",\n" +
            "            \"host\": \"sandbox.orcid.org\"\n" +
            "          },\n" +
            "          \"source-client-id\": null,\n" +
            "          \"source-name\": {\n" +
            "            \"value\": \"GX Triggs\"\n" +
            "          }\n" +
            "        },\n" +
            "        \"country\": {\n" +
            "          \"value\": \"GB\"\n" +
            "        },\n" +
            "        \"visibility\": \"PUBLIC\",\n" +
            "        \"path\": \"/0000-0002-0841-2240/address/4831\",\n" +
            "        \"put-code\": 4831,\n" +
            "        \"display-index\": 1\n" +
            "      }\n" +
            "    ],\n" +
            "    \"path\": \"/0000-0002-0841-2240/address\"\n" +
            "  },\n" +
            "  \"keywords\": {\n" +
            "    \"last-modified-date\": {\n" +
            "      \"value\": 1502895457682\n" +
            "    },\n" +
            "    \"keyword\": [\n" +
            "      {\n" +
            "        \"created-date\": {\n" +
            "          \"value\": 1502895457682\n" +
            "        },\n" +
            "        \"last-modified-date\": {\n" +
            "          \"value\": 1502895457682\n" +
            "        },\n" +
            "        \"source\": {\n" +
            "          \"source-orcid\": {\n" +
            "            \"uri\": \"http://sandbox.orcid.org/0000-0002-0841-2240\",\n" +
            "            \"path\": \"0000-0002-0841-2240\",\n" +
            "            \"host\": \"sandbox.orcid.org\"\n" +
            "          },\n" +
            "          \"source-client-id\": null,\n" +
            "          \"source-name\": {\n" +
            "            \"value\": \"GX Triggs\"\n" +
            "          }\n" +
            "        },\n" +
            "        \"content\": \"developer\",\n" +
            "        \"visibility\": \"PUBLIC\",\n" +
            "        \"path\": \"/0000-0002-0841-2240/keywords/5106\",\n" +
            "        \"put-code\": 5106,\n" +
            "        \"display-index\": 1\n" +
            "      }\n" +
            "    ],\n" +
            "    \"path\": \"/0000-0002-0841-2240/keywords\"\n" +
            "  },\n" +
            "  \"external-identifiers\": {\n" +
            "    \"last-modified-date\": null,\n" +
            "    \"external-identifier\": [],\n" +
            "    \"path\": \"/0000-0002-0841-2240/external-identifiers\"\n" +
            "  },\n" +
            "  \"path\": \"/0000-0002-0841-2240/person\"\n" +
            "}";

    @Test
    public void test() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
/*
        OrcidBiography biography = mapper.readValue(biographyJson, OrcidBiography.class);

        Assert.assertTrue("Validate created date", new Date(1502820654775L).equals(biography.getCreatedDate().getDate()));
        Assert.assertTrue("Validate last modified date", new Date(1502820654775L).equals(biography.getLastModifiedDate().getDate()));
        Assert.assertTrue("Validate content", "Testing biography for VIVO integrations.".equals(biography.getContent()));
        Assert.assertTrue("Validate visibility", "PUBLIC".equals(biography.getVisibility()));
        Assert.assertTrue("Validate path", "/0000-0002-0841-2240/biography".equals(biography.getPath()));
*/

        OrcidPerson person = mapper.readValue(personJson, OrcidPerson.class);
//        Assert.assertTrue("Validate last modified date", new Date(1502820654775L).equals(person.getLastModifiedDate().getDate()));

    }
}
