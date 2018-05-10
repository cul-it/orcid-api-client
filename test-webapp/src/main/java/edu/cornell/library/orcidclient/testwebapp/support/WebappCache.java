package edu.cornell.library.orcidclient.testwebapp.support;

import static edu.cornell.library.orcidclient.auth.AuthorizationStateProgress.NO_URI;
import static edu.cornell.library.orcidclient.testwebapp.support.WebappSetup.WEBAPP_CACHE_FILE_KEY;
import static org.apache.commons.io.FileUtils.readLines;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import edu.cornell.library.orcidclient.OrcidClientException;
import edu.cornell.library.orcidclient.actions.ApiScope;
import edu.cornell.library.orcidclient.auth.AccessToken;
import edu.cornell.library.orcidclient.auth.AuthorizationStateProgress;
import edu.cornell.library.orcidclient.auth.AuthorizationStateProgress.State;
import edu.cornell.library.orcidclient.auth.AuthorizationStateProgressCache;

/**
 * Keep the cache in memory as expected, but also:
 * 
 * When the cache is created, load any accessTokens from the backing file. Wrap
 * each one in a progress instance with a Success state.
 * 
 * When an item is stored or cleared, rewrite the backing file with all of the
 * current access token.
 */
public class WebappCache implements AuthorizationStateProgressCache {

	// ----------------------------------------------------------------------
	// The factory
	// ----------------------------------------------------------------------

	private volatile static WebappCache instance;

	/**
	 * The first time someone asks for the cache, create an instance. Don't do
	 * it sooner, in case WebappSetup has not run yet.
	 */
	public synchronized static WebappCache getCache()
			throws OrcidClientException {
		if (instance == null) {
			instance = new WebappCache();
		}
		return instance;
	}

	// ----------------------------------------------------------------------
	// The instance
	// ----------------------------------------------------------------------

	private Optional<String> cacheFilePath;
	private Map<String, AuthorizationStateProgress> progressMap = new HashMap<>();

	public WebappCache() throws OrcidClientException {
		locateBackingFile();

		if (!backingFileExists()) {
			createBackingFile();
		}

		readFromBackingFile();
	}

	private void locateBackingFile() {
		String path = WebappSetup.getWebappProperties()
				.get(WEBAPP_CACHE_FILE_KEY);
		if (path != null) {
			path = path.replaceFirst("^~", System.getProperty("user.home"));
		}
		cacheFilePath = Optional.ofNullable(path);
	}

	private boolean backingFileExists() throws OrcidClientException {
		if (!cacheFilePath.isPresent()) {
			return true;
		}

		File cacheFile = new File(cacheFilePath.get());
		if (!cacheFile.exists()) {
			return false;
		}

		if (!cacheFile.canWrite()) {
			throw new OrcidClientException(
					"Can't write to WebappCache backing file: "
							+ cacheFile.getAbsolutePath());
		}

		return true;
	}

	private void createBackingFile() throws OrcidClientException {
		File cacheFile = new File(cacheFilePath.get());
		File parent = cacheFile.getParentFile();
		if (!parent.isDirectory()) {
			throw new OrcidClientException("Can't create WebappCache "
					+ "backing file - parent directory does not exist: "
					+ cacheFile.getAbsolutePath());
		}

		if (!parent.canWrite()) {
			throw new OrcidClientException("Can't create WebappCache "
					+ "backing file - can't write to parent directory: "
					+ cacheFile.getAbsolutePath());
		}

		try {
			boolean created = cacheFile.createNewFile();
			if (!created) {
				throw new OrcidClientException(
						"Failed to create the WebappCache "
								+ "backing file - don't know why: "
								+ cacheFile.getAbsolutePath());
			}
		} catch (IOException e) {
			throw new OrcidClientException("Failed to create the WebappCache "
					+ "backing file: " + cacheFile.getAbsolutePath(), e);
		}
	}

	private void readFromBackingFile() throws OrcidClientException {
		if (!cacheFilePath.isPresent()) {
			return;
		}

		File cacheFile = new File(cacheFilePath.get());
		try {
			List<String> lines = readLines(cacheFile, "UTF-8");
			for (String line : lines) {
				AccessToken token = AccessToken.parse(line);
				ApiScope scope = token.getScope();
				AuthorizationStateProgress progress = AuthorizationStateProgress
						.create(scope, NO_URI, NO_URI).addAccessToken(token);
				progressMap.put(progress.getId(), progress);
			}
		} catch (IOException e) {
			throw new OrcidClientException(
					"Failed to load the cache backing file", e);
		}
	}

	private void writeToBackingFile() throws OrcidClientException {
		if (!cacheFilePath.isPresent()) {
			return;
		}

		File cacheFile = new File(cacheFilePath.get());
		try (PrintWriter out = new PrintWriter(cacheFile, "UTF-8")) {
			for (AuthorizationStateProgress progress : progressMap.values()) {
				if (progress.getState() == State.SUCCESS) {
					out.println(progress.getAccessToken().getJsonString());
				}
			}
		} catch (IOException e) {
			throw new OrcidClientException(
					"Failed to write the cache backing file", e);
		}
	}

	// ----------------------------------------------------------------------
	// The basic functionality
	// ----------------------------------------------------------------------

	@Override
	public void store(AuthorizationStateProgress progress)
			throws OrcidClientException {
		clearScopeProgress(progress.getScope());
		progressMap.put(progress.getId(), progress);
		writeToBackingFile();
	}

	@Override
	public AuthorizationStateProgress getByID(String id) {
		return progressMap.get(id);
	}

	@Override
	public AuthorizationStateProgress getByScope(ApiScope scope) {
		for (AuthorizationStateProgress progress : progressMap.values()) {
			if (scope == progress.getScope()) {
				return progress;
			}
		}
		return null;
	}

	@Override
	public void clearScopeProgress(ApiScope scope) throws OrcidClientException {
		Iterator<AuthorizationStateProgress> it = progressMap.values()
				.iterator();
		while (it.hasNext()) {
			if (it.next().getScope() == scope) {
				it.remove();
			}
		}
		writeToBackingFile();
	}

	@Override
	public String toString() {
		return String.format("WebappCache[cacheFilePath=%s, progressMap=%s]",
				cacheFilePath, progressMap);
	}
	
	// ----------------------------------------------------------------------
	// Extra functions
	// ----------------------------------------------------------------------

	public List<AuthorizationStateProgress> getProgressList() {
		return new ArrayList<>(progressMap.values());
	}
}
