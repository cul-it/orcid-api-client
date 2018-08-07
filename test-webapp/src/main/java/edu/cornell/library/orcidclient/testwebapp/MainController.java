package edu.cornell.library.orcidclient.testwebapp;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import edu.cornell.library.orcidclient.testwebapp.actors.AuthenticationClientOffer;
import edu.cornell.library.orcidclient.testwebapp.actors.AuthenticationClientRequest;
import edu.cornell.library.orcidclient.testwebapp.actors.AuthenticationRawOffer;
import edu.cornell.library.orcidclient.testwebapp.actors.CacheManagement;
import edu.cornell.library.orcidclient.testwebapp.actors.CheckConnectionHandler;
import edu.cornell.library.orcidclient.testwebapp.actors.EditExternalIdsOffer;
import edu.cornell.library.orcidclient.testwebapp.actors.EditExternalIdsReader;
import edu.cornell.library.orcidclient.testwebapp.actors.EditExternalIdsRequest;
import edu.cornell.library.orcidclient.testwebapp.actors.EditWorksOffer;
import edu.cornell.library.orcidclient.testwebapp.actors.EditWorksReader;
import edu.cornell.library.orcidclient.testwebapp.actors.EditWorksRequest;
import edu.cornell.library.orcidclient.testwebapp.actors.ErrorPage;
import edu.cornell.library.orcidclient.testwebapp.actors.IndexPage;
import edu.cornell.library.orcidclient.testwebapp.actors.ReadRecordOffer;
import edu.cornell.library.orcidclient.testwebapp.actors.ReadRecordRequest;
import edu.cornell.library.orcidclient.testwebapp.actors.ReadWorksFullyOffer;
import edu.cornell.library.orcidclient.testwebapp.actors.ReadWorksFullyRequest;

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
			if (has(req, "CheckConnection")) {
				new CheckConnectionHandler(req, resp).exec();
			} else if (has(req, "ConnectionSiteSucceed")) {
				new CheckConnectionHandler(req, resp).execSiteSucceed();
			} else if (has(req, "ConnectionSiteFail")) {
				new CheckConnectionHandler(req, resp).execSiteFail();
			} else if (has(req, "ConnectionOauthSucceed")) {
				new CheckConnectionHandler(req, resp).execOauthSucceed();
			} else if (has(req, "ConnectionOauthFailAuthCode")) {
				new CheckConnectionHandler(req, resp).execFailAuthCode();
			} else if (has(req, "ConnectionOauthFailAccessToken")) {
				new CheckConnectionHandler(req, resp).execFailAccessToken();
			} else if (has(req, "ConnectionApiSucceed")) {
				new CheckConnectionHandler(req, resp).execApiSucceed();
			} else if (has(req, "ConnectionApiFailPublic")) {
				new CheckConnectionHandler(req, resp).execApiFailPublic();
			} else if (has(req, "ConnectionApiFailMember")) {
				new CheckConnectionHandler(req, resp).execApiFailMember();
			} else if (has(req, "RawAuthentication")) {
				new AuthenticationRawOffer(req, resp).exec();
			} else if (has(req, "ClientAuthentication")) {
				new AuthenticationClientOffer(req, resp).exec();
			} else if (has(req, "ClientAuthenticationRequest")) {
				new AuthenticationClientRequest(req, resp).exec();
			} else if (has(req, "CacheManagement")) {
				new CacheManagement(req, resp).exec();
			} else if (has(req, "ReadRecord")) {
				new ReadRecordOffer(req, resp).exec();
			} else if (has(req, "ReadRecordRequest")) {
				new ReadRecordRequest(req, resp).exec();
			} else if (has(req, "EditExternalIds")) {
				new EditExternalIdsOffer(req, resp).exec();
			} else if (has(req, "EditExternalIdsGetList")) {
				new EditExternalIdsReader(req, resp).exec();
			} else if (has(req, "EditExternalIdsAdd")) {
				new EditExternalIdsRequest(req, resp).add();
			} else if (has(req, "EditExternalIdsUpdate")) {
				new EditExternalIdsRequest(req, resp).update();
			} else if (has(req, "EditExternalIdsRemove")) {
				new EditExternalIdsRequest(req, resp).remove();
			} else if (has(req, "EditWorks")) {
				new EditWorksOffer(req, resp).exec();
			} else if (has(req, "ReadWorksFully")) {
				new ReadWorksFullyOffer(req, resp).exec();
			} else if (has(req, "ReadWorksFullyRequest")) {
				new ReadWorksFullyRequest(req, resp).exec();
			} else if (has(req, "EditWorksGetList")) {
				new EditWorksReader(req, resp).exec();
			} else if (has(req, "EditWorksAdd")) {
				new EditWorksRequest(req, resp).add();
			} else if (has(req, "EditWorksUpdate")) {
				new EditWorksRequest(req, resp).update();
			} else if (has(req, "EditWorksRemove")) {
				new EditWorksRequest(req, resp).remove();
			} else {
				new IndexPage(req, resp).exec();
			}
		} catch (Exception e) {
			new ErrorPage(req, resp, e).exec();
		}
	}

	private boolean has(HttpServletRequest req, String name) {
		return req.getParameter(name) != null;
	}
}
