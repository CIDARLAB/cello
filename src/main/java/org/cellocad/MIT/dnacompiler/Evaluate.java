package org.cellocad.MIT.dnacompiler;
/**
 * Created by Bryan Der on 3/26/14.
 */

import org.cellocad.MIT.dnacompiler.Gate.GateType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

/***********************************************************************

 Synopsis    [ Compute out_rpu from in_rpu given the 4 transfer function parameters. ]

 ***********************************************************************/

public class Evaluate {


    /**
     * The function evaluateCircuit calls other evaluate functions based on the circuit_score chose by user.
     *
     * -circuit_score onoff_ratio
     *      score = log(ON/OFF), where ON is the lowest ON in the truthtable, and OFF is the highest off in the truthtable
     *
     * -circuit_score noise_margin
     *      noise margin is computed from input RPU distance from low margin (if low) or high margin (if high)
     *      score = average noise margin of all logic gates
     *      used for NOR/NOT only, and cannot be used if there are input gates and no logic gates
     *
     * -circuit_score histogram
     *      score = 1 - overlap penalty, where overlap is from the worst pair among ONs and OFFs in the truthtable
     *
     *
     * Circuit is evaluated by evaluating each gate, so the same function calls appear
     *      but with a Gate parameter instead of LogicCircuit parameter
     *
     */
    public static void evaluateCircuit(LogicCircuit lc, GateLibrary gate_library, Args options){

        refreshGateAttributes(lc, gate_library);

        //if sequential
        if(options.get_circuit_type() == DNACompiler.CircuitType.sequential) {

            SequentialHelper.setInitialRPUs(lc, gate_library);

            HashMap<String, ArrayList<ArrayList<Double>>> track_rpus = new HashMap<>();

            for (Gate g : lc.get_Gates()) {
                track_rpus.put(g.name, new ArrayList<ArrayList<Double>>());
                ArrayList<Double> copy_rpus = new ArrayList<Double>(g.get_outrpus());
                track_rpus.get(g.name).add(copy_rpus);
                track_rpus.get(g.name).add(copy_rpus);
            }

            boolean converges = SequentialHelper.convergeRPUs(lc, gate_library, options, track_rpus);

            if(!converges) {

                lc.get_scores().set_onoff_ratio(0.0);
                lc.get_scores().set_noise_margin_contract(false);

                return;
            }
        }


        //if combinational
        else if(options.get_circuit_type() == DNACompiler.CircuitType.combinational) {
            simulateRPU(lc, gate_library, options);
        }



        evaluateCircuitONOFFRatio(lc);

        if(options.is_noise_margin()) {
            evaluateCircuitNoiseMargin(lc, options);
        }

        if(options.is_snr()) {
            evaluateCircuitSNR(lc, options);
        }
    }


    /**
     *
     * To evaluate the ON/OFF ratio for a circuit, calculate the ON/OFF ratio for each output gate,
     * and choose the worst score among the outputs (#outputs >= 1).
     *
     */
    public static void evaluateCircuitONOFFRatio(LogicCircuit lc){

        double worst_out = Double.MAX_VALUE;

        for(int out=0; out<lc.get_output_gates().size(); ++out) {// if multiple outputs, average _scores
            Gate output = lc.get_output_gates().get(out);
            evaluateGateONOFFRatio(output);

            //if multiple outputs, circuit score = score of worst output
            if(output.get_scores().get_onoff_ratio() < worst_out) {
                worst_out = output.get_scores().get_onoff_ratio();
            }
        }

        lc.get_scores().set_onoff_ratio(worst_out);
    }

    /**
     *
     * noise_margin
     *
     */
    public static void evaluateCircuitNoiseMargin(LogicCircuit lc, Args options) {

        //initialize to true. circuit will fail if any gate fails.
        lc.get_scores().set_noise_margin_contract(true);

        if(options.is_noise_margin() == false) {
            return;
        }

        double sum_noise_margin = 0.0;
        double min_noise_margin = 999.0;

        for (Gate g : lc.get_logic_gates()) {

            //'options' are passed in to read the _noise_margin boolean
            evaluateGateNoiseMargin(g, options);

            if(g.get_scores().get_noise_margin() < min_noise_margin) {
                min_noise_margin = g.get_scores().get_noise_margin();
            }


            //g.get_noise_margin() returns the min of the NML and NMH values (noise margin low, noise margin high)

            sum_noise_margin += g.get_scores().get_noise_margin();
            //sum_noise_margin += g.get_scores().get_noise_margin() * g.get_distance_to_input();

            /**
             * if one gate fails, the whole circuit fails threshold analysis
             */
            if (g.get_scores().is_noise_margin_contract() == false) {
                lc.get_scores().set_noise_margin_contract(false);
                //lc.get_scores().set_noise_margin(0.0);
                lc.get_scores().set_noise_margin(min_noise_margin);
                break;
            }
        }

        //noise margin value is not being used.
        //noise margin is only used as a pass/fail filter, not a score term.
        if (lc.get_scores().is_noise_margin_contract() == true) {
            //lc.get_scores().set_noise_margin(sum_noise_margin / lc.get_logic_gates().size());
            lc.get_scores().set_noise_margin(sum_noise_margin);
        }
    }


    /**
     *
     * histogram
     *
     * Histogram overlap score worst-case = 0.0 and best-case = 1.0
     *
     */

    public static void evaluateCircuitHistogramOverlap(LogicCircuit lc, GateLibrary gate_library, Args options){ // output gate _score (average)

        refreshGateAttributes(lc, gate_library);

        //if sequential
        if(options.get_circuit_type() == DNACompiler.CircuitType.sequential) {

            //set initial
            SequentialHelper.setInitialHistogramRPUs(lc, gate_library);


            //track
            HashMap<String, ArrayList<ArrayList<double[]>>> track_rpus = new HashMap<>();
            for (Gate g : lc.get_Gates()) {
                track_rpus.put(g.name, new ArrayList<ArrayList<double[]>>());
                ArrayList<double[]> copy_hist_rpus = new ArrayList<double[]>(g.get_histogram_rpus());
                track_rpus.get(g.name).add(copy_hist_rpus);
                track_rpus.get(g.name).add(copy_hist_rpus); //looks for i-1
            }

            //converge
            SequentialHelper.convergeHistogramRPUs(lc, gate_library, options, track_rpus);
        }


        //if combinational
        else if(options.get_circuit_type() == DNACompiler.CircuitType.combinational) {
            Evaluate.simulateHistogramRPU(lc, gate_library, options);
        }



        double worst_out = Double.MAX_VALUE;

        for(int out=0; out<lc.get_output_gates().size(); ++out) {
            Gate output = lc.get_output_gates().get(out);
            evaluateGateHistogramOverlap(output);

            if(output.get_scores().get_conv_overlap() < worst_out) {
                worst_out = output.get_scores().get_conv_overlap();
            }
        }

        lc.get_scores().set_conv_overlap(worst_out);

    }

    public static void evaluateCircuitSNR(LogicCircuit lc, Args options){


        /*for(Gate g: lc.get_logic_gates()) {
            evaluateGateSNR(g, options);
        }
        for(Gate g: lc.get_output_gates()) {
            evaluateGateSNR(g, options);
        }*/


        ArrayList<Double> input_ons = new ArrayList<>();
        ArrayList<Double> input_offs = new ArrayList<>();

        for(Gate input: lc.get_input_gates()) {
            for (int i = 0; i < input.get_logics().size(); ++i) {
                if (input.get_logics().get(i) == 1) {
                    input_ons.add(input.get_outrpus().get(i));
                } else if (input.get_logics().get(i) == 0) {
                    input_offs.add(input.get_outrpus().get(i));
                }
            }
        }

        Double input_on_min  = Collections.min(input_ons);
        Double input_off_max = Collections.max(input_offs);


        ArrayList<Double> output_ons = new ArrayList<>();
        ArrayList<Double> output_offs = new ArrayList<>();

        for(Gate output: lc.get_output_gates()) {
            for (int i = 0; i < output.get_logics().size(); ++i) {
                if (output.get_logics().get(i) == 1) {
                    output_ons.add(output.get_outrpus().get(i));
                } else if (output.get_logics().get(i) == 0) {
                    output_offs.add(output.get_outrpus().get(i));
                }
            }
        }

        Double output_on_min  = Collections.min(output_ons);
        Double output_off_max = Collections.max(output_offs);

        Double out_snr = 20 * Math.log10((Math.log10(output_on_min / output_off_max)) / (2 * Math.log10(3.2)));

        Double in_snr  = 20 * Math.log10((Math.log10(input_on_min / input_off_max)) / (2 * Math.log10(3.2)));

        Double dsnr = out_snr - in_snr;

        lc.get_scores().set_snr(out_snr);
        lc.get_scores().set_dsnr(dsnr);
    }



    /////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////             Evaluate Gate                  //////////////////////////
    /////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////////////


    public static void evaluateGate(Gate g, Args options) {

        //refreshGateAttributes(g);
        //simulateRPU(g);

        evaluateGateONOFFRatio(g);

        if(options.is_noise_margin()) {
            evaluateGateNoiseMargin(g, options);
        }

        if(options.is_snr()) {
            evaluateGateSNR(g, options);
        }
    }


    /**
     *
     * find ON_lowest and OFF_highest as worst-case scenario
     *
     */
    public static void evaluateGateONOFFRatio(Gate g){

        double lowest_on_rpu   = Double.MAX_VALUE;
        double highest_off_rpu = Double.MIN_VALUE;

        for(int i=0; i<g.get_logics().size(); ++i) { //for each row in the truth table...
            //if (!Args.dontcare_rows.contains(i)) { //don't score dontcare rows
            Double rpu = g.get_outrpus().get(i);

            if (g.get_logics().get(i) == 1) {
                if (lowest_on_rpu > rpu) {
                    lowest_on_rpu = rpu;
                }
            } else if (g.get_logics().get(i) == 0) {
                if (highest_off_rpu < rpu) {
                    highest_off_rpu = rpu;
                }
            }
            //}
        }
        //g.get_scores().set_onoff_ratio( Math.log10(lowest_on_rpu/highest_off_rpu) );
        g.get_scores().set_onoff_ratio(lowest_on_rpu/highest_off_rpu );
    }

    /**
     * score = 1 - penalty of histogram overlap
     * score all ON:OFF pairs and report the worst score
     *
     * overlap penalty:
     *      for each bin, compute geometric mean of two ON:OFF histogram values, add add bin penalty to total penalty
     *      alternative is to penalize the min of the two ON:OFF histogram values
     *
     */
    public static void evaluateGateHistogramOverlap(Gate g){

        ArrayList<Double> scores_conv_overlap = new ArrayList<Double>();
        ArrayList<Integer> ons = new ArrayList<Integer>();
        ArrayList<Integer> offs = new ArrayList<Integer>();

        //get ons and offs
        for(int i=0; i<g.get_logics().size(); ++i){
            if(g.get_logics().get(i) == 1){
                ons.add(i);
            }
            else if(g.get_logics().get(i) == 0){
                offs.add(i);
            }
        }

        //compute scores of all on-off pairs
        for(int on=0; on<ons.size(); ++on) {
            for(int off=0; off<offs.size(); ++off) {

                //if(!Args.dontcare_rows.contains(on) && !Args.dontcare_rows.contains(off)) {

                double median_on = Math.pow(Math.E, HistogramUtil.median(g.get_histogram_rpus().get(ons.get(on)), g.get_histogram_bins() ));
                double median_off = Math.pow(Math.E, HistogramUtil.median(g.get_histogram_rpus().get(offs.get(off)), g.get_histogram_bins() ));
                double score = 1 - median_off / median_on;
                double overlap_penalty = 0.0;

                //if ON histogram is lower than OFF histogram, broken circuit
                if (score < 0) {
                    scores_conv_overlap.add(0.0);
                    continue;
                } else {
                    double[] on_norm = HistogramUtil.normalize(g.get_histogram_rpus().get(ons.get(on)));
                    double[] off_norm = HistogramUtil.normalize(g.get_histogram_rpus().get(offs.get(off)));

                    //penalty is sum of geometric means for each bin
                    //total counts have been normalized to 1
                    for (int bin = 0; bin < g.get_histogram_bins().get_NBINS(); ++bin) {
                        overlap_penalty += Math.sqrt(on_norm[bin] * off_norm[bin]); //geometric mean
                        //overlap_penalty += Math.min( on_norm[bin] , off_norm[bin]); //min of the two bin counts
                    }
                }
                score = 1 - overlap_penalty;

                scores_conv_overlap.add(score);

                //}
            }
        }

        Collections.sort(scores_conv_overlap);

        g.get_scores().set_conv_overlap(scores_conv_overlap.get(0)); //worst
    }


    /**
     * gate score = smallest noise margin (distance in log(RPU) of input RPU to margin RPU)
     */
    public static void evaluateGateNoiseMargin(Gate g, Args options){

        if(options.is_noise_margin() == false) {
            g.get_scores().set_noise_margin_contract(true);
            return;
        }

        if (g.type == GateType.INPUT || g.type == GateType.OUTPUT || g.type == GateType.OUTPUT_OR) {
            return;
        }

        //"x" to value
        HashMap<String, Double> lowest_on_rpu = GateUtil.getIncomingONlow(g);
        HashMap<String, Double> highest_off_rpu = GateUtil.getIncomingOFFhigh(g);

        ArrayList<Double> all_margins = new ArrayList<Double>();

        for (String var : highest_off_rpu.keySet()) {

            if (g.get_variable_thresholds().get(var) != null) {

                //IL is the input-low threshold
                Double IL = g.get_variable_thresholds().get(var)[0];

                //actual RPU
                Double log_input_rpu = Math.log10(highest_off_rpu.get(var));

                //NML is the margin/width between the actual RPU and the threshold RPU
                Double NML = Math.log10(IL) - log_input_rpu;

                all_margins.add(NML);
            }
        }

        for (String var : lowest_on_rpu.keySet()) {

            if (g.get_variable_thresholds().get(var) != null) {
                Double IH = g.get_variable_thresholds().get(var)[1];
                Double NMH = Math.log10(lowest_on_rpu.get(var)) - Math.log10(IH);
                all_margins.add(NMH);
            }
        }

        if (all_margins.isEmpty()) {
            g.get_scores().set_noise_margin(0.0);
            g.get_scores().set_noise_margin_contract(true);
        } else {
            Collections.sort(all_margins);

            g.get_scores().set_noise_margin(all_margins.get(0));

            if (all_margins.get(0) < 0) {
                g.get_scores().set_noise_margin_contract(false);
            } else {
                g.get_scores().set_noise_margin_contract(true);
            }
        }
    }

    public static void evaluateGateSNR(Gate g, Args options){

        if(g.type == GateType.INPUT) {
            return;
        }

        ArrayList<Double> ons = new ArrayList<>();
        ArrayList<Double> offs = new ArrayList<>();

        //get ons and offs
        for(int i=0; i<g.get_logics().size(); ++i){
            if(g.get_logics().get(i) == 1){
                ons.add(g.get_outrpus().get(i));
            }
            else if(g.get_logics().get(i) == 0){
                offs.add(g.get_outrpus().get(i));
            }
        }

        ArrayList<Double> child_ons = new ArrayList<>();
        ArrayList<Double> child_offs = new ArrayList<>();

        for(Gate child: g.getChildren()) {
            for (int i = 0; i < child.get_logics().size(); ++i) {
                if (child.get_logics().get(i) == 1) {
                    child_ons.add(child.get_outrpus().get(i));
                } else if (child.get_logics().get(i) == 0) {
                    child_offs.add(child.get_outrpus().get(i));
                }
            }
        }

        Double on_min  = Collections.min(ons);
        Double off_max = Collections.max(offs);

        Double child_on_min  = Collections.min(child_ons);
        Double child_off_max = Collections.max(child_offs);


        Double out_snr = 20 * Math.log10((Math.log10(on_min / off_max)) / (2 * Math.log10(3.2)));

        Double in_snr  = 20 * Math.log10((Math.log10(child_on_min / child_off_max)) / (2 * Math.log10(3.2)));

        Double dsnr = out_snr - in_snr;

        g.get_scores().set_snr(out_snr);
        g.get_scores().set_dsnr(dsnr);
    }


    /////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////             Simulate Circuit               //////////////////////////
    /////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////////////

    /**
     *
     * calls simulateLogic() in Gate.java
     *
     */
    public static void simulateLogic(LogicCircuit lc){

        for(int i=lc.get_logic_gates().size()-1; i>=0; --i) {
            lc.get_logic_gates().get(i).set_unvisited(true);
            simulateLogic(lc.get_logic_gates().get(i));
        }
        for(int i=0; i<lc.get_output_gates().size(); ++i) {
            lc.get_output_gates().get(i).set_unvisited(true);
            simulateLogic(lc.get_output_gates().get(i));
        }
    }


    /**
     *
     * for all logic and output gates:
     *     set flag simulate_rpu = true (indicates that RPU needs to be simulated)
     *     then call simulateRPU() in Gate.java
     *
     * Hill function evaluation
     *
     */
    public static void simulateRPU(LogicCircuit lc, GateLibrary gate_library, Args options){
        ArrayList<Gate> logic_and_output_gates = new ArrayList<Gate>();
        logic_and_output_gates.addAll(lc.get_logic_gates());
        logic_and_output_gates.addAll(lc.get_output_gates());


        //input gate RPU already set
        //make sure that all gates are re-simulated by setting simulate_rpu to TRUE
        for(Gate gate: logic_and_output_gates){
            gate.set_unvisited(true);
        }
        for(Gate gate: logic_and_output_gates){
            Evaluate.simulateRPU(gate, gate_library, options);
        }
    }

    /**
     *
     * Same as above, but converts input distr to output distr using a matrix transfer function
     *
     */
    public static void simulateHistogramRPU(LogicCircuit lc, GateLibrary gate_library, Args options){

        ArrayList<Gate> logic_and_output_gates = new ArrayList<Gate>();
        logic_and_output_gates.addAll(lc.get_logic_gates());
        logic_and_output_gates.addAll(lc.get_output_gates());

        //input gate RPU already set
        for(Gate gate: logic_and_output_gates){
            gate.set_unvisited(true);
        }
        for(Gate gate: logic_and_output_gates){
            simulateHistogramRPU(gate, gate_library, options);
        }
    }



    /**
     *
     * Set the transfer function parameters or matrix transfer functions based on the gate name
     *
     */
    public static void refreshGateAttributes(LogicCircuit lc, GateLibrary gate_library) {
        refreshGateAttributes(lc.get_logic_gates(), gate_library);
        refreshGateAttributes(lc.get_output_gates(), gate_library);
    }
    public static void refreshGateAttributes(ArrayList<Gate> gates, GateLibrary gate_library) {
        for (Gate g : gates) {
            refreshGateAttributes(g, gate_library);
        }
    }
    public static void refreshGateAttributes(Gate g, GateLibrary gate_library) {

        if (g.type == GateType.OUTPUT || g.type == GateType.OUTPUT_OR) {

            g.set_params(null);

            ArrayList<String> variable_names = new ArrayList<>();
            variable_names.add("x");
            g.set_variable_names(variable_names);

            HashMap<String, Double[]> variable_thresholds = new HashMap<>();
            variable_thresholds.put("x", new Double[]{null,null});
            g.set_variable_thresholds(variable_thresholds);

            String equation = String.valueOf(g.get_unit_conversion()) + "*x";
            g.set_equation(equation);
        }

        else if (gate_library.get_GATES_BY_NAME().containsKey(g.name)) {
            g.set_params(gate_library.get_GATES_BY_NAME().get(g.name).get_params());

            if(g.get_variable_names().isEmpty()) {
                g.set_variable_names(gate_library.get_GATES_BY_NAME().get(g.name).get_variable_names());
            }

            g.set_variable_thresholds(gate_library.get_GATES_BY_NAME().get(g.name).get_variable_thresholds());
            g.set_equation(gate_library.get_GATES_BY_NAME().get(g.name).get_equation());

            g.system    = gate_library.get_GATES_BY_NAME().get(g.name).system;
            g.colorHex  = gate_library.get_GATES_BY_NAME().get(g.name).colorHex;
            g.group     = gate_library.get_GATES_BY_NAME().get(g.name).group;
            g.regulator = gate_library.get_GATES_BY_NAME().get(g.name).regulator;
            g.inducer   = gate_library.get_GATES_BY_NAME().get(g.name).inducer;

            //if(Args.histogram) {
            if(gate_library.get_GATES_BY_NAME().get(g.name).get_xfer_hist() != null) {
                g.set_xfer_hist(gate_library.get_GATES_BY_NAME().get(g.name).get_xfer_hist());
            }
        }

        else {
            g.system    = "null";
            g.colorHex  = "null";
            g.group     = "null";
            g.regulator = "null";
            g.inducer   = "";
        }
    }


    /***********************************************************************

     Synopsis    [  ]

     keep tracing back to child until find one with logics defined
     assume it has either 1 or 2 children
     this method is recursive
     Note: Recursion is not necessary and does not occur if we sort the gates by distance to input, then simulate logic in that order.

     logic is computed according to Gate type and input logics

     ***********************************************************************/
    public static void simulateLogic(Gate g){

        if (g.is_unvisited()) {

            ArrayList<Gate> children = g.getChildren();

            for(Gate child: children) {
                if(child.is_unvisited()){
                    simulateLogic(child); //recursive
                }
            }

            //if all children have been visited, visit the current gate 'g'
            g.set_unvisited( false );
            g.set_logics( GateUtil.computeGateLogics(g) );
        }
    }

    /***********************************************************************

     Synopsis    [  ]

     keep tracing back to child until find one with logics defined
     assume it has either 1 or 2 children
     this method is recursive
     Note: Recursion is not necessary and does not occur if we sort the gates by distance to input, then simulate logic in that order.

     For NOT gate, compute output RPU based on child1 RPU and xfer function
     For NOR gate, compute output RPU based on child1 RPU + child2 RPU and xfer function
     For OUTPUT gate, output RPU = child1 RPU
     For OUTPUT_OR gate, output RPU = child1 RPU + child2 RPU

     ***********************************************************************/
    public static void simulateRPU(Gate g, GateLibrary gate_library, Args options){

        if (g.is_unvisited()) {

            g.set_unvisited( false );


            ArrayList<Gate> children = g.getChildren();
            for(Gate child: children) {
                if(child.is_unvisited()) {
                    Evaluate.simulateRPU(child, gate_library, options);
                }
            }


            /**
             * Multidimensional response functions are not symmetric, so which wire maps to which variable must be determined.
             * Not relevant if there is only a single independent variable in the response function (i.e. Hill equation).
             */
            if(g.get_variable_names().size() > 1) {
                setBestVariableMapping(g, gate_library, options);
            }

            ///////////////////////////////////////////////////////////////////
            //now that the best variable mapping was found, resimulate
            ///////////////////////////////////////////////////////////////////


            String var = "x";
            if (g.get_variable_names().size() == 1) {
                var = g.get_variable_names().get(0);
            }

            ArrayList<int[]> order_permute = new ArrayList<int[]>();
            Double best_score = 0.0;
            int best_order_index = 0;

            GateUtil.mapWiresToVariables(g, g.get_variable_names());


            if (g.get_porder() !=null) {
                g.set_porder(g.get_porder());
            }

            else {
//				TODO Hard coded
                if (g.get_variable_wires().get(var).size() == 2) {

                    int[] order1 = {0,1};
                    int[] order2 = {1,0};

                    order_permute.add(order1);
                    order_permute.add(order2);

                }

                else {
                    int[] order0 = {0};
                    order_permute.add(order0);
                }

                int index = 0;
//
//				find the best promoter order based on gate score
                for (int[] order : order_permute) {

                    g.set_porder(order);


                    g.get_outrpus().clear();
                    g.get_inrpus().clear();


                    for (int i = 0; i < g.get_logics().size(); ++i) { // rows in truth
                        // table
                        /*
                         * if (Args.dontcare_rows.contains(i)) {
                         * g.get_outreus().add(0.0); continue; }
                         */

                        GateUtil.mapWiresToVariables(g, g.get_variable_names());

                        /**
                         * For example, String = "x". Double = summed REUs for tandem
                         * promoters i is the row in the truth table.
                         */
                        HashMap<String, Double> variable_values = GateUtil.getVariableValues(g, i, gate_library, options);

                        // v = "x"
                        for (String v : variable_values.keySet()) {

                            if (!g.get_inrpus().containsKey(v)) {
                                g.get_inrpus().put(v, new ArrayList<Double>());
                            }
                            g.get_inrpus().get(v).add(variable_values.get(v));

                        }

                        double output_reu = ResponseFunction.computeOutput(
                                // sum contributions of tandem promoters for this row in
                                // the truth table
                                // ...unless there is tandem promoter data, then we look
                                // up the value instead of adding
                                variable_values, g.get_params(), g.get_equation());

                        g.get_outrpus().add(output_reu);
                    }


                    evaluateGate(g, options);

                    if (g.get_scores().get_score() > best_score) {
                        best_score = g.get_scores().get_score();
                        best_order_index = index;
                    }

//					JH's new selection for tandem promoter
//					if (g.get_scores().get_highest_off() < best_score) {
//						best_score = g.get_scores().get_highest_off();
//						best_order_index = index;
//					}

                    index++;
                }

//				TODO set promoter order
//
//				if (g.Name.equals("YFP")) {
//					best_order_index = 0;
//				}

//				Now the best order is found, re-simulate the gate REU
                g.set_porder(order_permute.get(best_order_index));

            }


            g.get_outrpus().clear();

            g.get_inrpus().clear();


            for (int i = 0; i < g.get_logics().size(); ++i) { //rows in truth table
                /*if (Args.dontcare_rows.contains(i)) {
                    g.get_outrpus().add(0.0);
                    continue;
                }*/

                GateUtil.mapWiresToVariables(g, g.get_variable_names());

                /**
                 * For example, String = "x".
                 * Double = summed RPUs for tandem promoters
                 * i is the row in the truth table.
                 */
                HashMap<String, Double> variable_values = GateUtil.getVariableValues(g, i, gate_library, options);

                //v = "x"
                for(String v: variable_values.keySet()) {

                    if(!g.get_inrpus().containsKey(v)) {
                        //initialize with empty arraylist
                        g.get_inrpus().put(v, new ArrayList<Double>());
                    }
                    g.get_inrpus().get(v).add(variable_values.get(v));
                }

                double output_rpu = ResponseFunction.computeOutput(
                        //sum contributions of tandem promoters for this row in the truth table
                        //...unless there is tandem promoter data, then we look up the value instead of adding
                        variable_values,
                        g.get_params(),
                        g.get_equation()
                );

                g.get_outrpus().add(output_rpu);
            }

            //evaluateGate(g);

            //////////////////////////////////////////////



        }
    }


    /**
     * A gate with two transcriptional units (e.g. AND) can have two different wirings.
     * Doesn't matter for a gate with one txn unit.
     *
     * @param g
     * @param gate_library
     * @param options
     */
    public static void setBestVariableMapping(Gate g, GateLibrary gate_library, Args options) {

        ArrayList<ArrayList<String>> variable_name_orders = Permute.getVariableNamePermutation(g.get_variable_names());
        Integer best_variable_name_order_index = 0;
        Double best_score = 0.0;

        int v = 0;
        for (ArrayList<String> variable_name_order : variable_name_orders) {

            g.get_outrpus().clear();

            for (int i = 0; i < g.get_logics().size(); ++i) { //rows in truth table

                /*if (Args.dontcare_rows.contains(i)) {
                    g.get_outrpus().add(0.0);
                    continue;
                }*/

                GateUtil.mapWiresToVariables(g, variable_name_order);

                double output_rpu = ResponseFunction.computeOutput(
                        GateUtil.getVariableValues(g, i, gate_library, options),
                        g.get_params(),
                        g.get_equation()
                );

                g.get_outrpus().add(output_rpu);
            }

            evaluateGate(g, options);

            if (g.get_scores().get_score() > best_score) {
                best_score = g.get_scores().get_score();
                best_variable_name_order_index = v;
            }
            v++;
        }

        //this is the critical part, it's ordering the variable names in the list
        g.set_variable_names(variable_name_orders.get(best_variable_name_order_index));

    }



    /***********************************************************************

     Synopsis    [  ]

     ***********************************************************************/
    public static void simulateHistogramRPU(Gate g, GateLibrary gate_library, Args options) {

        if (g.is_unvisited()) {

            g.set_unvisited(false);


            ArrayList<Gate> children = g.getChildren();
            for(Gate child: children) {
                if(child.is_unvisited()) {
                    simulateHistogramRPU(child,gate_library,options);
                }
            }

            //initialize
            g.get_histogram_rpus().clear();
            for (int i = 0; i < g.get_logics().size(); ++i) { //rows in truth table

                g.get_histogram_rpus().add(new double[g.get_histogram_bins().get_NBINS()]);
                for (int j = 0; j < g.get_histogram_bins().get_NBINS(); ++j) {
                    g.get_histogram_rpus().get(i)[j] = 0.0;
                }
            }

            if ( g.type == GateType.OUTPUT_OR || g.type == GateType.OUTPUT ) {
                g.set_histogram_rpus(GateUtil.getSumOfGateInputHistograms(g, gate_library, options));
                GateUtil.outputHistogramUnitConversion(g);
            }
            else if (g.type == GateType.AND) {
                g.set_histogram_rpus(GateUtil.getANDOfGateInputHistograms(g));
            }
            else if (g.type == GateType.NOT || g.type == GateType.NOR){
                //2. For each row: for each bin: for each output bin: add normalizeToValue
                ArrayList<double[]> input_convrpus = GateUtil.getSumOfGateInputHistograms(g, gate_library,options);
                g.set_in_histogram_rpus(input_convrpus);
                
                for (int i = 0; i < input_convrpus.size(); ++i) {

                    /*if(Args.dontcare_rows.contains(i)) {
                        for(int bin=0; bin< g.get_histogram_rpus().get(i).length; ++bin) {
                            g.get_histogram_rpus().get(i)[bin] = 0.0;
                        }
                        continue;
                    }*/

                    double[] convhist = input_convrpus.get(i);

                    for (int bin = 0; bin < g.get_histogram_bins().get_NBINS(); ++bin) {
                        double fractional_counts = convhist[bin];
                        double[] xslice = g.get_xfer_hist().get_xfer_interp().get(bin);

                        for(int xslice_bin = 0; xslice_bin<g.get_histogram_bins().get_NBINS(); ++xslice_bin) {
                            g.get_histogram_rpus().get(i)[xslice_bin] += xslice[xslice_bin] * fractional_counts;
                        }
                    }
                }
            }

            //evaluateGateHistogramOverlap(g); //to compute gate scores

        }
    }

};
