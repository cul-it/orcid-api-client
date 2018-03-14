/* $This file is distributed under the terms of the license in /doc/license.txt$ */

package edu.cornell.mannlib.orcidclient.mockorcidapp;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import edu.cornell.mannlib.orcidclient.OrcidClientException;
import edu.cornell.mannlib.orcidclient.responses.message_1_2.ExternalIdentifier;
import edu.cornell.mannlib.orcidclient.responses.message_1_2.ExternalIdentifiers;
import edu.cornell.mannlib.orcidclient.responses.message_1_2.OrcidBio;
import edu.cornell.mannlib.orcidclient.responses.message_1_2.OrcidMessage;
import edu.cornell.mannlib.orcidclient.responses.message_1_2.ScopePathType;

/**
 * Add an exernal ID to the profile.
 * 
 * Request looks like this:
 * 
 * <pre>
 * POST /0000-0003-3479-6011/orcid-bio/external-identifiers HTTP/1.1
 * Content-Type: application/orcid+xml
 * Authorization: bearer 3390f839-0253-4750-8805-07d56b43b846
 * Content-Length: 699
 * Host: api.sandbox-1.orcid.org
 * Connection: Keep-Alive
 * User-Agent: Apache-HttpClient/4.3.2 (java 1.5)
 * Accept-Encoding: gzip,deflate
 * 
 * <?xml version="1.0" encoding="UTF-8" standalone="yes"?>
 * <orcid-message xmlns="http://www.orcid.org/ns/orcid">
 *     <message-version>1.0.23</message-version>
 *     <orcid-profile>
 *         <orcid-bio>
 *             <external-identifiers visibility="public">
 *                 <external-identifier>
 *                     <external-id-common-name>VIVO Cornell</external-id-common-name>
 *                     <external-id-reference>http://bogus.uri.edu/1392916915913</external-id-reference>
 *                     <external-id-url>http://bogus.uri.edu/1392916915913</external-id-url>
 *                 </external-identifier>
 *             </external-identifiers>
 *         </orcid-bio>
 *     </orcid-profile>
 * </orcid-message>
 * </pre>
 * 
 * TODO What happens if the request contains more than one external ID?
 * 
 * TODO What if we try to change visibility?
 */
public class AddExternalIdAction extends AbstractAction {
	private static final Log log = LogFactory.getLog(AddExternalIdAction.class);

	public static boolean matches(String pathInfo) {
		return extractOrcid(pathInfo) != null;
	}

	private AuthorizationData auth;
	private OrcidMessage payload;
	private OrcidMessage profile;

	protected AddExternalIdAction(HttpServletRequest req,
			HttpServletResponse resp) {
		super(req, resp);
	}

	private static String extractOrcid(String pathInfo) {
		String[] parts = pathInfo.split("/");
		log.debug("parts is " + Arrays.toString(parts));
		if ((parts.length == 4) && "orcid-bio".equals(parts[2])
				&& "external-identifiers".equals(parts[3])) {
			return parts[1];
		} else {
			return null;
		}
	}

	public void doPost() throws OrcidClientException, IOException {
		checkAuthorized();
		marshallMessage();
		addToProfile();
		replySuccess();
	}

	private void checkAuthorized() {
		lookupAuthorization();
		if (auth == null) {
			throw new IllegalStateException("Not authorized: '"
					+ req.getHeader("Authorization") + "'");
		}
		if (ScopePathType.ORCID_BIO_EXTERNAL_IDENTIFIERS_CREATE != auth
				.getScope()) {
			throw new IllegalStateException("Authorized for the wrong scope: "
					+ auth.getScope().value());
		}
	}

	private void lookupAuthorization() {
		String header = req.getHeader("Authorization");
		if (header == null) {
			return;
		}
		String[] parts = header.split(" ");
		if (parts.length != 2) {
			log.debug("Expecting 2 parts, not " + parts.length);
			return;
		}
		auth = ocs.lookupByAccessToken(parts[1]);
	}

	private void marshallMessage() throws OrcidClientException, IOException {
		payload = OrcidMessageUtils.unmarshall(IOUtils.toString(req
				.getInputStream()));
	}

	private void addToProfile() {
		String orcid = auth.getOrcid();
		if (!OrcidProfiles.hasProfile(orcid)) {
			throw new IllegalStateException("No profile for " + orcid);
		}
		List<ExternalIdentifier> newIdList = locateNewIds();
		List<ExternalIdentifier> existingIdList = locateExistingIds(orcid);
		existingIdList.addAll(newIdList);
	}

	private List<ExternalIdentifier> locateNewIds() {
		List<ExternalIdentifier> newIdList;
		try {
			newIdList = payload.getOrcidProfile().getOrcidBio()
					.getExternalIdentifiers().getExternalIdentifier();
		} catch (Exception e) {
			throw new IllegalStateException(
					"Invalid payload: no External Identifiers");
		}
		return newIdList;
	}

	private List<ExternalIdentifier> locateExistingIds(String orcid) {
		profile = OrcidProfiles.getProfile(orcid);
		OrcidBio bio = profile.getOrcidProfile().getOrcidBio();
		ExternalIdentifiers ids = bio.getExternalIdentifiers();
		if (ids == null) {
			ids = new ExternalIdentifiers();
			bio.setExternalIdentifiers(ids);
		}
		List<ExternalIdentifier> existingIdList = ids.getExternalIdentifier();
		return existingIdList;
	}

	private void replySuccess() throws IOException, OrcidClientException {
		resp.setContentType("application/vnd.orcid+xml");
		resp.getWriter().println(OrcidMessageUtils.marshall(profile));
	}

}
