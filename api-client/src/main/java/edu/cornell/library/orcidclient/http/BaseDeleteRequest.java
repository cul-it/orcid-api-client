package edu.cornell.library.orcidclient.http;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.client.fluent.Request;

import edu.cornell.library.orcidclient.http.HttpWrapper.DeleteRequest;
import edu.cornell.library.orcidclient.http.HttpWrapper.HttpStatusCodeException;

/**
 * Use the Fluent library of HttpComponents to implement HttpWrapper.
 */
public class BaseDeleteRequest implements DeleteRequest {
	private final String url;
	private final Map<String, String> headers = new HashMap<>();

	public BaseDeleteRequest(String url) {
		this.url = url;
	}

	@Override
	public String getUrl() {
		return url;
	}

	@Override
	public BaseDeleteRequest addHeader(String key, String value) {
		headers.put(key, value);
		return this;
	}

	@Override
	public BaseHttpResponse execute()
			throws IOException, HttpStatusCodeException {
		Request request = Request.Delete(url);
		for (String headerName : headers.keySet()) {
			request = request.addHeader(headerName, headers.get(headerName));
		}

		return new BaseHttpResponse(request.execute());
	}
}
