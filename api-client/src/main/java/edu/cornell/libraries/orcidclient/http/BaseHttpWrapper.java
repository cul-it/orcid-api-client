package edu.cornell.libraries.orcidclient.http;

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
