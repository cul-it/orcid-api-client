package edu.cornell.library.orcidclient.auth;

import static edu.cornell.library.orcidclient.actions.ApiScope.AUTHENTICATE;
import static edu.cornell.library.orcidclient.actions.ApiScope.READ_PUBLIC;
import static edu.cornell.library.orcidclient.auth.AccessToken.NO_TOKEN;
import static edu.cornell.library.orcidclient.auth.OauthProgress.FailureCause.UNKNOWN;
import static edu.cornell.library.orcidclient.auth.OauthProgress.FailureDetails.NO_FAILURE;
import static edu.cornell.library.orcidclient.auth.OauthProgress.State.FAILURE;
import static edu.cornell.library.orcidclient.auth.OauthProgress.State.NONE;
import static edu.cornell.library.orcidclient.auth.OauthProgress.State.SEEKING_ACCESS_TOKEN;
import static edu.cornell.library.orcidclient.auth.OauthProgress.State.SEEKING_AUTHORIZATION;
import static edu.cornell.library.orcidclient.auth.OauthProgress.State.SUCCESS;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import java.lang.reflect.Field;
import java.net.URI;
import java.net.URISyntaxException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import edu.cornell.library.orcidclient.actions.ApiScope;
import edu.cornell.library.orcidclient.auth.OauthProgress.FailureCause;
import edu.cornell.library.orcidclient.auth.OauthProgress.FailureDetails;
import edu.cornell.library.orcidclient.auth.OauthProgress.State;
import edu.cornell.library.orcidclient.exceptions.OrcidClientException;
import edu.cornell.library.orcidclient.testing.AbstractTestClass;

/**
 * If I were more rigorous, these methods would be more restricted.
 * 
 * For example, not allowed to call addState() with FAILURE, not allowed to call
 * addAccessToken() unless SEEKING_ACCESS_TOKEN, etc.
 * 
 * In each case, confirm that the original instance was not changed by the
 * operation.
 */
public class OauthProgressTest extends AbstractTestClass {
	private static final URI URI_1 = uri("http://uri1");
	private static final URI URI_2 = uri("http://uri2");
	private static final URI URI_3 = uri("http://uri3");

	private static final String EXPECTED_SLAPPED_TOGETHER_TOKEN = "" //
			+ "AccessToken[" //
			+ "jsonString=\"NO_JSON_STRING\", " //
			+ "token=NO_TOKEN, " //
			+ "type=NO_TYPE, " //
			+ "refreshToken=NO_REFRESH_TOKEN, " //
			+ "expiresIn=-1, " //
			+ "scope=null, " //
			+ "name=NO_NAME, " //
			+ "orcid=NO_ORCID]";
	private static final String EXPECTED_SLAPPED_TOGETHER = "" //
			+ "OauthProgress[" //
			+ "id=1919892312, " //
			+ "state=NONE, " //
			+ "failureDetails=FailureDetails[cause=NONE, describe()=No failure], " //
			+ "scope=READ_PUBLIC, " //
			+ "successUrl=http://uri1, " //
			+ "failureUrl=http://uri2, " //
			+ "deniedUrl=http://uri3, " //
			+ "accessToken=" + EXPECTED_SLAPPED_TOGETHER_TOKEN + ", " //
			+ "authorizationCode=auth_code]";

	private static final AccessToken NEW_TOKEN = accessToken("" //
			+ "{" //
			+ "\"access_token\":\"89f0181c-168b-4d7d-831c-1fdda2d7bbbb\", " //
			+ "\"token_type\":\"bearer\", " //
			+ "\"refresh_token\":\"69e883f6-d84e-4ae6-87f5-ef0044e3e9a7\", " //
			+ "\"expires_in\":631138518, " //
			+ "\"scope\":\"/authenticate\", " //
			+ "\"orcid\":\"0000-0001-2345-6789\", " //
			+ "\"name\":\"Sofia Garcia \"" //
			+ "}");

	private OauthProgress initial;
	private String initialString;
	private OauthProgress modified;

	@Before
	public void setup() {
		initial = slapTogether(READ_PUBLIC, URI_1, URI_2, URI_3, NONE,
				NO_FAILURE, NO_TOKEN, "auth_code");
		initialString = initial.toString();
	}

	@After
	public void confirmImmutable() {
		assertEquals("mutated", initialString, initial.toString());
	}

	// ----------------------------------------------------------------------
	// The tests
	// ----------------------------------------------------------------------

	@Test
	public void confirmThatIdsAreDifferent() {
		// IDs are different every time.
		assertNotEquals(
				new OauthProgress(AUTHENTICATE, URI_1, URI_2, URI_3).getId(),
				new OauthProgress(AUTHENTICATE, URI_1, URI_2, URI_3).getId());
	}

	@Test
	public void confirmThat_slapTogether_works() {
		// Don't compare IDs, because they are different every time.
		assertEquals(removeId(EXPECTED_SLAPPED_TOGETHER),
				removeId(initial.toString()));
	}

	@Test
	public void addState_setsOnlyState() {
		modified = initial.copy();
		modified.addState(SEEKING_AUTHORIZATION);
		assertProgress(SEEKING_AUTHORIZATION, null, null, null);
	}

	@Test
	public void addCode_setsStateAndCode() {
		modified = initial.copy();
		modified.addCode("new_code");
		assertProgress(SEEKING_ACCESS_TOKEN, null, null, "new_code");
	}

	@Test
	public void addAccessToken_setsStateAndToken() {
		modified = initial.copy();
		modified.addAccessToken(NEW_TOKEN);
		assertProgress(SUCCESS, null, NEW_TOKEN, null);
	}

	@Test
	public void addFailure_setsStateAndDetails() {
		modified = initial.copy();
		modified.addFailure(new ExampleFailureDetails());
		assertProgress(FAILURE, UNKNOWN, null, null);
	}

	// ----------------------------------------------------------------------
	// Helper methods
	// ----------------------------------------------------------------------

	private OauthProgress slapTogether(ApiScope scope, URI successUrl,
			URI failureUrl, URI deniedUrl, State state,
			FailureDetails failureDetails, AccessToken accessToken,
			String authorizationCode) {
		OauthProgress oap = new OauthProgress(scope, successUrl, failureUrl,
				deniedUrl);
		setFieldByReflection(oap, "state", state);
		setFieldByReflection(oap, "failureDetails", failureDetails);
		setFieldByReflection(oap, "accessToken", accessToken);
		setFieldByReflection(oap, "authorizationCode", authorizationCode);
		return oap;
	}

	private void setFieldByReflection(OauthProgress progress, String name,
			Object value) {
		try {
			Field f = OauthProgress.class.getDeclaredField(name);
			f.setAccessible(true);
			f.set(progress, value);
		} catch (Exception e) {
			throw new RuntimeException(
					"Failed to set the value of '" + name + "'.", e);
		}
	}

	private void assertProgress(State state, FailureCause failureCause,
			AccessToken accessToken, String authorizationCode) {
		assertEquals("scope", initial.getScope(), modified.getScope());
		assertEquals("successUrl", initial.successUrl, modified.successUrl);
		assertEquals("failureUrl", initial.failureUrl, modified.failureUrl);
		assertEquals("deniedUrl", initial.deniedUrl, modified.deniedUrl);
		assertEquals("state", notNull(state, initial.getState()),
				modified.getState());
		assertEquals("failureCause",
				notNull(failureCause, initial.getFailureCause()),
				modified.getFailureCause());
		assertEquals("accessToken",
				notNull(accessToken, initial.getAccessToken()),
				modified.getAccessToken());
		assertEquals("authorizationCode",
				notNull(authorizationCode, initial.getAuthorizationCode()),
				modified.getAuthorizationCode());
	}

	private <T> T notNull(T preferredValue, T defaultValue) {
		return (preferredValue != null) ? preferredValue : defaultValue;
	}

	private static URI uri(String string) {
		try {
			return new URI(string);
		} catch (URISyntaxException e) {
			throw new RuntimeException(e);
		}
	}

	private static AccessToken accessToken(String json) {
		try {
			return AccessToken.parse(json);
		} catch (OrcidClientException e) {
			throw new RuntimeException(e);
		}
	}

	private static String removeId(String raw) {
		return raw.replaceFirst("id=\\d+", "id=99999999");
	}

	// ----------------------------------------------------------------------
	// Helper classes
	// ----------------------------------------------------------------------

	private static class ExampleFailureDetails extends FailureDetails {
		public ExampleFailureDetails() {
			super(UNKNOWN);
		}

		@Override
		public String describe() {
			return "ExampleFailureDetails[]";
		}
	}
}
