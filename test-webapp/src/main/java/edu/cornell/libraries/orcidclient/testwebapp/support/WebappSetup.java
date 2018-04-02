/* $This file is distributed under the terms of the license in /doc/license.txt$ */

package edu.cornell.libraries.orcidclient.testwebapp.support;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import edu.cornell.libraries.orcidclient.OrcidClientException;
import edu.cornell.libraries.orcidclient.context.OrcidClientContext;
import edu.cornell.libraries.orcidclient.context.OrcidClientContext.Setting;

/**
 * Read the settings file, store the webapp properties for later use, and
 * initialize the OrcidContext.
 */
@WebListener
public class WebappSetup implements ServletContextListener {
	private static final Log log = LogFactory.getLog(WebappSetup.class);

	private static final String ORCID_SETTINGS_PROPERTY = "orcid.settings";
	private static final String DEFAULT_SETTINGS_FILE = "settings.properties";

	public static final String WEBAPP_CACHE_FILE_KEY = "WEBAPP_CACHE_FILE";
	public static final String[] WEBAPP_SETTINGS_KEYS = {
			WEBAPP_CACHE_FILE_KEY };

	private static Map<String, String> webappProperties;

	@Override
	public void contextInitialized(ServletContextEvent sce) {
		String path = locatePropertiesFile(sce);
		Properties properties = loadProperties(path);
		webappProperties = extractWebappProperties(properties);
		Map<Setting, String> settings = convertToOrcidSettings(properties);
		initializeOrcidContext(settings);
	}

	private String locatePropertiesFile(ServletContextEvent sce) {
		ServletContext ctx = sce.getServletContext();
		String filename = System.getProperty(ORCID_SETTINGS_PROPERTY,
				DEFAULT_SETTINGS_FILE);
		String path = ctx.getRealPath(filename);
		if (path == null) {
			log.error("Can't find the property settings file at '" + filename
					+ "'");
		}
		return path;
	}

	private Properties loadProperties(String path) {
		Properties settings = new Properties();
		try {
			settings.load(new FileReader(path));
		} catch (FileNotFoundException e) {
			log.error("No settings file at '" + path + "'", e);
		} catch (IOException e) {
			log.error("Failed to load the property settings file at '" + path
					+ "'", e);
		}
		return settings;
	}

	private Map<String, String> extractWebappProperties(Properties properties) {
		Map<String, String> webappProps = new HashMap<>();
		for (String key : WEBAPP_SETTINGS_KEYS) {
			if (properties.containsKey(key)) {
				webappProps.put(key, properties.getProperty(key));
				properties.remove(key);
			}
		}
		return webappProps;
	}

	private Map<Setting, String> convertToOrcidSettings(Properties settings) {
		Map<Setting, String> settingsMap = new HashMap<>();
		for (String name : settings.stringPropertyNames()) {
			try {
				Setting key = Setting.valueOf(name);
				settingsMap.put(key, settings.getProperty(name));
			} catch (Exception e) {
				log.error(
						"Invalid property key: '" + name + "'. Valid keys are "
								+ Arrays.asList(Setting.values()));
			}
		}
		return settingsMap;
	}

	private void initializeOrcidContext(Map<Setting, String> settings) {
		try {
			OrcidClientContext.initialize(settings);
			log.info("Context is: " + OrcidClientContext.getInstance());
		} catch (OrcidClientException e) {
			log.error("Failed to initialize OrcidClientContent", e);
		}
	}

	public Map<String, String> getWebappProperties() {
		return new HashMap<>(webappProperties);
	}

	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		// Nothing to tear down.
	}

}
