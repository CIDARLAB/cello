import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.UUID;

import org.apache.log4j.Logger;
import org.cellocad.BU.dom.DAGW;
import org.cellocad.BU.netsynth.NetSynth;
import org.cellocad.BU.netsynth.NetSynthSwitch;
import org.cellocad.MIT.dnacompiler.Args;
import org.cellocad.MIT.dnacompiler.BuildCircuits;
import org.cellocad.MIT.dnacompiler.BuildCircuitsBreadthFirstSearch;
import org.cellocad.MIT.dnacompiler.DNACompiler;
import org.cellocad.MIT.dnacompiler.Gate;
import org.cellocad.MIT.dnacompiler.Gate.GateType;
import org.cellocad.MIT.dnacompiler.GateLibrary;
import org.cellocad.MIT.dnacompiler.GeneticLocationWriter;
import org.cellocad.MIT.dnacompiler.Part;
import org.cellocad.MIT.dnacompiler.Roadblock;
import org.cellocad.MIT.dnacompiler.Toxicity;
import org.cellocad.MIT.dnacompiler.PartLibrary;
import org.cellocad.MIT.dnacompiler.PlasmidUtil;
import org.cellocad.MIT.dnacompiler.UCF;
import org.cellocad.MIT.dnacompiler.Util;
import org.cellocad.MIT.dnacompiler.LogicCircuit;
import org.cellocad.MIT.dnacompiler.LogicCircuitUtil;
import org.cellocad.MIT.dnacompiler.Wire;
import org.cellocad.MIT.figures.PlotLibWriter;
import org.cellocad.adaptors.eugeneadaptor.EugeneAdaptor;
import org.cellocad.adaptors.sboladaptor.SBOLCircuitWriter;
import org.cellocad.adaptors.ucfadaptor.UCFReader;
import org.cellocad.adaptors.ucfadaptor.UCFAdaptor;
import org.json.JSONArray;
import org.junit.Test;

public class Rule30Test {

    @Test
    public void testRule30Circuit() {
        String rule30Verilog = "module rule30struct(output out1,  input in1, in2, in3);" + '\n' +
            "   wire w1,w2,w3,w4,w5;" + '\n' +
            "   nor (w1, in1, in2);" + '\n' +
            "   not (w2, w1);" + '\n' +
            "   not (w3, in3);" + '\n' +
            "   nor (w4, w1, in3);" + '\n' +
            "   nor (w5, w2, w3);" + '\n' +
            "   or (out1, w4, w5);" + '\n' +
            "endmodule // rule30struct" + '\n';

        // String rule30Verilog = "module rule30(output out1,  input in1, in2, in3);" + '\n' +
        //     "   always@(in1,in2,in3)" + '\n' +
        //     "     begin" + '\n' +
        //     "	case({in1,in2,in3})" + '\n' +
        //     "	  3'b000: {out1} = 1'b0;" + '\n' +
        //     "	  3'b001: {out1} = 1'b1;" + '\n' +
        //     "	  3'b010: {out1} = 1'b1;" + '\n' +
        //     "	  3'b011: {out1} = 1'b1;" + '\n' +
        //     "	  3'b100: {out1} = 1'b1;" + '\n' +
        //     "	  3'b101: {out1} = 1'b0;" + '\n' +
        //     "	  3'b110: {out1} = 1'b0;" + '\n' +
        //     "	  3'b111: {out1} = 1'b0;" + '\n' +
        //     "	endcase" + '\n' +
        //     "     end" + '\n' +
        //     "endmodule // rule30";

        NetSynth netsynth = new NetSynth("bar" + "netSynthAPIcall");
        DAGW GW = netsynth.runNetSynthCode(rule30Verilog, new ArrayList<NetSynthSwitch>(Arrays.asList(NetSynthSwitch.originalstructural,NetSynthSwitch.output_or)), new JSONArray());
        netsynth.cleanDirectory();

        org.cellocad.MIT.dnacompiler.LogicCircuit abstract_logic_circuit = new LogicCircuit(GW.Gates, GW.Wires);
        LogicCircuit lc = abstract_logic_circuit;
        // for(int i=0; i<lc.get_Wires().size(); ++i) {
        //     Wire w = lc.get_Wires().get(i);
        //     if(w.to.index == w.from.index) {
        //         lc.get_Wires().remove(i);
        //         i--;
        //     }
        // }

        // for(Gate g: abstract_logic_circuit.get_Gates()) {
        //     if(g.outgoing != null) {
        //         g.outgoing_wire_index = g.outgoing.index;
        //     }
        // }

        // for(Wire w: abstract_logic_circuit.get_Wires()) {
        //     if(w.from != null) {
        //         w.from_index = w.from.index;
        //     }
        //     if(w.to != null) {
        //         w.to_index = w.to.index;
        //     }
        //     if(w.next != null) {
        //         w.next_index = w.next.index;
        //     }
        // }

        // LogicCircuitUtil.renameGatesWires(lc);
        String threadDependentLoggername = String.valueOf(UUID.randomUUID());        

        Logger logger;
        logger = Logger.getLogger(threadDependentLoggername);

        UCFReader ucfReader = new UCFReader();
        UCFAdaptor ucfAdaptor = new UCFAdaptor();
        ucfAdaptor.setThreadDependentLoggername(threadDependentLoggername);
        ucfReader.setThreadDependentLoggername(threadDependentLoggername);

        UCF ucf = ucfReader.readAllCollections("/home/tsj/postdoc/resources/UCF/Eco1C1G1T0.UCF.json");
        
        PartLibrary part_library = ucfAdaptor.createPartLibrary(ucf);
        GateLibrary gate_library = ucfAdaptor.createGateLibrary(ucf, 2, 1, new Args());
        LinkedHashMap<String, Gate> NOR_Gates = gate_library.get_GATES_BY_TYPE().get(GateType.NOR);
        gate_library.get_GATES_BY_TYPE().put(GateType.NOT, NOR_Gates);
        LinkedHashMap<String, ArrayList<Gate>> NOR_Gate_Groups = gate_library.get_GATES_BY_GROUP().get(GateType.NOR);
        gate_library.get_GATES_BY_GROUP().put(GateType.NOT, NOR_Gate_Groups);

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

        ucfAdaptor.setGateParts(ucf, gate_library, part_library);
        ucfAdaptor.setGateToxicity(ucf, gate_library, new Args());
        Toxicity.initializeCircuitToxicity(lc);
        ucfAdaptor.setGateCytometry(ucf, gate_library, new Args());
        ucfAdaptor.setResponseFunctions(ucf, gate_library);
        LogicCircuitUtil.setInputOutputGroups(lc);

        ArrayList<String> eugene_part_rules = ucfAdaptor.getEugenePartRules(ucf);
        Roadblock roadblock = new Roadblock();
        roadblock.setThreadDependentLoggername(threadDependentLoggername);
        roadblock.set_roadblockers(eugene_part_rules, gate_library);
        
        Evaluate.evaluateCircuit(lc, gate_library, _options);
        System.out.println(gate_library.get_GATES_BY_NAME().values());
        System.out.println(lc.get_logic_gates());

        List<Gate> gates = lc.get_logic_gates();
        gates.get(0).name = "H1_HlyIIR";
        gates.get(1).name = "A1_AmtR";
        gates.get(2).name = "E1_BetI";
        gates.get(3).name = "S2_SrpR";
        gates.get(4).name = "P3_PhlF";
        
        Evaluate.evaluateCircuit(lc, gate_library, _options);
        PlasmidUtil.setGateParts(lc, gate_library, part_library);
        PlasmidUtil.findPartComponentsInOutputGates(lc, gate_library, part_library);
        Netlist.setBioNetlist(lc, false);
        


        String name_Eug_circuit_rules =  lc.get_assignment_name() + "_Eugene_circuit_module_rules.eug";
        String name_Eug_circuit_parts =  lc.get_assignment_name() + "_Eugene_circuit_module_part_list.txt";
        String name_Eug_circuit_gates =  lc.get_assignment_name() + "_Eugene_circuit_module_gate_list.txt";

        String name_Eug_output_rules  =  lc.get_assignment_name() + "_Eugene_output_module_rules.eug";
        String name_Eug_output_parts  =  lc.get_assignment_name() + "_Eugene_output_module_part_list.txt";



        logger.info("=========== Setting gate parts according to assigned gate names");

        // PlasmidUtil.setGateParts(lc, gate_library, part_library);

        PlasmidUtil.setTxnUnits(lc, gate_library);

        EugeneAdaptor eugeneAdaptor = new EugeneAdaptor();
        eugeneAdaptor.setThreadDependentLoggername(threadDependentLoggername);


        ArrayList<String> sensor_module_lines = Util.fileLines("/home/tsj/postdoc/src/cidarlab/cello/resources/data/inputs/sensor_module.txt");
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
        System.out.println(lc.get_circuit_module_parts());

        String circuit_eugene_file_string = "";

        ucfAdaptor.setThreadDependentLoggername(threadDependentLoggername);


        // ArrayList<String> eugene_part_rules = ucfAdaptor.getEugenePartRules(ucf);
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


            circuit_eugene_file_string = eugeneAdaptor.generateEugeneFile(logic_and_output_gates, name_Eug_circuit_rules, part_library, new Args());

            logger.info("Eugene: combinatorial design of plasmid layouts...\n");

            eugeneAdaptor.callEugene(name_Eug_circuit_rules, lc.get_circuit_module_parts(), part_library, new Args());

            logger.info("Number of circuit module layouts: " + lc.get_circuit_module_parts().size());

        }

        else if(!circuit_module_location_name.isEmpty() && ! output_module_location_name.isEmpty() && !circuit_module_location_name.equals(output_module_location_name)) {
            logger.info("=========== Eugene: circuit module ===========");

            circuit_eugene_file_string = eugeneAdaptor.generateEugeneFile(lc.get_logic_gates(), name_Eug_circuit_rules, part_library, new Args());

            logger.info("Eugene: combinatorial design of plasmid layouts...\n");

            eugeneAdaptor.callEugene(name_Eug_circuit_rules, lc.get_circuit_module_parts(), part_library, new Args());

            logger.info("Number of circuit module layouts: " + lc.get_circuit_module_parts().size());


            logger.info("=========== Eugene: output module ============");

            //_options.get_eugene_scars = false;
            String output_eugene_file_string = eugeneAdaptor.generateEugeneFile(lc.get_output_gates(), name_Eug_output_rules, part_library, new Args());

            eugeneAdaptor.callEugene(name_Eug_output_rules, lc.get_output_module_parts(), part_library, new Args());

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

            // Util.fileWriter(_options.get_output_directory() + name_Eug_circuit_parts, N + " " + parts_list.toString()+"\n", true);
            // Util.fileWriter(_options.get_output_directory() + name_Eug_circuit_gates, N + " " + gates_list.toString()+"\n", true);
            ++p_counter;
        }


        for(ArrayList<Part> module: lc.get_output_module_parts()) {

            String N = lc.get_assignment_name() + "_P" + String.format("%03d", p_counter);
            ArrayList<String> parts_list = new ArrayList<String>();
            ArrayList<String> gates_list = new ArrayList<String>();
            for (Part p : module) {
                parts_list.add(p.get_direction() + p.get_name());
            }
            // Util.fileWriter(_options.get_output_directory() + name_Eug_output_parts, N + " " + parts_list.toString()+"\n", true);
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

        ArrayList<String> all_plasmid_strings = new ArrayList<>();

        logger.info("\n=========== SBOL for circuit plasmids ========");

        //currently not writing an SBOL document for the output plasmid if the output module is on a different plasmid than the circuit.
        for(int i=0; i<lc.get_circuit_plasmid_parts().size(); ++i) {
            ArrayList<Part> plasmid = lc.get_circuit_plasmid_parts().get(i);

            SBOLCircuitWriter sbol_circuit_writer = new SBOLCircuitWriter();

            sbol_circuit_writer.setCircuitName(lc.get_assignment_name());
            String sbol_filename = lc.get_assignment_name() + "_sbol_circuit" + "_P" + String.format("%03d", i) + ".sbol";
            String sbol_plasmid_name = lc.get_assignment_name() + "_P" + String.format("%03d", i);

            String sbol_document = sbol_circuit_writer.writeSBOLCircuit(sbol_filename, lc, plasmid, sbol_plasmid_name, new Args());
            System.out.println(sbol_filename);
        }

        PlasmidUtil.resetParentGates(lc);

    }
}

