package edu.cornell.library.orcidclient.testwebapp;

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

/**
 * Helpful methods
 */
public class AbstractController extends HttpServlet {
	private static final Log log = LogFactory.getLog(AbstractController.class);

	@Override
	protected void service(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		if (log.isDebugEnabled()) {
			log.debug("Request parameters: " + dumpParameterMap(req));
		}
		super.service(req, resp);
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
