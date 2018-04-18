/* $This file is distributed under the terms of the license in /doc/license.txt$ */

package edu.cornell.libraries.orcidclient.http;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.client.fluent.Request;

import edu.cornell.libraries.orcidclient.http.HttpWrapper.GetRequest;
import edu.cornell.libraries.orcidclient.http.HttpWrapper.HttpResponse;
import edu.cornell.libraries.orcidclient.http.HttpWrapper.HttpStatusCodeException;

/**
 * Use the Fluent library of HttpComponents to implement HttpWrapper.
 */
public class BaseGetRequest implements GetRequest {
	private final String url;
	private final Map<String, String> headers = new HashMap<>();

	public BaseGetRequest(String url) {
		this.url = url;
	}

	@Override
	public String getUrl() {
		return url;
	}

	@Override
	public BaseGetRequest addHeader(String key, String value) {
		headers.put(key, value);
		return this;
	}

	@Override
	public HttpResponse execute() throws IOException, HttpStatusCodeException {
		Request request = Request.Get(url);
		for (String headerName : headers.keySet()) {
			request = request.addHeader(headerName, headers.get(headerName));
		}

		return new BaseHttpResponse(request.execute());
	}

}
