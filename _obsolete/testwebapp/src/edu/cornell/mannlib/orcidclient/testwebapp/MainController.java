/* $This file is distributed under the terms of the license in /doc/license.txt$ */

package edu.cornell.mannlib.orcidclient.testwebapp;

import java.io.IOException;
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

import edu.cornell.mannlib.orcidclient.context.OrcidClientContext;

/**
 * TODO
 */
public class MainController extends HttpServlet {
	private static final Log log = LogFactory.getLog(MainController.class);

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

		if (req.getParameter("Authenticate") != null) {
			new AuthenticationRequester(req, resp).exec();
		} else if (req.getParameter("ReadPublic") != null) {
			new PublicBioReader(req, resp).exec();
		} else if (req.getParameter("ReadProfile") != null) {
			new ProfileReader(req, resp).exec();
		} else if (req.getParameter("AddExternalId") != null) {
			new ExternalIdAdder(req, resp).exec();
		} else if (req.getParameter("RestrictExternalIds") != null) {
			new ExternalIdRestrictor(req, resp).exec();
		} else {
			doBogusRequest(req, resp);
		}
	}

	private void doBogusRequest(HttpServletRequest req, HttpServletResponse resp) {
		// TODO Auto-generated method stub
		throw new RuntimeException(
				"MainController.doBogusRequest() not implemented.");
	}

	private String dumpParameterMap(HttpServletRequest req) {
		@SuppressWarnings("unchecked")
		Map<String, String[]> raw = req.getParameterMap();
		Map<String, List<String>> cooked = new HashMap<>();

		for (String name : raw.keySet()) {
			cooked.put(name, Arrays.asList(raw.get(name)));
		}
		return cooked.toString();
	}

}
