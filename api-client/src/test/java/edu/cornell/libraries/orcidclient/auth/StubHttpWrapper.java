package edu.cornell.libraries.orcidclient.auth;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.cornell.libraries.orcidclient.http.HttpWrapper;

/**
 * TODO
 */
public class StubHttpWrapper implements HttpWrapper {
	private int statusCode;

	private String contentString;
	private String url;

	public void setResponse(int statusCode, String contentString) {
		this.statusCode = statusCode;
		this.contentString = contentString;
	}

	@Override
	public StubGetRequest createGetRequest(String requestUrl) {
		this.url = requestUrl;
		return new StubGetRequest();
	}

	@Override
	public StubPostRequest createPostRequest(String requestUrl) {
		this.url = requestUrl;
		return new StubPostRequest();
	}

	@Override
	public StubPutRequest createPutRequest(String requestUrl) {
		this.url = requestUrl;
		return new StubPutRequest();
	}

	@Override
	public StubDeleteRequest createDeleteRequest(String requestUrl) {
		this.url = requestUrl;
		return new StubDeleteRequest();
	}
	
	public class StubGetRequest implements GetRequest {
		private MultiMap headers = new MultiMap();

		@Override
		public String getUrl() {
			return url;
		}

		@Override
		public StubGetRequest addHeader(String key, String value) {
			headers.add(key, value);
			return this;
		}

		@Override
		public HttpResponse execute()
				throws IOException, HttpStatusCodeException {
			if (statusCode != 0 && statusCode >= 300) {
				throw new HttpStatusCodeException("Bad status code",
						statusCode);
			}
			return new StubHttpResponse();
		}

	}

	public class StubPostRequest implements PostRequest {
		private MultiMap formFields = new MultiMap();
		private MultiMap headers = new MultiMap();
		private String bodyString;

		@Override
		public String getUrl() {
			return url;
		}

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
		public StubPostRequest setBodyString(String body) {
			bodyString = body;
			return this;
		}

		@Override
		public StubHttpResponse execute()
				throws IOException, HttpStatusCodeException {
			if (statusCode != 0 && statusCode != 200) {
				throw new HttpStatusCodeException("Bad status code",
						statusCode);
			}
			return new StubHttpResponse();
		}
	}

	public class StubPutRequest implements PutRequest {
		private MultiMap headers = new MultiMap();
		private String bodyString;

		@Override
		public String getUrl() {
			return url;
		}

		@Override
		public StubPutRequest addHeader(String key, String value) {
			headers.add(key, value);
			return this;
		}

		@Override
		public StubPutRequest setBodyString(String body) {
			bodyString = body;
			return this;
		}

		@Override
		public HttpResponse execute() throws HttpStatusCodeException {
			if (statusCode != 0 && statusCode != 200) {
				throw new HttpStatusCodeException("Bad status code",
						statusCode);
			}
			return new StubHttpResponse();
		}

	}

	public class StubDeleteRequest implements DeleteRequest {
		private MultiMap headers = new MultiMap();
		
		@Override
		public String getUrl() {
			return url;
		}
		
		@Override
		public StubDeleteRequest addHeader(String key, String value) {
			headers.add(key, value);
			return this;
		}
		
		@Override
		public HttpResponse execute() throws HttpStatusCodeException {
			if (statusCode != 0 && statusCode != 200) {
				throw new HttpStatusCodeException("Bad status code",
						statusCode);
			}
			return new StubHttpResponse();
		}
	}
	
	public class StubHttpResponse implements HttpResponse {
		@Override
		public String getContentString() throws IOException {
			return contentString;
		}

		@Override
		public List<String> getHeaderValues(String key) throws IOException {
			// Not implemented
			return Collections.emptyList();
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
