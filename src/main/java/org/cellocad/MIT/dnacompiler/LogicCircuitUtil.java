package org.cellocad.MIT.dnacompiler;
/**
 * Created by Bryan Der on 3/26/14.
 */

import org.cellocad.MIT.dnacompiler.Gate.GateType;

import java.util.*;

/**
 *
 * Miscellaneous functions that were in LogicCircuit.java being moved here.
 *
 */
public class LogicCircuitUtil{


    /**
     *
     * Returns partially-abstract logic circuit(s) where the input gates and output gate(s) are assigned
     *
     * Default: input1 = pTac, input2 = pTet, input3 = pBAD
     *      return a single LogicCircuit
     *
     * if Args.permute_inputs:
     *      permute all possible assignments of inducible promoters to input gates
     *      and return ArrayList of LogicCircuits
     *
     */
    public static ArrayList<LogicCircuit> getInputAssignments(LogicCircuit lc, GateLibrary gate_library, boolean permute_inputs) {


        ArrayList<LogicCircuit> partially_abstract_lcs = new ArrayList<LogicCircuit>();

        ArrayList<int[]> indexes_set = new ArrayList<int[]>();

        //permute input assignments, order doesn't matter
        if(permute_inputs) {
            int n_input_gates = lc.get_input_gates().size();
            int n_bioinputs =   gate_library.get_INPUT_NAMES().length;

            int[] n = new int[n_input_gates];
            int[] Nr = new int[n_bioinputs];
            for (int i = 0; i<n_bioinputs; ++i){
                Nr[i] = n_bioinputs-1;
            }

            Permute.getIndexProduct(indexes_set, n, Nr, 0);  //indexes_set gets populated here
        }
        //order matters, 1 fixed assignment (this is the default behavior)
        else {
            int[] fixed_inputs = new int[lc.get_input_gates().size()];
            for(int i=0; i<lc.get_input_gates().size(); ++i) {
                fixed_inputs[i] = i; //  [ 3, 2, 1, 0 ] for a 4-input circuit
            }
            indexes_set.add(fixed_inputs); //size == 1
        }

        for(int a[]: indexes_set) {

            LogicCircuit input_lc = new LogicCircuit(lc);

            for(int i=0; i<input_lc.get_output_gates().size(); ++i) {
                input_lc.get_output_gates().get(i).name = gate_library.get_OUTPUT_NAMES()[i];
            }
            for(int i=0; i<input_lc.get_input_gates().size(); ++i) {
                input_lc.get_input_gates().get(i).name = gate_library.get_INPUT_NAMES()[a[i]]; //change the name, which will allow RPU values to change when calling setInputRPU
            }
            setInputRPU(input_lc, gate_library);

            partially_abstract_lcs.add(input_lc); //unassigned_lcs from DNAC
        }
        
        
     // TODO hard-coded for Jonghyeon
        boolean p_BAD_as_fisrt_input = false;
        
        if(p_BAD_as_fisrt_input) {
        	
        	ArrayList<LogicCircuit> specified_lc = new ArrayList<LogicCircuit>();
        	
        	for(LogicCircuit specified_input_lc: partially_abstract_lcs) {
        		
        		if (specified_input_lc.get_input_gates().get(0).name.equals("pBAD")) {
        			
        				specified_lc.add(specified_input_lc);
        		}
        	}
        	return specified_lc;
        }
        //
        
        else {
        	
            return partially_abstract_lcs; //size == 1 if Args.permute_inputs == false
        	
        }
        
    }


    /**
     *
     * For a 3-input circuit, the input gates have these logics:
     * gate_0 = 00001111
     * gate_1 = 00110011
     * gate_2 = 01010101
     *
     */
    public static void setInputLogics(LogicCircuit lc){
        int n_inputs = lc.get_input_gates().size();

        int[] n  = new int[n_inputs];
        int[] Nr = new int[n_inputs];
        for (int i = 0; i<n_inputs; ++i){
            Nr[i] = 1;
        }
        ArrayList<int[]> input_logics_set = new ArrayList<int[]>();
        Permute.getLogicPermutation(input_logics_set, n, Nr, 0);
        //8 rows, int[] size == 3
        // 000
        // 001
        // 010
        // 011
        // 100
        // 101
        // 110
        // 111

        for(int i=0; i<lc.get_input_gates().size(); ++i) {
            Gate gate_i = lc.get_input_gates().get(i);
            ArrayList<Integer> logics = new ArrayList<Integer>();

            //gate_0 = 00001111
            //gate_1 = 00110011
            //gate_2 = 01010101
            for(int[] input_logics: input_logics_set){    // rows in truth table
                logics.add(new Integer(input_logics[i])); // input_logics.length == n_inputs
                gate_i.set_unvisited( false );
            }


            /*for(int row=0; row<logics.size(); ++row) {
                if(Args.dontcare_rows.contains(row)) {
                    logics.set(row, 2);
                }
            }*/

            gate_i.set_logics(logics);
        }
    }

    /**
     *
     * RPU = relative expression  units
     * Once names are assigned to input gates (getInputAssignments), add high RPU if logic is 1, add low RPU if logic is 0
     *
     * For normal scoring or histogram scoring
     *
     */
    public static void setInputRPU(LogicCircuit lc, GateLibrary gate_library) {

        for(Gate input_gate: lc.get_input_gates()) {

            if(input_gate.get_logics() == null || input_gate.get_logics().size() == 0) {
                throw new IllegalStateException("trying to initialize rpus before initializing logics.  exiting.");
            }
            ArrayList<Double> rpus = new ArrayList<Double>();
            ArrayList<double[]> convrpus = new ArrayList<double[]>();
            for(Integer i: input_gate.get_logics()) {
                if(i == 0) {
                    rpus.add(gate_library.get_INPUTS_OFF().get(input_gate.name)); //low
                    convrpus.add(gate_library.get_INPUTS_HIST_OFF().get(input_gate.name)); //low
                }
                else if(i == 1) {
                    rpus.add(gate_library.get_INPUTS_ON().get(input_gate.name)); //high
                    convrpus.add(gate_library.get_INPUTS_HIST_ON().get(input_gate.name)); //high
                }
                else if(i == 2) {
                    rpus.add(0.0);
                    convrpus.add(new double[input_gate.get_histogram_bins().get_NBINS()]);
                }
            }
            input_gate.set_unvisited( false );
            input_gate.set_unvisited( false );

            input_gate.set_outrpus(rpus);
            input_gate.set_histogram_rpus(convrpus);
        }

    }


    /**
     *
     * Alec's priorities.  The only firm requirement for promoter order is no roadblocking.
     * However, roadblocking is not actually boolean in a cell, Alec established these priorities
     * based measured roadblocking values.
     *
     * Lower number means further upstream, higher number means further downstream
     *
     */
    /*public static HashMap<String, Integer> getPromoterPriorities() {
        HashMap<String, Integer> priorities = new HashMap<String, Integer>();

        priorities.put("pTac",    -6);
        priorities.put("pBAD",    -5);
        priorities.put("SrpR",    -4);
        priorities.put("PhlF",    -3);
        priorities.put("BM3R1",   -2);
        priorities.put("QacR",    -1);
        priorities.put("pLuxStar", 1);
        priorities.put("pTet",     2);
        priorities.put("AmtR",     3);
        priorities.put("BetI",     4);
        priorities.put("IcaRA",    5);
        priorities.put("HlyIIR",   6);
        priorities.put("LitR",     7);
        priorities.put("AmeR",     8);
        priorities.put("PsrA",     9);
        priorities.put("LmrA",    10);
        priorities.put("pSal",    11);

        return priorities;
    }*/

    /**
     *
     * Alec's cloning method puts repressors in slots for legacy and practical reasons.
     * For example, PhlF is almost always used, so it is given a slot connected to the backbone.
     * The infrequently used repressors fall somewhere in the middle.
     *
     * This is the order of those slots.
     *
     */
    /*public static HashMap<String, Integer> getCloningSlotPriorities() {
        HashMap<String, Integer> priorities = new HashMap<String, Integer>();

        priorities.put("PhlF",    1);
        priorities.put("SrpR",    2);
        priorities.put("BM3R1",   3);
        priorities.put("BetI",    4);
        priorities.put("AmeR",    5);
        priorities.put("LitR",    6);
        priorities.put("IcaRA",   7);
        priorities.put("QacR",    8);
        priorities.put("PsrA",    9);
        priorities.put("LmrA",   10);
        priorities.put("HlyIIR", 11);
        priorities.put("AmtR",   12);



        return priorities;
    }*/

    /**
     *
     * An assembly scar separates each txn unit.  Scars are one-time use only.
     *
     * Some scars / scar combinations are better than others.  These are the scar priorities that Alec uses.
     *
     */
    /*public static HashMap<String, Integer> getScarPriorities() {
        HashMap<String, Integer> priorities = new HashMap<String, Integer>();

        priorities.put("E-scar",    0); //first module start

        priorities.put("D-scar",    1);
        priorities.put("B-scar",    2);
        priorities.put("A-scar",    3);
        priorities.put("F-scar",    4);
        priorities.put("G-scar",    5);
        priorities.put("X-scar",    6);
        priorities.put("V-scar",    7);
        priorities.put("U-scar",    8);
        priorities.put("R-scar",    9);

        //not in use by Alec
        priorities.put("Q-scar",   11);
        priorities.put("P-scar",   12);
        priorities.put("O-scar",   13);
        priorities.put("N-scar",   14);
        priorities.put("M-scar",   15);
        priorities.put("L-scar",   16);
        priorities.put("Y-scar",   17);

        priorities.put("S-scar",  998); //ribo prefix, used internally in gates
        priorities.put("C-scar",  999); //last module end

        return priorities;
    }*/


    public static void sortGatesByStage(LogicCircuit lc){

        for (Gate g : lc.get_logic_gates()) {

            GateUtil.calculateDistanceToFarthestInput(g);

        }

        LogicCircuitUtil.sortLogicGatesByDist2In(lc);

        Collections.reverse(lc.get_logic_gates());

    }



    public static void sortLogicGatesByDist2In(LogicCircuit lc) {
        Collections.sort(lc.get_logic_gates(),
                new Comparator<Gate>() {
                    public int compare(Gate g1, Gate g2) {
                        int result = 0;
                        if ( (g1.get_distance_to_input() - g2.get_distance_to_input()) > 1e-10 ){
                            result = -1;
                        } else if ( (g1.get_distance_to_input() - g2.get_distance_to_input()) < -1.0e-10){
                            result = 1;
                        }
                        else if ( (g1.get_distance_to_input() - g2.get_distance_to_input() == 0) ) {
                            if(g1.index < g2.index) {
                                return -1;
                            }
                            else {
                                return 1;
                            }
                        }
                        return result;
                    }
                }
        );
    }


    public static void sortGatesByIndex(LogicCircuit lc) {
        Collections.sort(lc.get_Gates(),
                new Comparator<Gate>() {
                    public int compare(Gate g1, Gate g2) {
                        int result = 0;
                        if ( g1.index < g2.index ){
                            result = -1;
                        }
                        else {
                            result = 1;
                        }

                        return result;
                    }
                }
        );
    }


    public static boolean libraryGatesCoverCircuitGates(LogicCircuit lc, GateLibrary gate_library) {

        HashSet<String> used_groups = new HashSet<String>();

        HashMap<GateType, Integer> assigned_groups = new HashMap<GateType, Integer>();

        for (Gate.GateType gtype : lc.get_logic_gate_types().keySet()) {

            if (!assigned_groups.containsKey(gtype)) {
                assigned_groups.put(gtype, 0);
            }

            if(!gate_library.get_GATES_BY_GROUP().containsKey(gtype)) {
                return false;
            }

            for (String group_name : gate_library.get_GATES_BY_GROUP().get(gtype).keySet()) {

                if (assigned_groups.get(gtype) < lc.get_gate_types().get(gtype).size()) {

                    if (!used_groups.contains(group_name)) {

                        used_groups.add(group_name);


                        Integer n = assigned_groups.get(gtype);
                        assigned_groups.put(gtype, (n + 1));

                    }

                }

            }

        }

        for(Gate.GateType gtype: assigned_groups.keySet()) {

            if(assigned_groups.get(gtype) < lc.get_gate_types().get(gtype).size()) {
                return false;
            }

        }

        return true;

    }


    public static void setInputOutputGroups(LogicCircuit lc) {

        for(Gate g: lc.get_input_gates()) {
            g.group = g.name;
            g.regulator = g.name;
        }

        for(Gate g: lc.get_output_gates()) {
            g.group = g.name;
            g.regulator = g.name;
        }

    }


    public static ArrayList<LogicCircuit> uniqueRepressorAssignments(ArrayList<LogicCircuit> assigned_lcs) {

        HashMap<String, LogicCircuit> unique_repressor_assignments = new HashMap<>();

        for(int i=0; i<assigned_lcs.size(); ++i) {

            LogicCircuit lc = assigned_lcs.get(i);

            ArrayList<String> tus = new ArrayList<String>();

            String asn = "";

            for(Gate g: lc.get_logic_gates()) {
                asn += g.regulator;

                ArrayList<String> promoter_names = new ArrayList<>();
                String cds = "";

                for(Part p: g.get_txn_units().get(0)) {
                    if(p.get_type().equalsIgnoreCase("promoter")) {
                        promoter_names.add( p.get_name() );
                    }
                    if(p.get_type().equalsIgnoreCase("cds")) {
                        cds = p.get_name();
                    }
                }

                Collections.sort(promoter_names);

                String tu = "";
                for(String s: promoter_names) {
                    tu += s;
                }
                tu += cds;


                tus.add(tu);
            }

            Collections.sort(tus);

            asn = "";
            for(String s: tus) {
                asn += s + "_";
            }

            if(!unique_repressor_assignments.containsKey(asn)) {
                unique_repressor_assignments.put(asn, lc);
            }

        }

        return new ArrayList<LogicCircuit>(unique_repressor_assignments.values());
    }


    public static void renameGatesWires(LogicCircuit lc) {
        for(Gate g: lc.get_Gates()) {
            if(g.type != GateType.INPUT && g.type != GateType.OUTPUT && g.type != GateType.OUTPUT_OR) {
                g.name = "g"+g.rIndex;
            }
        }

        for(Wire w: lc.get_Wires()) {
            if(w.to != null) {
                if(w.to.type == GateType.INPUT) {
                    w.name = w.to.name;
                }
                else {
                    w.name = "w" + w.to.rIndex;
                }
            }
        }
    }


    public static boolean dataFoundForAllTandemPromoters(GateLibrary gate_library, LogicCircuit lc) {

        ArrayList<Gate> logic_and_output_gates = new ArrayList<>();
        logic_and_output_gates.addAll(lc.get_logic_gates());
        logic_and_output_gates.addAll(lc.get_output_gates());

        for(Gate g: logic_and_output_gates) {

            String var = "x"; //hard-coded

            if(g.get_variable_names().size() == 1) {
                var = g.get_variable_names().get(0);
            }

            if (g.get_variable_wires().get(var).size() == 2) { //hard-coded

                ArrayList<String> fanin_gate_names = new ArrayList<>();

                Gate child1 = g.getChildren().get(0);
                Gate child2 = g.getChildren().get(1);

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

                boolean tp_exists = false;
                if (gate_library.get_TANDEM_PROMOTERS().containsKey(tandem_promoter_name_1)) {
                    tp_exists = true;
                } else if (gate_library.get_TANDEM_PROMOTERS().containsKey(tandem_promoter_name_2)) {
                    tp_exists = true;
                }

                if(!tp_exists) {
                    return false;
                }
            }


        }

        return true;
    }

}
