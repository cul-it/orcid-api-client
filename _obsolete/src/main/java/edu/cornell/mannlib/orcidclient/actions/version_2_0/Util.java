package edu.cornell.mannlib.orcidclient.actions.version_2_0;

import edu.cornell.mannlib.orcidclient.model.ExternalIdentifier;
import edu.cornell.mannlib.orcidclient.model.OrcidBio;
import edu.cornell.mannlib.orcidclient.model.OrcidId;
import edu.cornell.mannlib.orcidclient.model.OrcidProfile;
import edu.cornell.mannlib.orcidclient.responses.message_2_0.OrcidExternalIdentifier;
import edu.cornell.mannlib.orcidclient.responses.message_2_0.OrcidPerson;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;

import javax.naming.Name;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

public class Util {
    private static final CloseableHttpClient httpClient =
            HttpClientBuilder.create().setMaxConnPerRoute(50).setMaxConnTotal(300).build();

    /**
     * Read JSON from the URL
     * @param url
     * @return
     */
    public static String readJSON(String url, List<NameValuePair> headers) {
        try {
            HttpGet request = new HttpGet(url);
//            if (nvps != null) {
//                request.setEntity(new UrlEncodedFormEntity(nvps));
//            }

            // Content negotiate for csl / citeproc JSON
            request.setHeader("Accept", "application/json");
            if (headers != null) {
                for (NameValuePair header : headers) {
                    request.setHeader(header.getName(), header.getValue());
                }
            }

            HttpResponse response = httpClient.execute(request);
            switch (response.getStatusLine().getStatusCode()) {
                case 200:
                    try (InputStream in = response.getEntity().getContent()) {
                        StringWriter writer = new StringWriter();
                        IOUtils.copy(in, writer, "UTF-8");
                        return writer.toString();
                    }
            }
        } catch (IOException e) {
        }

        return null;
    }

    public static OrcidProfile toModel(OrcidPerson om) {
        if (om != null) {
            OrcidProfile profile = new OrcidProfile();
            OrcidBio bio = new OrcidBio();

            if (om.getExternalIdentifiers() != null) {
                List<ExternalIdentifier> idList = new ArrayList<ExternalIdentifier>();

                for (OrcidExternalIdentifier oldId : om.getExternalIdentifiers().getExternalIdentifiers()) {
                    ExternalIdentifier newId = new ExternalIdentifier();

                    if (oldId.getExtCommonName() != null) {
                        newId.setExternalIdCommonName(oldId.getExtCommonName());
                    }
                    if (oldId.getExtReference() != null) {
                        newId.setExternalIdReference(oldId.getExtReference());
                    }
                    if (oldId.getExtUrl() != null) {
                        newId.setExternalIdUrl(oldId.getExtUrl().getValue());
                    }

                    idList.add(newId);
                }

                bio.setExternalIdentifiers(idList);
            }
            profile.setOrcidBio(bio);

            if (om.getName() != null) {
                OrcidId oid = new OrcidId();

                oid.setPath(om.getName().getPath());
                oid.setUri("http://orcid.org/" + om.getName().getPath());
                profile.setOrcidIdentifier(oid);
            }

            return profile;
        }

        return null;
    }
}
