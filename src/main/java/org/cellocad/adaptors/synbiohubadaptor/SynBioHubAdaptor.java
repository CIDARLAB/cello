package org.cellocad.adaptors.synbiohubadaptor;

import java.net.URI;
import java.util.HashMap;
import java.util.Set;

import java.lang.UnsupportedOperationException;
import org.cellocad.MIT.dnacompiler.GateLibrary;
import org.cellocad.MIT.dnacompiler.Part;
import org.cellocad.MIT.dnacompiler.PartLibrary;
import org.sbolstandard.core2.ComponentDefinition;
import org.sbolstandard.core2.SBOLDocument;
import org.sbolstandard.core2.SequenceOntology;
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

    public SynBioHubAdaptor() {
        partLibrary = new PartLibrary();
        fetchCelloSBOL();
        createLibraries();
    }

    private void fetchCelloSBOL() {
        SynBioHubFrontend sbh = new SynBioHubFrontend("https://synbiohub.programmingbiology.org");

        URI u = URI.create("https://synbiohub.programmingbiology.org/public/Cello_Parts/Cello_Parts_collection/1");

        try {
            celloSBOL = sbh.getSBOL(u);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void createLibraries() {
        HashMap<String,Part> allParts = new HashMap();

        Set<ComponentDefinition> celloCD = celloSBOL.getComponentDefinitions();

        for (ComponentDefinition cd : celloCD) {
            // i dont know why there would be more than one type per part
            // in the cello parts there is one type per part, so just grab the first type below
            URI type = cd.getTypes().iterator().next();
            if (type.equals(URI.create("http://www.biopax.org/release/biopax-level3.owl#DnaRegion"))) {
                URI role = cd.getRoles().iterator().next();
                if (role.equals(SequenceOntology.ENGINEERED_REGION)) { // if the CD is a gate
                    // GateLibrary gate_library = new GateLibrary(n_inputs, n_outputs);
                    //read gates and create gate objects
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

                    allParts.put(name, new Part(name, roleString, seq));
                }
            }
        }
        partLibrary.set_ALL_PARTS(allParts);
    }

    /**
     * @return the SBOLDocument containing the parts library
     */
    public SBOLDocument getCelloSBOL() {
        if (celloSBOL == null)
            fetchCelloSBOL();

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
        if ( gateLibrary == null ) {
            throw(new UnsupportedOperationException("Not yet implemented."));
        }
        return gateLibrary;
    }
}
