package edu.cornell.library.orcidclient.elements;

import org.orcid.jaxb.model.common_v2.Url;
import org.orcid.jaxb.model.record_v2.ExternalID;
import org.orcid.jaxb.model.record_v2.Relationship;

/**
 * A conversational tool for building an External ID.
 */
public class ExternalIdBuilder {
	private final Relationship relationship;
	private String type;
	private String url;
	private String value;

	public ExternalIdBuilder() {
		this(Relationship.SELF);
	}

	public ExternalIdBuilder(Relationship relationship) {
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

	public ExternalID build() {
		ExternalID id = new ExternalID();
		id.setRelationship(relationship);
		id.setType(type);
		id.setUrl(new Url(url));
		id.setValue(value);
		return id;
	}
}
