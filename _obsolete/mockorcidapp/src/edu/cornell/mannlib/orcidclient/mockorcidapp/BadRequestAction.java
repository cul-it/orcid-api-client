/* $This file is distributed under the terms of the license in /doc/license.txt$ */

package edu.cornell.mannlib.orcidclient.mockorcidapp;

import static javax.servlet.http.HttpServletResponse.SC_NOT_FOUND;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * TODO
 */
public class BadRequestAction extends AbstractAction {
	private static final Log log = LogFactory.getLog(BadRequestAction.class);

	public BadRequestAction(HttpServletRequest req, HttpServletResponse resp) {
		super(req, resp);
	}

	public void doGet() throws IOException {
		log.error("Unrecognized call to GET, url=" + req.getRequestURL());
		resp.sendError(SC_NOT_FOUND);
	}

	public void doPost() throws IOException {
		log.error("Unrecognized call to POST, url=" + req.getRequestURL());
		resp.sendError(SC_NOT_FOUND);
	}

	public void exception(Exception e) throws IOException {
		resp.setContentType("text/html");
		resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		PrintWriter out = resp.getWriter();
		out.println("<html><head></head><body>");
		out.println("<h1>Servlet threw an exception:</h1>");
		out.println("<pre>");
		e.printStackTrace(out);
		out.println("</pre>");
		out.println("</body></html>");

		log.error("Servlet threw an exception", e);
		log.error("Path info was '" + req.getPathInfo() + "'");
		log.error("Servlet path was '" + req.getServletPath() + "'");
		log.error("SessionStatus is: " + oss);
		log.error("ContextStatus is: " + ocs);
	}

}
