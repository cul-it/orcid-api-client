package edu.cornell.library.orcidclient.elements;

import edu.cornell.library.orcidclient.orcid_message_2_1.common.ExternalId;
import edu.cornell.library.orcidclient.orcid_message_2_1.common.RelationshipType;

/**
 * A conversational tool for building an External ID.
 */
public class ExternalIdBuilder {
	private final RelationshipType relationship;
	private String type;
	private String url;
	private String value;

	public ExternalIdBuilder() {
		this(RelationshipType.SELF);
	}

	public ExternalIdBuilder(RelationshipType relationship) {
		this.relationship = relationship;
	}

	public ExternalIdBuilder setType(String type) {
		this.type = type;
		return this;
	}

	public ExternalIdBuilder setUrl(String url) {
		this.url = url;
		return this;
	}

	public ExternalIdBuilder setValue(String value) {
		this.value = value;
		return this;
	}

	public ExternalId build() {
		ExternalId id = new ExternalId();
		id.setExternalIdRelationship(relationship);
		id.setExternalIdType(type);
		id.setExternalIdUrl(url);
		id.setExternalIdValue(value);
		return id;
	}
}
