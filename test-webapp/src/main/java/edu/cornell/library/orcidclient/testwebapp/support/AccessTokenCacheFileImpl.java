package edu.cornell.library.orcidclient.testwebapp.support;

import static edu.cornell.library.orcidclient.testwebapp.support.WebappSetup.WEBAPP_CACHE_FILE_KEY;
import static org.apache.commons.io.FileUtils.readLines;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import edu.cornell.library.orcidclient.actions.ApiScope;
import edu.cornell.library.orcidclient.auth.AccessToken;
import edu.cornell.library.orcidclient.auth.AccessTokenCache;
import edu.cornell.library.orcidclient.exceptions.OrcidClientException;

/**
 * TODO
 */
public class AccessTokenCacheFileImpl implements AccessTokenCache {
	private final static String SESSION_ATTRIBUTE_KEY = AccessTokenCacheFileImpl.class
			.getName();

	// ----------------------------------------------------------------------
	// Factory
	// ----------------------------------------------------------------------

	public static AccessTokenCacheFileImpl instance(HttpServletRequest req)
			throws OrcidClientException {
		return instance(req.getSession());
	}

	public static AccessTokenCacheFileImpl instance(HttpSession session)
			throws OrcidClientException {
		Object attribute = session.getAttribute(SESSION_ATTRIBUTE_KEY);
		if (attribute instanceof AccessTokenCacheFileImpl) {
			return (AccessTokenCacheFileImpl) attribute;
		} else {
			AccessTokenCacheFileImpl cache = new AccessTokenCacheFileImpl();
			session.setAttribute(SESSION_ATTRIBUTE_KEY, cache);
			return cache;
		}
	}

	// ----------------------------------------------------------------------
	// The instance
	// ----------------------------------------------------------------------

	private Optional<String> cacheFilePath;
	private Map<ApiScope, AccessToken> tokenMap = new HashMap<>();

	public AccessTokenCacheFileImpl() throws OrcidClientException {
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
					"Can't write to AccessTokenCache backing file: "
							+ cacheFile.getAbsolutePath());
		}

		return true;
	}

	private void createBackingFile() throws OrcidClientException {
		File cacheFile = new File(cacheFilePath.get());
		File parent = cacheFile.getParentFile();
		if (!parent.isDirectory()) {
			throw new OrcidClientException("Can't create the AccessTokenCache "
					+ "backing file - parent directory does not exist: "
					+ cacheFile.getAbsolutePath());
		}

		if (!parent.canWrite()) {
			throw new OrcidClientException("Can't create the AccessTokenCache "
					+ "backing file - can't write to parent directory: "
					+ cacheFile.getAbsolutePath());
		}

		try {
			boolean created = cacheFile.createNewFile();
			if (!created) {
				throw new OrcidClientException(
						"Failed to create the AccessTokenCache "
								+ "backing file - don't know why: "
								+ cacheFile.getAbsolutePath());
			}
		} catch (IOException e) {
			throw new OrcidClientException(
					"Failed to create the AccessTokenCache " + "backing file: "
							+ cacheFile.getAbsolutePath(),
					e);
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
				tokenMap.put(token.getScope(), token);
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
			for (AccessToken token : tokenMap.values()) {
				out.println(token.getJsonString());
			}
		} catch (IOException e) {
			throw new OrcidClientException(
					"Failed to write the cache backing file", e);
		}
	}

	@Override
	public void addAccessToken(AccessToken token) throws OrcidClientException {
		tokenMap.put(token.getScope(), token);
		writeToBackingFile();
	}

	@Override
	public AccessToken getToken(ApiScope scope) throws OrcidClientException {
		return tokenMap.get(scope);
	}

	@Override
	public void removeAccessToken(AccessToken accessToken)
			throws OrcidClientException {
		tokenMap.values().remove(accessToken);
		writeToBackingFile();
	}

	@Override
	public String toString() {
		return String.format(
				"AccessTokenCacheFileImpl[cacheFilePath=%s, tokenMap=%s]",
				cacheFilePath, tokenMap);
	}

	/** NOTE: specific to this class, not to the interface. */
	public List<AccessToken> getAccessTokens() {
		return new ArrayList<>(tokenMap.values());
	}

	/** NOTE: specific to this class, not to the interface. */
	public void clear() throws OrcidClientException {
		tokenMap.clear();
		writeToBackingFile();
	}
}
