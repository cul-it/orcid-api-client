/* $This file is distributed under the terms of the license in /doc/license.txt$ */

package edu.cornell.libraries.orcidclient.http;

import java.io.IOException;

import org.apache.http.client.HttpResponseException;
import org.apache.http.client.fluent.Response;

import edu.cornell.libraries.orcidclient.http.HttpWrapper.HttpResponse;
import edu.cornell.libraries.orcidclient.http.HttpWrapper.HttpStatusCodeException;

/**
 * Use the Fluent library of HttpComponents to implement HttpWrapper.
 */
public class BaseHttpResponse implements HttpResponse {
	private final String contentString;

	public BaseHttpResponse(Response response) throws IOException, HttpStatusCodeException{
		try {
			this.contentString = response.returnContent().asString();
		} catch (HttpResponseException e) {
			throw new HttpStatusCodeException(e.getMessage(), e.getStatusCode());
		}
	}

	@Override
	public String getContentString() throws IOException {
		return contentString;
	}

}
