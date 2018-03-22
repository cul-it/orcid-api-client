/* $This file is distributed under the terms of the license in /doc/license.txt$ */

package edu.cornell.libraries.orcidclient.context;

import static edu.cornell.libraries.orcidclient.context.OrcidClientContext.Setting.API_PLATFORM;
import static edu.cornell.libraries.orcidclient.context.OrcidClientContext.Setting.AUTHORIZED_API_BASE_URL;
import static edu.cornell.libraries.orcidclient.context.OrcidClientContext.Setting.CALLBACK_PATH;
import static edu.cornell.libraries.orcidclient.context.OrcidClientContext.Setting.OAUTH_AUTHORIZE_URL;
import static edu.cornell.libraries.orcidclient.context.OrcidClientContext.Setting.OAUTH_TOKEN_URL;
import static edu.cornell.libraries.orcidclient.context.OrcidClientContext.Setting.PUBLIC_API_BASE_URL;
import static edu.cornell.libraries.orcidclient.context.OrcidClientContext.Setting.WEBAPP_BASE_URL;

import java.io.StringReader;
import java.io.StringWriter;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.PropertyException;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.client.utils.URIUtils;

import edu.cornell.libraries.orcidclient.OrcidClientException;
import edu.cornell.libraries.orcidclient.actions.OrcidApiClient;
import edu.cornell.libraries.orcidclient.auth.OrcidAuthorizationClient;
import edu.cornell.libraries.orcidclient.context.OrcidPlatformUrls.CustomPlatformUrls;
import edu.cornell.libraries.orcidclient.context.OrcidPlatformUrls.StandardPlatform;
import edu.cornell.libraries.orcidclient.responses.OrcidMessage;

/**
 * TODO
 */
public class OrcidClientContextImpl extends OrcidClientContext {
	private static final Log log = LogFactory
			.getLog(OrcidClientContextImpl.class);

	private final Map<Setting, String> settings;
	private final JAXBContext jaxbContext;

	private final String callbackUrl;
	private final OrcidPlatformUrls platform;

	public OrcidClientContextImpl(Map<Setting, String> settings)
			throws OrcidClientException {
		for (Setting s : Setting.values()) {
			if (s.isRequired() && !settings.containsKey(s)) {
				throw new MissingSettingException(s);
			}
		}
		this.settings = new EnumMap<>(settings);

		try {
			log.warn(
					"Do we still need the 'marshall' and 'unmarshall' methods?");
			String packageName = "edu.cornell.libraries.orcidclient.orcid_message_2_1.record";
			jaxbContext = JAXBContext.newInstance(packageName);

			callbackUrl = resolvePathWithWebapp(
					getRequiredSetting(CALLBACK_PATH));

			String platformSetting = getRequiredSetting(API_PLATFORM)
					.toUpperCase();
			if (platformSetting.equals("CUSTOM")) {
				try {
					platform = new CustomPlatformUrls(
							getRequiredSetting(PUBLIC_API_BASE_URL),
							getRequiredSetting(AUTHORIZED_API_BASE_URL),
							getRequiredSetting(OAUTH_AUTHORIZE_URL),
							getRequiredSetting(OAUTH_TOKEN_URL));
				} catch (MissingSettingException e) {
					throw new OrcidClientException("If " + API_PLATFORM
							+ " is 'CUSTOM', you must provide a value for '"
							+ e.getMissingSetting() + "'");
				}
			} else {
				platform = StandardPlatform.valueOf(
						getRequiredSetting(API_PLATFORM).toUpperCase());
			}
		} catch (IllegalArgumentException e) {
			throw new OrcidClientException(
					API_PLATFORM + " must be 'CUSTOM', or one of: "
							+ Arrays.toString(StandardPlatform.values()));
		} catch (JAXBException | URISyntaxException e) {
			throw new OrcidClientException(
					"Failed to create the OrcidClientContext", e);
		}
	}

	private String getRequiredSetting(Setting key)
			throws MissingSettingException {
		if (settings.containsKey(key)) {
			return settings.get(key);
		} else {
			throw new MissingSettingException(key);
		}
	}

	@Override
	public String getSetting(Setting key) {
		if (settings.containsKey(key)) {
			return settings.get(key);
		} else {
			return "";
		}
	}

	@Override
	public String getCallbackUrl() {
		return callbackUrl;
	}

	@Override
	public String getAuthCodeRequestUrl() {
		return platform.getOAuthUrl();
	}

	@Override
	public String getAccessTokenRequestUrl() {
		return platform.getTokenUrl();
	}

	@Override
	public String getApiPublicUrl() {
		return platform.getPublicUrl();
	}

	@Override
	public String getApiMemberUrl() {
		return platform.getMemberUrl();
	}

	@Override
	public OrcidApiClient getApiClient(HttpServletRequest req) {
		return new OrcidApiClient(this, req);
	}

	@Override
	public OrcidAuthorizationClient getAuthorizationClient(
			HttpServletRequest req) {
		return new OrcidAuthorizationClient(this, req);
	}

	@Override
	public String marshall(OrcidMessage message) throws OrcidClientException {
		try {
			Marshaller m = jaxbContext.createMarshaller();
			m.setProperty("jaxb.formatted.output", Boolean.TRUE);

			StringWriter sw = new StringWriter();
			m.marshal(message, sw);
			log.debug("marshall message=" + message + "\n, string=" + sw);
			return sw.toString();
		} catch (PropertyException e) {
			throw new OrcidClientException("Failed to create the Marshaller",
					e);
		} catch (JAXBException e) {
			throw new OrcidClientException(
					"Failed to marshall the message '" + message + "'", e);
		}
	}

	@Override
	public OrcidMessage unmarshall(String xml) throws OrcidClientException {
		try {
			Unmarshaller u = jaxbContext.createUnmarshaller();

			StreamSource source = new StreamSource(new StringReader(xml));
			JAXBElement<OrcidMessage> doc = u.unmarshal(source,
					OrcidMessage.class);
			log.debug("unmarshall string=" + xml + "\n, message="
					+ doc.getValue());
			return doc.getValue();
		} catch (JAXBException e) {
			throw new OrcidClientException(
					"Failed to unmarshall the message '" + xml + "'", e);
		}
	}

	@Override
	public String resolvePathWithWebapp(String path) throws URISyntaxException {
		URI baseUri = new URI(getSetting(WEBAPP_BASE_URL));
		return URIUtils.resolve(baseUri, path).toString();
	}

	@Override
	public String toString() {
		return "OrcidClientContextImpl[settings=" + settings + ", callbackUrl="
				+ callbackUrl + ", authCodeRequestUrl="
				+ getAuthCodeRequestUrl() + ", accessTokenRequestUrl="
				+ getAccessTokenRequestUrl() + "]";
	}

	private static class MissingSettingException extends OrcidClientException {
		private final Setting missingSetting;

		public MissingSettingException(Setting missingSetting) {
			super(toMessage(missingSetting));
			this.missingSetting = missingSetting;
		}

		public MissingSettingException(Setting missingSetting,
				Throwable cause) {
			super(toMessage(missingSetting), cause);
			this.missingSetting = missingSetting;
		}

		private static String toMessage(Setting ms) {
			return "You must provide a value for '" + ms + "'";
		}

		public Setting getMissingSetting() {
			return missingSetting;
		}

	}
}
