package edu.cornell.libraries.orcidclient.testwebapp;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import edu.cornell.libraries.orcidclient.OrcidClientException;
import edu.cornell.libraries.orcidclient.testwebapp.actors.AuthenticationClientOffer;
import edu.cornell.libraries.orcidclient.testwebapp.actors.AuthenticationClientRequest;
import edu.cornell.libraries.orcidclient.testwebapp.actors.AuthenticationRawOffer;
import edu.cornell.libraries.orcidclient.testwebapp.actors.CacheManagement;
import edu.cornell.libraries.orcidclient.testwebapp.actors.EditExternalIdsOffer;
import edu.cornell.libraries.orcidclient.testwebapp.actors.EditExternalIdsReader;
import edu.cornell.libraries.orcidclient.testwebapp.actors.EditExternalIdsRequest;
import edu.cornell.libraries.orcidclient.testwebapp.actors.ErrorPage;
import edu.cornell.libraries.orcidclient.testwebapp.actors.IndexPage;
import edu.cornell.libraries.orcidclient.testwebapp.actors.ReadRecordOffer;
import edu.cornell.libraries.orcidclient.testwebapp.actors.ReadRecordRequest;

/**
 * Present the index page, or react to selections from it.
 */
@WebServlet("/request/*")
public class MainController extends AbstractController {
	@Override
	public void init() throws ServletException {
		// Nothing to do (yet)
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		try {
			if (req.getParameter("RawAuthentication") != null) {
				new AuthenticationRawOffer(req, resp).exec();
			} else if (req.getParameter("ClientAuthentication") != null) {
				new AuthenticationClientOffer(req, resp).exec();
			} else if (req
					.getParameter("ClientAuthenticationRequest") != null) {
				new AuthenticationClientRequest(req, resp).exec();
			} else if (req
					.getParameter("CacheManagement") != null) {
				new CacheManagement(req, resp).exec();
			} else if (req
					.getParameter("ReadRecord") != null) {
				new ReadRecordOffer(req, resp).exec();
			} else if (req
					.getParameter("ReadRecordRequest") != null) {
				new ReadRecordRequest(req, resp).exec();
			} else if (req
					.getParameter("EditExternalIds") != null) {
				new EditExternalIdsOffer(req, resp).exec();
			} else if (req
					.getParameter("EditExternalIdsGetList") != null) {
				new EditExternalIdsReader(req, resp).exec();
			} else if (req
					.getParameter("EditExternalIdsAdd") != null) {
				new EditExternalIdsRequest(req, resp).add();
			} else if (req
					.getParameter("EditExternalIdsUpdate") != null) {
				new EditExternalIdsRequest(req, resp).update();
			} else if (req
					.getParameter("EditExternalIdsRemove") != null) {
				new EditExternalIdsRequest(req, resp).remove();
			} else {
				new IndexPage(req, resp).exec();
			}
		} catch (Exception e) {
			new ErrorPage(req, resp, e).exec();
		}
	}
}
