/* $This file is distributed under the terms of the license in /doc/license.txt$ */

package edu.cornell.libraries.orcidclient.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

/**
 * A more flexible (and type-safe) container for the parameters on an
 * HttpRequest.
 */
public class ParameterMap {
	private final Map<String, List<String>> map;

	/**
	 * Create it from an actual request.
	 */
	public ParameterMap(HttpServletRequest req) {
		Map<String, List<String>> m = new HashMap<>();

		for (Enumeration<String> names = req.getParameterNames(); names
				.hasMoreElements();) {
			String name = names.nextElement();
			m.put(name, new ArrayList<>(
					Arrays.asList(req.getParameterValues(name))));
		}

		this.map = Collections.unmodifiableMap(m);
	}
	
	/**
	 * Create it from a map of lists.
	 */
	public ParameterMap(Map<String, List<String>> sourceMap) {
		Map<String, List<String>> m = new HashMap<>();

		for (String name: sourceMap.keySet()) {
			m.put(name, new ArrayList<>(sourceMap.get(name)));
		}

		this.map = Collections.unmodifiableMap(m);
	}

	/**
	 * Returns the first value of this parameter, or null.
	 */
	public String getParameter(String key) {
		if (map.containsKey(key)) {
			List<String> values = map.get(key);
			if (values.size() > 0) {
				return values.get(0);
			}
		}
		return null;
	}

	/**
	 * Returns all values of this parameter. May be empty, but never null.
	 */
	public List<String> getParameterValues(String key) {
		if (map.containsKey(key)) {
			return new ArrayList<>(map.get(key));
		} else {
			return Collections.emptyList();
		}
	}
}
