/* $This file is distributed under the terms of the license in /doc/license.txt$ */

package edu.cornell.mannlib.orcidclient.mockorcidapp;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import edu.cornell.mannlib.orcidclient.responses.message_1_2.ScopePathType;

/**
 * TODO
 * 
 * Ignore client_id and response_type.
 * 
 * <pre>
 *    GET http://sandbox-1.orcid.org/oauth/authorize ?
 * 	      client_id=0000-0002-4639-029X & 
 * 	      scope=%2Forcid-profile%2Fread-limited &
 * 	      response_type=code & 
 * 	      redirect_uri=http%3A%2F%2Fjeb228-dev.library.cornell.edu%2Forcivo%2Fcallback & 
 * 	      state=1728933982
 *    If not logged in, show the login screen, with the parameters in hidden fields
 *        Else, redirect to /login with the orcid and parameters
 * </pre>
 * 
 * When we get here, the required info may be in a pending AuthorizationInfo
 * object, instead of the request. This would happen if we weren't logged in
 * when we first got here, and had to stash the info and redirect to the login
 * page.
 * 
 * TODO What do we do if they are already authorized for a scope? It appears
 * that we go through it again.
 */
public class OauthAuthorizeAction extends AbstractAction {
	private static final Log log = LogFactory
			.getLog(OauthAuthorizeAction.class);

	private String redirectUri;
	private String state;
	private ScopePathType scope;

	/**
	 * Match something like http://sandbox-1.orcid.org/oauth/authorize
	 */
	public static boolean matches(String pathInfo) {
		return "/oauth/authorize".equals(pathInfo);
	}

	public OauthAuthorizeAction(HttpServletRequest req, HttpServletResponse resp) {
		super(req, resp);
	}

	public void doGet() throws IOException {
		obtainAndValidateParameters();
		setPendingAuthorization();

		if (oss.isLoggedIn()) {
			showAskAuthPage();
		} else {
			redirectToLoggingIn();
		}
	}

	private void obtainAndValidateParameters() {
		if (oss.isAuthorizationPending()) {
			AuthorizationData pending = oss.getPendingAuthorization();
			redirectUri = pending.getRedirectUri();
			state = pending.getState();
			scope = pending.getScope();
		} else {
			redirectUri = getRequiredParameter("redirect_uri");
			state = getRequiredParameter("state");
			scope = convertToScope(getRequiredParameter("scope"));
		}
	}

	private ScopePathType convertToScope(String scopeString) {
		return ScopePathType.fromValue(scopeString);
	}

	private void setPendingAuthorization() {
		oss.setPendingAuthorization(redirectUri, scope, state);
	}

	private void redirectToLoggingIn() throws IOException {
		String loggingInUrl = req.getContextPath() + req.getServletPath()
				+ "/login";
		log.debug("redirecting to logging in: " + loggingInUrl);
		resp.sendRedirect(loggingInUrl);
	}

	private void showAskAuthPage() throws IOException {
		resp.setContentType("text/html");
		String html = substituteStrings(getTemplate());
		log.debug("html for login page: \n" + html);
		resp.getWriter().println(html);
	}

	private String getTemplate() throws IOException {
		return IOUtils.toString(ctx.getResourceAsStream("/askAuth.template"));
	}

	private String substituteStrings(String template) {
		return template.replace("_CONTEXT_", req.getContextPath())
				.replace("_SERVLET_", req.getServletPath())
				.replace("_SCOPE_", getScopeString());
	}

	private CharSequence getScopeString() {
		switch (scope) {
		case ORCID_BIO_EXTERNAL_IDENTIFIERS_CREATE:
			return "createExternalID";
		case ORCID_PROFILE_READ_LIMITED:
			return "readLimitedProfile";
		case AUTHENTICATE:
			return "authenticate";
		default:
			return "BOGUS";
		}
	}

}
