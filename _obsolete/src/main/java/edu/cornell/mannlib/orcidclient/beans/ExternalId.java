/* $This file is distributed under the terms of the license in /doc/license.txt$ */

package edu.cornell.mannlib.orcidclient.beans;

/**
 * Information required to add an External ID to the ORCID Profile.
 */
public class ExternalId {
	private String commonName;
	private String reference;
	private String url;
	private Visibility visibility;

	public String getCommonName() {
		return commonName;
	}

	public ExternalId setCommonName(String commonName) {
		this.commonName = commonName;
		return this;
	}

	public String getReference() {
		return reference;
	}

	public ExternalId setReference(String reference) {
		this.reference = reference;
		return this;
	}

	public String getUrl() {
		return url;
	}

	public ExternalId setUrl(String url) {
		this.url = url;
		return this;
	}

	public Visibility getVisibility() {
		return visibility;
	}

	public ExternalId setVisibility(Visibility visibility) {
		this.visibility = visibility;
		return this;
	}

}
