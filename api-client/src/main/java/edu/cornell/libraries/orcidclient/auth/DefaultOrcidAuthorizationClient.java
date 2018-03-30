/* $This file is distributed under the terms of the license in /doc/license.txt$ */

package edu.cornell.libraries.orcidclient.auth;

import javax.servlet.http.HttpServletRequest;

import edu.cornell.libraries.orcidclient.actions.ApiScope;
import edu.cornell.libraries.orcidclient.context.OrcidClientContextImpl;

/**
 * TODO
 */
public class DefaultOrcidAuthorizationClient extends OrcidAuthorizationClient {
	static {
		if (true)
			throw new RuntimeException(
					"DefaultOrcidAuthorizationClient not implemented.");
	}

	public DefaultOrcidAuthorizationClient(
			OrcidClientContextImpl orcidClientContextImpl,
			HttpServletRequest req) {
		// TODO Auto-generated constructor stub
		throw new RuntimeException(
				"DefaultOrcidAuthorizationClient Constructor not implemented.");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.cornell.libraries.orcidclient.auth.OrcidAuthorizationClient#
	 * resetProgress(edu.cornell.libraries.orcidclient.actions.ApiScope)
	 */
	@Override
	public void resetProgress(ApiScope scope) {
		// TODO Auto-generated method stub
		throw new RuntimeException(
				"DefaultOrcidAuthorizationClient.resetProgress() not implemented.");

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.cornell.libraries.orcidclient.auth.OrcidAuthorizationClient#
	 * getAuthProcessResolution(edu.cornell.libraries.orcidclient.actions.
	 * ApiScope)
	 */
	@Override
	public AuthProcessResolution getAuthProcessResolution(ApiScope scope) {
		// TODO Auto-generated method stub
		throw new RuntimeException(
				"DefaultOrcidAuthorizationClient.getAuthProcessResolution() not implemented.");

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.cornell.libraries.orcidclient.auth.OrcidAuthorizationClient#
	 * buildAuthorizationCall(edu.cornell.libraries.orcidclient.actions.
	 * ApiScope, java.lang.String)
	 */
	@Override
	public String buildAuthorizationCall(ApiScope scope, String returnUrl) {
		// TODO Auto-generated method stub
		throw new RuntimeException(
				"DefaultOrcidAuthorizationClient.buildAuthorizationCall() not implemented.");

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.cornell.libraries.orcidclient.auth.OrcidAuthorizationClient#
	 * buildAuthorizationCall(edu.cornell.libraries.orcidclient.actions.
	 * ApiScope, java.lang.String, java.lang.String)
	 */
	@Override
	public String buildAuthorizationCall(ApiScope scope, String successUrl,
			String failureUrl) {
		// TODO Auto-generated method stub
		throw new RuntimeException(
				"DefaultOrcidAuthorizationClient.buildAuthorizationCall() not implemented.");

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.cornell.libraries.orcidclient.auth.OrcidAuthorizationClient#
	 * getAccessToken(edu.cornell.libraries.orcidclient.actions.ApiScope)
	 */
	@Override
	public AccessToken getAccessToken(ApiScope scope)
			throws IllegalStateException {
		// TODO Auto-generated method stub
		throw new RuntimeException(
				"DefaultOrcidAuthorizationClient.getAccessToken() not implemented.");

	}

	public String displayProgress(ApiScope scope) {
		throw new RuntimeException(
				"DefaultOrcidAuthorizationClient.displayProgress not implemented.");

	}

	/* (non-Javadoc)
	 * @see edu.cornell.libraries.orcidclient.auth.OrcidAuthorizationClient#processAuthorizationResponse(javax.servlet.http.HttpServletRequest)
	 */
	@Override
	public String processAuthorizationResponse(HttpServletRequest req) {
		// TODO Auto-generated method stub
		throw new RuntimeException("OrcidAuthorizationClient.processAuthorizationResponse() not implemented.");
		
	}

}
