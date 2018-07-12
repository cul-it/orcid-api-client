package edu.cornell.library.orcidclient.testwebapp.actors;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jtwig.JtwigModel;

/**
 * TODO
 */
public class ErrorPage extends AbstractActor {
	private static final Log log = LogFactory.getLog(ErrorPage.class);

	private final Throwable exception;

	public ErrorPage(HttpServletRequest req, HttpServletResponse resp,
			Throwable e) {
		super(req, resp);
		this.exception = e;
	}

	public void exec() {
		try {
			log.error("Error detected", exception);

			StringWriter sw = new StringWriter();
			exception.printStackTrace(new PrintWriter(sw));
			render("/templates/errorPage.twig.html", //
					JtwigModel.newModel() //
							.with("exception", sw.toString()));
		} catch (IOException e) {
			log.error(e, e);
		}
	}

}
