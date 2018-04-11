/* $This file is distributed under the terms of the license in /doc/license.txt$ */

package edu.cornell.libraries.orcidclient.http;

import java.io.IOException;

/**
 * A simple abstraction of an HTTP POST request and response.
 */
public interface HttpPostRequester {
	PostRequest createPostRequest(String accessTokenRequestUrl);

	interface PostRequest {
		PostRequest addFormField(String key, String value);

		PostRequest addHeader(String key, String value);

		PostResponse execute() throws IOException;
	}

	interface PostResponse {
		String getContentString() throws IOException;

		int getStatusCode() throws IOException;
	}

}
