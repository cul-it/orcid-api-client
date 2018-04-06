/* $This file is distributed under the terms of the license in /doc/license.txt$ */

package edu.cornell.libraries.orcidclient.auth;

import static org.junit.Assert.*;

import org.junit.Test;

import edu.cornell.libraries.orcidclient.testing.AbstractTestClass;

/**
 * TODO
 */
public class OrcidAuthorizationClientTest extends AbstractTestClass {
	static {
		if (true)
			throw new RuntimeException(
					"OrcidAuthorizationClientTest not implemented.");
	}

	@Test
	public void failure() {
		fail("failure not implemented");
	}

	/**
	 * Test plan:
	 * <pre>
	 * createProgressObject writes to the cache.
	 * 
	 * processAuthorizationResponse:
	 *   proper URL in any case
	 *   all failures write to the cache - inspect the result
	 * 
	 *   request has no "State" parameter
	 *   "state" refers to non-existent progress object
	 *   progress object is SUCCESS, not seeking
	 *   request has error parameter but no error_description
	 *   request has error and error_description
	 *   request has no code 
	 *   request has empty code (is this the same as DENIED?)
	 *   
	 *   successful code writes to the cache - inspect the result
	 *   
	 *   failed token writes to the cache - inspect the result
	 *   successful token writes to the cache - inspect the result
	 *   
	 * getAccessTokenFromAuthCode:
	 *   result code not 200
	 *   JSON response is invalid
	 * </pre>
	 */
}
