/* $This file is distributed under the terms of the license in /doc/license.txt$ */

package edu.cornell.mannlib.orcidclient.testwebapp;

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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import edu.cornell.mannlib.orcidclient.OrcidClientException;
import edu.cornell.mannlib.orcidclient.context.OrcidClientContext;
import edu.cornell.mannlib.orcidclient.context.OrcidClientContext.Setting;

/**
 * TODO
 */
public class OrcidContextSetup implements ServletContextListener {
	private static final Log log = LogFactory.getLog(OrcidContextSetup.class);

	private static final String ORCID_SETTINGS_PROPERTY = "orcid.settings";
	private static final String DEFAULT_SETTINGS_FILE = "settings.properties";

	@Override
	public void contextInitialized(ServletContextEvent sce) {
		String path = locatePropertiesFile(sce);
		Properties properties = loadProperties(path);
		Map<Setting, String> settings = convertToSettings(properties);
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

	private Map<Setting, String> convertToSettings(Properties settings) {
		Map<Setting, String> settingsMap = new HashMap<>();
		for (String name : settings.stringPropertyNames()) {
			try {
				Setting key = Setting.valueOf(name);
				settingsMap.put(key, settings.getProperty(name));
			} catch (Exception e) {
				log.error("Invalid property key: ''. Valid keys are "
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

	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		// Nothing to tear down.
	}

}
