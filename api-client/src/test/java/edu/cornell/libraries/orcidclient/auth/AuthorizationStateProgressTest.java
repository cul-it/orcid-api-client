/* $This file is distributed under the terms of the license in /doc/license.txt$ */

package edu.cornell.libraries.orcidclient.auth;

import static org.junit.Assert.*;

import org.junit.Test;

import edu.cornell.libraries.orcidclient.testing.AbstractTestClass;

/**
 * TODO
 */
public class AuthorizationStateProgressTest extends AbstractTestClass {
	static {
		if (true)
			throw new RuntimeException(
					"AuthorizationStateProgressTest not implemented.");
	}

	@Test
	public void failure() {
		fail("failure not implemented");
	}

	/**
	 * Test plan:
	 * <pre>
	 * addFailure sets both state and details
	 * 
	 * addState just sets state
	 * 
	 * addCode sets state and details
	 * 
	 * addAccessToken sets state and details
	 * </pre>
	 */
}
