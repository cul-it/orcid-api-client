/* $This file is distributed under the terms of the license in /doc/license.txt$ */

package edu.cornell.mannlib.orcidclient.actions;

import javax.servlet.http.HttpServletRequest;

import edu.cornell.mannlib.orcidclient.context.OrcidAPIConfig;
import edu.cornell.mannlib.orcidclient.context.OrcidClientContext;

/**
 * TODO
 */
public class ActionManager {
	private final OrcidClientContext context;
	private final HttpServletRequest req;

	public ActionManager(OrcidClientContext context, HttpServletRequest req) {
		this.context = context;
		this.req = req;
	}

	public AddExternalIdAction createAddExternalIdAction() {
		if (context.getApiVersion() == OrcidAPIConfig.Versions.V1_2) {
			return new edu.cornell.mannlib.orcidclient.actions.version_1_0.AddExternalIdAction();
		}

		return new edu.cornell.mannlib.orcidclient.actions.version_2_0.AddExternalIdAction();
	}

	public ReadProfileAction createReadProfileAction() {
		if (context.getApiVersion() == OrcidAPIConfig.Versions.V1_2) {
			return new edu.cornell.mannlib.orcidclient.actions.version_1_0.ReadProfileAction();
		}

		return new edu.cornell.mannlib.orcidclient.actions.version_2_0.ReadProfileAction();
	}

	public ReadPublicBioAction createReadPublicBioAction() {
		if (context.getApiVersion() == OrcidAPIConfig.Versions.V1_2) {
			return new edu.cornell.mannlib.orcidclient.actions.version_1_0.ReadPublicBioAction();
		}

		return new edu.cornell.mannlib.orcidclient.actions.version_2_0.ReadPublicBioAction();
	}

	public UpdateBioAction createUpdateBioAction() {
		if (context.getApiVersion() == OrcidAPIConfig.Versions.V1_2) {
			return new edu.cornell.mannlib.orcidclient.actions.version_1_0.UpdateBioAction();
		}

		return new edu.cornell.mannlib.orcidclient.actions.version_2_0.UpdateBioAction();
	}
}
