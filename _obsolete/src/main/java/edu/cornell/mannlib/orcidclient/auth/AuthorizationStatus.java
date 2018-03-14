/* $This file is distributed under the terms of the license in /doc/license.txt$ */

package edu.cornell.mannlib.orcidclient.auth;

import static edu.cornell.mannlib.orcidclient.auth.AuthorizationStatus.State.FAILURE;
import static edu.cornell.mannlib.orcidclient.auth.AuthorizationStatus.State.NONE;
import static edu.cornell.mannlib.orcidclient.auth.AuthorizationStatus.State.SEEKING_ACCESS_TOKEN;
import static edu.cornell.mannlib.orcidclient.auth.AuthorizationStatus.State.SEEKING_AUTHORIZATION;
import static edu.cornell.mannlib.orcidclient.auth.AuthorizationStatus.State.SUCCESS;

import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;

import edu.cornell.mannlib.orcidclient.actions.ApiAction;

/**
 * Holds information about the authorization request.
 */
public class AuthorizationStatus {
	public static final Exception NO_EXCEPTION = new NoException();
	public static final Exception NULL_EXCEPTION = new Exception(
			"NULL exception");

	public enum State {
		NONE, SEEKING_AUTHORIZATION, SEEKING_ACCESS_TOKEN, FAILURE, SUCCESS
	}

	public enum ErrorCode {
		ACCESS_DENIED("access_denied") {
			@Override
			String describe(Object[] args) {
				return String.valueOf(args[0]);
			}
		},
		NO_AUTH_CODE("no_authorization_code") {
			@Override
			String describe(Object[] args) {
				return "Authorization request did not return an authorization code.";
			}
		},
		INVALID_STATE("invalid_state") {
			@Override
			String describe(Object[] args) {
				return "State was " + args[0] + ", but expecting " + args[1];
			}
		};

		final String code;

		ErrorCode(String code) {
			this.code = code;
		}

		abstract String describe(Object[] args);
	}

	private static final PermittedTransitions PERMITTED_TRANSITIONS = new PermittedTransitions();

	// ----------------------------------------------------------------------
	// The factory
	// ----------------------------------------------------------------------

	public static AuthorizationStatus empty(ApiAction action) {
		AuthorizationStatus auth = new AuthorizationStatus(action);
		auth.state = State.NONE;
		return auth;
	}

	public static AuthorizationStatus create(ApiAction action,
			String successUrl, String failureUrl) {
		AuthorizationStatus auth = new AuthorizationStatus(action);
		auth.state = State.SEEKING_AUTHORIZATION;
		auth.successUrl = successUrl;
		auth.failureUrl = failureUrl;
		return auth;
	}

	// ----------------------------------------------------------------------
	// The instance
	// ----------------------------------------------------------------------

	private final ApiAction action;
	private final String id;

	private State state = State.NONE;
	private String successUrl = "";
	private String failureUrl = "";
	private String authorizationCode = "";
	private AccessToken accessToken = AccessToken.NO_TOKEN;
	private String errorCode = "";
	private String errorDescription = "";
	private Exception exception = NO_EXCEPTION;

	private AuthorizationStatus(ApiAction action) {
		this.action = action;
		this.id = String.valueOf(hashCode());
	}

	public AuthorizationStatus(AuthorizationStatus auth) {
		this.action = auth.action;
		this.id = auth.id;
	}

	public boolean isNone() {
		return State.NONE == state;
	}

	public boolean isSeekingAuthorization() {
		return State.SEEKING_AUTHORIZATION == state;
	}

	public boolean isSeekingAccessToken() {
		return State.SEEKING_ACCESS_TOKEN == state;
	}

	public boolean isDenied() {
		return State.FAILURE == state
				&& ErrorCode.ACCESS_DENIED.code.equals(errorCode);
	}

	public boolean isFailure() {
		return State.FAILURE == state;
	}

	public boolean isSuccess() {
		return State.SUCCESS == state;
	}

	public AuthorizationStatus setSeekingAccessToken(String authorizationCode) {
		changeState(SEEKING_ACCESS_TOKEN);
		this.authorizationCode = authorizationCode;
		return this;
	}

	public AuthorizationStatus setSuccess(AccessToken accessToken) {
		changeState(SUCCESS);
		this.accessToken = accessToken;
		return this;
	}

	public AuthorizationStatus setFailure(String errorCode,
			String errorDescription) {
		changeState(FAILURE);
		this.errorCode = nonNull(errorCode, "NULL error code");
		this.errorDescription = nonNull(errorDescription, "NULL description");
		return this;
	}

	public AuthorizationStatus setFailure(String errorDescription,
			Exception exception) {
		changeState(FAILURE);
		this.errorDescription = nonNull(errorDescription, "NULL description");
		this.exception = nonNull(exception, NULL_EXCEPTION);
		return this;
	}

	public AuthorizationStatus setFailure(ErrorCode error, Object... args) {
		changeState(FAILURE);
		this.errorCode = error.code;
		this.errorDescription = error.describe(args);
		return this;
	}

	private void changeState(State newState) {
		if (PERMITTED_TRANSITIONS.isPermitted(state, newState)) {
			state = newState;
		} else {
			throw new IllegalStateException("Can't change from " + state
					+ " to " + newState);
		}
	}

	private <T> T nonNull(T value, T defaultValue) {
		return (value == null) ? defaultValue : value;
	}

	public ApiAction getAction() {
		return action;
	}

	public String getId() {
		return id;
	}

	public State getState() {
		return state;
	}

	public String getSuccessUrl() {
		return successUrl;
	}

	public String getFailureUrl() {
		return failureUrl;
	}

	public String getAuthorizationCode() {
		return authorizationCode;
	}

	public AccessToken getAccessToken() {
		return accessToken;
	}

	public String getErrorCode() {
		return errorCode;
	}

	public String getErrorDescription() {
		return errorDescription;
	}

	public Exception getException() {
		return exception;
	}

	@Override
	public String toString() {
		return "AuthorizationStatus [action=" + action + ", id=" + id
				+ ", state=" + state + ", successUrl=" + successUrl
				+ ", failureUrl=" + failureUrl + ", authorizationCode="
				+ authorizationCode + ", accessToken=" + accessToken
				+ ", errorCode=" + errorCode + ", errorDescription="
				+ errorDescription + ", exception=" + exception + "]";
	}

	private static class PermittedTransitions {
		private final Map<State, Set<State>> map;

		PermittedTransitions() {
			map = new EnumMap<>(State.class);
			map.put(NONE, EnumSet.of(FAILURE));
			map.put(SEEKING_AUTHORIZATION,
					EnumSet.of(SEEKING_ACCESS_TOKEN, FAILURE));
			map.put(SEEKING_ACCESS_TOKEN, EnumSet.of(SUCCESS, FAILURE));
			map.put(SUCCESS, EnumSet.of(FAILURE));
			map.put(FAILURE, EnumSet.noneOf(State.class));
		}

		public boolean isPermitted(State state, State newState) {
			return map.get(state).contains(newState);
		}

	}

	private static class NoException extends Exception {

		@Override
		public String getMessage() {
			return "No exception";
		}

		@Override
		public String toString() {
			return "No exception";
		}

	}

}
