/* $This file is distributed under the terms of the license in /doc/license.txt$ */

package edu.cornell.libraries.orcidclient.auth;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.cornell.libraries.orcidclient.http.HttpPostRequester;

/**
 * TODO
 */
public class StubHttpPostRequester implements HttpPostRequester {
	private int statusCode;
	private String contentString;

	public void setResponse(int statusCode, String contentString) {
		this.statusCode = statusCode;
		this.contentString = contentString;
	}

	@Override
	public StubPostRequest createPostRequest(String accessTokenRequestUrl) {
		return new StubPostRequest();
	}

	public class StubPostRequest implements PostRequest {
		private MultiMap formFields = new MultiMap();
		private MultiMap headers = new MultiMap();

		@Override
		public StubPostRequest addFormField(String key, String value) {
			formFields.add(key, value);
			return this;
		}

		@Override
		public StubPostRequest addHeader(String key, String value) {
			headers.add(key, value);
			return this;
		}

		@Override
		public StubPostResponse execute() throws IOException {
			return new StubPostResponse();
		}
	}

	public class StubPostResponse implements PostResponse {
		@Override
		public String getContentString() throws IOException {
			return contentString;
		}

		@Override
		public int getStatusCode() throws IOException {
			return statusCode;
		}
	}

	private static class MultiMap {
		private final Map<String, List<String>> map = new HashMap<>();

		public void add(String key, String value) {
			if (!map.containsKey(key)) {
				map.put(key, new ArrayList<>());
			}
			map.get(key).add(value);
		}
	}
}
