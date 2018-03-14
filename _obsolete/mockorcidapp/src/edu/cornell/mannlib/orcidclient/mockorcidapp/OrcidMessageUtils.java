/* $This file is distributed under the terms of the license in /doc/license.txt$ */

package edu.cornell.mannlib.orcidclient.mockorcidapp;

import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.PropertyException;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import edu.cornell.mannlib.orcidclient.OrcidClientException;
import edu.cornell.mannlib.orcidclient.responses.message_1_2.OrcidMessage;

/**
 * TODO
 */
public class OrcidMessageUtils {
	private static final Log log = LogFactory.getLog(OrcidMessageUtils.class);

	private static final JAXBContext jaxbContext = createContext();

	private static JAXBContext createContext() {
		try {
			String packageName = OrcidMessage.class.getPackage().getName();
			return JAXBContext.newInstance(packageName);
		} catch (JAXBException e) {
			throw new RuntimeException("Failed to create the JAXB context.", e);
		}
	}

	public static String marshall(OrcidMessage message)
			throws OrcidClientException {
		try {
			Marshaller m = jaxbContext.createMarshaller();
			m.setProperty("jaxb.formatted.output", Boolean.TRUE);

			StringWriter sw = new StringWriter();
			m.marshal(message, sw);
			log.debug("marshall message=" + message + "\n, string=" + sw);
			return sw.toString();
		} catch (PropertyException e) {
			throw new OrcidClientException("Failed to create the Marshaller", e);
		} catch (JAXBException e) {
			throw new OrcidClientException("Failed to marshall the message '"
					+ message + "'", e);
		}
	}

	public static OrcidMessage unmarshall(String xml)
			throws OrcidClientException {
		try {
			Unmarshaller u = jaxbContext.createUnmarshaller();

			StreamSource source = new StreamSource(new StringReader(xml));
			JAXBElement<OrcidMessage> doc = u.unmarshal(source,
					OrcidMessage.class);
			log.debug("unmarshall string=" + xml + "\n, message="
					+ doc.getValue());
			return doc.getValue();
		} catch (JAXBException e) {
			throw new OrcidClientException("Failed to unmarshall the message '"
					+ xml + "'", e);
		}
	}

}
