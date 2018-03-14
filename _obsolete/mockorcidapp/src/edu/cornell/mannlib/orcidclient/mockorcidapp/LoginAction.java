/* $This file is distributed under the terms of the license in /doc/license.txt$ */

package edu.cornell.mannlib.orcidclient.mockorcidapp;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * <pre>
 *    GET /login?orcid=xxxx-xxxx-xxxx-xxxx
 *        Record the log in, in the session. retrieve the auth parameters from the hidden fields, 
 *            and continue with the auth request
 *        Show the appropriate auth screen, with the scope in a hidden field
 * </pre>
 * 
 * TODO
 */
public class LoginAction extends AbstractAction {
	private static final Log log = LogFactory.getLog(LoginAction.class);

	private static final Pattern LOGIN_LINK_PATTERN = Pattern
			.compile("\\{\\{loginLink(.*)\\}\\}");

	public static boolean matches(String pathInfo) {
		return "/login".equals(pathInfo);
	}

	public LoginAction(HttpServletRequest req, HttpServletResponse resp) {
		super(req, resp);
	}

	public void doGet() throws IOException {
		String orcid = req.getParameter("orcid");
		if (orcid == null) {
			showLoginPage();
		} else {
			recordLogin(orcid);
			returnToAuthRequest();
		}
	}

	private void recordLogin(String orcid) {
		oss.setOrcid(orcid);
	}

	private void returnToAuthRequest() throws IOException {
		String url = req.getContextPath() + req.getServletPath()
				+ "/oauth/authorize";
		log.debug("redirecting to auth request: " + url);
		resp.sendRedirect(url);
	}

	private void showLoginPage() throws IOException {
		resp.setContentType("text/html");
		String html = insertContext(insertLinks(getTemplate()));
		log.debug("html for login page: \n" + html);
		resp.getWriter().println(html);
	}

	private String getTemplate() throws IOException {
		return IOUtils.toString(ctx.getResourceAsStream("/login.template"));
	}

	private String insertLinks(String htmlTemplate) {
		String linkTemplate = findLinkTemplate(htmlTemplate);
		String links = assembleLinksHtml(linkTemplate);
		return putLinksIntoTemplate(htmlTemplate, links);
	}

	private String findLinkTemplate(String htmlTemplate) {
		Matcher matcher = LOGIN_LINK_PATTERN.matcher(htmlTemplate);
		if (!matcher.find()) {
			throw new IllegalStateException("Didn't find the "
					+ "login link pattern in the login.html template.");
		}
		return matcher.group(1);
	}

	private String assembleLinksHtml(String linkTemplate) {
		StringBuilder links = new StringBuilder();
		for (String orcid : OrcidProfiles.getOrcids()) {
			links.append(linkTemplate.replace("_ORCID_", orcid));
		}
		return links.toString();
	}

	private String putLinksIntoTemplate(String htmlTemplate, String links) {
		return LOGIN_LINK_PATTERN.matcher(htmlTemplate).replaceFirst(links);
	}

	private String insertContext(String template) {
		return template.replace("_CONTEXT_", req.getContextPath()).replace(
				"_SERVLET_", req.getServletPath());
	}

}
