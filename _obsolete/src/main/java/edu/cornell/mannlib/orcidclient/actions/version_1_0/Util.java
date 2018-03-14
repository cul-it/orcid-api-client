package edu.cornell.mannlib.orcidclient.actions.version_1_0;

import edu.cornell.mannlib.orcidclient.model.ExternalIdentifier;
import edu.cornell.mannlib.orcidclient.model.ExternalIdentifiers;
import edu.cornell.mannlib.orcidclient.model.OrcidBio;
import edu.cornell.mannlib.orcidclient.model.OrcidId;
import edu.cornell.mannlib.orcidclient.model.OrcidProfile;
import edu.cornell.mannlib.orcidclient.responses.message_1_2.OrcidMessage;

import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;
import java.util.ArrayList;
import java.util.List;

public class Util {
    public static OrcidProfile toModel(OrcidMessage om) {
        if (om != null && om.getOrcidProfile() != null) {
            OrcidProfile profile = new OrcidProfile();

            if (om.getOrcidProfile().getOrcidBio() != null) {
                OrcidBio bio = new OrcidBio();

                if (om.getOrcidProfile().getOrcidBio().getExternalIdentifiers() != null) {
                    List<ExternalIdentifier> idList = new ArrayList<ExternalIdentifier>();

                    for (edu.cornell.mannlib.orcidclient.responses.message_1_2.ExternalIdentifier oldId : om.getOrcidProfile().getOrcidBio().getExternalIdentifiers().getExternalIdentifier()) {
                        ExternalIdentifier newId = new ExternalIdentifier();

                        if (oldId.getExternalIdCommonName() != null) {
                            newId.setExternalIdCommonName(oldId.getExternalIdCommonName().getContent());
                        }
                        if (oldId.getExternalIdReference() != null) {
                            newId.setExternalIdReference(oldId.getExternalIdReference().getContent());
                        }
                        if (oldId.getExternalIdUrl() != null) {
                            newId.setExternalIdUrl(oldId.getExternalIdUrl().getValue());
                        }

                        idList.add(newId);
                    }

                    bio.setExternalIdentifiers(idList);
                }

                profile.setOrcidBio(bio);
            }

            ;

            if (om.getOrcidProfile().getOrcidIdentifier() != null) {
                OrcidId oid = new OrcidId();

                for (JAXBElement<String> content : om.getOrcidProfile().getOrcidIdentifier().getContent()) {
                    if ("path".equalsIgnoreCase(content.getName().getLocalPart())) {
                        oid.setPath(content.getValue());
                    }
                    if ("uri".equalsIgnoreCase(content.getName().getLocalPart())) {
                        oid.setUri(content.getValue());
                    }
                }

                profile.setOrcidIdentifier(oid);
            }

            return profile;
        }

        return null;
    }
}
