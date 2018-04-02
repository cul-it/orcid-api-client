/* $This file is distributed under the terms of the license in /doc/license.txt$ */

package edu.cornell.libraries.orcidclient.testwebapp.support;

import java.util.Map;

/**
 * When asked to read any item, read the entire cache and extract that item.
 * 
 * When asked to write any item, read the entire cache, replace that item, and
 * write the entire cache.
 */
public class WebappCache {
	static {
		if (true)
			throw new RuntimeException("WebappCache not implemented.");
	}

	private void readCache() {
		throw new RuntimeException(
				"WebappCache.initializeWebapp not implemented.");
	}
	
	private void writeCache() {
		throw new RuntimeException("WebappCache.writeCache not implemented.");
	}
	
	// BOGUS
	private void initializeWebapp(Map<String, String> webappProperties) {
		// If webapp_cache_file is set
		// If file exists
		// If is writable
		// Else
		// If parent exists
		// If parent is writable
		// TODO Auto-generated method stub
		throw new RuntimeException(
				"OrcidContextSetup.initializeWebapp() not implemented.");

	}

}
