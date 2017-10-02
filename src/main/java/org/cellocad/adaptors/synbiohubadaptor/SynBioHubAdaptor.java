package org.cellocad.adaptors.synbiohubadaptor;

import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.util.HashMap;
import java.util.Set;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.cellocad.MIT.dnacompiler.Gate;
import org.cellocad.MIT.dnacompiler.GateLibrary;
import org.cellocad.MIT.dnacompiler.Part;
import org.cellocad.MIT.dnacompiler.PartLibrary;
import org.sbolstandard.core2.Annotation;
import org.sbolstandard.core2.ComponentDefinition;
import org.sbolstandard.core2.SBOLDocument;
import org.sbolstandard.core2.SequenceOntology;
import org.synbiohub.frontend.SynBioHubException;
import org.synbiohub.frontend.SynBioHubFrontend;

/**
 * An adaptor to build the parts library from SynBioHub.
 *
 * @author: Tim Jones
 */
public class SynBioHubAdaptor {
    private SBOLDocument celloSBOL;
    private PartLibrary partLibrary;
    private GateLibrary gateLibrary;

    private SynBioHubFrontend sbh;

    public SynBioHubAdaptor() throws SynBioHubException, IOException {
        partLibrary = new PartLibrary();
        gateLibrary = new GateLibrary(2,1);

        sbh = new SynBioHubFrontend("https://synbiohub.programmingbiology.org");

        URI u = URI.create("https://synbiohub.programmingbiology.org/public/Cello_Parts/Cello_Parts_collection/1");

        celloSBOL = sbh.getSBOL(u);

        createLibraries();
    }

    private void createLibraries() throws SynBioHubException, IOException {
        HashMap<String,Part> allParts = new HashMap();

        Set<ComponentDefinition> celloCD = celloSBOL.getComponentDefinitions();

        // Set<ModuleDefinition> celloMD = celloSBOL.getModuleDefinitions();
        // System.out.println(celloMD.size());
        // System.out.println(celloMD.iterator().next().getFunctionalComponents());

        for (ComponentDefinition cd : celloCD) {
            // i dont know why there would be more than one type per part
            // in the cello parts there is one type per part, so just grab the first type below
            URI type = cd.getTypes().iterator().next();

            if (type.equals(URI.create("http://www.biopax.org/release/biopax-level3.owl#DnaRegion"))) {
                URI role = cd.getRoles().iterator().next();

                if (role.equals(SequenceOntology.ENGINEERED_REGION)) { // if the CD is a gate

                    Gate g = new Gate();
                    g.name = cd.getName();

                    // if a gate on synbiohub ever had more than one
                    // toxicity attachment, the last one would be what
                    // the gate gets here.
                    for (Annotation a : cd.getAnnotations()) {
                        String annotationType = a.getQName().getLocalPart();
                        if (annotationType == "gate_type") {
                            g.type = Gate.GateType.valueOf(a.getStringValue());
                        }
                        if (annotationType == "group-name") {
                            g.group = a.getStringValue();
                        }
                        if (annotationType == "family") {
                            g.system = a.getStringValue();
                        }
                        if (annotationType == "gate-color-hexcode") {
                            g.colorHex = a.getStringValue();
                        }
                        if (annotationType == "attachment") {

                            URI attachmentUri = a.getURIValue();

                            // http://lifelongprogrammer.blogspot.com/2014/11/handling-gzip-response-in-apache.html
                            if (sbh.getSBOL(attachmentUri).getGenericTopLevel(attachmentUri).getName().contains("toxicity")) {
                                URL url = new URL(attachmentUri.toString() + "/download");
                                HttpClientBuilder builder = HttpClientBuilder.create();
                                CloseableHttpClient httpClient = builder.build();
                                HttpGet httpGet = new HttpGet(url.toString());
                                HttpResponse httpResponse = httpClient.execute(httpGet);
                                String toxicityJSON = EntityUtils.toString(httpResponse.getEntity());
                                // setGateToxicity(toxicityJSON);
                            }
                        }
                    }

                    gateLibrary.get_GATES_BY_NAME().put(g.name, g);
                    gateLibrary.setHashMapsForGates();

                } else { // otherwise it's a part
                    String name = cd.getName();
                    String roleString = "NOT_SET";
                    if (role.equals(SequenceOntology.PROMOTER))
                        roleString = "promoter";
                    if (role.equals(SequenceOntology.CDS))
                        roleString = "cds";
                    if (role.equals(SequenceOntology.RIBOSOME_ENTRY_SITE))
                        roleString = "rbs";
                    if (role.equals(SequenceOntology.TERMINATOR))
                        roleString = "terminator";
                    if (role.equals(URI.create("http://identifiers.org/so/SO:0000374")))
                        roleString = "ribozyme";
                    if (role.equals(URI.create("http://identifiers.org/so/SO:0001953")))
                        roleString = "scar";

                    String seq = cd.getSequences().iterator().next().getElements();

                    allParts.put(name, new Part(name, roleString, seq, cd.getIdentity()));
                }
            }
        }
        partLibrary.set_ALL_PARTS(allParts);
    }

    /**
     * @return the SBOLDocument containing the SBOL of an arbitrary URI
     */
    public SBOLDocument getSBOL(URI uri) throws SynBioHubException {
        return sbh.getSBOL(uri);
    }

    /**
     * @return the SBOLDocument containing the parts library
     */
    public SBOLDocument getCelloSBOL() {
        return celloSBOL;
    }

    /**
     * @return the PartLibrary object that was build from the SBOL
     */
    public PartLibrary getPartLibrary() {
        return partLibrary;
    }

    /**
     * @return the gateLibrary
     */
    public GateLibrary getGateLibrary() {
        return gateLibrary;
    }
}
