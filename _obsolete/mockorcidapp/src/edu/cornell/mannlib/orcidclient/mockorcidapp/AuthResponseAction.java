/* $This file is distributed under the terms of the license in /doc/license.txt$ */

package edu.cornell.mannlib.orcidclient.mockorcidapp;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * <pre>
 *    GET /authResponse?scope&approve=true
 *        redirect to the callback
 *        on approval, 
 *           http://jeb228-dev.library.cornell.edu/orcivo/callback ? 
 *                code=Feb8OP & 
 *                state=1728933982
 *           where code is random. Store it with the parameters in the context.
 *        on disapproval,
 *           http://jeb228-dev.library.cornell.edu/orcivo/callback ? 
 *                error=access_denied &
 *                error_description=User+denied+access&state=372991735
 * </pre>
 */
public class AuthResponseAction extends AbstractAction {

	private AuthorizationData pending;
	private boolean authorized;

	public static boolean matches(String pathInfo) {
		return "/authResponse".equals(pathInfo);
	}

	public AuthResponseAction(HttpServletRequest req, HttpServletResponse resp) {
		super(req, resp);
	}

	public void doGet() throws IOException {
		confirmAuthParameter();
		confirmPendingAuth();
		if (authorized) {
			recordApproval();
			redirectWithSuccess();
		} else {
			failAuth();
			redirectWithFailure();
		}
	}

	private void confirmAuthParameter() {
		String value = req.getParameter("auth");
		if (value == null) {
			throw new IllegalStateException(
					"Authorize response has no parameter for 'auth'");
		}
		authorized = Boolean.valueOf(value);
	}

	private void confirmPendingAuth() {
		if (!oss.isAuthorizationPending()) {
			throw new IllegalStateException("Authorization is not pending.");
		}
		pending = oss.getPendingAuthorization();
	}

	private void recordApproval() {
		oss.confirmPendingAuthorization(String.valueOf(System.currentTimeMillis()));
	}

	private void redirectWithSuccess() throws IOException {
		resp.sendRedirect(String.format("%s?code=%s&state=%s",
				pending.getRedirectUri(), pending.getAuthCode(),
				pending.getState()));
	}

	private void failAuth() {
		oss.clearPendingAuthorization();
	}

	private void redirectWithFailure() throws IOException {
		resp.sendRedirect(String.format(
				"%s?error=%s&error_description=%s&state=%s",
				pending.getRedirectUri(), "access_denied",
				"User+denied+access", pending.getState()));
	}

}
