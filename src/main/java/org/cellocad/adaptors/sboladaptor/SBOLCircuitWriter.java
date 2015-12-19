package org.cellocad.adaptors.sboladaptor;


import org.cellocad.MIT.dnacompiler.*;
import org.sbolstandard.core2.*;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class SBOLCircuitWriter {



    public void main(String[] args) {

        DNACompiler dnac = new DNACompiler();

        GateLibrary gate_library = dnac.glJSONToObj();
        LogicCircuit lc = dnac.lcJSONToObj(gate_library);

        writeSBOLCircuit("test_circuit_sbol.xml", lc, "name", new Args());
    }


    /***********************************************************************

     ***********************************************************************/
    public String writeSBOLCircuit(String output_filename, LogicCircuit lc, String prefix, Args options) {

        _sequence_ontology_map.put("ribozyme", org.sbolstandard.core.util.SequenceOntology.INSULATOR);
        _sequence_ontology_map.put("rbs", org.sbolstandard.core.util.SequenceOntology.FIVE_PRIME_UTR);
        _sequence_ontology_map.put("cds", org.sbolstandard.core.util.SequenceOntology.CDS);
        _sequence_ontology_map.put("terminator", org.sbolstandard.core.util.SequenceOntology.TERMINATOR);
        _sequence_ontology_map.put("promoter", org.sbolstandard.core.util.SequenceOntology.PROMOTER);
        _sequence_ontology_map.put("output", URI.create("http://www.biopax.org/release/biopax-level3.owl#DnaRegion"));


        _document = new org.sbolstandard.core2.SBOLDocument();

        _document.setDefaultURIprefix("http://cellocad.org");

        _circuit_name = prefix;


        /////////////////////////////////////////////
        //////////  Populate circuit design data
        /////////////////////////////////////////////

        setInteractionMap(lc);

        setTxnUnits(lc);


        /////////////////////////////////////////////
        //////////  Structural layer: component definitions
        /////////////////////////////////////////////

        setPartComponentDefinitions();

        setTxnUnitComponentDefinitions();

        setCircuitComponentDefinition();

        addPartComponentsToTxnUnitComponentDefinitions();

        addTxnUnitComponentsToCircuitComponentDefinition();


        /////////////////////////////////////////////
        //////////  Functional layer: Module definitions, modules, functional components
        /////////////////////////////////////////////

        setCircuitModuleDefinition();

        setRegulatoryNetworkModuleDefinition();

        setPartFunctionalComponents();

        setCircuitFunctionalComponent();

        addRegulatoryNetworkModuleToCircuitModuleDefinition();

        addInteractions();


        //////////////////////////////////////////////////
        /////////////// write to sbolDocument
        //////////////////////////////////////////////////

        String output_filepath = options.get_output_directory() + "/" + output_filename;
        try {
            org.sbolstandard.core2.SBOLWriter.write(_document, output_filepath);
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return _document.toString();

    }


    public void addInteractions() {

        for(String cds: _repression_map.keySet()) {

            FunctionalComponent fc_cds = null;
            FunctionalComponent fc_promoter = null;

            String promoter = _repression_map.get(cds);

            for(FunctionalComponent fc: _regulatory_network_module_definition.getFunctionalComponents()) {

                if(fc.getDisplayId().equals(cds)) {
                    fc_cds = fc;

                }
            }

            for(FunctionalComponent fc: _regulatory_network_module_definition.getFunctionalComponents()) {

                if(fc.getDisplayId().equals(promoter)) {
                    fc_promoter = fc;
                }
            }

            addInteraction(fc_cds, fc_promoter);

        }

    }


    public void addInteraction(FunctionalComponent fc1, FunctionalComponent fc2) {

        Set<URI> interactionTypes = new HashSet<URI>();
        interactionTypes.add(URI.create("http://identifiers.org/biomodels.sbo/SBO:0000169"));

        Set<URI> inhibitorRoles = new HashSet<URI>();
        inhibitorRoles.add(URI.create("http://identifiers.org/biomodels.sbo/SBO:0000020"));

        String interactionDisplayID = fc1.getDisplayId() + "_represses_" + fc2.getDisplayId();

        for(Interaction interaction: _regulatory_network_module_definition.getInteractions()) {
            if(interaction.getDisplayId().equals(interactionDisplayID)) {
                return;
            }
        }

        Interaction interaction = _regulatory_network_module_definition.createInteraction(interactionDisplayID, interactionTypes);

        String fc1ParticipationID = fc1.getDisplayId() + "_participation";
        String fc2ParticipationID = fc2.getDisplayId() + "_participation";

        Participation fc1Participation = interaction.createParticipation(fc1ParticipationID, fc1.getDisplayId());
        Participation cdsParticipation = interaction.createParticipation(fc2ParticipationID, fc2.getDisplayId());
    }



    public void addRegulatoryNetworkModuleToCircuitModuleDefinition() {

        _circuit_module_definition.createModule(_regulatory_network_module_definition.getDisplayId() + "_module", _regulatory_network_module_definition.getIdentity());
    }



    public void addPartComponentsToTxnUnitComponentDefinitions() {

        for(int i=0; i< _txn_units.size(); ++i) {

            ComponentDefinition txn_unit_component_definition = _txn_unit_component_definitions.get(i);

            for(ComponentDefinition cd: _part_component_definitions.get(i)) {

                Component subComponent = txn_unit_component_definition.createComponent(cd.getDisplayId(), AccessType.PUBLIC, cd.getIdentity());
            }

            //ArrayList<ComponentDefinition> txnUnitComponentDefinition = _part_component_definitions.get(i);

            String promoter_cds_name = "";
            for (Part p : _txn_units.get(i)) {
                //if (p.get_type().equals("promoter") || p.get_type().equals("cds")) {
                promoter_cds_name += p.get_name() + "_";
                //}
            }

            String cassette_seq = "";
            _annotation_index = 1;

            for(Part p: _txn_units.get(i)) {
                //if(!p.get_type().equals("promoter")) {
                cassette_seq += p.get_seq();
                //}
            }

            ///////////////////////////////////////////////////////////
            /////////////  sequence
            ///////////////////////////////////////////////////////////
            URI encodingURI = URI.create("http://www.chem.qmul.ac.uk/iubmb/misc/naseq.html");
            String sequenceDisplayID = txn_unit_component_definition.getDisplayId() + "_sequence";
            Sequence s = _document.createSequence(sequenceDisplayID, cassette_seq, encodingURI);
            HashSet<URI> s_set = new HashSet<URI>();
            s_set.add(s.getIdentity());
            txn_unit_component_definition.setSequences(s_set);


            int current_bp = 1;

            //createSequenceAnnotation(String displayId, String locationId, int start, int end) {

            ArrayList<Component> ordered_components = new ArrayList<>();

            for(Part p: _txn_units.get(i)) {
                for(Component c: txn_unit_component_definition.getComponents()) {
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

                SequenceAnnotation sequenceAnnotation = txn_unit_component_definition.createSequenceAnnotation(annotationID, "locationID"+_annotation_index, current_bp, next_bp);
                sequenceAnnotation.setComponent(c.getIdentity());

                _annotation_index++;

                current_bp += next_bp;
            }
        }
    }


    public void addTxnUnitComponentsToCircuitComponentDefinition() {

        for (ComponentDefinition cd : _txn_unit_component_definitions) {
            Component subComponent = _circuit_component_definition.createComponent(cd.getDisplayId(), AccessType.PUBLIC, cd.getIdentity());
        }
    }



    public void setCircuitModuleDefinition() {

        URI gateRoleURI = URI.create("http://cellocad.org/LogicCircuit");

        _circuit_module_definition = _document.createModuleDefinition(_circuit_name + "_circuit_function");
        _circuit_module_definition.addRole(gateRoleURI);
    }




    public void setRegulatoryNetworkModuleDefinition() {

        URI circuitRoleURI = URI.create("http://cellocad.org/RegulatoryNetwork");

        _regulatory_network_module_definition = _document.createModuleDefinition(_circuit_name + "_GRN");
        _regulatory_network_module_definition.addRole(circuitRoleURI);
    }



    public void setCircuitFunctionalComponent() {

        FunctionalComponent circuitFunctionalComponent  = _circuit_module_definition.createFunctionalComponent(
                _circuit_module_definition.getDisplayId(),
                AccessType.PUBLIC,
                _circuit_component_definition.getIdentity(),
                DirectionType.NONE
        );
    }


    public void setPartFunctionalComponents() {

        for(ArrayList<ComponentDefinition> txnUnitComponentDefinition: _part_component_definitions) {

            for(ComponentDefinition partComponentDefinition: txnUnitComponentDefinition) {

                for (URI role : partComponentDefinition.getRoles()) {
                    if (role.equals(_sequence_ontology_map.get("promoter")) || role.equals(_sequence_ontology_map.get("cds"))) {

                        boolean fc_exists = false;
                        for(FunctionalComponent fc: _regulatory_network_module_definition.getFunctionalComponents()) {
                            if(fc.getDisplayId().equals(partComponentDefinition.getDisplayId())) {
                                fc_exists = true;
                            }
                        }

                        if(!fc_exists) {
                            FunctionalComponent partFunctionalComponent = _regulatory_network_module_definition.createFunctionalComponent(
                                    partComponentDefinition.getDisplayId(),
                                    AccessType.PUBLIC,
                                    partComponentDefinition.getIdentity(),
                                    DirectionType.NONE
                            );
                        }

                    }
                }
            }
        }

    }



    public void setCircuitComponentDefinition() {

        URI circuitTypeURI = URI.create("http://www.biopax.org/release/biopax-level3.owl#DnaRegion");

        HashSet<URI> circuitTypeURIs = new HashSet<URI>();
        circuitTypeURIs.add(circuitTypeURI);

        _circuit_component_definition = _document.createComponentDefinition(_circuit_name + "_circuit", circuitTypeURIs);
    }


    public void setTxnUnitComponentDefinitions() {

        for(ArrayList<Part> txn_unit: _txn_units) {

            String promoter_cds_name = "";
            for(Part p: txn_unit) {
                //if(p.get_type().equals("promoter") || p.get_type().equals("cds")) {
                promoter_cds_name += p.get_name() + "_";
                //}
            }

            String txn_unit_display_id = promoter_cds_name + "txnunit";
            URI txnUnitTypeURI = URI.create("http://www.biopax.org/release/biopax-level3.owl#DnaRegion");
            HashSet<URI> txnUnitTypeURIs = new HashSet<URI>();
            txnUnitTypeURIs.add(txnUnitTypeURI);

            ComponentDefinition txnUnitComponentDefinition = _document.createComponentDefinition(txn_unit_display_id, txnUnitTypeURIs);
            _txn_unit_component_definitions.add(txnUnitComponentDefinition);
        }
    }


    public void setPartComponentDefinitions() {

        for(ArrayList<Part> txn_unit: _txn_units) {

            ArrayList<ComponentDefinition> part_component_definitions = new ArrayList<ComponentDefinition>();

            for(Part p: txn_unit) {

                //adds cd to document
                ComponentDefinition part_cd = getComponentDefinitionForPart(p);

                //adds seq to document
                Sequence part_seq = getComponentSequenceForPart(p);

                //seq is property of cd
                HashSet<URI> s_set = new HashSet<URI>();
                s_set.add(part_seq.getIdentity());
                part_cd.setSequences(s_set);

                if(_sequence_ontology_map.containsKey(p.get_type())) {
                    part_cd.addRole(_sequence_ontology_map.get(p.get_type()));
                }

                part_component_definitions.add(part_cd);

                _part_sequences.add(part_seq);

            }

            _part_component_definitions.add(part_component_definitions);
        }

    }

    public Sequence getComponentSequenceForPart(Part p) {

        String sequenceDisplayID = p.get_name() + "_sequence";

        //if exists, return the Sequence that was already created.

        for(Sequence s: _document.getSequences()) {
            if(s.getDisplayId().equals(sequenceDisplayID)) {
                return s;
            }
        }

        //if does not exists, create Sequence for part

        URI encodingURI = URI.create("http://www.chem.qmul.ac.uk/iubmb/misc/naseq.html");

        Sequence s = _document.createSequence(sequenceDisplayID, p.get_seq(), encodingURI);

        return s;
    }


    public ComponentDefinition getComponentDefinitionForPart(Part p) {

        String cd_displayID = p.get_name();


        //if exists, return the CD that was already created.

        for(ComponentDefinition cd: _document.getComponentDefinitions()) {
            if(cd.getDisplayId().equals(cd_displayID)) {
                return cd;
            }
        }

        //if does not exists, create CD for part

        URI partTypeURI = URI.create("http://www.biopax.org/release/biopax-level3.owl#DnaRegion");
        HashSet<URI> partTypeURIs = new HashSet<URI>();
        partTypeURIs.add(partTypeURI);

        ComponentDefinition partComponentDefinition = _document.createComponentDefinition(p.get_name(), partTypeURIs);

        if(_sequence_ontology_map.containsKey(p.get_type())) {
            partComponentDefinition.addRole(_sequence_ontology_map.get(p.get_type()));
        }

        return partComponentDefinition;

    }



    public void setTxnUnits(LogicCircuit lc) {

        for(Gate g: lc.get_Gates()) {

            if(g.Type == Gate.GateType.INPUT) {
                continue;
            }


            ArrayList<Part> txn_unit = new ArrayList<Part>();

            for(Gate child: g.getChildren()) {
                txn_unit.add(child.get_regulable_promoter());
            }
            txn_unit.addAll(g.get_downstream_parts().get("x"));

            _txn_units.add(txn_unit);

        }
    }

    public void setInteractionMap(LogicCircuit lc) {

        for(Gate g: lc.get_logic_gates()) {

            String cds_name = "";
            String promoter_name = g.get_regulable_promoter().get_name();

            for(Part p: g.get_downstream_parts().get("x")) {
                if(p.get_type().equals("cds")) {
                    cds_name = p.get_name();
                }
            }


            _repression_map.put(cds_name, promoter_name);

        }

    }




    public void setCircuitName(String name) {
        _circuit_name = name;
    }

    private String _filepath = "";

    public HashMap<String, URI> _sequence_ontology_map = new HashMap<String, URI>();

    private String _circuit_name;

    private ArrayList<ArrayList<Part>> _txn_units = new ArrayList<ArrayList<Part>>();

    private HashMap<String, String> _repression_map = new HashMap<String, String>();



    private ArrayList<Sequence> _part_sequences = new ArrayList<Sequence>();

    private ComponentDefinition _circuit_component_definition;
    private ArrayList<ComponentDefinition> _txn_unit_component_definitions = new ArrayList<ComponentDefinition>();
    private ArrayList<ArrayList<ComponentDefinition>> _part_component_definitions = new ArrayList<ArrayList<ComponentDefinition>>();


    private ModuleDefinition _circuit_module_definition;
    private ModuleDefinition _regulatory_network_module_definition;
    private Module _regulatory_network_module;

    //private ArrayList<FunctionalComponent> _part_functional_components = new ArrayList<FunctionalComponent>();

    private int _annotation_index = 1;

    private SBOLDocument _document;


}
