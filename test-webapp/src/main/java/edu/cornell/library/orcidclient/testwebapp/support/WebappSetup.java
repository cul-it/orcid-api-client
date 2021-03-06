package edu.cornell.library.orcidclient.testwebapp.support;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import edu.cornell.library.orcidclient.context.OrcidClientContext;
import edu.cornell.library.orcidclient.context.OrcidClientContextImpl;
import edu.cornell.library.orcidclient.exceptions.OrcidClientException;

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

	private static volatile Map<String, String> webappProperties = Collections
			.emptyMap();

	@Override
	public void contextInitialized(ServletContextEvent sce) {
		String path = locatePropertiesFile(sce);
		Properties properties = loadProperties(path);
		webappProperties = extractWebappProperties(properties);
		Map<String, String> settings = convertToStringMap(properties);
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
		return Collections.unmodifiableMap(webappProps);
	}

	private Map<String, String> convertToStringMap(Properties settings) {
		Map<String, String> settingsMap = new HashMap<>();
		for (String name : settings.stringPropertyNames()) {
			settingsMap.put(name, settings.getProperty(name));
		}
		return settingsMap;
	}

	private void initializeOrcidContext(Map<String, String> settings) {
		try {
			OrcidClientContext.initialize(new OrcidClientContextImpl(settings));
			log.info("Context is: " + OrcidClientContext.getInstance());
		} catch (OrcidClientException e) {
			log.error("Failed to initialize OrcidClientContent", e);
		}
	}

	public static Map<String, String> getWebappProperties() {
		return new HashMap<>(webappProperties);
	}

	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		// Nothing to tear down.
	}

}
