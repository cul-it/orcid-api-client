package edu.cornell.library.orcidclient.http;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.client.fluent.Form;
import org.apache.http.client.fluent.Request;
import org.apache.http.entity.ContentType;

import edu.cornell.library.orcidclient.http.HttpWrapper.HttpStatusCodeException;
import edu.cornell.library.orcidclient.http.HttpWrapper.PostRequest;

/**
 * Use the Fluent library of HttpComponents to implement HttpWrapper.
 */
public class BasePostRequest implements PostRequest {
	private final String url;
	private final Map<String, String> formFields = new HashMap<>();
	private final Map<String, String> headers = new HashMap<>();
	private String bodyString;

	public BasePostRequest(String url) {
		this.url = url;
	}

	@Override
	public String getUrl() {
		return url;
	}

	@Override
	public BasePostRequest addFormField(String key, String value) {
		formFields.put(key, value);
		return this;
	}

	@Override
	public BasePostRequest addHeader(String key, String value) {
		headers.put(key, value);
		return this;
	}

	@Override
	public BasePostRequest setBodyString(String body) {
		this.bodyString = body;
		return this;
	}

	@Override
	public BaseHttpResponse execute()
			throws IOException, HttpStatusCodeException {
		Request request = Request.Post(url);
		for (String headerName : headers.keySet()) {
			request = request.addHeader(headerName, headers.get(headerName));
		}

		Form form = Form.form();
		for (String fieldName : formFields.keySet()) {
			form = form.add(fieldName, formFields.get(fieldName));
		}
		request = request.bodyForm(form.build());

		if (bodyString != null) {
			request.bodyString(bodyString,
					ContentType.APPLICATION_FORM_URLENCODED);
		}

		return new BaseHttpResponse(request.execute());
	}
}
