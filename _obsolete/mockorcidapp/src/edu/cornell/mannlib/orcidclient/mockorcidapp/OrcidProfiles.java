/* $This file is distributed under the terms of the license in /doc/license.txt$ */

package edu.cornell.mannlib.orcidclient.mockorcidapp;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletContext;

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import edu.cornell.mannlib.orcidclient.OrcidClientException;
import edu.cornell.mannlib.orcidclient.responses.message_1_2.OrcidMessage;

/**
 * Keep the ORCID profiles in memory.
 * 
 * At startup, load the profiles from WEB-INF/resources. Any changes that happen
 * while running will not be saved.
 */
public class OrcidProfiles {
	private static final Log log = LogFactory.getLog(OrcidProfiles.class);

	private static final Map<String, OrcidMessage> map = new HashMap<>();

	public static void load(ServletContext ctx) throws OrcidClientException,
			IOException {
		@SuppressWarnings("unchecked")
		Set<String> paths = ctx.getResourcePaths("/WEB-INF/resources/");

		for (String path : paths) {
			String orcid = path.split("[./]")[3];
			InputStream in = ctx.getResourceAsStream(path);
			String xml = IOUtils.toString(in);
			OrcidMessage profile = OrcidMessageUtils.unmarshall(xml);
			map.put(orcid, profile);
			log.info("Loaded ORCID profile for " + orcid);
		}
	}

	public static boolean hasProfile(String orcid) {
		return map.containsKey(orcid);
	}

	public static OrcidMessage getProfile(String orcid) {
		return map.get(orcid);
	}

	public static Collection<String> getOrcids() {
		return new ArrayList<>(map.keySet());
	}

}
