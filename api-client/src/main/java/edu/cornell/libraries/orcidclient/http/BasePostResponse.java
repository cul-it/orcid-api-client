/* $This file is distributed under the terms of the license in /doc/license.txt$ */

package edu.cornell.libraries.orcidclient.http;

import java.io.IOException;

import org.apache.http.client.fluent.Response;

import edu.cornell.libraries.orcidclient.http.HttpPostRequester.PostResponse;

/**
 * Use the Fluent library of HttpComponents to implement HttpPostRequester. 
 */
public class BasePostResponse implements PostResponse {
	private final Response response;

	public BasePostResponse(Response response) {
		this.response = response;
	}

	@Override
	public String getContentString() throws IOException {
		return response.returnContent().asString();
	}

}
