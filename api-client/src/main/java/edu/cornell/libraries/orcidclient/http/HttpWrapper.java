/* $This file is distributed under the terms of the license in /doc/license.txt$ */

package edu.cornell.libraries.orcidclient.http;

import java.io.IOException;
import java.util.List;

/**
 * A simple abstraction of HTTP GET and POST requests and responses.
 */
public interface HttpWrapper {
	GetRequest createGetRequest(String url);

	PostRequest createPostRequest(String url);

	interface GetRequest {
		String getUrl();

		GetRequest addHeader(String key, String value);

		HttpResponse execute() throws IOException, HttpStatusCodeException;
	}

	interface PostRequest {
		String getUrl();

		PostRequest addFormField(String key, String value);

		PostRequest addHeader(String key, String value);
		
		PostRequest setBodyString(String body);

		HttpResponse execute() throws IOException, HttpStatusCodeException;
	}

	interface HttpResponse {
		String getContentString() throws IOException;
		List<String> getHeaderValues(String key) throws IOException;
	}

	public static class HttpWrapperException extends Exception {
		public HttpWrapperException(String message) {
			super(message);
		}

		public HttpWrapperException(Throwable cause) {
			super(cause);
		}

		public HttpWrapperException(String message, Throwable cause) {
			super(message, cause);
		}
	}

	public static class HttpStatusCodeException extends HttpWrapperException {
		private final int statusCode;

		public HttpStatusCodeException(String message, int statusCode) {
			super(message + ": statusCode=" + statusCode);
			this.statusCode = statusCode;
		}

		public int getStatusCode() {
			return statusCode;
		}
	}
}
