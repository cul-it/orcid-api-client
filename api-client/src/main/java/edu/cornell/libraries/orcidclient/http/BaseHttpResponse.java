package edu.cornell.libraries.orcidclient.http;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.fluent.Response;
import org.apache.http.util.EntityUtils;

import edu.cornell.libraries.orcidclient.http.HttpWrapper.HttpResponse;
import edu.cornell.libraries.orcidclient.http.HttpWrapper.HttpStatusCodeException;

/**
 * Use the Fluent library of HttpComponents to implement HttpWrapper.
 */
public class BaseHttpResponse implements HttpResponse {
	private String contentString;
	private int statusCode;
	private String reasonPhrase;
	private Map<String, List<String>> headerValues;

	public BaseHttpResponse(Response response)
			throws IOException, HttpStatusCodeException {
		response.handleResponse(new BaseResponseHandler());
		if (statusCode >= 400) {
			throw new HttpStatusCodeException(reasonPhrase, statusCode);
		}
	}

	@Override
	public String getContentString() throws IOException {
		return contentString;
	}

	@Override
	public List<String> getHeaderValues(String key) throws IOException {
		if (headerValues.containsKey(key)) {
			return headerValues.get(key);
		} else {
			return Collections.emptyList();
		}
	}

	private class BaseResponseHandler implements ResponseHandler<Object> {
		@Override
		public Object handleResponse(org.apache.http.HttpResponse innerResponse)
				throws ClientProtocolException, IOException {
			StatusLine statusLine = innerResponse.getStatusLine();
			statusCode = statusLine.getStatusCode();
			reasonPhrase = statusLine.getReasonPhrase();

			Map<String, List<String>> headers = new HashMap<>();
			for (Header header : innerResponse.getAllHeaders()) {
				String name = header.getName();
				if (!headers.containsKey(name)) {
					headers.put(name, new ArrayList<>());
				}
				headers.get(name).add(header.getValue());
			}
			headerValues = headers;

			HttpEntity entity = innerResponse.getEntity();
			if (entity == null) {
				contentString = "";
			} else {
				contentString = EntityUtils.toString(entity);
			}
			return "";
		}
	}
}
