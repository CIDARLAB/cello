package org.cellocad.MIT.dnacompiler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

/**
 * Created by Bryan Der on 12/5/14.
 */


public class GateUtil {


    /**
     * If the output module is on a different plasmid
     * (with a different copy number or different affect on cell growth),
     * the RPU units of the circuit might need to be converted.
     *
     * @param g
     */
    public static void outputHistogramUnitConversion(Gate g) {

        if(g.type == Gate.GateType.OUTPUT || g.type == Gate.GateType.OUTPUT_OR) {

            //outer arraylist: rows in truth table
            //inner array: fractional counts representing a histogram
            ArrayList<double[]> histogram_rpus = g.get_histogram_rpus();

            ArrayList<double[]> shifted_histogram_rpus = new ArrayList<double[]>();

            for (double[] histogram : histogram_rpus) {

                //calculate the new median
                double current_median = HistogramUtil.median(histogram, g.get_histogram_bins());
                double new_median = current_median * g.get_unit_conversion();

                //shift the histogram to the new median
                double[] shifted_histogram = HistogramUtil.normalizeHistogramToNewMedian(histogram, new_median, g.get_histogram_bins());

                shifted_histogram_rpus.add(shifted_histogram);
            }

            g.set_histogram_rpus(shifted_histogram_rpus);

        }
    }


    /**
     *
     * Given gate type and child gate logics, compute gate logic
     *
     */
    public static ArrayList<Integer> computeGateLogics(Gate g) {

        ArrayList<Gate> children = g.getChildren();
        ArrayList<Integer> gate_logics = new ArrayList<Integer>();

        if(g.type != Gate.GateType.INPUT) {
            for (int i = 0; i < children.get(0).get_logics().size(); ++i) { //rows in truth table

                //arraylength = # children
                ArrayList<Integer> this_row_child_logics = new ArrayList<Integer>();

                for (Gate child : children) {
                    this_row_child_logics.add(child.get_logics().get(i));
                }

                //compute Boolean value based on gate type and child logics
                Integer logic = BooleanLogic.computeLogic(g.type, this_row_child_logics);

                /*if(Args.dontcare_rows.contains(i)) {
                    logic = 2; //integer required, 2 used as placeholder for dont-care.  will be changed to - when printing
                }*/

                gate_logics.add(logic);

            }
            return gate_logics;
        }
        else {
            return g.get_logics();
        }

    }


    /**
     *
     * Compute depth of gate
     *
     *      recurse until reaching inputs
     *      set gates connected to inputs == 1
     *      when recursing back up from inputs, dist += 1 of child
     *      if multiple paths, choose farthest
     *
     */
    public static void calculateDistanceToFarthestInput(Gate g){

        if (g.is_unvisited()) {

            //initialize with 0
            g.set_distance_to_input(0);
            g.set_unvisited(false);

            if (g.type != Gate.GateType.INPUT) { //inputs are an endpoint (inputs gates are also initialized with unvisited=false;

                for (Gate child : g.getChildren()) {

                    calculateDistanceToFarthestInput(child); //recurse until reaching inputs

                    //then when recursing up from inputs, dist += 1 of child
                    g.set_distance_to_input(child.get_distance_to_input() + 1);

                    //if the distance_to_input exceeds any prior distances, update the farthest_dist2in
                    if (g.get_farthest_dist2in() < g.get_distance_to_input()) {
                        g.set_farthest_dist2in(g.get_distance_to_input());
                    }
                    else {
                        g.set_distance_to_input(g.get_farthest_dist2in());
                    }
                }
            }



        }
    }


    /**
     *
     * return min of all ON incoming rpus.
     *
     * Used to in noise margin analysis.  This is different than calculating ONmin or OFFmax for a gate,
     * which is based on the output RPU.  Noise margin analysis is based in the input RPU value(s).
     *
     */
    public static HashMap<String, Double> getIncomingONlow(Gate g) {

        GateUtil.mapWiresToVariables(g, g.get_variable_names());

        HashMap<String, ArrayList<Double>> all_incoming_ON = new HashMap<>();
        HashMap<String, Double> lowest_ons = new HashMap<>();

        for (String var : g.get_variable_wires().keySet()) {
            all_incoming_ON.put(var, new ArrayList<Double>());
        }

        for(int i=0; i<g.get_logics().size(); ++i) {

            for (String var : g.get_variable_wires().keySet()) {

                boolean ON = false;

                for (Wire w : g.get_variable_wires().get(var)) {
                    if (w.to.get_logics().get(i) == 1) {
                        ON = true;
                    }
                }

                if(ON) {

                    Double sum_of_ons = g.get_inrpus().get(var).get(i);

                    /**
                     * _inrpus (prompted by tandem promoter eval) replacing this...
                     */
                    /*
                    Double sum_of_ons = 0.0;
                    for (Wire w : g.get_variable_wires().get(var)) {
                        sum_of_ons += w.To.get_outrpus().get(i);
                    }*/

                    all_incoming_ON.get(var).add(sum_of_ons);
                }
            }
        }

        for(String var: g.get_variable_wires().keySet()) {

            Double lowest_on = Collections.min(all_incoming_ON.get(var));

            lowest_ons.put(var, lowest_on);

        }

        return lowest_ons;
    }

    /**
     *
     * return max of all OFF incoming rpus
     *
     * Used to in noise margin analysis.  This is different than calculating ONmin or OFFmax for a gate,
     * which is based on the output RPU.  Noise margin analysis is based in the input RPU value(s).
     *
     */
    public static HashMap<String, Double> getIncomingOFFhigh(Gate g) {

        GateUtil.mapWiresToVariables(g, g.get_variable_names());

        HashMap<String, ArrayList<Double>> all_incoming_OFF = new HashMap<>();
        HashMap<String, Double> highest_offs = new HashMap<>();

        for (String var : g.get_variable_wires().keySet()) {
            all_incoming_OFF.put(var, new ArrayList<Double>());
        }

        for(int i=0; i<g.get_logics().size(); ++i) {

            for (String var : g.get_variable_wires().keySet()) {

                boolean OFF = true;

                for (Wire w : g.get_variable_wires().get(var)) {

                    if (w.to.get_logics().get(i) == 1) {
                        OFF = false;
                    }
                }

                if(OFF) {

                    Double sum_of_offs = g.get_inrpus().get(var).get(i);

                    /**
                     * _inrpus (prompted by tandem promoter eval) replacing this...
                     */
                    /*
                    Double sum_of_offs = 0.0;
                    for (Wire w : g.get_variable_wires().get(var)) {
                        sum_of_offs += w.To.get_outrpus().get(i);
                    }*/

                    all_incoming_OFF.get(var).add(sum_of_offs);
                }
            }
        }


        for(String var: g.get_variable_wires().keySet()) {

            //Collections.sort(all_incoming_OFF.get(var));
            //Collections.reverse(all_incoming_OFF.get(var));

            Double highest_off = Collections.max(all_incoming_OFF.get(var));

            highest_offs.put(var, highest_off);

        }

        return highest_offs;
    }


    /**
     *
     * Cello assumes tandem promoters have additive activity (RPU)
     *
     */
    //replaced by gate.get_inrpus()
    /*public static ArrayList<Double> getSumOfGateInputRPUs(Gate g) {

        ArrayList<Gate> children = g.getChildren();
        ArrayList<Double> sum_child_rpus = new ArrayList<Double>();
        for(int i = 0; i<children.get(0).get_outrpus().size(); ++i){ //rows in truth table
            Double this_row_sum_rpus = 0.0;
            for(Gate child: children) {
                this_row_sum_rpus += child.get_outrpus().get(i);
            }
            sum_child_rpus.add(this_row_sum_rpus);
        }
        return sum_child_rpus;
    }*/



    //TODO using gird look up for medians of inputs RPU histograms or grid look up for inputs RPU
    // should be very close depent on the bins been chosen
    
//    public static ArrayList<Double[]> getSumOfGateInputHistograms_v1(Gate g) {
//    	ArrayList <Double[]> tt_rpu_medians = new ArrayList<Double>();
//    	
//    	return tt_rpu_medians;
//    }

    public static HashMap<String, Double> getInputParams(Gate input, GateLibrary gate_library) {
        HashMap<String, Double> inputparams = new HashMap<String, Double>();

        inputparams.put("ymin", gate_library.get_INPUTS_OFF().get(input.name));
        inputparams.put("ymax", gate_library.get_INPUTS_ON().get(input.name));
        inputparams.put("a", gate_library.get_INPUTS_a().get(input.name));
        inputparams.put("b", gate_library.get_INPUTS_b().get(input.name));

        return inputparams;
    }

    //	Get input gate output RPU for model prediction
    public static Double getInputoutREU(Gate inputgate, int rownumber, HashMap<String, Double> user_input_values) {
        Double inputvalue = 0.0;

        if (inputgate.get_logics().get(rownumber) == 0) {
            inputvalue = inputgate.get_outrpus().get(rownumber);
        } else if (inputgate.get_logics().get(rownumber) == 1) {
            if (inputgate.name.equals("pTac")) {

                if (!user_input_values.isEmpty()) {
                    inputvalue = user_input_values.get("pTac");
                }
                else {
                    inputvalue = inputgate.get_outrpus().get(rownumber);
                }
            }
            else if (inputgate.name.equals("pTet")) {

                if (!user_input_values.isEmpty()) {
                    inputvalue = user_input_values.get("pTet");
                }
                else {
                    inputvalue = inputgate.get_outrpus().get(rownumber);
                }
            }
            else if (inputgate.name.equals("pBAD")) {

                if (!user_input_values.isEmpty()) {
                    inputvalue = user_input_values.get("pBAD");
                }
                else {
                    inputvalue = inputgate.get_outrpus().get(rownumber);
                }
            }
            else if (inputgate.name.equals("pLuxStar")) {

                if (!user_input_values.isEmpty()) {
                    inputvalue = user_input_values.get("pLuxStar");
                }
                else {
                    inputvalue = inputgate.get_outrpus().get(rownumber);
                }
            }
            else {
                inputvalue = inputgate.get_outrpus().get(rownumber);
            }
        }

        return inputvalue;
    }

    public static double getinputreu(
            HashMap<String, Double> upstream_params,
            double x1,
            HashMap<String, Double> downstream_params,
            double x2,
            String promoter_type,
            int logic) {

        double outreu = 0.0;

        double ymax1 = upstream_params.get("ymax");
        double ymin1 = upstream_params.get("ymin");
        double K1;
        double n1;

        double ymax2 = downstream_params.get("ymax");
        double ymin2 = downstream_params.get("ymin");
        double K2;
        double n2;

        double a2 = downstream_params.get("a");
        double b2 = downstream_params.get("b");

//		double ymin = 0.0;
        double pdown =0.0;
        double pup= 0.0;
        double factor = 0.0;


//		Modelup

        if (promoter_type.equals("Repressor_Repressor")) {

            K1 = upstream_params.get("K");
            n1 = upstream_params.get("n");
            K2 = downstream_params.get("K");
            n2 = downstream_params.get("n");

            pdown = ymin2 + ((ymax2-ymin2)*Math.pow(K2, n2))/(Math.pow(K2, n2) + Math.pow(x2, n2));
            pup = ymin1 + ((ymax1-ymin1)*Math.pow(K1, n1))/(Math.pow(K1, n1) + Math.pow(x1, n1));
            factor = (1.0 + b2*Math.pow(x2, n2)/Math.pow(K2, n2))*a2*Math.pow(K2, n2)/(Math.pow(K2, n2) + Math.pow(x2, n2));

            outreu = pdown + pup*factor;

        }
        else if (promoter_type.equals("Input_Repressor")) {

            K2 = downstream_params.get("K");
            n2 = downstream_params.get("n");

            pdown = ymin2 + ((ymax2-ymin2)*Math.pow(K2, n2))/(Math.pow(K2, n2) + Math.pow(x2, n2));
            pup = x1;
            factor = (1.0 + b2*Math.pow(x2, n2)/Math.pow(K2, n2))*a2*Math.pow(K2, n2)/(Math.pow(K2, n2) + Math.pow(x2, n2));

            outreu = pdown + pup*factor;
        }
        else if (promoter_type.equals("Repressor_Input")) {

            K1 = upstream_params.get("K");
            n1 = upstream_params.get("n");

            pdown = x2;
            pup = ymin1 + ((ymax1-ymin1)*Math.pow(K1, n1))/(Math.pow(K1, n1) + Math.pow(x1, n1));

            if(logic==0) {
                factor = a2*b2;
            }
            else {
                factor = a2;
            }
//			factor = (1.0 + b2*Math.pow(K2, n2)/Math.pow(x2, n2))*a2*Math.pow(x2, n2)/(Math.pow(K2, n2) + Math.pow(x2, n2));

            outreu = pdown + pup*factor;


        }
        else if (promoter_type.equals("Input_Input")) {

            pdown = x2;
            pup = x1;
            if(logic==0) {
                factor = a2*b2;
            }
            else {
                factor = a2;
            }

//			factor = (1.0 + b2*Math.pow(K2, n2)/Math.pow(x2, n2))*a2*Math.pow(x2, n2)/(Math.pow(K2, n2) + Math.pow(x2, n2));

            outreu = pdown + pup*factor;

        }

        else {
            outreu = 0.0;
        }


        return outreu;

    }

    public static ArrayList<double[]> getSumOfGateInputHistograms(Gate g, GateLibrary gate_library, Args options) {

        ArrayList<Double> tt_rpu_medians = new ArrayList<Double>();
        HashMap<String, Double> user_input_values = options.get_input_values();

        // TODO changed
        /**
         * New way, does not assume additivity but does not use distribution medians.
         */

        if (options.is_tpmodel()) {

            HistogramBins hbins = new HistogramBins();
            hbins.init();
            int[] porder = g.get_porder();
            Gate child1 = new Gate();
            Gate child2 = new Gate();
            HashMap<String, Double> child1_params = new HashMap<String, Double>();
            HashMap<String, Double> child2_params = new HashMap<String, Double>();
            Double child1_in = 0.0;
            Double child2_in = 0.0;

            String var = "x";
            if (g.get_variable_names().size() == 1) {
                var = g.get_variable_names().get(0);
            }



            for (int i = 0; i < g.get_logics().size(); ++i) {
                int row = i;
                Double inputreu = 0.0;

//				TODO Hard coded
                if (g.get_variable_wires().get(var).size() == 2) {


//
//					Use model to calculate incoming REU for this gate

                    child1 = g.getChildren().get(0);
                    child2 = g.getChildren().get(1);


                    child1_params = child1.get_params();
                    child2_params = child2.get_params();

                    String child1_type, child2_type;

                    if (child1.type == Gate.GateType.INPUT) {
                        child1_type = "Input";
                        child1_params = getInputParams(child1, gate_library);
//					TODO for JS test
//						child1_in = getInputONOFF(child1, row, user_input_values);
                        child1_in = getInputoutREU(child1, row, user_input_values);

//						if (child1.get_logics().get(row) == 0) {
//							child1_in = Math.pow(10, hbins.get_LOGMIN());
//						} else if (child1.get_logics().get(row) == 1) {
//							child1_in = Math.pow(10, hbins.get_LOGMAX());
//						}
                    } else {
                        child1_type = "Repressor";
                        child1_in = child1.get_inrpus().get(var).get(row);
                    }

                    if (child2.type == Gate.GateType.INPUT) {
                        child2_type = "Input";
                        child2_params = getInputParams(child2, gate_library);
//						TODO for JS test
//						child2_in = getInputONOFF(child2, row, user_input_values);
                        child2_in = getInputoutREU(child2, row, user_input_values);


//						if (child2.get_logics().get(row) == 0) {
//							child2_in = Math.pow(10, hbins.get_LOGMIN());
//						} else if (child2.get_logics().get(row) == 1) {
//							child2_in = Math.pow(10, hbins.get_LOGMAX());
//						}
                    } else {
                        child2_type = "Repressor";
                        child2_in = child2.get_inrpus().get(var).get(row);
                    }


                    String promoter_type;

                    for (int k=0; k<porder.length; k++) {
                        if (porder[0] == 0 && porder[1] == 1) {
                            promoter_type = child1_type + "_" + child2_type;
                            inputreu = getinputreu(child1_params, child1_in, child2_params, child2_in, promoter_type, child2.get_logics().get(row));

                        }
                        else{
                            promoter_type = child2_type + "_" + child1_type;
                            inputreu = getinputreu(child2_params, child2_in, child1_params, child1_in, promoter_type, child1.get_logics().get(row));
                        }
                    }

                }

                else {

//					TODO check output histogram?
//						If the gate contains only one wire, use this wire to get the input REU
                    for (Gate child : g.getChildren()) {

                        int bin_median = HistogramUtil.bin_median(child.get_histogram_rpus().get(i));
                        Double reu_median = Math.pow(10, g.get_histogram_bins().get_LOG_BIN_CENTERS()[bin_median]);
                        inputreu += reu_median;
                    }
                }

                tt_rpu_medians.add(inputreu);
            }

        }

        else if (options.is_tandem_promoter()){
        	
            HistogramBins hbins = new HistogramBins();
            hbins.init();
        	
            String var = "x";
            if(g.get_variable_names().size() == 1) {
                var = g.get_variable_names().get(0);
            }
            
            boolean tp_exists = false;
            String tp_name = "";
            double[][] grid = new double[hbins.get_NBINS()][hbins.get_NBINS()];
            Gate child1 = new Gate();
            Gate child2 = new Gate();
            ArrayList<String> fanin_gate_names = new ArrayList<>();
        	
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
                
	            for (int i = 0; i < g.get_logics().size(); ++i) { //rows in truth table
	                
	            	double sum_median = 0.0;
	            	
	            	int bin_median1 = 0;  
	            	int bin_median2 = 0;
	            	
                    if(child1.type == Gate.GateType.INPUT) {
                        if(child1.get_logics().get(i) == 0) {
                            bin_median1 = 0;
                        }
                        else if(child1.get_logics().get(i) == 1) {
                        	bin_median1 = hbins.get_NBINS() - 1;
                        }
                    }
                    else {
                    	bin_median1 = HistogramUtil.bin_median(child1.get_in_histogram_rpus().get(i));
                    }
                    

                    if(child2.type == Gate.GateType.INPUT) {
                        if(child2.get_logics().get(i) == 0) {
                            bin_median2 = 0;
                        }
                        else if(child2.get_logics().get(i) == 1) {
                        	bin_median2 = hbins.get_NBINS() - 1;
                        }
                    }
                    else {
                    	bin_median2 = HistogramUtil.bin_median(child2.get_in_histogram_rpus().get(i));
                    }
                    
                    String gate1_name = fanin_gate_names.get(0);
                    String gate2_name = fanin_gate_names.get(1);
                    
	                if (tp_name.startsWith(gate1_name) && tp_name.endsWith(gate2_name)) {
                        //correct in1 and in2 order
                    }
                    else if (tp_name.startsWith(gate2_name) && tp_name.endsWith(gate1_name)) {
                        int temp = bin_median1;
                        bin_median1 = bin_median2;
                        bin_median2 = temp;
                    }
                    else {
                        throw new IllegalStateException("Problem with tandem promoter lookup");
                    }
	                
	                sum_median = grid[bin_median1][bin_median2];
                    double out = Math.pow(10, sum_median);
	                
	                tt_rpu_medians.add(out);
	            }
	        }
	        
	        else{	            
	       
	            for (int i = 0; i < g.get_logics().size(); ++i) { //rows in truth table
	
	                double sum_median = 0.0;
	
	                for (Gate child : g.getChildren()) {
	
	                    int bin_median = HistogramUtil.bin_median(child.get_histogram_rpus().get(i));
	                    Double rpu_median = Math.pow(10, g.get_histogram_bins().get_LOG_BIN_CENTERS()[bin_median]);
	                    sum_median += rpu_median;
	                }
	
	                tt_rpu_medians.add(sum_median);
	            }	            
	        }
        }

        /**
         * Old way, assumes additivity but uses distribution medians
         */
        else {
            for (int i = 0; i < g.get_logics().size(); ++i) { //rows in truth table

                double sum_median = 0.0;
                //if(!Args.dontcare_rows.contains(i)) {

                for (Gate child : g.getChildren()) {

                    int bin_median = HistogramUtil.bin_median(child.get_histogram_rpus().get(i));
                    Double rpu_median = Math.pow(10, g.get_histogram_bins().get_LOG_BIN_CENTERS()[bin_median]);
                    sum_median += rpu_median;
                }

                //}

                tt_rpu_medians.add(sum_median);
            }
        }


        ArrayList<double[]> tt_sum_zeroed = new ArrayList<double[]>();

        //initialize
        for (int i = 0; i < g.get_logics().size(); ++i) { //rows in truth table
            tt_sum_zeroed.add(new double[g.get_histogram_bins().get_NBINS()]);
            for (int j = 0; j < g.get_histogram_bins().get_NBINS(); ++j) {
                tt_sum_zeroed.get(i)[j] = 0.0;
            }
        }


        for (int i = 0; i < g.get_logics().size(); ++i) { //rows in truth table

            for(Gate child: g.getChildren()) {

                Double target_median = tt_rpu_medians.get(i);
                double[] child_histogram = child.get_histogram_rpus().get(i);
                double[] shifted_histogram = HistogramUtil.normalizeHistogramToNewMedian(child_histogram, target_median, g.get_histogram_bins());

                //for each child of this gate, sum the zeroed fractional counts.
                for(int bin=0; bin<g.get_histogram_bins().get_NBINS(); ++bin) {
                    tt_sum_zeroed.get(i)[bin] += shifted_histogram[bin];
                }
            }

        }

        ArrayList<double[]> tt_normalized_zeroed = new ArrayList<double[]>();
        for(double[] sum_zeroed: tt_sum_zeroed) {
            tt_normalized_zeroed.add(HistogramUtil.normalize(sum_zeroed));
        }

        return tt_normalized_zeroed;
    }



    /***********************************************************************

     Synopsis    [  ]

     ***********************************************************************/
    public static ArrayList<double[]> getANDOfGateInputHistograms(Gate g) {

        ArrayList<double[]> tt_AND = new ArrayList<double[]>();

        for (int i = 0; i < g.get_logics().size(); ++i) { //rows in truth table

            double min_median = 1000.0;

            double[] min_histogram = new double[g.get_histogram_bins().get_NBINS()];

            for (Gate child : g.getChildren()) {
                if(HistogramUtil.median( child.get_histogram_rpus().get(i), g.get_histogram_bins()) < min_median ) {
                    min_median = HistogramUtil.median( child.get_histogram_rpus().get(i), g.get_histogram_bins());
                    min_histogram = child.get_histogram_rpus().get(i);
                }
            }
            tt_AND.add(min_histogram);
        }

        return tt_AND;
    }


    public static ArrayList<ArrayList<Double>> getGateInputRPUs(Gate g) {

        ArrayList<Gate> children = g.getChildren();

        //outer is truth table
        //inner is children
        ArrayList<ArrayList<Double>> all_child_rpus = new ArrayList<ArrayList<Double>>();

        for(int i = 0; i<children.get(0).get_outrpus().size(); ++i) { //rows in truth table

            ArrayList<Double> all_child_rpu_for_row = new ArrayList<Double>();

            for(Gate child: children) {

                all_child_rpu_for_row.add(child.get_outrpus().get(i));
            }

            all_child_rpus.add(all_child_rpu_for_row);

        }

        return all_child_rpus;
    }



    public static void mapWiresToVariables(Gate g, ArrayList<String> variables) {

        HashMap<String, ArrayList<Wire>> map = new HashMap<String, ArrayList<Wire>>();

        /**
         * List of all input wires for gate 'g'
         */
        ArrayList<Wire> input_wires = new ArrayList<Wire>();
        if (g.outgoing != null) {
            input_wires.add(g.outgoing);

            Wire w = g.outgoing;
            while(w.next != null) {
                input_wires.add(w.next);
                w = w.next;
            }
        }

        /**
         * If response function as 1 independent variable, all wires map to that variable.
         */
        if(variables.size() == 1) {
            String v = variables.get(0);
            map.put(v, input_wires);
        }


        /**
         * Else, for example, a 2-input AND might have 2 independent variables.
         * In this case, we have two different ways to map 2 promoters to the 2 independent variables.
         * Just choose a default mapping for now, see also: Evaluate.setBestVariableMapping, which establishes the
         * best variable name order.
         */
        else if(variables.size() == input_wires.size()) {

            for(int i=0; i<variables.size(); ++i) {

                ArrayList<Wire> input_wire = new ArrayList<Wire>();
                input_wire.add(input_wires.get(i));

                map.put(variables.get(i), input_wire);
            }
        }


        //
        g.set_variable_wires(map);

    }



    public static double gateinputreu(Gate g, int row, String v, Args options, GateLibrary gate_library) {

//		only support one variable: "x"

        HashMap<String, Double> user_input_values = options.get_input_values();

        double inputreu = 0.0;

        String var = v;
        if (g.get_variable_names().size() == 1) {
            var = g.get_variable_names().get(0);
        }

        HistogramBins hbins = new HistogramBins();
        hbins.init();
        int[] porder = g.get_porder();
        Gate child1 = new Gate();
        Gate child2 = new Gate();
        HashMap<String, Double> child1_params = new HashMap<String, Double>();
        HashMap<String, Double> child2_params = new HashMap<String, Double>();
        Double child1_in = 0.0;
        Double child2_in = 0.0;
        String child1_type, child2_type;

        if (g.get_variable_wires().get(var).size() == 2) {

            child1 = g.getChildren().get(0);
            child2 = g.getChildren().get(1);

            child1_params = child1.get_params();
            child2_params = child2.get_params();

            if (child1.type == Gate.GateType.INPUT) {
                child1_type = "Input";
                child1_params = getInputParams(child1, gate_library);
//				TODO for JS test
//					child1_in = getInputONOFF(child1, row, user_input_values);
                child1_in = getInputoutREU(child1, row, user_input_values);

//				if (child1.get_logics().get(row) == 0) {
//					child1_in = Math.pow(10, hbins.get_LOGMIN());
//				} else if (child1.get_logics().get(row) == 1) {
//					child1_in = Math.pow(10, hbins.get_LOGMAX());
//				}
            } else {
                child1_type = "Repressor";
                child1_in = child1.get_inrpus().get(var).get(row);
            }

            if (child2.type == Gate.GateType.INPUT) {
                child2_type = "Input";
                child2_params = getInputParams(child2, gate_library);
//				TODO for JS test
//					child2_in = getInputONOFF(child2, row, user_input_values);
                child2_in = getInputoutREU(child2, row, user_input_values);

//				if (child2.get_logics().get(row) == 0) {
//					child2_in = Math.pow(10, hbins.get_LOGMIN());
//				} else if (child2.get_logics().get(row) == 1) {
//					child2_in = Math.pow(10, hbins.get_LOGMAX());
//				}
            } else {
                child2_type = "Repressor";
                child2_in = child2.get_inrpus().get(var).get(row);
            }

            String promoter_type;



            for (int i=0; i<porder.length; i++) {
                if (porder[0] == 0 && porder[1] == 1) {
                    promoter_type = child1_type + "_" + child2_type;
                    inputreu = getinputreu(child1_params, child1_in, child2_params, child2_in, promoter_type, child2.get_logics().get(row));

                }
                else{
                    promoter_type = child2_type + "_" + child1_type;
                    inputreu = getinputreu(child2_params, child2_in, child1_params, child1_in, promoter_type, child1.get_logics().get(row));
                }
            }


        }
        else {
//			If the gate contains only one wire, use this wire to get the input REU
            for (Wire w : g.get_variable_wires().get(var)) {
                inputreu += w.to.get_outrpus().get(row);
            }
        }

        return inputreu;
    }

    /**
     * Sum RPUs for tandem promoters, map the summed value to the name of the independent variable
     * from the gate's response function.
     *
     * @param g
     * @param row
     * @return
     */
    //need GateLibrary (get_TANDEM_PROMOTERS() )
    //need options ( is_tandem_promoter() )
    public static HashMap<String, Double> getVariableValues(Gate g, int row, GateLibrary gate_library, Args options) {

        HashMap<String, Double> variable_values = new HashMap<>();

        if (options.is_tpmodel()) {

            for (String v : g.get_variable_wires().keySet()) {

                Double inputreu = 0.0;

                inputreu = gateinputreu(g, row, v, options, gate_library);

                variable_values.put(v, inputreu);

//                System.out.print(inputreu);
            }
        }


        else if(options.is_tandem_promoter()) {
            //TODO Shuyi's idea is to calculate the grid here, so that we only calculate those we actually need.
            // the trick here is to avoid calculating the grid more than once.

            //P3_PhlF_B1_BM3R1
            //B1_BM3R1_P3_PhlF

            HistogramBins hbins = new HistogramBins();
            hbins.init();


            /**
             * First goal is to figure out if the data exists for this tandem promoter: 'tp_exists'
             */
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


            //get a number for additive assumption, testing

            /*boolean DEBUG = false;
            if(DEBUG) {
                Double additive_val = 0.0;

                for (String v : g.get_variable_wires().keySet()) {

                    for (Wire w : g.get_variable_wires().get(v)) {
                        //this is the line that adds RPU values for tandem promoters
                        additive_val += w.To.get_outrpus().get(row);
                    }
                }
            }*/


            //for example, String v = "x" in the hill equation "ymin+(ymax-ymin)/(1.0+(x/K)^n)"
            //"x" is the variable, while ymax, ymin, K, n are parameters
            for (String v : g.get_variable_wires().keySet()) {
                Double d = 0.0;
                Double dtest = 0.0;

                for (Wire w : g.get_variable_wires().get(v)) {
                    //this is the line that adds RPU values for tandem promoters
                    dtest += w.to.get_outrpus().get(row);
                }


                /**
                 * Non-additive tandem promoter data
                 */
                if (tp_exists) {


                    String gate1_name = fanin_gate_names.get(0);
                    String gate2_name = fanin_gate_names.get(1);

                    Double in1 = 0.0;
                    Double in2 = 0.0;

                    if(child1.type == Gate.GateType.INPUT) {
                        if(child1.get_logics().get(row) == 0) {
                            in1 = Math.pow(10, hbins.get_LOGMIN());
                        }
                        else if(child1.get_logics().get(row) == 1) {
                            in1 = Math.pow(10, hbins.get_LOGMAX());
                        }
                    }
                    else {
                        in1 = child1.get_inrpus().get(v).get(row);
                    }


                    if(child2.type == Gate.GateType.INPUT) {
                        if(child2.get_logics().get(row) == 0) {
                            in2 = Math.pow(10, hbins.get_LOGMIN());
                        }
                        else if(child2.get_logics().get(row) == 1) {
                            in2 = Math.pow(10, hbins.get_LOGMAX());
                        }
                    }
                    else {
                        in2 = child2.get_inrpus().get(v).get(row);
                    }


                    if (tp_name.startsWith(gate1_name) && tp_name.endsWith(gate2_name)) {
                        //correct in1 and in2 order
                    }
                    else if (tp_name.startsWith(gate2_name) && tp_name.endsWith(gate1_name)) {
                        Double temp = new Double(in1);
                        in1 = in2;
                        in2 = temp;
                    }
                    else {
                        throw new IllegalStateException("Problem with tandem promoter lookup");
                    }

                    Integer bin1 = HistogramUtil.bin_of_logrpu(Math.log10(in1), hbins);
                    Integer bin2 = HistogramUtil.bin_of_logrpu(Math.log10(in2), hbins);

                    Double log_out = grid[bin1][bin2];
                    Double out = Math.pow(10, log_out);

                    variable_values.put(v, out);
                }
                /**
                 * Additive
                 */
                else {
                    for (Wire w : g.get_variable_wires().get(v)) {
                        //this is the line that adds RPU values for tandem promoters
                        d += w.to.get_outrpus().get(row);
                    }
                    variable_values.put(v, d);
                }
            }
        }


        else {

            for(String v: g.get_variable_wires().keySet()) {
                Double d = 0.0;
                for (Wire w : g.get_variable_wires().get(v)) {
                    //this is the line that adds RPU values for tandem promoters
                    d += w.to.get_outrpus().get(row);
                }
                variable_values.put(v, d);
            }
        }



        return variable_values;

    }

}
