package edu.cornell.mannlib.orcidclient.responses.message_2_0;

import com.fasterxml.jackson.annotation.JsonProperty;

public class OrcidToken {
    @JsonProperty("error-desc")
    String errorDesc;

    @JsonProperty("message-version")
    String messageVersion;

    @JsonProperty("orcid-profile")
    OrcidProfile orcidProfile;

    private class OrcidProfile {
        @JsonProperty("orcid-activities")
        OrcidActivities orcidActivities;

        @JsonProperty("orcid-bio")
        OrcidBio orcidBio;

        @JsonProperty("orcid-history")
        OrcidHistory orcidHistory;

        @JsonProperty("orcid-identifier")
        OrcidIdentifier orcidIdentifier;

        private class OrcidActivities {
            @JsonProperty("orcid-works")
            OrcidWorks orcidWorks;

            private class OrcidWorks {
                @JsonProperty("orcid-work")
                OrcidWork[] orcidWork;

                private class OrcidWork {
                    @JsonProperty("work-external-identifiers")
                    WorkExternalIdentifiers workExternalIdentifiers;

                    private class WorkExternalIdentifiers {
                        @JsonProperty("work-external-identifier")
                        WorkExternalIdentifier[] workExternalIdentifier;

                        private class WorkExternalIdentifier {
                            @JsonProperty("work-external-identifier-id")
                            OrcidString workExternalIdentifierId;

                            @JsonProperty("work-external-identifier-type")
                            String workExternalIdentifierType;
                        }
                    }
                }
            }
        }

        private class OrcidBio {
            OrcidString biography;

            @JsonProperty("contact-details")
            ContactDetails contactDetails;

            Keywords keywords;

            @JsonProperty("personal-details")
            PersonalDetails personalDetails;

            @JsonProperty("researcher-urls")
            ResearcherUrls researcherUrls;

            private class ContactDetails {
                Address address;
                Email[] email;

                private class Address {
                    OrcidString country;
                }

                private class Email {
                    boolean primary;
                    String value;
                    String visibility;
                }
            }

            private class Keywords {
                OrcidString[] keyword;
                String visibility;
            }

            private class PersonalDetails {
                @JsonProperty("family-name")
                OrcidString familyName;

                @JsonProperty("given-names")
                OrcidString givenNames;
            }

            private class ResearcherUrls {
                @JsonProperty("researcher-url")
                ResearcherUrl[] researcherUrl;
                String visibility;

                private class ResearcherUrl {
                    OrcidString url;

                    @JsonProperty("url-name")
                    OrcidString urlName;
                }
            }
        }

        private class OrcidHistory {
            @JsonProperty("last-modified-date")
            DateValue lastModifiedDate;

            private class DateValue {
                long value;
            }
        }

        private class OrcidIdentifier {
            String host;
            String path;
            String uri;
            String value;
        }
    }
}
