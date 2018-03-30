/* $This file is distributed under the terms of the license in /doc/license.txt$ */

package edu.cornell.libraries.orcidclient.testwebapp;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import edu.cornell.libraries.orcidclient.auth.OrcidAuthorizationClient;
import edu.cornell.libraries.orcidclient.context.OrcidClientContext;

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
		if (AuthenticationRequester.CALLBACK_STATE.equals(req.getParameter("state"))) {
			new AuthenticationCallback(req, resp).exec();
		}

		OrcidAuthorizationClient authManager = occ.getAuthorizationClient(req);
		try {
			String redirectUrl = authManager.processAuthorizationResponse(req);
			resp.sendRedirect(redirectUrl);
		} catch (Exception e) {
			log.error("Invalid authorization response", e);
			fail(resp, "Invalid authorization response: " + e);
		}
	}

	private void fail(HttpServletResponse resp, String message)
			throws IOException {
		resp.setContentType("text/html");
		PrintWriter out = resp.getWriter();
		out.println("<html><head></head><body>");
		out.println("<h1>Callback Failure</h1>");
		out.println("<h2>" + message + "</h2>");
		out.println("</body></html");
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
