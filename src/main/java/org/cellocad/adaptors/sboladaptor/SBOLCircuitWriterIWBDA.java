package org.cellocad.adaptors.sboladaptor;


import org.cellocad.MIT.dnacompiler.Args;
import org.cellocad.MIT.dnacompiler.Gate;
import org.cellocad.MIT.dnacompiler.LogicCircuit;
import org.cellocad.MIT.dnacompiler.Part;
import org.sbolstandard.core2.*;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;


// TODO Needs to be generalized for other gate types

public class SBOLCircuitWriterIWBDA {


    /***********************************************************************

     ***********************************************************************/
    public String writeSBOLCircuit(String output_filename, LogicCircuit lc, ArrayList<Part> plasmid, String prefix, Args options) {

        _sequence_ontology_map.put("ribozyme", SequenceOntology.INSULATOR);
        _sequence_ontology_map.put("rbs", SequenceOntology.RIBOSOME_ENTRY_SITE);
        _sequence_ontology_map.put("cds", SequenceOntology.CDS);
        _sequence_ontology_map.put("terminator", SequenceOntology.TERMINATOR);
        _sequence_ontology_map.put("promoter", SequenceOntology.PROMOTER);
        _sequence_ontology_map.put("output", SequenceOntology.CDS);

        _document = new org.sbolstandard.core2.SBOLDocument();
        _document.setDefaultURIprefix("http://cellocad.org");

        _circuit_name = prefix;


        /////////////////////////////////////////////
        //////////  Populate circuit design data
        /////////////////////////////////////////////

        setInteractionMap(lc);


        /////////////////////////////////////////////
        //////////  Structural layer: component definitions
        /////////////////////////////////////////////

        setCircuitComponentDefinition(plasmid);

        setPartComponentDefinitions(plasmid);

        setAnnotations();

        /////////////////////////////////////////////
        //////////  Functional layer: Module definitions, modules, functional components
        /////////////////////////////////////////////

        setCircuitModuleDefinition();

        setPartFunctionalComponents();

        setProductionInteractions(plasmid);
        setRepressionInteractions(plasmid);
        setInducerInteractions(plasmid);




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


    /**
     * Annotate plasmid with start bp and end bp for each part
     */
    public void setAnnotations() {

        int current_bp = 1;

        //part components were added in setPartComponentDefinitions
        for(Component c: _part_components) {

            String annotationID = "sequence_annotation_" + c.getDisplayId() + "_" + _annotation_index;

            ComponentDefinition cd = c.getDefinition();

            Integer next_bp = current_bp;

            Set<Sequence> component_seq_set = cd.getSequences();
            for(Sequence component_seq: component_seq_set) {
                next_bp += component_seq.getElements().length();
            }

            SequenceAnnotation sequenceAnnotation = _circuit_component_definition.createSequenceAnnotation(annotationID, "locationID"+_annotation_index, current_bp, next_bp);
            sequenceAnnotation.setComponent(c.getIdentity());

            _annotation_index++;

            current_bp = next_bp + 1;
        }

    }


    /**
     * Highest level in the functional layer.
     * Contains functional components (promoter, protein, inducer) and interactions between fc's.
     */
    public void setCircuitModuleDefinition() {

        URI gateRoleURI = URI.create("http://cellocad.org/LogicCircuit");

        _circuit_module_definition = _document.createModuleDefinition(_circuit_name + "_module");
        _circuit_module_definition.addRole(gateRoleURI);
    }


    /**
     * Functional components for promoters and cds's.
     */
    public void setPartFunctionalComponents() {

        for(ComponentDefinition part_cd: _part_component_definitions) {

            for (URI role : part_cd.getRoles()) {
                if (role.equals(_sequence_ontology_map.get("promoter")) || role.equals(_sequence_ontology_map.get("cds"))) {

                    boolean fc_exists = false;
                    for(FunctionalComponent fc: _circuit_module_definition.getFunctionalComponents()) {
                        if(fc.getDisplayId().equals(part_cd.getDisplayId())) {
                            fc_exists = true;
                        }
                    }

                    if(!fc_exists) {

                        FunctionalComponent partFunctionalComponent = _circuit_module_definition.createFunctionalComponent(
                                part_cd.getDisplayId(),
                                AccessType.PUBLIC,
                                part_cd.getIdentity(),
                                DirectionType.NONE
                        );
                    }

                }
            }
        }


    }


    /**
     * The circuit plasmid is the highest level in structural layer.
     * Will include vector backbone if one was specified in the UCF genetic_locations collection.
     * Currently, I am not creating an SBOL document for each output plasmid, if the output module is on
     * a separate plasmid from the circuit module.
     * @param plasmid
     */
    public void setCircuitComponentDefinition(ArrayList<Part> plasmid) {

        URI circuitTypeURI = URI.create("http://www.biopax.org/release/biopax-level3.owl#DnaRegion");

        HashSet<URI> circuitTypeURIs = new HashSet<URI>();
        circuitTypeURIs.add(circuitTypeURI);

        _circuit_component_definition = _document.createComponentDefinition(_circuit_name + "_circuit", circuitTypeURIs);


        String concatenated_plasmid_sequence = "";

        for(Part p: plasmid) {
            concatenated_plasmid_sequence += p.get_seq(); //what if revcomp?
        }


        String sequenceDisplayID = _circuit_name + "_circuit" + "_sequence";

        URI encodingURI = URI.create("http://www.chem.qmul.ac.uk/iubmb/misc/naseq.html");

        Sequence s = _document.createSequence(sequenceDisplayID, concatenated_plasmid_sequence, encodingURI);
    }



    public void setProductionInteractions(ArrayList<Part> plasmid) {

        //production

        for(int i=0; i<_production_promoters.size(); ++i) {

            String promoter_name = _production_promoters.get(i);
            String protein_name =  _production_proteins.get(i);


            Set<URI> geneticProductionTypes = new HashSet<URI>();
            geneticProductionTypes.add(SystemsBiologyOntology.GENETIC_PRODUCTION);

            Set<URI> proteinTypeURIs = new HashSet<URI>();
            proteinTypeURIs.add(URI.create("http://www.biopax.org/release/biopax-level3.owl#Protein"));


            boolean promoter_cd_exists = false;
            ComponentDefinition promoter_cd = null;
            for (ComponentDefinition cd : _document.getComponentDefinitions()) {
                if (cd.getDisplayId().equals(promoter_name)) {
                    promoter_cd = cd;
                    promoter_cd_exists = true;
                    break;
                }
            }
            if (!promoter_cd_exists) {
                //promoter (likely driving output) is on a different plasmid
                continue;
            }


            boolean protein_cd_exists = false;
            ComponentDefinition protein_cd;

            for (ComponentDefinition cd : _document.getComponentDefinitions()) {
                if (cd.getDisplayId().equals(protein_name)) {
                    protein_cd_exists = true;
                }
            }

            if (protein_cd_exists) {
                protein_cd = _document.getComponentDefinition(protein_name, "");
            } else {
                protein_cd = _document.createComponentDefinition(protein_name, proteinTypeURIs);
            }


            boolean promoter_fc_exists = false;
            FunctionalComponent promoter_fc;

            for (FunctionalComponent fc : _circuit_module_definition.getFunctionalComponents()) {
                if (fc.getDisplayId().equals(promoter_cd.getDisplayId())) {
                    promoter_fc_exists = true;
                }
            }

            if (promoter_fc_exists) {
                promoter_fc = _circuit_module_definition.getFunctionalComponent(promoter_cd.getDisplayId());
            } else {
                promoter_fc = _circuit_module_definition.createFunctionalComponent(
                        promoter_cd.getDisplayId(),
                        AccessType.PUBLIC,
                        promoter_cd.getIdentity(),
                        DirectionType.NONE
                );
            }

            //FunctionalComponent promoter_fc = _circuit_module_definition.getFunctionalComponent(promoter_name);


            boolean protein_fc_exists = false;
            FunctionalComponent protein_fc;

            for (FunctionalComponent fc : _circuit_module_definition.getFunctionalComponents()) {
                if (fc.getDisplayId().equals(protein_cd.getDisplayId())) {
                    protein_fc_exists = true;
                }
            }

            if (protein_fc_exists) {
                protein_fc = _circuit_module_definition.getFunctionalComponent(protein_cd.getDisplayId());
            } else {

                protein_fc = _circuit_module_definition.createFunctionalComponent(
                        protein_cd.getDisplayId(),
                        AccessType.PUBLIC,
                        protein_cd.getIdentity(),
                        DirectionType.NONE
                );
            }

            String productionInteractionDisplayID = promoter_name + "_produces_" + protein_name;

            Interaction productionInteraction = _circuit_module_definition.createInteraction(productionInteractionDisplayID, geneticProductionTypes);

            String promoterParticipationID = promoter_name + "_participation";
            String proteinParticipationID = protein_cd.getDisplayId() + "_participation";
            //Add for CDS
            //(fn component for cds?)

            Participation production_promoter = productionInteraction.createParticipation(promoterParticipationID, promoter_fc.getDisplayId());
            Participation production_regulator = productionInteraction.createParticipation(proteinParticipationID, protein_fc.getDisplayId());
            //Do this again for the CDS

            //missing roles
            production_promoter.addRole(SystemsBiologyOntology.PROMOTER);
            production_regulator.addRole(SystemsBiologyOntology.PRODUCT);
            //CDS. production_cds.addRole(SystemsBiologyOntology.MODIFIER)

        }
    }

    public void setRepressionInteractions(ArrayList<Part> plasmid) {

        //repression

        for (String protein_name : _repression_map.keySet()) {

            String promoter_name = _repression_map.get(protein_name);


            Set<URI> inhibitionTypeURIs = new HashSet<URI>();
            inhibitionTypeURIs.add(SystemsBiologyOntology.INHIBITION);


            FunctionalComponent protein_fc = _circuit_module_definition.getFunctionalComponent(protein_name);

            FunctionalComponent promoter_fc = _circuit_module_definition.getFunctionalComponent(promoter_name);


            String repressionInteractionDisplayID = protein_name + "_represses_" + promoter_name;

            Interaction repressionInteraction = _circuit_module_definition.createInteraction(repressionInteractionDisplayID, inhibitionTypeURIs);


            String promoterParticipationID = promoter_name + "_participation";
            String proteinParticipationID = protein_name + "_participation";


            Participation repression_regulator = repressionInteraction.createParticipation(proteinParticipationID, protein_fc.getDisplayId());


            boolean plasmid_has_promoter = false;
            for (Part p : plasmid) {
                if (p.get_name().equals(promoter_name)) {
                    plasmid_has_promoter = true;
                    break;
                }
            }
            if (plasmid_has_promoter) {
                Participation repression_promoter = repressionInteraction.createParticipation(promoterParticipationID, promoter_fc.getDisplayId());
                repression_promoter.addRole(SystemsBiologyOntology.PROMOTER);
            }


            //inhibitor
            URI inhibitorRole = SystemsBiologyOntology.INHIBITOR;
            repression_regulator.addRole(inhibitorRole);
        }
    }


    public void setInducerInteractions(ArrayList<Part> plasmid) {


        for (String protein_name : _repression_map.keySet()) {


            // non-covalent binding

            String inducer_name = "";

            if(_noncovalent_map.containsKey(protein_name)) {
                inducer_name = _noncovalent_map.get(protein_name);
            }

            if(inducer_name != "") {


                //component definition type
                Set<URI> smallMoleculeTypeURIs = new HashSet<URI>();
                smallMoleculeTypeURIs.add(URI.create("http://www.biopax.org/release/biopax-level3.owl#SmallMolecule"));

                //component definition type
                Set<URI> complexTypeURIs = new HashSet<URI>();
                complexTypeURIs.add(URI.create("http://www.biopax.org/release/biopax-level3.owl#Complex"));



                ComponentDefinition inducer = _document.createComponentDefinition(inducer_name, smallMoleculeTypeURIs);

                String complex_name = inducer_name + "_" + protein_name + "_Complex";
                ComponentDefinition complex = _document.createComponentDefinition(complex_name, complexTypeURIs);


                FunctionalComponent protein_fc = _circuit_module_definition.getFunctionalComponent(protein_name);

                FunctionalComponent inducer_fc = _circuit_module_definition.createFunctionalComponent(
                        inducer.getDisplayId(),
                        AccessType.PUBLIC,
                        inducer.getIdentity(),
                        DirectionType.NONE
                );

                FunctionalComponent complex_fc = _circuit_module_definition.createFunctionalComponent(
                        complex.getDisplayId(),
                        AccessType.PUBLIC,
                        complex.getIdentity(),
                        DirectionType.NONE
                );


                String complexInteractionDisplayID = protein_name + "_binds_" + inducer.getDisplayId();


                Interaction noncovalentInteraction = _circuit_module_definition.createInteraction(complexInteractionDisplayID, complexTypeURIs);


                String inducerParticipationID = inducer.getDisplayId() + "_participation";
                String complexParticipationID = complex.getDisplayId() + "_participation";
                String proteinParticipationID = protein_name + "_participation";

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
    }




    public void setPartComponentDefinitions(ArrayList<Part> plasmid) {


        for(Part p: plasmid) {

            //adds cd to document
            ComponentDefinition part_cd = getComponentDefinitionForPart(p);
            _part_component_definitions.add(part_cd);

            //adds seq to document
            Sequence part_seq = getComponentSequenceForPart(p);

            //seq is property of cd
            HashSet<URI> s_set = new HashSet<URI>();
            s_set.add(part_seq.getIdentity());
            part_cd.setSequences(s_set);

            if(_sequence_ontology_map.containsKey(p.get_type())) {
                part_cd.addRole(_sequence_ontology_map.get(p.get_type()));
            }

            _part_sequences.add(part_seq);


            boolean exists = false;
            for(Component c: _circuit_component_definition.getComponents()) {
                if(c.getDisplayId().equals(part_cd.getDisplayId() + "_component")) {
                    _part_components.add(c);
                    exists = true;
                }
            }
            if(! exists) {
                _part_components.add(_circuit_component_definition.createComponent(part_cd.getDisplayId() + "_component", AccessType.PUBLIC, part_cd.getIdentity()));
            }
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



    public void setInteractionMap(LogicCircuit lc) {

        for (Gate g : lc.get_Gates()) {

            if(g.Type == Gate.GateType.INPUT) {
                continue;
            }


            else if(g.Type == Gate.GateType.OUTPUT || g.Type == Gate.GateType.OUTPUT_OR) {
                for(Gate child: g.getChildren()) {
                    _production_promoters.add(child.get_regulable_promoter().get_name());
                    _production_proteins.add(g.Regulator + "_protein");
                }
            }

            else {
                String promoter_name = g.get_regulable_promoter().get_name();

                _repression_map.put(g.Regulator + "_protein", promoter_name);

                for(Gate child: g.getChildren()) {
                    _production_promoters.add(child.get_regulable_promoter().get_name());
                    _production_proteins.add(g.Regulator + "_protein");
                }

                if(g.Inducer != "") {
                    _noncovalent_map.put(g.Regulator + "_protein", g.Inducer);
                }
            }

        }
    }




    public void setCircuitName(String name) {
        _circuit_name = name;
    }

    private String _filepath = "";

    public HashMap<String, URI> _sequence_ontology_map = new HashMap<String, URI>();

    private String _circuit_name;

    private HashMap<String, String> _repression_map = new HashMap<String, String>();

    private ArrayList<String> _production_promoters = new ArrayList<>();
    private ArrayList<String> _production_proteins = new ArrayList<>();

    //private HashMap<String, String> _production_map = new HashMap<String, String>();

    private HashMap<String, String> _noncovalent_map = new HashMap<String, String>();
    //private HashMap<String, String> _output_map = new HashMap<String, String>();

    private ArrayList<Sequence> _part_sequences = new ArrayList<Sequence>();
    private ArrayList<Component> _part_components = new ArrayList<>();

    private ComponentDefinition _circuit_component_definition;
    private ArrayList<ComponentDefinition> _part_component_definitions = new ArrayList<ComponentDefinition>();

    private ModuleDefinition _circuit_module_definition;


    //private ArrayList<FunctionalComponent> _part_functional_components = new ArrayList<FunctionalComponent>();

    private int _annotation_index = 1;

    private SBOLDocument _document;


}
