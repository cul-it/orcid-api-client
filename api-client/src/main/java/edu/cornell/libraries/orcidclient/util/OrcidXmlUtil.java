/* $This file is distributed under the terms of the license in /doc/license.txt$ */

package edu.cornell.libraries.orcidclient.util;

import java.io.StringReader;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import edu.cornell.libraries.orcidclient.OrcidClientException;

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

	private OrcidXmlUtil() {
		// No reason to instantiate.
	}
}
