package edu.cornell.libraries.orcidclient.http;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.client.fluent.Request;
import org.apache.http.entity.ContentType;

import edu.cornell.libraries.orcidclient.http.HttpWrapper.HttpStatusCodeException;
import edu.cornell.libraries.orcidclient.http.HttpWrapper.PutRequest;

/**
 * Use the Fluent library of HttpComponents to implement HttpWrapper.
 */
public class BasePutRequest implements PutRequest {
	private final String url;
	private final Map<String, String> headers = new HashMap<>();
	private String bodyString;

	public BasePutRequest(String url) {
		this.url = url;
	}

	@Override
	public String getUrl() {
		return url;
	}

	@Override
	public BasePutRequest addHeader(String key, String value) {
		headers.put(key, value);
		return this;
	}

	@Override
	public BasePutRequest setBodyString(String body) {
		this.bodyString = body;
		return this;
	}

	@Override
	public BaseHttpResponse execute()
			throws IOException, HttpStatusCodeException {
		Request request = Request.Put(url);
		for (String headerName : headers.keySet()) {
			request = request.addHeader(headerName, headers.get(headerName));
		}

		if (bodyString != null) {
			request.bodyString(bodyString,
					ContentType.APPLICATION_FORM_URLENCODED);
		}

		return new BaseHttpResponse(request.execute());
	}
}
