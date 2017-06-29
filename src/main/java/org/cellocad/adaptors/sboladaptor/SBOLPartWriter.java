package org.cellocad.adaptors.sboladaptor;


import org.cellocad.MIT.dnacompiler.Args;
import org.cellocad.MIT.dnacompiler.Part;
import org.cellocad.MIT.dnacompiler.PartLibrary;
import org.cellocad.MIT.dnacompiler.UCF;
import org.cellocad.adaptors.ucfadaptor.UCFAdaptor;
import org.cellocad.adaptors.ucfadaptor.UCFReader;
import org.sbolstandard.core2.ComponentDefinition;
import org.sbolstandard.core2.SBOLDocument;
import org.sbolstandard.core2.SBOLValidationException;
import org.sbolstandard.core2.Sequence;
import org.sbolstandard.core2.SequenceOntology;

import java.net.URI;
import java.util.HashMap;
import java.util.HashSet;


public class SBOLPartWriter {

    private static HashMap<String, URI> sequence_ontology_map = new HashMap<String, URI>();
    private static String _filepath = "";


    public static void main(String args[]) {

        Args options = new Args();
        _filepath = options.get_home();

        sequence_ontology_map.put("ribozyme", SequenceOntology.INSULATOR);
        sequence_ontology_map.put("rbs", SequenceOntology.FIVE_PRIME_UTR);
        sequence_ontology_map.put("cds", SequenceOntology.CDS);
        sequence_ontology_map.put("terminator", SequenceOntology.TERMINATOR);
        sequence_ontology_map.put("promoter", SequenceOntology.PROMOTER);



        UCFReader ucf_reader = new UCFReader();
        UCF ucf = ucf_reader.readAllCollections(_filepath + "/resources/UCF/Eco1C1G1T1.UCF.json");
        UCFAdaptor ucf_adaptor = new UCFAdaptor();

        PartLibrary part_library = ucf_adaptor.createPartLibrary(ucf);
	try {
	    writeSBOLParts(part_library);
	} catch (Exception e) {
	    e.printStackTrace();
	}
    }

    public static void writeSBOLParts(PartLibrary part_library) throws SBOLValidationException {

        for(Part p: part_library.get_ALL_PARTS().values()) {

            System.out.println(p.get_name());

            org.sbolstandard.core2.SBOLDocument sbolDocument = new SBOLDocument();

            sbolDocument.setDefaultURIprefix("http://cellocad.org");


            //HashSet<URI> partRoleURIs = new HashSet<URI>();
            //partRoleURIs.add(sequence_ontology_map.get(p.get_type()));

            URI partTypeURI = URI.create("http://www.biopax.org/release/biopax-level3.owl#DnaRegion");
            HashSet<URI> partTypeURIs = new HashSet<URI>();
            partTypeURIs.add(partTypeURI);


            //URI partURI = URI.create("http://www.cellocad.org/" + p.get_name());

	    ComponentDefinition cd = sbolDocument.createComponentDefinition(p.get_name(), partTypeURIs);
            String sequenceDisplayID = p.get_name() + "_sequence";
            URI encodingURI = URI.create("http://www.chem.qmul.ac.uk/iubmb/misc/naseq.html");

	    Sequence s = sbolDocument.createSequence(sequenceDisplayID, p.get_seq(), encodingURI);
	    HashSet<URI> s_set = new HashSet<URI>();
	    s_set.add(s.getIdentity());

	    cd.setSequences(s_set);
	    if (sequence_ontology_map.containsKey(p.get_type())) {
		cd.addRole(sequence_ontology_map.get(p.get_type()));
	    }

            String sbolDocumentFileName = _filepath+"/resources/sbol2_xml/parts/part_" + p.get_name() + "_SBOL.xml";

	    try {
		org.sbolstandard.core2.SBOLWriter.write(sbolDocument, sbolDocumentFileName);
	    } catch (Exception e) {
		e.printStackTrace();
	    }

        }

    }

}
