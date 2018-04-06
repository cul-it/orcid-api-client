/* $This file is distributed under the terms of the license in /doc/license.txt$ */

package edu.cornell.libraries.orcidclient.http;

/**
 * Use the Fluent library of HttpComponents to implement HttpPostRequester.
 */
public class BaseHttpPostRequester implements HttpPostRequester {

	@Override
	public PostRequest createPostRequest(String url) {
		return new BasePostRequest(url);
	}

}
