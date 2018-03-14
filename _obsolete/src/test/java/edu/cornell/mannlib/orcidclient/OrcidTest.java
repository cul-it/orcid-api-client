package edu.cornell.mannlib.orcidclient;

import edu.cornell.mannlib.orcidclient.actions.ApiAction;
import edu.cornell.mannlib.orcidclient.auth.AuthorizationManager;
import edu.cornell.mannlib.orcidclient.context.OrcidClientContext;
import org.junit.Test;

import java.util.EnumMap;
import java.util.Map;

import static edu.cornell.mannlib.orcidclient.context.OrcidClientContext.Setting.*;
import static edu.cornell.mannlib.orcidclient.context.OrcidClientContext.Setting.CALLBACK_PATH;
import static edu.cornell.mannlib.orcidclient.context.OrcidClientContext.Setting.WEBAPP_BASE_URL;

public class OrcidTest {
    @Test
    public void test() {
        Map<OrcidClientContext.Setting, String> settings  = new EnumMap<>(OrcidClientContext.Setting.class);

        settings.put(CLIENT_ID,       "APP-1SAGTJHUUYJ6OQB4");
        settings.put(CLIENT_SECRET,   "56239b44-da17-4dc0-b5d5-e877ed0bb10f");
        settings.put(API_VERSION,     "2.0");
        settings.put(API_ENVIRONMENT, "sandbox");
        settings.put(WEBAPP_BASE_URL, "http://localhost:8080/vivo");
        settings.put(CALLBACK_PATH,   "orcid/callback");

        try {
            OrcidClientContext.initialize(settings);
            OrcidClientContext occ = OrcidClientContext.getInstance();

            AuthorizationManager authManager = occ.getAuthorizationManager(null);
            String seekUrl = authManager.seekAuthorization(ApiAction.READ_PROFILE, null, null);

        } catch (OrcidClientException oce) {

        }
    }
}
