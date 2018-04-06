/* $This file is distributed under the terms of the license in /doc/license.txt$ */

package edu.cornell.libraries.orcidclient.http;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.client.fluent.Form;
import org.apache.http.client.fluent.Request;

import edu.cornell.libraries.orcidclient.http.HttpPostRequester.PostRequest;
import edu.cornell.libraries.orcidclient.http.HttpPostRequester.PostResponse;

/**
 * Use the Fluent library of HttpComponents to implement HttpPostRequester. 
 */
public class BasePostRequest implements PostRequest {
	private final String url;
	private final Map<String, String>formFields = new HashMap<>();
	private final Map<String, String>headers = new HashMap<>();
	
	public BasePostRequest(String url) {
		this.url = url;
	}

	@Override
	public PostRequest addFormField(String key, String value) {
		formFields.put(key, value);
		return this;
	}

	@Override
	public PostRequest addHeader(String key, String value) {
		headers.put(key,  value);
		return this;
	}

	@Override
	public PostResponse execute() throws IOException {
		Request request = Request.Post(url);
		for (String headerName: headers.keySet()) {
			request = request.addHeader(headerName, headers.get(headerName));
		}
		
		Form form = Form.form();
		for (String fieldName: formFields.keySet()) {
			form = form.add(fieldName, formFields.get(fieldName));
		}

		request = request.bodyForm(form.build());
		
		return new BasePostResponse(request.execute());
	}
}

