package org.cellocad.adaptors.sboladaptor;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.cellocad.MIT.dnacompiler.*;
import org.cellocad.MIT.dnacompiler.Gate.GateType;
import org.cellocad.adaptors.ucfadaptor.UCFAdaptor;
import org.cellocad.adaptors.ucfadaptor.UCFReader;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.sbolstandard.core2.*;

import javax.xml.namespace.QName;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;



// TODO Needs to be generalized for other gate types

public class SBOLGateWriter {


    private static HashMap<String, URI> sequence_ontology_map = new HashMap<String, URI>();
    private static String _filepath = "";

    public static void main(String args[]) {

        Args options = new Args();
        _filepath = options.get_home();

        sequence_ontology_map.put("ribozyme", org.sbolstandard.core.util.SequenceOntology.INSULATOR);
        sequence_ontology_map.put("rbs", URI.create("http://identifiers.org/so/SO:0000139"));
        sequence_ontology_map.put("cds", URI.create("http://identifiers.org/so/SO:0000316"));
        sequence_ontology_map.put("terminator", URI.create("http://identifiers.org/so/SO:0000141"));
        sequence_ontology_map.put("promoter", URI.create("http://identifiers.org/so/SO:0000167"));
        sequence_ontology_map.put("output", URI.create("http://www.biopax.org/release/biopax-level3.owl#DnaRegion"));



        UCFReader ucf_reader = new UCFReader();
        _ucf = ucf_reader.readAllCollections(_filepath + "/resources/UCF/Eco1C1G1T1.UCF.json");
        UCFAdaptor ucf_adaptor = new UCFAdaptor();

        GateLibrary gate_library = ucf_adaptor.createGateLibrary(_ucf, 0, 0, new Args());
        PartLibrary part_library = ucf_adaptor.createPartLibrary(_ucf);

        ucf_adaptor.setGateParts(_ucf, gate_library, part_library);
        ucf_adaptor.setResponseFunctions(_ucf, gate_library);

        for(Gate g: gate_library.get_GATES_BY_NAME().values()) {
            System.out.println(g.get_params().toString());
        }


        for(Gate g: gate_library.get_GATES_BY_NAME().values()) {
            writeSBOL(g);
        }

    }


    public static void writeSBOL(Gate g) {

        System.out.println(g.Name);

        _document = new org.sbolstandard.core2.SBOLDocument();

        _document.setDefaultURIprefix("http://cellocad.org");

        _cassetteSubComponentDefinitions = new ArrayList<ComponentDefinition>();

        ArrayList<Part> gate_parts = new ArrayList<Part>();
        gate_parts.addAll(g.get_downstream_parts().get("x"));
        gate_parts.add(g.get_regulable_promoter());


        setGateModuleDefinition(g);

        setPartComponentDefinitions(gate_parts);

        setCassetteComponentDefinition();

        setCassetteSubComponents();

        setCassetteSequenceAnnotations(gate_parts);

        setPromoterCDSFunctionalComponents();

        //setPromoterCDSInteraction();

        setMoleculeComponentDefinitionsInteractions(g);



        //setCassetteFunctionalComponent();

        //setGateModel();


        //String output_filename = "resources/sbol2_xml/gates/gate_" + g.Name + "_SBOL.xml";
        String output_filename = "resources/sbol2_xml/gates/gate_" + g.Name + ".sbol";
        try {
            org.sbolstandard.core2.SBOLWriter.write(_document, output_filename);
        }
        catch (Exception e) {
            e.printStackTrace();
        }


    }

    private static void setGateModuleDefinition(Gate g) {

        //URI gateModuleDefinitionURI = URI.create("http://cellocad.org/" + g.Name);
        URI gateRoleURI = URI.create("http://cellocad.org/Gate");

        _gate_module_definition = _document.createModuleDefinition(g.Name);
        _gate_module_definition.addRole(gateRoleURI);

        String prURI=_document.getDefaultURIprefix();
        String prPrefix="cellocad";

        _document.addNamespace( URI.create(_document.getDefaultURIprefix()), prPrefix);



        JSONObject obj = new JSONObject();
        obj.put("equation", g.get_equation());

        JSONArray variable_arr = new JSONArray();
        JSONObject variable_obj = new JSONObject();
        variable_obj.put("name", "x");
        variable_obj.put("off_threshold", g.get_variable_thresholds().get("x")[0]);
        variable_obj.put("on_threshold", g.get_variable_thresholds().get("x")[1]);
        variable_arr.add(variable_obj);
        obj.put("variables", variable_arr);

        JSONArray param_arr = new JSONArray();
        for(String param: g.get_params().keySet()) {
            JSONObject param_obj = new JSONObject();
            param_obj.put("name", param);
            param_obj.put("value", g.get_params().get(param));
            param_arr.add(param_obj);
        }
        obj.put("parameters", param_arr);

        String response_fn_string = "\n" + _gson.toJson(obj).replace("\"", "\'") + "\n";




        _gate_module_definition.createAnnotation(new QName(prURI, "response_function", prPrefix), response_fn_string);

    }


    public static void setMoleculeComponentDefinitionsInteractions(Gate g) {


        // interaction types

        Set<URI> geneticProductionTypes = new HashSet<URI>();
        geneticProductionTypes.add(URI.create("http://identifiers.org/biomodels.sbo/SBO:0000589"));

        Set<URI> inhibitionTypeURIs = new HashSet<URI>();
        inhibitionTypeURIs.add(URI.create("http://identifiers.org/biomodels.sbo/SBO:0000169"));


        //component definition types

        Set<URI> proteinTypeURIs = new HashSet<URI>();
        proteinTypeURIs.add(URI.create("http://www.biopax.org/release/biopax-level3.owl#Protein"));


        //component definitions

        String protein_name = g.Regulator + "_protein";
        ComponentDefinition protein = _document.createComponentDefinition(protein_name, proteinTypeURIs);


        //functional components

        FunctionalComponent promoter_fc = _promoterFC;

        FunctionalComponent cds_fc = _cdsFC;

        FunctionalComponent protein_fc = _gate_module_definition.createFunctionalComponent(
                protein.getDisplayId(),
                AccessType.PUBLIC,
                protein.getIdentity(),
                DirectionType.NONE
        );


        // interaction ID's

        String productionInteractionDisplayID = _cdsComponentDefinition.getDisplayId() + "_produces_" + protein.getDisplayId();

        String repressionInteractionDisplayID = protein.getDisplayId() + "_represses_" + _promoterComponentDefinition.getDisplayId();


        // interaction

        Interaction productionInteraction = _gate_module_definition.createInteraction(productionInteractionDisplayID, geneticProductionTypes);

        Interaction repressionInteraction = _gate_module_definition.createInteraction(repressionInteractionDisplayID, inhibitionTypeURIs);


        // participation

        String promoterParticipationID = _promoterComponentDefinition.getDisplayId() + "_participation";
        String cdsParticipationID = _cdsComponentDefinition.getDisplayId() + "_participation";
        String proteinParticipationID = protein.getDisplayId() + "_participation";

        //production interaction between CDS and Regulator
        Participation production_cds = productionInteraction.createParticipation(cdsParticipationID, cds_fc.getDisplayId());
        Participation production_regulator = productionInteraction.createParticipation(proteinParticipationID, protein_fc.getDisplayId());

        //repression interaction between Regulator and Promoter
        Participation repression_regulator = repressionInteraction.createParticipation(proteinParticipationID, protein_fc.getDisplayId());
        Participation repression_promoter = repressionInteraction.createParticipation(promoterParticipationID, promoter_fc.getDisplayId());


        // participation roles

        //inhibitor
        URI inhibitorRole = URI.create("http://identifiers.org/biomodels.sbo/SBO:0000020");
        repression_regulator.addRole(inhibitorRole);




        if(! g.Inducer.isEmpty()) {

            String regulation_type = "";
            if(g.Type == GateType.NOT || g.Type == GateType.NOR) {
                regulation_type = "repression";
            }
            if(g.Type == GateType.AND) {
                regulation_type = "activation";
            }


            //component definition type
            Set<URI> smallMoleculeTypeURIs = new HashSet<URI>();
            smallMoleculeTypeURIs.add(URI.create("http://www.biopax.org/release/biopax-level3.owl#SmallMolecule"));

            //component definition type
            Set<URI> complexTypeURIs = new HashSet<URI>();
            complexTypeURIs.add(URI.create("http://www.biopax.org/release/biopax-level3.owl#Complex"));



            //non-covalent complex
            //Set<URI> noncovalentRoles = new HashSet<URI>();
            //noncovalentRoles.add(URI.create("http://identifiers.org/biomodels.sbo/SBO:0000253"));

            //interaction type
            //Set<URI> noncovalentBinding = new HashSet<URI>();
            //noncovalentBinding.add(URI.create("http://identifiers.org/biomodels.sbo/SBO:0000177"));



            String inducer_name = g.Inducer;
            ComponentDefinition inducer = _document.createComponentDefinition(inducer_name, smallMoleculeTypeURIs);

            String complex_name = inducer_name + "_" + g.Regulator + "_Complex";
            ComponentDefinition complex = _document.createComponentDefinition(complex_name, complexTypeURIs);



            FunctionalComponent inducer_fc = _gate_module_definition.createFunctionalComponent(
                    inducer.getDisplayId(),
                    AccessType.PUBLIC,
                    inducer.getIdentity(),
                    DirectionType.NONE
            );

            FunctionalComponent complex_fc = _gate_module_definition.createFunctionalComponent(
                    complex.getDisplayId(),
                    AccessType.PUBLIC,
                    complex.getIdentity(),
                    DirectionType.NONE
            );


            String complexInteractionDisplayID = protein.getDisplayId() + "_binds_" + inducer.getDisplayId();


            Interaction noncovalentInteraction = _gate_module_definition.createInteraction(complexInteractionDisplayID, complexTypeURIs);



            String inducerParticipationID = inducer.getDisplayId() + "_participation";
            String complexParticipationID = complex.getDisplayId() + "_participation";

            //non-covalent interaction between Regulator and Small Molecule
            Participation complex_regulator = noncovalentInteraction.createParticipation(proteinParticipationID, protein_fc.getDisplayId());
            Participation complex_inducer = noncovalentInteraction.createParticipation(inducerParticipationID, inducer_fc.getDisplayId());
            Participation complex_complex = noncovalentInteraction.createParticipation(complexParticipationID, complex_fc.getDisplayId());


            //ligand
            URI ligandRole = URI.create("http://identifiers.org/biomodels.sbo/SBO:0000280");

            //complex
            URI complexRole = URI.create("http://identifiers.org/biomodels.sbo/SBO:0000253");

            complex_inducer.addRole(ligandRole);
            complex_regulator.addRole(ligandRole);
            complex_complex.addRole(complexRole);
        }
    }


    public static void setPartComponentDefinitions(ArrayList<Part> gate_parts) {

        URI partTypeURI = URI.create("http://www.biopax.org/release/biopax-level3.owl#DnaRegion");
        HashSet<URI> partTypeURIs = new HashSet<URI>();
        partTypeURIs.add(partTypeURI);

        for(Part p: gate_parts) {

            ///////////////////////////////////////////////////////////
            /////////////  CD
            ///////////////////////////////////////////////////////////
            ComponentDefinition cd = _document.createComponentDefinition(p.get_name(), partTypeURIs);

            ///////////////////////////////////////////////////////////
            /////////////  sequence
            ///////////////////////////////////////////////////////////
            String sequenceDisplayID = p.get_name() + "_sequence";
            URI encodingURI = URI.create("http://www.chem.qmul.ac.uk/iubmb/misc/naseq.html");
            Sequence s = _document.createSequence(sequenceDisplayID, p.get_seq(), encodingURI);
            HashSet<URI> s_set = new HashSet<URI>();
            s_set.add(s.getIdentity());
            cd.setSequences(s_set);
            if(sequence_ontology_map.containsKey(p.get_type())) {
                cd.addRole(sequence_ontology_map.get(p.get_type()));
            }


            if(p.get_type().equals("promoter")) {
                _promoterComponentDefinition = cd;
            }
            else {
                _cassetteSubComponentDefinitions.add(cd);
                System.out.println("adding subcomponent defintion " + cd.getDisplayId());
            }

            if(p.get_type().equals("cds")) {
                _cdsComponentDefinition = cd;
            }

        }

    }

    public static void setCassetteComponentDefinition() {

        URI cassetteTypeURI = URI.create("http://www.biopax.org/release/biopax-level3.owl#DnaRegion");

        HashSet<URI> cassetteTypeURIs = new HashSet<URI>();
        cassetteTypeURIs.add(cassetteTypeURI);

        //URI cassetteRoleURI = URI.create("http://identifiers.org/so/SO:0000804");
        //HashSet<URI> cassetteRoleURIs = new HashSet<URI>();
        //cassetteRoleURIs.add(cassetteRoleURI);

        _cassetteComponentDefinition = _document.createComponentDefinition(_gate_module_definition.getDisplayId() + "_cassette", cassetteTypeURIs);
    }

    public static void setCassetteSubComponents() {

        ///////////////////////////////////////////////////////////
        /////////////  cassette subcomponents
        ///////////////////////////////////////////////////////////

        for(ComponentDefinition cd: _cassetteSubComponentDefinitions) {
            Component subComponent = _cassetteComponentDefinition.createComponent(cd.getDisplayId(), AccessType.PUBLIC, cd.getIdentity());
        }
    }

    public static void setPromoterCDSFunctionalComponents() {

        ///////////////////////////////////////////////////////////
        ///////////// promoter functional component
        ///////////////////////////////////////////////////////////

        _promoterFC = _gate_module_definition.createFunctionalComponent(
                _promoterComponentDefinition.getDisplayId(),
                AccessType.PUBLIC,
                _promoterComponentDefinition.getIdentity(),
                DirectionType.NONE
        );

        _cdsFC = _gate_module_definition.createFunctionalComponent(
                _cdsComponentDefinition.getDisplayId(),
                AccessType.PUBLIC,
                _cdsComponentDefinition.getIdentity(),
                DirectionType.NONE
        );
    }


    public static void setPromoterCDSInteraction() {

        Set<URI> interactionTypes = new HashSet<URI>();
        interactionTypes.add(URI.create("http://identifiers.org/biomodels.sbo/SBO:0000169"));

        Set<URI> inhibitorRoles = new HashSet<URI>();
        inhibitorRoles.add(URI.create("http://identifiers.org/biomodels.sbo/SBO:0000020"));

        String interactionDisplayID = _cdsComponentDefinition.getDisplayId() + "_represses_" + _promoterComponentDefinition.getDisplayId();

        Interaction interaction = _gate_module_definition.createInteraction(interactionDisplayID, interactionTypes);

        String promoterParticipationID = _promoterComponentDefinition.getDisplayId() + "_participation";
        String cdsParticipationID = _cdsComponentDefinition.getDisplayId() + "_participation";

        Participation promoterParticipation = interaction.createParticipation(promoterParticipationID, _promoterFC.getDisplayId());
        Participation cdsParticipation = interaction.createParticipation(cdsParticipationID, _cdsFC.getDisplayId());
    }


    public static void setCassetteSequenceAnnotations(ArrayList<Part> gate_parts) {

        String cassette_seq = "";
        _annotation_index = 1;

        for(Part p: gate_parts) {

            if(!p.get_type().equals("promoter")) {
                cassette_seq += p.get_seq();
            }
        }

        ///////////////////////////////////////////////////////////
        /////////////  sequence
        ///////////////////////////////////////////////////////////
        URI encodingURI = URI.create("http://www.chem.qmul.ac.uk/iubmb/misc/naseq.html");
        String sequenceDisplayID = _cassetteComponentDefinition.getDisplayId() + "_sequence";
        Sequence s = _document.createSequence(sequenceDisplayID, cassette_seq, encodingURI);
        HashSet<URI> s_set = new HashSet<URI>();
        s_set.add(s.getIdentity());
        _cassetteComponentDefinition.setSequences(s_set);


        int current_bp = 1;

        //createSequenceAnnotation(String displayId, String locationId, int start, int end) {

        ArrayList<Component> ordered_components = new ArrayList<>();

        for(Part p: gate_parts) {
            for(Component c: _cassetteComponentDefinition.getComponents()) {
                if(c.getDisplayId().equals(p.get_name())) {
                    ordered_components.add(c);
                }
            }
        }

        for(Component c: ordered_components) {

            String annotationID = "sequence_annotation_" + c.getDisplayId();

            ComponentDefinition cd = c.getDefinition();

            Integer next_bp = current_bp;

            Set<Sequence> component_seq_set = cd.getSequences();
            for(Sequence component_seq: component_seq_set) {
                next_bp += component_seq.getElements().length();
            }

            SequenceAnnotation sequenceAnnotation = _cassetteComponentDefinition.createSequenceAnnotation(annotationID, "locationID"+_annotation_index, current_bp, next_bp);
            sequenceAnnotation.setComponent(c.getIdentity());

            _annotation_index++;

            current_bp += next_bp;
        }

    }






    private static SBOLDocument _document;

    private static ArrayList<ComponentDefinition> _cassetteSubComponentDefinitions = new ArrayList<ComponentDefinition>();
    private static ComponentDefinition _promoterComponentDefinition;
    private static ComponentDefinition _cdsComponentDefinition;
    private static ComponentDefinition _cassetteComponentDefinition;

    private static FunctionalComponent _promoterFC;
    private static FunctionalComponent _cdsFC;
    private static FunctionalComponent _cassetteFC;

    private static int _annotation_index;

    private static ModuleDefinition _gate_module_definition;

    private static Gson _gson = new GsonBuilder().disableHtmlEscaping().setPrettyPrinting().create();

    private static UCF _ucf = new UCF();

}
