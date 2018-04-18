/* $This file is distributed under the terms of the license in /doc/license.txt$ */

package edu.cornell.libraries.orcidclient.http;

import edu.cornell.libraries.orcidclient.http.HttpWrapper.GetRequest;

/**
 * Use the Fluent library of HttpComponents to implement HttpWrapper.
 */
public class BaseHttpWrapper implements HttpWrapper {

	@Override
	public GetRequest createGetRequest(String url) {
		return new BaseGetRequest(url);
	}

	@Override
	public PostRequest createPostRequest(String url) {
		return new BasePostRequest(url);
	}

}
