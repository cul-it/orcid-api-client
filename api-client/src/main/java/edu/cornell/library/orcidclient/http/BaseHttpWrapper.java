package edu.cornell.library.orcidclient.http;

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

	@Override
	public PutRequest createPutRequest(String url) {
		return  new BasePutRequest(url);
	}

	@Override
	public DeleteRequest createDeleteRequest(String url) {
		return  new BaseDeleteRequest(url);
	}
	
}
