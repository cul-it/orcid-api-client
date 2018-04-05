/* $This file is distributed under the terms of the license in /doc/license.txt$ */

package edu.cornell.libraries.orcidclient.testwebapp;

import static org.jtwig.JtwigTemplate.classpathTemplate;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jtwig.JtwigModel;

import edu.cornell.libraries.orcidclient.OrcidClientException;
import edu.cornell.libraries.orcidclient.context.OrcidClientContext;
import edu.cornell.libraries.orcidclient.testwebapp.actors.AuthenticationClientOffer;
import edu.cornell.libraries.orcidclient.testwebapp.actors.AuthenticationClientRequest;
import edu.cornell.libraries.orcidclient.testwebapp.actors.AuthenticationRawOffer;

/**
 * TODO
 */
@WebServlet("/request/*")
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

		try {
			if (req.getParameter("RawAuthentication") != null) {
				new AuthenticationRawOffer(req, resp).exec();
			} else if (req.getParameter("ClientAuthentication") != null) {
				new AuthenticationClientOffer(req, resp).exec();
			} else if (req
					.getParameter("ClientAuthenticationRequest") != null) {
				new AuthenticationClientRequest(req, resp).exec();
			} else {
				doFrontPage(req, resp);
			}
		} catch (OrcidClientException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void doFrontPage(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		JtwigModel model = JtwigModel.newModel().with("var", "World");
		String path = "/templates/index.twig.html";
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
