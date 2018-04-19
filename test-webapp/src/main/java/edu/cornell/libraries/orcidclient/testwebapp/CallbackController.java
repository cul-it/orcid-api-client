package edu.cornell.libraries.orcidclient.testwebapp;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import edu.cornell.libraries.orcidclient.OrcidClientException;
import edu.cornell.libraries.orcidclient.auth.OrcidAuthorizationClient;
import edu.cornell.libraries.orcidclient.context.OrcidClientContext;
import edu.cornell.libraries.orcidclient.http.BaseHttpWrapper;
import edu.cornell.libraries.orcidclient.http.HttpWrapper;
import edu.cornell.libraries.orcidclient.testwebapp.actors.AuthenticationClientCallback;
import edu.cornell.libraries.orcidclient.testwebapp.actors.AuthenticationRawCallback;
import edu.cornell.libraries.orcidclient.testwebapp.actors.AuthenticationRawOffer;
import edu.cornell.libraries.orcidclient.testwebapp.actors.CallbackFailed;
import edu.cornell.libraries.orcidclient.testwebapp.support.WebappCache;

/**
 * ORCID has issued a redirect to here. Read the "state" parameter to figure out
 * what we were doing, and call the appropriate actor.
 */
public class CallbackController extends AbstractController {
	private OrcidClientContext occ;
	private HttpWrapper httpWrapper;

	@Override
	public void init() throws ServletException {
		occ = OrcidClientContext.getInstance();
		httpWrapper = new BaseHttpWrapper();
	}

	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		try {
			OrcidAuthorizationClient auth = getAuthorizationClient();
			
			String state = req.getParameter("state");
			if (AuthenticationRawOffer.CALLBACK_STATE.equals(state)) {
				new AuthenticationRawCallback(req, resp).exec();
			} else if (auth.getProgressById(state) != null) {
				new AuthenticationClientCallback(req, resp).exec();
			} else {
				new CallbackFailed(req, resp, state).exec();
			}
		} catch (OrcidClientException e) {
			throw new ServletException(e);
		}
	}

	private OrcidAuthorizationClient getAuthorizationClient()
			throws OrcidClientException {
		return new OrcidAuthorizationClient(occ, WebappCache.getCache(),
				httpWrapper);
	}
}
