/* $This file is distributed under the terms of the license in /doc/license.txt$ */

package edu.cornell.libraries.orcidclient.context;

import static org.junit.Assert.fail;

import org.junit.Test;

/**
 * TODO
 */
public class OrcidClientContextImplTest {
	static {
		if (true)
			throw new RuntimeException(
					"OrcidClientContextImplTest not implemented.");
	}

	@Test
	public void failer() {
		fail("fail not implemented");
	}

	/**
	 * Test plan
	 * 
	 * <pre>
	 * Missing setting throws exception
	 * 
	 * Invalid platform throws exception
	 * 
	 * Platform URL overrides supplied URL
	 * 
	 * Syntactically invalid callback/base combination  throws exception
	 * 
	 * Resolve  works  whether base endswith a slash or not.
	 * </pre>
	 */
}
