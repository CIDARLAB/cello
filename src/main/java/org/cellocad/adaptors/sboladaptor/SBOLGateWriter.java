package org.cellocad.adaptors.sboladaptor;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.cellocad.MIT.dnacompiler.*;
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


public class SBOLGateWriter {


    private static HashMap<String, URI> sequence_ontology_map = new HashMap<String, URI>();
    private static String _filepath = "";

    public static void main(String args[]) {

        Args options = new Args();
        _filepath = options.get_home();

        sequence_ontology_map.put("ribozyme", org.sbolstandard.core.util.SequenceOntology.INSULATOR);
        sequence_ontology_map.put("rbs", org.sbolstandard.core.util.SequenceOntology.FIVE_PRIME_UTR);
        sequence_ontology_map.put("cds", org.sbolstandard.core.util.SequenceOntology.CDS);
        sequence_ontology_map.put("terminator", org.sbolstandard.core.util.SequenceOntology.TERMINATOR);
        sequence_ontology_map.put("promoter", org.sbolstandard.core.util.SequenceOntology.PROMOTER);


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

        setPromoterCDSInteraction();



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

    /*public static void setCassetteFunctionalComponent() {

        URI cassetteFCURI = URI.create(_gate_module_definition.getIdentity().toString() + "/" + _cassetteComponentDefinition.getDisplayId());

        FunctionalComponent cassetteFunctionalComponent = new FunctionalComponent(
                cassetteFCURI,
                ComponentInstance.AccessType.PUBLIC,
                _cassetteComponentDefinition.getIdentity(),
                FunctionalComponent.DirectionType.NONE
        );
        cassetteFunctionalComponent.setDisplayId(_cassetteComponentDefinition.getDisplayId());

        _cassetteFC = cassetteFunctionalComponent;

        _gate_module_definition.addFunctionalComponent(_cassetteFC);

    }


    public static void setGateModel() {

        //URI identity,URI source, URI language, URI framework, Set<URI> roles

        //URI identityURI
        //URI sourceURI
        //URI languageURI
        //URI frameworkURI
        //Set<URI> roleURIs

        //ResponseFunction m = new ResponseFunction()
    }


*/




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
