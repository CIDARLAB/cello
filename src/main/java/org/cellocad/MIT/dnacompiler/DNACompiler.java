package org.cellocad.MIT.dnacompiler;
/**
 * Created by Bryan Der on 3/26/14.
 */

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.Getter;
import lombok.Setter;
import org.apache.log4j.*;
import org.cellocad.BU.netsynth.NetSynth;
import org.cellocad.BU.netsynth.NetSynthSwitch;
import org.cellocad.BU.netsynth.Utilities;
import org.cellocad.MIT.dnacompiler.Gate.GateType;
import org.cellocad.MIT.figures.*;
import org.cellocad.MIT.tandem_promoter.InterpolateTandemPromoter;
import org.cellocad.adaptors.eugeneadaptor.EugeneAdaptor;
import org.cellocad.adaptors.sboladaptor.SBOLCircuitWriter;
import org.cellocad.adaptors.ucfadaptor.UCFAdaptor;
import org.cellocad.adaptors.ucfadaptor.UCFReader;
import org.cellocad.adaptors.ucfadaptor.UCFValidator;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ObjectNode;
import org.json.JSONException;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Modifier;
import java.util.*;


//@Slf4j
public class DNACompiler {


    public DNACompiler() {

    }
    public DNACompiler(String username) {
        _username = username;
    }

    /*
     * These enum values are used to report a job/result status to the front-end web application.
     * If the job does not succeed, it is helpful to know why.
     */
    public enum ResultStatus{
        success,
        wiring_diagram_invalid,
        not_enough_gates_in_library,
        no_assignments_found,
        roadblocking_inputs,
        ucf_invalid,
        arguments_invalid,
        breadth_first_not_allowed,
        abstract_only,
    }

    public enum CircuitType{
        combinational,
        sequential
    }

    /**
     *
     * Cello main
     *
     * 1. Set runtime arguments/options.
     * 2. Get abstract circuit (wiring diagram).
     * 3. Load input, repressor, output, toxicity, histogram data.
     * 4. Assignment of repressors to gates.
     * 5. Generate plasmid DNA sequences.
     * 6. Generate figures.
     *
     */

    public void run(String[] args) {



        //System.setProperty("logfile.name", "default.log");
        //PropertyConfigurator.configure(_options.get_home() + "/src/main/resources/log4j.properties");


        /**
         * read command-line arguments.  Verilog file required, others are optional.
         */
//        _options.setThreadDependentLoggername(threadDependentLoggername);
        _options.set_username(_username);
        _options.parse(args);



        /**
         * set paths to data files (Inputs, Outputs, NOR_Gates, AND_Gates, ToxicityTable)
         * set jobID prefix for output files
         */
        setPaths();
        Util.createDirectory(_options.get_output_directory());



        String logfile = _options.get_output_directory() + _options.get_jobID() + "_dnacompiler_output.txt";

        FileAppender appender = new FileAppender();
        appender.setFile(logfile);
        appender.setLayout(new PatternLayout("%m%n"));
        appender.setName(threadDependentLoggername);
        appender.setThreshold(Level.DEBUG);
        appender.activateOptions();

        // ConsoleAppender is set in log4j.properties
        ConsoleAppender console = new ConsoleAppender();
        console.setLayout(new PatternLayout("%m%n"));
        console.setThreshold(Level.DEBUG);
        console.activateOptions();

        logger = Logger.getLogger(threadDependentLoggername);
        logger.addAppender(appender);
        logger.addAppender(console);



        /**
         * Instead of logger.info, use a logging system.
         * Need to set logfile.name before any logger is instantiated to avoid error messages.
         * Here we are using log4j.  See /src/main/resources/log4j.properties for the configuration details.
         */



        //the logger will write to the specified file
//        System.setProperty("logfile.name", logfile);
//        PropertyConfigurator.configure(_options.get_home() + "/src/main/resources/log4j.properties");
//        log = Logger.getLogger(this.getClass().getPackage().getName());



        logger.info("\n");
        logger.info("///////////////////////////////////////////////////////////");
        logger.info("///////////////   Welcome to Cello   //////////////////////");
        logger.info("///////////////////////////////////////////////////////////\n");


        //print the options for record-keeping
        //logger.info(Arrays.toString(args).replaceAll(",", "") + "\n");
        logger.info("\n");
        logger.info("///////////////////////////////////////////////////////////");
        logger.info("///////////////   Options   ///////////////////////////////");
        logger.info("///////////////////////////////////////////////////////////\n");
//        logger.info(objToJSONString(_options));

        if(_options.get_assignment_algorithm() == null) {
            logger.info("Assignment algorithm invalid");
            _result_status = ResultStatus.arguments_invalid;
            return;
        }

        logger.info("\n");
        logger.info("///////////////////////////////////////////////////////////");
        logger.info("///////////////   UCF Validation   ////////////////////////");
        logger.info("///////////////////////////////////////////////////////////\n");


        //UCFAdaptor helps generate Gate/Part libraries from the UCF, and get other data from the UCF.
        ucfAdaptor.setThreadDependentLoggername(threadDependentLoggername);

        //UCFReader reads the JSON text file and creates the UCF object.
        ucfReader.setThreadDependentLoggername(threadDependentLoggername);

        //UCFValidator. returns 'false' if something is not valid in the UCF. (some collections are optional)
        ucfValidator.setThreadDependentLoggername(threadDependentLoggername);

        //UCF.  JSON objects organized by 'collection'.
        UCF ucf = ucfReader.readAllCollections(_options.get_UCFfilepath());
        if(ucf == null) {
            _result_status = ResultStatus.ucf_invalid;
            logger.info("invalid UCF");
            return;
        }



        JSONObject ucf_validation_map = ucfValidator.validateAllUCFCollections(ucf, _options);
        logger.info(gson.toJson(ucf_validation_map));

        boolean is_ucf_valid = (boolean) ucf_validation_map.get("is_valid");

        if (!is_ucf_valid) {
            _result_status = ResultStatus.ucf_invalid;
            logger.info("invalid UCF");
            return;
        }

        //optional collections
        // toxicity
        // cytometry
        // eugene rules
        // motif_library
        // genetic locations

        //options is passed in order to turn off the toxicity, histogram, plasmid options if that data
        //is missing from the UCF.



        /**
         * abstract_lc:     Boolean circuit.  Also called wiring diagram.
         *
         * unassigned_lcs:  input gates and output gates assigned, logic gates not assigned.
         *                  multiple possible if permuting input order
         *
         * assigned_lcs:    all logic gates assigned with genetic gates
         */
        LogicCircuit abstract_lc = new LogicCircuit();
        ArrayList<LogicCircuit> unassigned_lcs = new ArrayList<LogicCircuit>();
        ArrayList<LogicCircuit> assigned_lcs = new ArrayList<LogicCircuit>();

        logger.info("\n");
        logger.info("///////////////////////////////////////////////////////////");
        logger.info("///////////////   Logic synthesis, Wiring diagram   ///////");
        logger.info("///////////////////////////////////////////////////////////\n");




        /**
         * NetSynth: convert Verilog to Boolean wiring diagram
         */
        try {
            abstract_lc = getAbstractCircuit(_options.get_fin_verilog(), ucf);
        } catch(Exception e) {
            throw new IllegalStateException("Error in abstract circuit.  Exiting.");
        }




        /**
         * A logic circuit must have at least one input gate and one output gate
         */
        if (abstract_lc.get_input_gates().size() == 0 || abstract_lc.get_output_gates().size() == 0) {
            _result_status = ResultStatus.wiring_diagram_invalid;
            logger.info("incorrect wiring diagram, no inputs/outputs");
            return;
        }


        /**
         * circuit size
         */
        for (GateType gtype : abstract_lc.get_gate_types().keySet()) {
            logger.info("Circuit has " + abstract_lc.get_gate_types().get(gtype).size() + " " + gtype + " gates.");
        }

        logger.info("N logic gates: " + abstract_lc.get_logic_gates().size() + "");


        /**
         *
         * Set the logic for the input gates.
         * For combinational logic, this means permuting all input combinations.
         * For sequential logic, the input 'waveforms' will used as the truth table.
         *
         * For a 3-input circuit:
         *
         * in1 in2 in3
         *  0   0   0
         *  0   0   1
         *  0   1   0
         *  0   1   1
         *  1   0   0
         *  1   0   1
         *  1   1   0
         *  1   1   1
         */

        if(_options.get_circuit_type() == CircuitType.sequential) {
            
            
            HashMap<String, List<Integer>> initial_logics = new HashMap<>();
            int nrows = SequentialHelper.loadInitialLogicsFromTruthtable(initial_logics, get_options().get_fin_sequential_waveform());

            SequentialHelper.setInitialLogics(abstract_lc, initial_logics, nrows);
            SequentialHelper.printTruthTable(abstract_lc);

            logger.info("Cycle 1");
            SequentialHelper.updateLogics(abstract_lc);
            SequentialHelper.printTruthTable(abstract_lc);

            logger.info("Cycle 2");
            SequentialHelper.updateLogics(abstract_lc);
            SequentialHelper.printTruthTable(abstract_lc);

            logger.info("Cycle 3");
            SequentialHelper.updateLogics(abstract_lc);
            SequentialHelper.printTruthTable(abstract_lc);

            //assert logic is valid
            if(! SequentialHelper.validLogic(abstract_lc)) {
                throw new IllegalStateException("SequentialHelper: Invalid logic.  Exiting.");
            }

        }
        else {

            LogicCircuitUtil.setInputLogics(abstract_lc);

            /**
             *  propagate logic through gates
             */
            //initialize logic to all zeroes
            Integer nrows = abstract_lc.get_input_gates().get(0).get_logics().size();
            for (Gate g : abstract_lc.get_Gates()) {
                if (g.get_logics().isEmpty()) {
                    ArrayList<Integer> logics = new ArrayList<>();
                    for (int i = 0; i < nrows; ++i) {
                        logics.add(0);
                    }
                    g.set_logics(logics);
                }
            }
            //compute Boolean logic for each gate in the circuit.
            Evaluate.simulateLogic(abstract_lc);

        }


        /**
         *  abstract circuit is now fully specified
         *
         *  generate figures for abstract circuit
         */
        logger.info(abstract_lc.printGraph());

        if (_options.is_figures()) {
            logger.info("=========== Graphviz wiring diagram ==========");
            Graphviz graphviz = new Graphviz(_options.get_home(), _options.get_output_directory(), _options.get_jobID());
            graphviz.printGraphvizDotText(abstract_lc, _options.get_jobID() + "_wiring_agrn.dot");

            ScriptCommands script_commands = new ScriptCommands(_options.get_home(), _options.get_output_directory(), _options.get_jobID());
            script_commands.makeDot2Png(_options.get_jobID() + "_wiring_agrn.dot");

            if (_options.is_dnaplotlib()) {
                //PlotLibAbstractTruthtableWriter.writeAbstractCircuitTruthtableForDNAPlotLib( abstract_lc );
            }
        }

        /**
         * If you only want to see the Boolean wiring diagram, we are done.
         */
        if (_options.get_assignment_algorithm() == BuildCircuits.AssignmentAlgorithm.abstract_only) {
            _result_status = ResultStatus.abstract_only;
            return;
        }




        // TODO organize DNACompiler in a more modular way.
        // TODO with a more clear API.
        // abstract circuit: Verilog in, LogicCircuit out
        // assigned circuits: abstract_lc in, UCF in, Args in, assigned_lcs out.
        // plasmids: assigned_lc in, ArrayList<Part> out
        // SBOL: plasmid in, SBOL out
        // figures: assigned_lcs in, Args in,


        /**
         * The UCF (user constraint file) specifies the gate library, data, and other options.
         * UCF.java: UCF object
         * UCFReader: reads .json text file and creates UCF object.
         * UCFAdaptor: returns java data types from the UCF object.
         */

        logger.info("\n");
        logger.info("///////////////////////////////////////////////////////////");
        logger.info("///////////////   Loading parts   /////////////////////////");
        logger.info("///////////////////////////////////////////////////////////\n");

        /**
         * Part objects mapped to the part name.
         */
        PartLibrary part_library = ucfAdaptor.createPartLibrary(ucf);

        for (Part p : part_library.get_ALL_PARTS().values()) {
            logger.info("Part: " + p.get_type() + " " + p.get_name());
        }


        logger.info("\n");
        logger.info("///////////////////////////////////////////////////////////");
        logger.info("///////////////   Loading Gate Library   //////////////////");
        logger.info("///////////////////////////////////////////////////////////\n");


        /**
         * In order to initialize gate_library, the number of inputs/outputs must be known.
         * This is because data members in the gate library are 'final' and cannot be modified.
         */
        Integer n_inputs = InputOutputGateReader.nInputs(_options.get_fin_input_promoters());
        Integer n_outputs = InputOutputGateReader.nOutputs(_options.get_fin_output_genes());

        //TODO this is a comment with blue

        /**
         * The gate library is a list of input, logic, and output gates.
         * input and output gates were specified using text files (user input),
         * but the logic gates were specified in the UCF.
         * input gates and logic gates were purposefully omitted from the UCF,
         * the idea being that the same gate library can be used to design circuits with different inputs/outputs.
         */
        GateLibrary gate_library = ucfAdaptor.createGateLibrary(ucf, n_inputs, n_outputs, _options);


        for(Gate g: gate_library.get_GATES_BY_NAME().values()) {
            logger.info("loading gate from UCF gates collection: " + g.name);
        }


        logger.info("\n");
        logger.info("///////////////////////////////////////////////////////////");
        logger.info("///////////////   Loading input and output gates   ////////");
        logger.info("///////////////////////////////////////////////////////////\n");

        /**
         *
         * read text files for input promoters and output genes, populate gate_library with data.
         *
         * Inputs:  pTac, pTet, pBAD, etc.
         *      includes promoter name, RPU OFF, RPU ON, and DNA sequence
         *
         * Outputs: YFP, etc.
         *      includes output gene name, and the concatenated DNA sequence of the output cassette (typically ribozyme, rbs, cds, terminator concatenated)
         *
         * gate_library is passed in because it will be modified with the input/output data that's read in
         */
        InputOutputGateReader.readInputsFromFile(_options.get_fin_input_promoters(), gate_library, _options.is_tpmodel());
        InputOutputGateReader.readOutputsFromFile(_options.get_fin_output_genes(), gate_library);



        logger.info("\n");
        logger.info("///////////////////////////////////////////////////////////");
        logger.info("///////////////   Loading Gate Parts   ////////////////////");
        logger.info("///////////////////////////////////////////////////////////\n");

        //associate Part objects with the _downstream_parts and _regulable_promoter data members of Gate.java
        ucfAdaptor.setGateParts(ucf, gate_library, part_library);


        //make sure all gates have gate parts defined
        if(!ucfValidator.allGatesHaveGateParts(gate_library)) {
            _result_status = ResultStatus.ucf_invalid;
            return;
        }



        logger.info("\n");
        logger.info("///////////////////////////////////////////////////////////");
        logger.info("///////////////   Loading Response Functions   ////////////");
        logger.info("///////////////////////////////////////////////////////////\n");

        ucfAdaptor.setResponseFunctions(ucf, gate_library);


        //make sure all gates have a response function defined.
        if(!ucfValidator.allGatesHaveResponseFunctions(gate_library)) {
            _result_status = ResultStatus.ucf_invalid;
            return;
        }

        //printing the response functions
        for (Gate g : gate_library.get_GATES_BY_NAME().values()) {
            logger.info(g.name + " " + g.get_equation() + " " + g.get_params().toString());
        }


        logger.info("\n");
        logger.info("///////////////////////////////////////////////////////////");
        logger.info("///////////////   Loading Toxicity Data   /////////////////");
        logger.info("///////////////////////////////////////////////////////////\n");

        /**
         * populate the gate objects with the Part objects for 'downstream_parts' and 'regulable_promoter'.
         */
        ucfAdaptor.setGateToxicity(ucf, gate_library, _options);

        if (_options.is_toxicity()) {
            Toxicity.initializeCircuitToxicity(abstract_lc);
        }


        logger.info("\n");
        logger.info("///////////////////////////////////////////////////////////");
        logger.info("///////////////   Loading Cytometry Data   ////////////////");
        logger.info("///////////////////////////////////////////////////////////\n");

        ucfAdaptor.setGateCytometry(ucf, gate_library, _options);






        /**
         * populate the gate objects with the Part objects for 'downstream_parts' and 'regulable_promoter'.
         */
        if(_options.is_tandem_promoter()) {

            logger.info("\n");
            logger.info("///////////////////////////////////////////////////////////");
            logger.info("///////////////   Loading Tandem Promoter Data   //////////");
            logger.info("///////////////////////////////////////////////////////////\n");

            ucfAdaptor.setTandemPromoters(ucf, gate_library, _options);
        }





        /**
         * print statements for inputs/outputs
         */
        for (String i : gate_library.get_INPUT_NAMES()) {
            String input_info = "input:    " + String.format("%-16s", i);
            input_info += "   off_rpu=" + Util.sc(gate_library.get_INPUTS_OFF().get(i));
            input_info += "   on_rpu=" + Util.sc(gate_library.get_INPUTS_ON().get(i));
            logger.info(input_info);
        }
        for (String i : gate_library.get_OUTPUT_NAMES()) {
            String output_info = "output:   " + String.format("%-16s", i);
            logger.info(output_info);
        }


        /**
         * Allow NOR gates to also be used as NOT gates
         */
        if (_options.is_NOTequalsNOR1() && gate_library.get_GATES_BY_TYPE().containsKey(GateType.NOR)) {

            LinkedHashMap<String, Gate> NOR_Gates = gate_library.get_GATES_BY_TYPE().get(GateType.NOR);

            gate_library.get_GATES_BY_TYPE().put(GateType.NOT, NOR_Gates);

            LinkedHashMap<String, ArrayList<Gate>> NOR_Gate_Groups = gate_library.get_GATES_BY_GROUP().get(GateType.NOR);

            gate_library.get_GATES_BY_GROUP().put(GateType.NOT, NOR_Gate_Groups);

        }

        for (Gate g : gate_library.get_GATES_BY_NAME().values()) {
            logger.info("Gate: " + g.system + " " + g.type + " " + g.name + " " + g.group);
        }

        for (GateType gtype : gate_library.get_GATES_BY_GROUP().keySet()) {
            logger.info("GateLibrary groups: " + gtype + " " + gate_library.get_GATES_BY_GROUP().get(gtype).size());
        }

        for (GateType gtype : gate_library.get_GATES_BY_GROUP().keySet()) {

            LinkedHashMap<String, ArrayList<Gate>> groups = gate_library.get_GATES_BY_GROUP().get(gtype);

            for (String group_name : groups.keySet()) {

                String group_string_builder = gtype + ": group name: " + group_name;

                ArrayList<Gate> gates = groups.get(group_name);

                for (Gate g : gates) {
                    group_string_builder += " " + g.name;
                }

                logger.info(group_string_builder);
            }
        }


        for(Gate g: _abstract_lc.get_logic_gates()) {
            logger.info(g.type);
        }


        /**
         * are there enough gates of each type (input, output, logic to build the circuit
         */
        if (abstract_lc.get_input_gates().size() > gate_library.get_INPUT_NAMES().length) {
            logger.info("Number of input gates out of range: " + abstract_lc.get_input_gates().size());
            return;
        }

        if (abstract_lc.get_output_gates().size() > gate_library.get_OUTPUT_NAMES().length) {
            logger.info("Number of output gates out of range: " + abstract_lc.get_output_gates().size());
            return;
        }

        if (!LogicCircuitUtil.libraryGatesCoverCircuitGates(abstract_lc, gate_library)) {
            //logger.info("Not enough gates in the library to cover the gates in the circuit.");
            //return;
            logger.info("Not enough gates in the library to cover the gates in the circuit.");
            return;

        } else {
            logger.info("The gates library can cover the circuit.");
        }


        /**
         *
         * Assign input promoters to input gates, and also assign output gates
         *
         * _options.get_permute_inputs == false: unassigned_lcs.size() == 1
         * _options.get_permute_inputs == true:  unassigned_lcs.size() == 2^(n_inputs)
         *
         */
        //_options.get_permute_inputs = true;


        unassigned_lcs = LogicCircuitUtil.getInputAssignments(abstract_lc, gate_library, _options.is_permute_inputs());



        for(LogicCircuit lc: unassigned_lcs) {
            LogicCircuitUtil.setInputOutputGroups(lc);
        }

        /**
         *
         * Promoter interference has been observed for certain promoters when downstream in the txn unit,
         *      likely due to the inability of RNAP to pass through.  Our term for this is roadblocking,
         *      which disrupts the assumption that tandem promoter activities are additive.
         *
         * As a result, two roadblocking promoters cannot be inputs to a NOR gate or OUTPUT_OR gate,
         *      because one of the promoters must be downstream in the txn unit, which leads to interference.
         *
         *
         * Roadblocking inducible promoters: pTac, pBAD
         * Roadblocking repressible promoters: pSrpR, pPhlF, pBM3R1, pQacR
         *
         */

        ArrayList<String> eugene_part_rules = ucfAdaptor.getEugenePartRules(ucf);
        Roadblock roadblock = new Roadblock();
        roadblock.setThreadDependentLoggername(threadDependentLoggername);
        roadblock.set_roadblockers(eugene_part_rules, gate_library);


        //gate_library.setHashMapsForGates();

        /**
         *
         * If inducible promoters (pTac, pBAD) result in roadblocking in the input order given, promoter interference
         *      will occur, but we design the circuit and provide a warning message.
         *
         * However, when a repressible promoter participates in roadblocking, this will be treated as an illegal assignment.
         *
         */
        ArrayList<LogicCircuit> nonRB_unassigned_lcs = new ArrayList<LogicCircuit>();
        for (LogicCircuit unassigned_lc : unassigned_lcs) {


            /*if (_options.is_assign_C_pBAD()) {
            	// This is a hack for Lauren's latched designs
                for (Gate g : unassigned_lc.get_input_gates()) {
                    if (g.Group.equals("C") && g.Name.equals("pBAD")) {
                        if (!roadblock.illegalInputRoadblocking(unassigned_lc)) {
                            nonRB_unassigned_lcs.add(unassigned_lc);
                        }
                    }
                }
            }*/


            if (!roadblock.illegalInputRoadblocking(unassigned_lc)) {
                nonRB_unassigned_lcs.add(unassigned_lc);
            }

        }


        if (nonRB_unassigned_lcs.size() == 0) {
            logger.info("\n");
            logger.info("-----------------------------------------------------------");
            logger.info("---------------   Warning: input promoter roadblocking ----");
            logger.info("-----------------------------------------------------------\n");

            /*
             * Choose one input assignment... it's roadblocking, but we will continue with the design anyway.
             */
            nonRB_unassigned_lcs.add(unassigned_lcs.get(0));
        }


        /**
         *
         * Ready to assign genetic gates
         *
         * Random
         * BreadthFirstSearch (guarantees global max)
         * Hill climbing
         * Simulated annealing
         *
         */


        //No assignment needed if no logic gates.  2-input OR has no logic gates, for example
        if (unassigned_lcs.get(0).get_logic_gates().size() == 0) {
            assigned_lcs = nonRB_unassigned_lcs;
        }

        else {
            logger.info("\n");
            logger.info("///////////////////////////////////////////////////////////");
            logger.info("///////////////   Assignment algorithm   //////////////////");
            logger.info("///////////////////////////////////////////////////////////\n");
            logger.info("assignment algorithm:  " + _options.get_assignment_algorithm());

            Date datestart = new Date();
            long starttime = datestart.getTime();


            BuildCircuits circuit_builder = new BuildCircuits(); //base class

            logger.info("=========== Assignment algorithm =============");
            
            //default: simulated annealing. similar to hill climbing, but with a cooling schedule
            if (_options.get_assignment_algorithm() == BuildCircuits.AssignmentAlgorithm.sim_annealing) {
                circuit_builder = new BuildCircuitsSimAnnealing(_options, gate_library, roadblock);
            }
            //hill climbing.  Many swaps with accept/reject based on score increase/decrease.
            else if (_options.get_assignment_algorithm() == BuildCircuits.AssignmentAlgorithm.hill_climbing) {
                circuit_builder = new BuildCircuitsHillClimbing(_options, gate_library, roadblock);
            }
            //Breadth First Search algorithm. Performs an exhaustive search.
            else if (_options.get_assignment_algorithm() == BuildCircuits.AssignmentAlgorithm.breadth_first) {
                circuit_builder = new BuildCircuitsBreadthFirstSearch(_options, gate_library, roadblock);

                /**
                 * Breadth-first is memory intensive and is not used in the publicly available tool on cellocad.org.
                 */
//                _result_status = ResultStatus.breadth_first_not_allowed;
//                return;
            }
            //similar to hill climbing, but explores all options for a single swap and chooses the best swap each time.
            else if (_options.get_assignment_algorithm() == BuildCircuits.AssignmentAlgorithm.steepest_ascent) {
                circuit_builder = new BuildCircuitsSteepestAscent(_options, gate_library, roadblock);
            }
            //completely randomizes the gate assignment.  Does this many times.
            else if (_options.get_assignment_algorithm() == BuildCircuits.AssignmentAlgorithm.random) {
                circuit_builder = new BuildCircuitsRandom(_options, gate_library, roadblock);
            }
            //exhaustive... does not scale.
            else if (_options.get_assignment_algorithm() == BuildCircuits.AssignmentAlgorithm.permute) {
                circuit_builder = new BuildCircuitsPermuteNOR(_options, gate_library, roadblock);
            }
            //if you want to reload a prior assignment.  Based on x_logic_circuit.txt parsing.
            else if (_options.get_assignment_algorithm() == BuildCircuits.AssignmentAlgorithm.reload) {
                circuit_builder = new BuildCircuitsReload(_options, gate_library, roadblock);
            }
            //do not use.
            else if (_options.get_assignment_algorithm() == BuildCircuits.AssignmentAlgorithm.preset) {
                circuit_builder = new BuildCircuitsPreset(_options, gate_library, roadblock);
            }
            else {

            }

            circuit_builder.setThreadDependentLoggername(threadDependentLoggername);


            //when circuits have one or more feedback loops, it's a sequential circuit rather than combinational.
            //currently hacky, needs to be refined.
            /*if (_options.get_assignment_algorithm() == BuildCircuits.AssignmentAlgorithm.sequential) {
                _options.set_histogram(false);
                circuit_builder = new SequentialHelper(_options, gate_library, roadblock);
            }*/


            logger.info(unassigned_lcs.size());

            for (LogicCircuit unassigned_lc : nonRB_unassigned_lcs) {

                circuit_builder.set_unassigned_lc(unassigned_lc);


                /**
                 * Run assignment algorithm
                 */
                circuit_builder.buildCircuits();


                // TODO hard-coded for Jonghyeon

                boolean only_tp = false;

                if (only_tp) {

                    for (LogicCircuit lc : circuit_builder.get_logic_circuits()) {

                        boolean has_all_tp_data = LogicCircuitUtil.dataFoundForAllTandemPromoters(gate_library, lc);

                        if (has_all_tp_data) {
                            assigned_lcs.add(lc);
                        }
                    }
                }
                //

                else {

                    assigned_lcs.addAll(circuit_builder.get_logic_circuits());

                }
            }

            /**
             *
             * Assignment complete.
             *
             */
            logger.info("=========== Assigned circuits ================");
            logger.info("assigned lcs: " + assigned_lcs.size() + "");


            Date datestop = new Date();
            long endtime = datestop.getTime();
            long elapsedtime = endtime - starttime;
            logger.info("Total elapsed time for assignment algorithm: " + elapsedtime + " milliseconds");

            if (assigned_lcs.size() == 0) {

                _result_status = ResultStatus.no_assignments_found;

                logger.info("\n");
                logger.info("///////////////////////////////////////////////////////////");
                logger.info("////////   No assignments found. Exiting Cello.   /////////");
                logger.info("///////////////////////////////////////////////////////////\n");

                return;
            }
        }



        /**
         *
         * Multiple circuits will exist:
         *      if permuting input order,
         *      if the search algorithm saves multiple circuits instead of just the best circuit.
         *
         * To get the best circuit, sort the LogicCircuit objects by score
         *
         */
        sortLogicCircuitsByScore(assigned_lcs);

        logger.info("best assignment score: " + String.format("%-5.4f", assigned_lcs.get(0).get_scores().get_score()));


        /**
         * Predict distributions (optional).
         * Generate plasmids.
         * Generate figures.
         */
        logger.info("\n");
        logger.info("///////////////////////////////////////////////////////////");
        logger.info("///////////////   Processing best circuits   //////////////");
        logger.info("///////////////////////////////////////////////////////////\n");




        //sim annealing / hill climbing can add duplicate assignments
        //this doesn't matter if only 1 assignment will be the Cello output
        //but could cause problems if more than 1 assignment is desired

        ArrayList<LogicCircuit> unique_lcs = assigned_lcs;

        LinkedHashMap<String, LogicCircuit> unique_lcs_map = new LinkedHashMap<>();
        if(_options.is_output_all_assignments()) {
            for(LogicCircuit lc: assigned_lcs) {
                String score = Util.sc(lc.get_scores().get_score());
                unique_lcs_map.put(score, lc);
            }
            unique_lcs = new ArrayList<>(unique_lcs_map.values());
        }

        logger.info("all lcs " + assigned_lcs.size());
        logger.info("unique lcs " + unique_lcs.size());
        sortLogicCircuitsByScore(unique_lcs);


        /*if (_options.is_unique_rbs_assignments()) {
            ArrayList<LogicCircuit> unique_assignment_lcs = BuildCircuitsUtil.removeIdenticalTUs(unique_lcs, gate_library, part_library);
            unique_lcs = unique_assignment_lcs;
        }
        else if (_options.is_unique_repressor_assignments()) {
            ArrayList<LogicCircuit> unique_repressor_lcs = BuildCircuitsUtil.getUniqueRepressorAssignments(unique_lcs, gate_library, part_library);
            unique_lcs = unique_repressor_lcs;
        }*/


        if(_options.get_nA() > unique_lcs.size()) {
            _options.set_nA(unique_lcs.size());
        }

        if(_options.is_output_all_assignments()) {

            if(_options.is_histogram()) {

                for(Gate g: gate_library.get_GATES_BY_NAME().values()) {
                    HistogramUtil.interpolateTransferFunctionTitrations(g.name, gate_library);
                }

                String file_name_default = _options.get_home() + _options.get_datapath() + "default_histogram.txt";
                InputOutputGateReader.makeHistogramsforInputRPUs(gate_library, file_name_default);

                for(LogicCircuit lc: unique_lcs) {
                    LogicCircuitUtil.setInputRPU(lc, gate_library);

                    for (Gate g : lc.get_Gates()) {
                        g.get_histogram_bins().init();
                    }

                    for (Gate g : lc.get_logic_gates()) {
                        g.set_xfer_hist(gate_library.get_GATES_BY_NAME().get(g.name).get_xfer_hist());
                    }

                    Evaluate.evaluateCircuitHistogramOverlap(lc, gate_library, _options);
                }


                for(LogicCircuit lc: unique_lcs) {
                    Double overlap_score = lc.get_scores().get_conv_overlap();
                    lc.get_scores().set_onoff_ratio(overlap_score);
                }

                sortLogicCircuitsByScore(unique_lcs);

            }

            int counter = 0;
            for(LogicCircuit lc: unique_lcs) {

                lc.set_assignment_name( _options.get_jobID() + "_A" + String.format("%03d", counter) );
                counter++;
                Util.fileWriter(_options.get_output_directory() + lc.get_assignment_name() + "_logic_circuit.txt", lc.toString(), false);

                logger.info("=========== Circuit bionetlist ===============");
                PlasmidUtil.setGateParts(lc, gate_library, part_library);
                Netlist.setBioNetlist(lc, false);
                Util.fileWriter(_options.get_output_directory() + lc.get_assignment_name() + "_bionetlist.txt", lc.get_netlist(), false);
            }

            return;
        }




        sortLogicCircuitsByScore(unique_lcs);
        _logic_circuits = new ArrayList<>();

        for(int a=0; a<_options.get_nA(); ++a) {



            LogicCircuit lc = new LogicCircuit(unique_lcs.get(a));
            lc.set_index(a);
            lc.set_assignment_name( _options.get_jobID() + "_A" + String.format("%03d", a) );
            _logic_circuits.add(lc);


            Double unit_conversion = ucfAdaptor.getUnitConversion(ucf);
            for(Gate g: lc.get_output_gates()) {
                g.set_unit_conversion(unit_conversion);
            }

            Evaluate.evaluateCircuit(lc, gate_library, _options);
            for (Gate g : lc.get_Gates()) {
                Evaluate.evaluateGate(g, _options);
            }
            if (_options.is_toxicity()) {
                Toxicity.evaluateCircuitToxicity(lc, gate_library);
            }



            logger.info("=========== Circuit assignment details =======");
            logger.info(lc.toString() + "\n");
            Util.fileWriter(_options.get_output_directory() + lc.get_assignment_name() + "_logic_circuit.txt", lc.toString(), false);


            // TODO
            logger.info("=========== Circuit bionetlist ===============");
            PlasmidUtil.setGateParts(lc, gate_library, part_library);
            Netlist.setBioNetlist(lc, false);
            logger.info(lc.get_netlist());
            Util.fileWriter(_options.get_output_directory() + lc.get_assignment_name() + "_bionetlist.txt", lc.get_netlist(), false);



            if(_options.is_histogram()) {

                logger.info("=========== Simulate cytometry distributions");

                String file_name_default = _options.get_home() + _options.get_datapath() + "default_histogram.txt";
                InputOutputGateReader.makeHistogramsforInputRPUs(gate_library, file_name_default);

                LogicCircuitUtil.setInputRPU(lc, gate_library);


                for(Gate g: lc.get_Gates()) {
                    g.get_histogram_bins().init();
                }


                for(Gate g: lc.get_logic_gates()) {

                    HistogramUtil.interpolateTransferFunctionTitrations(g.name, gate_library);

                    g.set_xfer_hist(gate_library.get_GATES_BY_NAME().get(g.name).get_xfer_hist());

                    logger.info("histogram interpolation for " + g.name + " " + g.get_xfer_hist().get_xfer_interp().size() + " " + g.get_xfer_hist().get_xfer_interp().get(0).length );

                }

                Evaluate.evaluateCircuitHistogramOverlap(lc, gate_library, _options);

                logger.info("distribution score: " + lc.get_scores().get_conv_overlap());
            }

            //if(_options.get_histogram() && lc.get_scores().get_conv_overlap() < _options.get_histogram()_threshold) {
            //    continue;
            //}



            /*SBOLCircuitWriterIWBDA sbol_circuit_writer = new SBOLCircuitWriterIWBDA();

            sbol_circuit_writer.setCircuitName(lc.get_assignment_name());
            //String sbol_document = sbol_circuit_writer.writeSBOLCircuit(lc.get_assignment_name() + "_SBOL.xml", lc, lc.get_assignment_name(), _options);
            String sbol_document = sbol_circuit_writer.writeSBOLCircuit(lc.get_assignment_name() + "_circuit.sbol", lc, lc.get_assignment_name(), _options);*/


            if (_options.is_figures()) {
                logger.info("\n");
                logger.info("///////////////////////////////////////////////////////////");
                logger.info("////////////////////////   Figures   //////////////////////");
                logger.info("///////////////////////////////////////////////////////////\n");

                generateFigures(lc, gate_library);
            }

            if(_options.is_plasmid()) {
                logger.info("\n");
                logger.info("///////////////////////////////////////////////////////////");
                logger.info("///////////////   Plasmid DNA sequences   /////////////////");
                logger.info("///////////////////////////////////////////////////////////\n");

                PlasmidUtil.findPartComponentsInOutputGates(lc, gate_library, part_library);

                generatePlasmids(lc, gate_library, part_library, ucf);
            }



            ScriptCommands script_commands = new ScriptCommands(_options.get_home(), _options.get_output_directory(), _options.get_jobID());
            script_commands.removeEPSFiles(_options.get_output_directory());
            //script_commands.removeGateFiles(_options.get_output_directory());


            /**
             * This warning was printed earlier, but it is more noticeable at the end of the text file
             */
            if (roadblock.illegalInputRoadblocking(lc)) {

                _result_status = ResultStatus.roadblocking_inputs;

                logger.info("\n");
                logger.info("-----------------------------------------------------------");
                logger.info("---------------   Warning: input promoter roadblocking ----");
                logger.info("-----------------------------------------------------------\n");
            }
        }

        if(_result_status != ResultStatus.roadblocking_inputs) {
            _result_status = ResultStatus.success;
        }



        logger.info("\n");
        logger.info("///////////////////////////////////////////////////////////");
        logger.info("///////////////   Cello finished playing   ////////////////");
        logger.info("///////////////////////////////////////////////////////////\n");


        return;
    }


    ///////////////////////////////////////////////////////////////////////////////////////////////
    //////////////           DNACompiler functions              ///////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////


    /**
     *
     *  Use Eugene for rules-based permutation of txn unit order/orientation to generate additional plasmid variants.
     *
     * @param lc
     * @param gate_library
     * @param part_library
     */
    public void generatePlasmids(LogicCircuit lc, GateLibrary gate_library, PartLibrary part_library, UCF ucf) {

        String name_Eug_circuit_rules =  lc.get_assignment_name() + "_Eugene_circuit_module_rules.eug";
        String name_Eug_circuit_parts =  lc.get_assignment_name() + "_Eugene_circuit_module_part_list.txt";
        String name_Eug_circuit_gates =  lc.get_assignment_name() + "_Eugene_circuit_module_gate_list.txt";

        String name_Eug_output_rules  =  lc.get_assignment_name() + "_Eugene_output_module_rules.eug";
        String name_Eug_output_parts  =  lc.get_assignment_name() + "_Eugene_output_module_part_list.txt";



        logger.info("=========== Setting gate parts according to assigned gate names");

        //Plasmid.setGateParts(lc, gate_library, part_library);

        PlasmidUtil.setTxnUnits(lc, gate_library, _options);

        EugeneAdaptor eugeneAdaptor = new EugeneAdaptor();
        eugeneAdaptor.setThreadDependentLoggername(threadDependentLoggername);


        ArrayList<String> sensor_module_lines = Util.fileLines(_options.get_fin_sensor_module());
        String sensor_module_sequence = "";
        for(String s: sensor_module_lines) {
            sensor_module_sequence += s;
        }
        Part sensor_module_part = new Part("sensor_module", "backbone", sensor_module_sequence);
        ArrayList<Part> sensor_module_list = new ArrayList<Part>();
        ArrayList<ArrayList<Part>> sensor_module_lists = new ArrayList<ArrayList<Part>>();
        sensor_module_list.add(sensor_module_part);
        sensor_module_lists.add(sensor_module_list);

        lc.set_sensor_module_parts(sensor_module_lists);

        String circuit_eugene_file_string = "";

        ucfAdaptor.setThreadDependentLoggername(threadDependentLoggername);


        ArrayList<String> eugene_part_rules = ucfAdaptor.getEugenePartRules(ucf);
        ArrayList<String> eugene_gate_rules = ucfAdaptor.getEugeneGateRules(ucf);

        eugeneAdaptor.set_eugene_part_rules(eugene_part_rules);
        eugeneAdaptor.set_eugene_gate_rules(eugene_gate_rules);


        String circuit_module_location_name = ucfAdaptor.getCircuitModuleLocationName(ucf);
        String output_module_location_name = ucfAdaptor.getOutputModuleLocationName(ucf);


        if(circuit_module_location_name == null || circuit_module_location_name.isEmpty() ||
                output_module_location_name == null || output_module_location_name.isEmpty() ||
                circuit_module_location_name.equals(output_module_location_name) ) {

            logger.info("=========== Eugene: circuit module and output module combined ===========");

            ArrayList<Gate> logic_and_output_gates = new ArrayList<>();
            logic_and_output_gates.addAll(lc.get_logic_gates());
            logic_and_output_gates.addAll(lc.get_output_gates());

            for(Gate g: logic_and_output_gates) {
                if(g.system.equals("Ecoligenome") || g.getChildren().get(0).system.equals("Ecoligenome")){
                    _options.set_genome(true);
                }
                if(g.system.equals("Yeast") || g.getChildren().get(0).system.equals("Yeast")){
                    _options.set_yeast(true);
                }
            }

            if(_options.is_yeast()) {
                circuit_eugene_file_string = eugeneAdaptor.generateEugeneFile1(logic_and_output_gates, name_Eug_circuit_rules, part_library, _options);
            }

            else if(_options.is_genome()) {
                circuit_eugene_file_string = eugeneAdaptor.generateEugeneFile2(logic_and_output_gates, name_Eug_circuit_rules, part_library, _options, lc.get_logic_gates().size());
            }

            else{
                circuit_eugene_file_string = eugeneAdaptor.generateEugeneFile(logic_and_output_gates, name_Eug_circuit_rules, part_library, _options);
            }

            logger.info("Eugene: combinatorial design of plasmid layouts...\n");

            eugeneAdaptor.callEugene(name_Eug_circuit_rules, lc.get_circuit_module_parts(), part_library, _options);

            logger.info("Number of circuit module layouts: " + lc.get_circuit_module_parts().size());

        }

        else if(!circuit_module_location_name.isEmpty() && ! output_module_location_name.isEmpty() && !circuit_module_location_name.equals(output_module_location_name)) {
            logger.info("=========== Eugene: circuit module ===========");

            circuit_eugene_file_string = eugeneAdaptor.generateEugeneFile(lc.get_logic_gates(), name_Eug_circuit_rules, part_library, _options);

            logger.info("Eugene: combinatorial design of plasmid layouts...\n");

            eugeneAdaptor.callEugene(name_Eug_circuit_rules, lc.get_circuit_module_parts(), part_library, _options);

            logger.info("Number of circuit module layouts: " + lc.get_circuit_module_parts().size());


            logger.info("=========== Eugene: output module ============");

            //_options.get_eugene_scars = false;
            String output_eugene_file_string = eugeneAdaptor.generateEugeneFile(lc.get_output_gates(), name_Eug_output_rules, part_library, _options);

            eugeneAdaptor.callEugene(name_Eug_output_rules, lc.get_output_module_parts(), part_library, _options);

            logger.info("Number of output module layouts: " + lc.get_output_module_parts().size());
        }


        int p_counter = 0;

        for(ArrayList<Part> module: lc.get_circuit_module_parts()) {

            String N = lc.get_assignment_name() + "_P" + String.format("%03d", p_counter);
            ArrayList<String> parts_list = new ArrayList<String>();
            ArrayList<String> gates_list = new ArrayList<String>();
            for(Part p: module) {
                parts_list.add(p.get_direction() + p.get_name());
                if(p.get_type().equals("cds")) {
                    gates_list.add(p.get_direction() + "gate_" + p.get_name());
                }
            }

            Util.fileWriter(_options.get_output_directory() + name_Eug_circuit_parts, N + " " + parts_list.toString()+"\n", true);
            Util.fileWriter(_options.get_output_directory() + name_Eug_circuit_gates, N + " " + gates_list.toString()+"\n", true);
            ++p_counter;
        }


        for(ArrayList<Part> module: lc.get_output_module_parts()) {

            String N = lc.get_assignment_name() + "_P" + String.format("%03d", p_counter);
            ArrayList<String> parts_list = new ArrayList<String>();
            ArrayList<String> gates_list = new ArrayList<String>();
            for (Part p : module) {
                parts_list.add(p.get_direction() + p.get_name());
            }
            Util.fileWriter(_options.get_output_directory() + name_Eug_output_parts, N + " " + parts_list.toString()+"\n", true);
        }



        if(ucf.get_genetic_locations().isEmpty()) {
            lc.set_sensor_plasmid_parts(lc.get_sensor_module_parts());
            lc.set_circuit_plasmid_parts(lc.get_circuit_module_parts());
            lc.set_output_plasmid_parts(lc.get_output_module_parts());
        }
        else {
            if (! ucf.get_genetic_locations().containsKey("sensor_module_location")) {
                lc.set_sensor_plasmid_parts(lc.get_sensor_module_parts());
            }
            if (! ucf.get_genetic_locations().containsKey("circuit_module_location")) {
                logger.info("Setting circuit module parts");
                lc.set_circuit_plasmid_parts(lc.get_circuit_module_parts());
            }
            if (! ucf.get_genetic_locations().containsKey("output_module_location")) {
                lc.set_output_plasmid_parts(lc.get_output_module_parts());
            }
        }


        GeneticLocationWriter.insertModulePartsIntoGeneticLocations(lc, ucf);

//        logger.info("Circuit module parts");
//        logger.info(lc.get_circuit_module_parts().get(0).toString());
//
//        logger.info("Circuit plasmid parts");
//        logger.info(lc.get_circuit_plasmid_parts().get(0).toString());

        ArrayList<String> all_plasmid_strings = new ArrayList<>();

        logger.info("\n=========== Writing plasmid files ============");
        if(lc.get_sensor_plasmid_parts().size() > 0) {
            all_plasmid_strings.addAll( PlasmidUtil.writePlasmidFiles1(lc.get_sensor_plasmid_parts(), lc.get_assignment_name(), "plasmid_sensor", _options.get_output_directory(), _options, part_library) );
        }
        if(lc.get_circuit_plasmid_parts().size() > 0) {
            all_plasmid_strings.addAll( PlasmidUtil.writePlasmidFiles1(lc.get_circuit_plasmid_parts(), lc.get_assignment_name(), "plasmid_circuit", _options.get_output_directory(), _options, part_library) );
        }
        if(lc.get_output_plasmid_parts().size() > 0) {
            all_plasmid_strings.addAll( PlasmidUtil.writePlasmidFiles1(lc.get_output_plasmid_parts(), lc.get_assignment_name(), "plasmid_output", _options.get_output_directory(), _options, part_library) );
        }


        logger.info("\n=========== SBOL for circuit plasmids ========");

        //currently not writing an SBOL document for the output plasmid if the output module is on a different plasmid than the circuit.
        for(int i=0; i<lc.get_circuit_plasmid_parts().size(); ++i) {
            ArrayList<Part> plasmid = lc.get_circuit_plasmid_parts().get(i);

            SBOLCircuitWriter sbol_circuit_writer = new SBOLCircuitWriter();

            sbol_circuit_writer.setCircuitName(lc.get_assignment_name());
            String sbol_filename = lc.get_assignment_name() + "_sbol_circuit" + "_P" + String.format("%03d", i) + ".sbol";
            String sbol_plasmid_name = lc.get_assignment_name() + "_P" + String.format("%03d", i);

            String sbol_document = sbol_circuit_writer.writeSBOLCircuit(sbol_filename, lc, plasmid, sbol_plasmid_name, _options);
        }

        PlasmidUtil.resetParentGates(lc);

        if(_options.is_figures()) {
            if (_options.is_dnaplotlib()) {
                logger.info("\n");
                logger.info("=========== DNAPlotLib =======================");
                logger.info("rendering genetic diagram image...");

                PlotLibWriter.writeCircuitsForDNAPlotLib(lc.get_circuit_plasmid_parts(), lc.get_index(), _options);
            }
        }

        logger.info("");
    }

    /**
     *
     * Automated figure generation.
     *
     */
    public void generateFigures(LogicCircuit lc, GateLibrary gate_library) {

        Integer a = lc.get_index();
        String name_wiring_xfer =  lc.get_assignment_name() + "_wiring_xfer.dot";
        String name_wiring_rpu  =  lc.get_assignment_name() + "_wiring_rpu.dot";
        String name_wiring_grn  =  lc.get_assignment_name() + "_wiring_grn.dot";

        Gnuplot gnuplot = new Gnuplot(_options.get_home(), _options.get_output_directory(), _options.get_jobID());
        Graphviz graphviz = new Graphviz(_options.get_home(), _options.get_output_directory(), _options.get_jobID());
        ScriptCommands script_commands = new ScriptCommands(_options.get_home(), _options.get_output_directory(), _options.get_jobID());

        logger.info("=========== Graphviz wiring diagram ==========");
        Colors.setColors();
        graphviz.printGraphvizDotText(lc, name_wiring_grn);
        script_commands.makeDot2Png(name_wiring_grn);

        if(_options.is_response_fn()) {
            logger.info("=========== Graphviz Xfer figures ============");
            gnuplot.printGnuplotXfer(lc, _options);
            graphviz.printGraphvizXferPNG(lc, name_wiring_xfer);
            script_commands.makeCircuitRPUFigure(lc.get_assignment_name());
            script_commands.makeDot2Png(name_wiring_xfer);
        }
        if(_options.is_snr()) {
            logger.info("=========== SNR figures =======================");
            for(Gate g: lc.get_logic_gates()) {
                gnuplot.printGnuplotGateSNR(g, lc.get_assignment_name(), _options);
            }
            script_commands.makeCircuitSNRFigure(lc.get_assignment_name());
        }
        if(_options.is_tandem_promoter()) {
            logger.info("=========== Tandem promoter figures =======================");
            gnuplot.makeTandemPromoterHeatmaps(lc, gate_library, _options);


            InterpolateTandemPromoter itp = new InterpolateTandemPromoter();

            HistogramBins hbins = new HistogramBins();
            hbins.init();

            for(Gate g: lc.get_Gates()) {
                if(g.type == GateType.INPUT) {
                    continue;
                }


                boolean tp_exists = false;
                String tp_name = "";
                double[][] grid = new double[hbins.get_NBINS()][hbins.get_NBINS()];
                Gate child1 = new Gate();
                Gate child2 = new Gate();
                ArrayList<String> fanin_gate_names = new ArrayList<>();

                String var = "x";
                if(g.get_variable_names().size() == 1) {
                    var = g.get_variable_names().get(0);
                }


                if (g.get_variable_wires().get(var).size() == 2) { //hard-coded

                    child1 = g.getChildren().get(0);
                    child2 = g.getChildren().get(1);

                    if (child1.type == Gate.GateType.INPUT) {
                        fanin_gate_names.add("input_" + child1.name);
                    } else {
                        fanin_gate_names.add(child1.name);
                    }

                    if (child2.type == Gate.GateType.INPUT) {
                        fanin_gate_names.add("input_" + child2.name);
                    } else {
                        fanin_gate_names.add(child2.name);
                    }


                    String tandem_promoter_name_1 = fanin_gate_names.get(0) + "_" + fanin_gate_names.get(1);
                    String tandem_promoter_name_2 = fanin_gate_names.get(1) + "_" + fanin_gate_names.get(0);
                    tp_name = tandem_promoter_name_1;

                    if (gate_library.get_TANDEM_PROMOTERS().containsKey(tandem_promoter_name_1)) {
                        grid = gate_library.get_TANDEM_PROMOTERS().get(tandem_promoter_name_1);
                        tp_name = tandem_promoter_name_1;
                        tp_exists = true;
                    } else if (gate_library.get_TANDEM_PROMOTERS().containsKey(tandem_promoter_name_2)) {
                        grid = gate_library.get_TANDEM_PROMOTERS().get(tandem_promoter_name_2);
                        tp_name = tandem_promoter_name_2;
                        tp_exists = true;
                    }
                }


                if(tp_exists) {

                    String file_points_on  = "grid_tp_" + tp_name + "_points_on.txt";
                    String file_points_off = "grid_tp_" + tp_name + "_points_off.txt";
                    String file_interp = "grid_tp_" + tp_name + ".txt";
                    String file_points_on_path  = _options.get_output_directory() + "/" + file_points_on;
                    String file_points_off_path = _options.get_output_directory() + "/" + file_points_off;
                    String file_interp_path = _options.get_output_directory() + "/" + file_interp;

                    logger.info("////////////////////////////////////////// ");
                    logger.info("making " + file_interp);

                    itp.writeGridstoFiles(grid, file_interp_path, 5);




                    String gate1_name = fanin_gate_names.get(0);
                    String gate2_name = fanin_gate_names.get(1);

                    String points_on  = "";
                    String points_off = "";

                    String v = "x";

                    for(int row=0; row<g.get_logics().size(); ++row) {

                        Double in1 = 0.0;
                        Double in2 = 0.0;

                        if (child1.type == Gate.GateType.INPUT) {
                            if (child1.get_logics().get(row) == 0) {
                                in1 = Math.pow(10, hbins.get_LOGMIN());
                            } else if (child1.get_logics().get(row) == 1) {
                                in1 = Math.pow(10, hbins.get_LOGMAX());
                            }
                        } else {
                            in1 = child1.get_inrpus().get(v).get(row);
                        }


                        if (child2.type == Gate.GateType.INPUT) {
                            if (child2.get_logics().get(row) == 0) {
                                in2 = Math.pow(10, hbins.get_LOGMIN());
                            } else if (child2.get_logics().get(row) == 1) {
                                in2 = Math.pow(10, hbins.get_LOGMAX());
                            }
                        } else {
                            in2 = child2.get_inrpus().get(v).get(row);
                        }


                        if (tp_name.startsWith(gate1_name) && tp_name.endsWith(gate2_name)) {
                            //correct in1 and in2 order
                        } else if (tp_name.startsWith(gate2_name) && tp_name.endsWith(gate1_name)) {
                            Double temp = new Double(in1);
                            in1 = in2;
                            in2 = temp;
                        } else {
                            throw new IllegalStateException("Problem with tandem promoter lookup");
                        }

                        Integer bin1 = HistogramUtil.bin_of_logrpu(Math.log10(in1), hbins);
                        Integer bin2 = HistogramUtil.bin_of_logrpu(Math.log10(in2), hbins);

                        int logic = g.get_logics().get(row);

                        if (g.type == GateType.NOR) {
                            logic = BooleanLogic.computeNOT(logic);
                        }

                        if(logic == 0) {
                            points_off += bin1 + " " + bin2 + " 1\n";
                        }
                        if(logic == 1) {
                            points_on += bin1 + " " + bin2 + " 1\n";
                        }

                    }

                    Util.fileWriter(file_points_on_path, points_on, false);
                    Util.fileWriter(file_points_off_path, points_off, false);


                    String cmd = "perl " + _options.get_home() + "/resources/scripts/make_tandem_promoter_heatmaps.pl " +
                            _options.get_output_directory() + " " +
                            _options.get_jobID() + " " +
                            _options.get_home() + "/resources/scripts/" + " " +
                            file_interp + " " +
                            file_points_on + " " +
                            file_points_off + " " +
                            tp_name;

                    String command_result = Util.executeCommand(cmd);
                }

                else {
                    logger.info(tp_name + " DOES NOT EXIST ");
                }


            }

        }


        if (_options.is_truthtable_rpu()) {
            logger.info("=========== Truth table figure(s) ============");

            if (_options.is_histogram()) {
                logger.info("=========== histogram multiplots =============");

                String input_truth = "";
                for(int i=0; i<lc.get_input_gates().get(0).get_logics().size(); ++i) {
                    for (Gate g : lc.get_input_gates()) {
                        input_truth += g.get_logics().get(i);
                    }
                    input_truth += ",";
                }

                gnuplot.makeHistogramMultiplot(lc, "truth", input_truth);



                for (Gate g : lc.get_logic_gates()) {
                    gnuplot.makeHistogramMultiplotGate(g, lc.get_assignment_name(), "gate", input_truth);
                }
                /*for (Gate g : lc.get_input_gates()) {
                    gnuplot.makeHistogramMultiplotGate(g, lc.get_assignment_name(), "gate");
                }*/
                for (Gate g : lc.get_output_gates()) {
                    gnuplot.makeHistogramMultiplotGate(g, lc.get_assignment_name(), "gate", input_truth);
                }
                graphviz.printGraphvizDistrPNG(lc, name_wiring_rpu);
                script_commands.makeDot2Png(name_wiring_rpu);
            }
            else {
                logger.info("=========== bargraph multiplots ==============");
                gnuplot.makeTruthtableBargraph(lc, "truth");

                for (Gate g : lc.get_logic_gates()) {
                    gnuplot.makeTruthtableBargraph(g, lc.get_assignment_name(), "gate");
                }
                /*for (Gate g : lc.get_input_gates()) {
                    gnuplot.makeTruthtableBargraph(g, lc.get_assignment_name(), "gate");
                }*/
                for (Gate g : lc.get_output_gates()) {
                    gnuplot.makeTruthtableBargraph(g, lc.get_assignment_name(), "gate");
                }
                graphviz.printGraphvizDistrPNG(lc, name_wiring_rpu);
                script_commands.makeDot2Png(name_wiring_rpu);
            }
        }

        if(_options.is_toxicity() && _options.is_truthtable_tox()) {
            logger.info("============== cell growth plots =============");
            gnuplot.makeCellGrowthFigure(lc, "toxicity");
        }


        logger.info("=========== Table of predicted expression levels (RPU)");
        String rpu_table = lc.printRPUTable();
        String outfile_rputable = lc.get_assignment_name() + "_rputable.txt";
        Util.fileWriter(_options.get_output_directory() + outfile_rputable, rpu_table, false);
        logger.info(rpu_table);

        if(_options.is_toxicity()) {
            logger.info("=========== Table of predicted cell growth (relative OD600)");
            String tox_table = Toxicity.writeToxicityTable(lc);
            logger.info(tox_table);
            String outfile = lc.get_assignment_name() + "_toxtable.txt";
            Util.fileWriter(_options.get_output_directory() + outfile, tox_table, false);
        }

    }





    /**
     *
     * set filepaths to load input, output, repressor, toxicity data.
     *
     * Command-line arguments can be used for custom file inputs
     *
     */
    public void setPaths() {

        //Date: prefix for output files
        if(_options.get_jobID().equals("")) {
            java.util.Date date=new Date();
            _options.set_jobID( "job_" + String.valueOf(date.getTime()) );
        }

        if(_options.get_fin_sensor_module() == "") {
            _options.set_fin_sensor_module( _options.get_home() + _options.get_datapath() + "inputs/sensor_module.txt" );
        }
        if(_options.get_fin_input_promoters() == "") {
            _options.set_fin_input_promoters( _options.get_home() + _options.get_datapath() + "inputs/Inputs.txt" );
        }
        if(_options.get_fin_output_genes() == "") {
            _options.set_fin_output_genes( _options.get_home() + _options.get_datapath() + "outputs/Outputs.txt" );
        }

        if(_options.get_UCFfilepath() == "") {
            _options.set_UCFfilepath( _options.get_home() + "/resources/UCF/Eco1C1G1T1.UCF.json" );
        }

        if(_options.get_output_directory().equals("")) {
            _options.set_output_directory( _options.get_jobID() + "/" );
        }

    }


    /**
     *
     * Prashant Vaidyanathan's NetlistSynthesizer generates the wiring diagram from Verilog input
     *
     * Flow 1: ABC to AND-Inverter Graph to NOR/NOT
     * Flow 2: Espresso to POS to NOR/NOT
     * Other: precomputed netlists for 3-input 1-output (by Swapnil Bhatia)
     *
     */
    public LogicCircuit getAbstractCircuit(String verilog_filepath, UCF ucf) throws IOException, ParseException {

        if(_options.get_circuit_type() == CircuitType.sequential) {

            if( ! _options.get_synthesis().equals("originalstructural")) {
                throw new IllegalStateException("ARGUMENTS: sequential logic requires originalstructural logic synthesis.");
            }

            if(get_options().get_fin_sequential_waveform() == "") {
                throw new IllegalStateException("ARGUMENTS: missing sequential waveform");
            }

            LogicCircuit abstract_lc = StructuralVerilogToDAG.createDAG(get_options().get_fin_verilog());

            return abstract_lc;
        }



        LogicCircuit abstract_logic_circuit = new LogicCircuit();

        ////////////////// Create LogicCircuit from NetSynth //////////////


        //get Abstract Circuit with options
        org.cellocad.BU.dom.DAGW GW = new org.cellocad.BU.dom.DAGW();

        String verilog_string = "";
        ArrayList<String> verilog_lines = Util.fileLines(verilog_filepath);
        for(String s: verilog_lines) {
            verilog_string += s + "\n";
        }

        List<NetSynthSwitch> switches = new ArrayList<>();
        org.json.JSONArray motifLibrary = new org.json.JSONArray();

        if (_options.get_synthesis().equals("abc")) {
            switches.add(NetSynthSwitch.abc);
        }
        if (_options.get_synthesis().equals("espresso")) {
            switches.add(NetSynthSwitch.espresso);
        }
        if (_options.get_synthesis().equals("originalstructural")) {
            switches.add(NetSynthSwitch.originalstructural);
        }
        if (_options.get_output_or().equals("true")) {
            switches.add(NetSynthSwitch.output_or);
        }


        //convert org.simple.json to org.json
        for(int i=0; i<ucf.get_motif_library().size(); ++i) {
            String objString = ucf.get_motif_library().get(i).toString();
            try {
                motifLibrary.put(new org.json.JSONObject(objString));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        NetSynth netsynth = new NetSynth("netSynth", Utilities.getNetSynthResourcesFilepath() ,_options.get_output_directory());

        GW = netsynth.runNetSynth(
                verilog_filepath,
                switches,
//                new ArrayList<NetSynthSwitch>(),
                motifLibrary
        );

        netsynth.cleanDirectory();


        abstract_logic_circuit = new LogicCircuit(GW.Gates, GW.Wires);

        //Prashant needs to fix bug with extra output wire.
        LogicCircuit lc = abstract_logic_circuit;
        for(int i=0; i<lc.get_Wires().size(); ++i) {
            Wire w = lc.get_Wires().get(i);
            if(w.to.index == w.from.index) {
                lc.get_Wires().remove(i);
                i--;
            }
        }


        for(Gate g: abstract_logic_circuit.get_Gates()) {
            if(g.outgoing != null) {
                g.outgoing_wire_index = g.outgoing.index;
            }
        }
        for(Wire w: abstract_logic_circuit.get_Wires()) {
            if(w.from != null) {
                w.from_index = w.from.index;
            }
            if(w.to != null) {
                w.to_index = w.to.index;
            }
            if(w.next != null) {
                w.next_index = w.next.index;
            }
        }

        LogicCircuitUtil.renameGatesWires(abstract_logic_circuit);

        logger.info(Netlist.getNetlist(abstract_logic_circuit));

        _abstract_lc = abstract_logic_circuit;


        return abstract_logic_circuit;
    }



    /**
     *
     * sorts LogicCircuit objects by score
     *
     *
     */
    public static void sortLogicCircuitsByScore(ArrayList<LogicCircuit> circuits) {
        Collections.sort(circuits,
                new Comparator<LogicCircuit>() {
                    public int compare(LogicCircuit lc1, LogicCircuit lc2){
                        int result = 0;
                        if ( (lc2.get_scores().get_score() - lc1.get_scores().get_score()) > 1e-10 ){
                            result = 1;
                        }else if ( (lc2.get_scores().get_score() - lc1.get_scores().get_score()) < -1.0e-10){
                            result = -1;
                        }
                        return result;
                    }
                }
        );
    }

    public ObjectNode lcToNode(LogicCircuit lc) {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode node = mapper.valueToTree(lc);
        return node;
    }


    public String objToJSONString(Object o) {
        Gson gson = new GsonBuilder().excludeFieldsWithModifiers(Modifier.TRANSIENT).setPrettyPrinting().create();
        //Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String json = gson.toJson(o);
        return json.toString();
    }

    public void objToJSON(Object o, String filename) {

        ObjectMapper mapper = new ObjectMapper();
        try
        {
            mapper.writerWithDefaultPrettyPrinter().writeValue(new File(_options.get_home() + "/src/test/resources/"+filename), o);
        } catch (JsonGenerationException e)
        {
            e.printStackTrace();
        } catch (JsonMappingException e)
        {
            e.printStackTrace();
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public LogicCircuit lcJSONToObj(GateLibrary gate_library) {

        GsonBuilder gson_builder = new GsonBuilder();

        try {

            java.io.BufferedReader br = new java.io.BufferedReader(
                    new java.io.FileReader(_options.get_home() + "/src/test/resources/circuit.json"));

            //convert the json string back to object
            LogicCircuit obj = gson_builder.create().fromJson(br, LogicCircuit.class);
            obj.reconnectCircuitByIndexes();
            LogicCircuit lc = new LogicCircuit(obj);

            Evaluate.simulateLogic(lc);
            Evaluate.evaluateCircuit(lc, gate_library, _options);

            return lc;

        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }


    public GateLibrary glJSONToObj() {

        GsonBuilder gson_builder = new GsonBuilder();

        try {

            java.io.BufferedReader br = new java.io.BufferedReader(
                    new java.io.FileReader(_options.get_home() + "/src/test/resources/gate_library.json"));

            //convert the json string back to object
            GateLibrary obj = gson_builder.create().fromJson(br, GateLibrary.class);

            return obj;

        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    /////////////////////////
    //
    // Private member data
    //
    /////////////////////////

    private String _username = "";

    @Getter @Setter private ArrayList<Assignment> _assignments = new ArrayList<>();

    @Getter @Setter private LogicCircuit _abstract_lc = new LogicCircuit();
    @Getter @Setter private ArrayList<LogicCircuit> _logic_circuits = new ArrayList<>();

    @Getter @Setter private ResultStatus _result_status;

    @Getter private final Args _options = new Args();


    private UCFAdaptor ucfAdaptor = new UCFAdaptor();
    private UCFReader ucfReader = new UCFReader();
    private UCFValidator ucfValidator = new UCFValidator();
    
    private String threadDependentLoggername = String.valueOf(UUID.randomUUID());
    private Logger logger;
    private Gson gson = new GsonBuilder().setPrettyPrinting().create();


}


