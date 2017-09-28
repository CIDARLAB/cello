package org.cellocad.MIT.dnacompiler;
/**
 * Created by Bryan Der on 3/26/14.
 */

import java.util.ArrayList;
import java.util.Collections;

/**
 * Gate Toxicity: 1=nontoxic growth, 0=no growth. Circuit Toxicity is multiplicative for all gates.
 *
 * Toxicity data for a repressor takes the form of an array of OD measurements, where each OD measurement
 * corresponds to an input RPU level.  Toxicity evaluation is performed by a weighted average of the nearest OD datapoints
 * given the incoming RPU.
 *
 * In the Toxicity table, OD measurements are normalized by the uninduced OD measurement for each repressor.
 * Value of 1.0 means non-toxic.  Value below 1.0 indicates the degree of Toxicity.
 * To compute Circuit Toxicity for each row in the truth table, multiply Toxicity values for reach repressor in that row of the truth table.
 *
 */
public class Toxicity {


    public static void initializeCircuitToxicity(LogicCircuit lc) { //cell growth = 1.0, avoids edge-case error when there are no logic gates.

        for(Gate g: lc.get_output_gates()) {

            g.get_toxicity().clear();

            for(int i=0; i<g.get_logics().size(); ++i) {

                g.get_toxicity().add(1.00);

            }
        }
    }


    /**
     * Interpolate OD value (get weighted average) for nearest input RPUs from the ToxicityTable.
     * Do this for all rows in truth table.
     *
     */
    public static void evaluateGateToxicity(Gate g) {

        if(g.get_variable_names().size() == 1) { //multi-input toxicity not handled.

            String var = g.get_variable_names().get(0);


            if (g.get_toxtable().size() == 0) {

                //System.out.println("no toxicity data for " + g.Name);
                //System.exit(-1);

                ArrayList<Double> toxicity = new ArrayList<Double>();

                ArrayList<Double> input_rpu_rows = g.get_inrpus().get(var);

                for (int i = 0; i < input_rpu_rows.size(); ++i) {
                    toxicity.add(1.0);
                }

                g.set_toxicity(toxicity);

            } else {

                ArrayList<Double> toxicity = new ArrayList<Double>();

                ArrayList<Double> input_rpu_rows = g.get_inrpus().get(var);

                for (int i = 0; i < input_rpu_rows.size(); ++i) {

                    double incoming_rpu = input_rpu_rows.get(i);

                    double tox_score = 1.0;

                    //if incoming rpu is below the first titration rpu
                    if (incoming_rpu < g.get_toxtable().get(0).get_x()) {
                        tox_score = g.get_toxtable().get(0).get_y();
                    }

                    //if incoming rpu is above the last titration rpu
                    else if (incoming_rpu > g.get_toxtable().get(g.get_toxtable().size() - 1).get_x()) {
                        tox_score = g.get_toxtable().get(g.get_toxtable().size() - 1).get_y();
                    }

                    //if incoming rpu is in the titration range, use weighted average of the two surrounding titration points
                    else {
                        //...search titrations until titration > incoming rpu
                        for (int t = 0; t < g.get_toxtable().size(); ++t) {

                            if (incoming_rpu < g.get_toxtable().get(t).get_x()) {

                                double lower_rpu = Math.log10(g.get_toxtable().get(t - 1).get_x());
                                double upper_rpu = Math.log10(g.get_toxtable().get(t).get_x());

                                double lower_tox = g.get_toxtable().get(t - 1).get_y();
                                double upper_tox = g.get_toxtable().get(t).get_y();

                                double weight = (Math.log10(incoming_rpu) - lower_rpu) / (upper_rpu - lower_rpu);

                                double weighted_avg = (lower_tox * (1 - weight)) + (upper_tox * weight);

                                tox_score = weighted_avg;
                                //tox_score = tox_table.get(Name.Repressor(g)).get(t - 1);

                                //for example,
                                // input_rpu = 16
                                // rpu titration  8.44 = tox 0.12 for QacR
                                // rpu titration 17.52 = tox 0.05 for QacR
                                // weighted_avg_tox = 0.051
                                break;
                            }
                        }
                    }

                    if (tox_score > 1.0)
                        tox_score = 1.0;
                    if (tox_score < 0.01)
                        tox_score = 0.01;


                    toxicity.add(tox_score);

                }

                g.set_toxicity(toxicity);

            }
        }
    }


    /**
     * For each row in the truth table of the output gate(s),
     * overall circuit Toxicity will have a multiplicative score for all repressors.
     *
     * All output gates will have the same Toxicity scores.
     *
     */
    public static void evaluateCircuitToxicity(LogicCircuit lc, GateLibrary gate_library){

        ArrayList<Double> circuit_toxicity = new ArrayList<Double>();

        for(int i=0; i<lc.get_output_gates().get(0).get_logics().size(); ++i) { //rows in truth table

            double row_toxicity = 1.0;

            for(Gate g: lc.get_logic_gates()) {

                g.set_toxtable(gate_library.get_GATES_BY_NAME().get(g.name).get_toxtable());

                evaluateGateToxicity(g);

                row_toxicity = row_toxicity * g.get_toxicity().get(i);

                if(row_toxicity < 0.01) {
                    row_toxicity = 0.01;
                }

            }

            circuit_toxicity.add(row_toxicity);

        }

        for(Gate g: lc.get_output_gates()) {

            g.set_toxicity(circuit_toxicity);

        }

    }

    /**
     * A gate Toxicity score == worst Toxicity score among all rows of the truth table
     */
    public static double mostToxicRow(Gate g) {

        return Collections.min(g.get_toxicity());

        /*double most_toxic_row = 1.0; //initialize to highest possible value

        for(int i=0; i<g.get_toxicity().size(); ++i) {

            if(g.get_toxicity().get(i) < most_toxic_row) {

                most_toxic_row = g.get_toxicity().get(i);

            }

        }

        return most_toxic_row;*/
    }


    /**
     * A gate Toxicity score == worst Toxicity score among all rows of the truth table
     */
    public static double mostToxicRow(LogicCircuit lc) {

        //output gate toxicity = circuit gate toxicity with multiplicative growth value,
        //all output gates will have the same toxicity values.
        //TODO can consider allowing the user to specify a toxicity curve for an output gene.

        Double min_growth = 1.0;

        for(Gate g: lc.get_output_gates()) {
            if(!g.get_toxicity().isEmpty()) {
                if (Collections.min(g.get_toxicity()) < min_growth) {
                    min_growth = Collections.min(g.get_toxicity());
                }
            }
        }

        return min_growth;


        /*double most_toxic_row = 1.0; //initialize to highest possible value

        for(double tox: lc.get_output_gates().get(0).get_toxicity()) {

            if(tox < most_toxic_row) {

                most_toxic_row = tox;

            }

        }

        return most_toxic_row;*/
    }


    /**
     * Print a table of growth values for each logic gate an output gate.
     *
     * @param lc
     * @return
     */
    public static String writeToxicityTable(LogicCircuit lc) {

        String s = "";

        s += String.format("%14s", "truth table") + "\t";

        for(int j=lc.get_logic_gates().size()-1; j>=0; --j) {

            Gate gate = lc.get_logic_gates().get(j);

            if (gate.get_logics().size() > 0 && gate.get_toxicity().size() > 0){
                s += String.format("%7s", gate.group) + "\t";
            }

        }

        s += String.format("%11s", "circuit") + "\t";
        s += "\n";

        //all output gates will have the same growth values, calculated by multiplying all logic gate growth_values
        //so we can just output the circuit growth once, which is why the 0'th index is used
        for(int i=0; i<lc.get_output_gates().get(0).get_logics().size(); ++i){

            s += String.format("%14s", lc.getLogicRow(i)) + "\t";

            for(int j=lc.get_logic_gates().size()-1; j>=0; --j) {

                Gate gate = lc.get_logic_gates().get(j);

                if (gate.get_logics().size() > 0 && gate.get_toxicity().size() > 0){
                    s += String.format("%7s", String.format("%3.2f",gate.get_toxicity().get(i))) + "\t";
                }

            }

            s += String.format("%11s", String.format("%3.2f",lc.get_output_gates().get(0).get_toxicity().get(i))) + "\t";
            s += "\n";

        }

        return s;

    }

}
