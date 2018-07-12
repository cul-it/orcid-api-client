package edu.cornell.library.orcidclient.exceptions;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import edu.cornell.library.orcidclient.http.HttpWrapper;
import edu.cornell.library.orcidclient.http.HttpWrapper.HttpStatusCodeException;
import edu.cornell.library.orcidclient.orcid_message_2_1.error.ErrorElement;
import edu.cornell.library.orcidclient.util.OrcidXmlUtil;

/**
 * Indicates that the API returned an error response.
 * 
 * Break down the fields of the response, or use dummy values on a parsing
 * error.
 */
public class OrcidApiErrorResponseException extends OrcidClientException {
	private static final Log log = LogFactory
			.getLog(OrcidApiErrorResponseException.class);

	private String developerMessage;
	private int errorCode;

	public OrcidApiErrorResponseException(String message,
			HttpWrapper.HttpStatusCodeException cause) {
		super(message, cause);

		String content = cause.getResponseContent();
		try {
			ErrorElement error = OrcidXmlUtil.unmarshall(content,
					ErrorElement.class);
			this.developerMessage = error.getDeveloperMessage();
			this.errorCode = error.getErrorCode().intValue();
		} catch (OrcidClientException e) {
			log.warn("Failed to parse the error response: " + content, e);
			this.developerMessage = content;
			this.errorCode = -1;
		}
	}

	@Override
	public synchronized HttpStatusCodeException getCause() {
		return (HttpStatusCodeException) super.getCause();
	}

	@Override
	public String getMessage() {
		return super.getMessage() + "\n   developer message='"
				+ developerMessage + "'";
	}

	public String getDeveloperMessage() {
		return developerMessage;
	}

	public int getErrorCode() {
		return errorCode;
	}

}
