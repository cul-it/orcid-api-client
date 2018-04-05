/* $This file is distributed under the terms of the license in /doc/license.txt$ */

package edu.cornell.libraries.orcidclient.testwebapp;

import static edu.cornell.libraries.orcidclient.context.OrcidClientContext.Setting.WEBAPP_BASE_URL;
import static org.jtwig.JtwigTemplate.classpathTemplate;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jtwig.JtwigModel;

import edu.cornell.libraries.orcidclient.OrcidClientException;
import edu.cornell.libraries.orcidclient.auth.OrcidAuthorizationClient;
import edu.cornell.libraries.orcidclient.context.OrcidClientContext;
import edu.cornell.libraries.orcidclient.testwebapp.actors.AuthenticationClientCallback;
import edu.cornell.libraries.orcidclient.testwebapp.actors.AuthenticationRawCallback;
import edu.cornell.libraries.orcidclient.testwebapp.actors.AuthenticationRawOffer;

/**
 * TODO
 */
public class CallbackController extends HttpServlet {
	private static final Log log = LogFactory.getLog(CallbackController.class);

	private OrcidClientContext occ;

	@Override
	public void init() throws ServletException {
		occ = OrcidClientContext.getInstance();
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		if (log.isDebugEnabled()) {
			log.debug("Request parameters: " + dumpParameterMap(req));
		}

		OrcidAuthorizationClient auth = occ.getAuthorizationClient(req);

		try {
			String state = req.getParameter("state");
			if (AuthenticationRawOffer.CALLBACK_STATE.equals(state)) {
				new AuthenticationRawCallback(req, resp).exec();
			} else if (auth.getProgressById(state) != null) {
				new AuthenticationClientCallback(req, resp).exec();
			} else {
				fail(resp, state, "Didn't recognize the callback");
			}

		} catch (OrcidClientException e) {
			throw new ServletException(e);
		}
	}

	private void fail(HttpServletResponse resp, String state, String message)
			throws IOException {
		JtwigModel model = JtwigModel.newModel() //
				.with("message", message) //
				.with("state", state) //
				.with("mainPageUrl", occ.getSetting(WEBAPP_BASE_URL));

		String path = "/templates/callbackFailure.twig.html";
		ServletOutputStream outputStream = resp.getOutputStream();
		classpathTemplate(path).render(model, outputStream);
	}

	private String dumpParameterMap(HttpServletRequest req) {
		Map<String, String[]> raw = req.getParameterMap();
		Map<String, List<String>> cooked = new HashMap<>();

		for (String name : raw.keySet()) {
			cooked.put(name, Arrays.asList(raw.get(name)));
		}
		return cooked.toString();
	}

}
