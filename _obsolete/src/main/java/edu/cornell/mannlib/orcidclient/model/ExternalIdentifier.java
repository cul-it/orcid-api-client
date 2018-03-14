package edu.cornell.mannlib.orcidclient.model;

public class ExternalIdentifier {
    protected String externalIdUrl;
    protected String externalIdCommonName;
    protected String externalIdReference;

    /**
     * Gets the value of the externalIdUrl property.
     */
    public String getExternalIdUrl() {
        return externalIdUrl;
    }

    public String getExternalIdCommonName() { return externalIdCommonName; }

    public String getExternalIdReference() {
        return externalIdReference;
    }

    public void setExternalIdUrl(String url) { this.externalIdUrl = url; }

    public void setExternalIdCommonName(String name) { this.externalIdCommonName = name; }

    public void setExternalIdReference(String ref) { this.externalIdReference = ref; }
}
