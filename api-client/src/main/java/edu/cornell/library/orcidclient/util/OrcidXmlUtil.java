package edu.cornell.library.orcidclient.util;

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

import edu.cornell.library.orcidclient.OrcidClientException;

/**
 * Some convenience methods for dealing with the XML messages in the ORCID API.
 */
public class OrcidXmlUtil {
	private static final Log log = LogFactory.getLog(OrcidXmlUtil.class);

	public static <T> T unmarshall(String xml, Class<T> clazz)
			throws OrcidClientException {
		try {
			String packageName = clazz.getPackage().getName();
			JAXBContext jaxbContext = JAXBContext.newInstance(packageName);

			Unmarshaller u = jaxbContext.createUnmarshaller();

			StreamSource source = new StreamSource(new StringReader(xml));
			JAXBElement<T> doc = u.unmarshal(source, clazz);
			log.debug("unmarshall string=" + xml + "\n, message="
					+ doc.getValue());
			return doc.getValue();
		} catch (JAXBException e) {
			throw new OrcidClientException(
					"Failed to unmarshall the message '" + xml + "'", e);
		}
	}

	public static String marshall(Object xmlObject)
			throws OrcidClientException {
		try {
			JAXBContext jaxbContext = JAXBContext
					.newInstance(xmlObject.getClass());
			Marshaller m = jaxbContext.createMarshaller();
			m.setProperty("jaxb.formatted.output", Boolean.TRUE);

			StringWriter sw = new StringWriter();
			m.marshal(xmlObject, sw);
			log.debug("marshall message=" + xmlObject + "\n, string=" + sw);
			return sw.toString();
		} catch (PropertyException e) {
			throw new OrcidClientException("Failed to create the Marshaller",
					e);
		} catch (JAXBException e) {
			throw new OrcidClientException(
					"Failed to marshall the XML for '" + xmlObject + "'", e);
		}
	}

	private OrcidXmlUtil() {
		// No reason to instantiate.
	}

}
