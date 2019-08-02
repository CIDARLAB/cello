package org.cellocad.MIT.dnacompiler;

import java.util.ArrayList;

/**
 * Created by Bryan Der on 4/5/14.
 */


/**
 * Read data from files.
 *
 * Input promoters:
 * promoter name, OFF RPU, ON RPU, promoter DNA sequence.
 *
 * Output gene cassette(s):
 * output name, cassette DNA sequence (concatenation of ribozyme, rbs, cds, terminator would be typical).
 *
 */
public class InputOutputGateReader {


    //get the number of inputs, just used to initialize GateLibrary with a number of inputs
    public static int nInputs(String fin_inputs) {
        ArrayList<ArrayList<String>> inputs_list = Util.fileTokenizer(fin_inputs);
        ArrayList<String> input_names = new ArrayList<String>();

        for(int i=inputs_list.size()-1; i>=0; --i) {

            if(inputs_list.get(i).get(0).substring(0,1).equals("#")) { //commented out
                continue;
            }
            input_names.add(inputs_list.get(i).get(0));
        }
        return input_names.size();
    }

    //get the number of outputs, just used to initialize GateLibrary with a number of outputs
    public static int nOutputs(String fin_outputs) {
        ArrayList<ArrayList<String>> outputs_list = Util.fileTokenizer(fin_outputs);
        ArrayList<String> output_names = new ArrayList<String>();

        for(int i=outputs_list.size()-1; i>=0; --i) {

            if(outputs_list.get(i).get(0).substring(0,1).equals("#")) { //commented out
                continue;
            }
            output_names.add(outputs_list.get(i).get(0));
        }
        return output_names.size();
    }

    /**
     *
     * pTac   0.0204  8.7918  AACGATCGTTGGCTGTGTTGACAATTAATCATCGGCTCGTATAATGTGTGGAATTGTGAGCGCTCACAATT
     * pTet   0.1312  16.837  TACTCCACCGTTGGCTTTTTTCCCTATCAGTGATAGAGATTGACATCCCTATCAGTGATAGAGATAATGAGCAC
     * pBAD   0.013   7.801   ACTTTTCATACTCCCGCCATTCAGAGAAGAAACCAATTGTCCATATTGCATCAGACATTGCCGTCACTGCGTCTTTTACTGGCTCTTCTCGCTAACCAAACCGGTAACCCCGCTTATTAAAAGCATTCTGTAACAAAGCGGGACCAAAGCCATGACAAAAACGCGTAACAAAAGTGTCTATAATCACGGCAGAAAAGTCCACATTGATTATTTGCACGGCGTCACACTTTGCTATGCCATAGCATTTTTATCCATAAGATTAGCGGATCCTACCTGACGCTTTTTATCGCAACTCTCTACTGTTTCTCCATACCCGTTTTTTTGGGCTAGC
     *
     *
     * Sets the _INPUT_NAMES, _INPUTS_OFF, _INPUTS_ON, _INPUTS_SEQ in GateLibrary
     */
    public static void readInputsFromFile(String fin_inputs, GateLibrary gate_library, boolean tp) {

        ArrayList<ArrayList<String>> inputs_list = Util.fileTokenizer(fin_inputs);
        ArrayList<String> input_names = new ArrayList<String>();

        gate_library.get_INPUTS_ON().clear();
        gate_library.get_INPUTS_OFF().clear();

        if (tp) {
            gate_library.get_INPUTS_a().clear();
            gate_library.get_INPUTS_b().clear();
        }

        //for(int i=0; i<inputs_list.size(); ++i) {
        for(int i=inputs_list.size()-1; i>=0; --i) {

            if(inputs_list.get(i).get(0).substring(0,1).equals("#")) { //commented out
                continue;
            }

            String name =         inputs_list.get(i).get(0);
            Double off_rpu =      Double.valueOf(inputs_list.get(i).get(1));
            Double on_rpu =       Double.valueOf(inputs_list.get(i).get(2));
            Double a = 0.0;
            Double b = 0.0;
            String promoter_seq = "";


            if(tp) {
                a =      Double.valueOf(inputs_list.get(i).get(3));
                b =      Double.valueOf(inputs_list.get(i).get(4));
                promoter_seq = inputs_list.get(i).get(5);
            }
            else {
                promoter_seq = inputs_list.get(i).get(3);
            }

            gate_library.get_INPUTS_OFF().put(name, off_rpu);
            gate_library.get_INPUTS_ON().put(name, on_rpu);
            gate_library.get_INPUTS_a().put(name, a);
            gate_library.get_INPUTS_b().put(name, b);
            gate_library.get_INPUTS_SEQ().put(name, promoter_seq);

            input_names.add(name);
        }


        //TODO confirm that the correct input order is used for case, assign, structural
        //TODO ask Prashant how input order is determined for different verilog inputs... is it in the module definition, or in case statements, is it in the case condition?
        for(int i=0; i<input_names.size(); ++i) {
            gate_library.get_INPUT_NAMES()[i] = input_names.get(i);
        }
    }

    /**
     *
     * YFP CTGAAGCTGTCACCGGATGTGCTTTCCGGTCTGA...
     * RFP CTGAAGTGGTCGTGATCTGAAACTCGATCACCTG...
     * BFP CTGAAGTTCCAGTCGAGACCTGAAGTGGGTTTCC...
     *
     *
     * Sets the _OUTPUT_NAMES and _OUTPUT_SEQ in GateLibrary
     */
    public static void readOutputsFromFile(String fin_outputs, GateLibrary gate_library) {

        ArrayList<ArrayList<String>> outputs_list = Util.fileTokenizer(fin_outputs);
        ArrayList<String> output_names = new ArrayList<String>();

        for(int i=0; i<outputs_list.size(); ++i) {

            if(outputs_list.get(i).get(0).substring(0,1).equals("#")) { //commented out
                continue;
            }

            String name       = outputs_list.get(i).get(0);
            String output_seq = outputs_list.get(i).get(1);

            output_names.add(name);
            gate_library.get_OUTPUTS_SEQ().put(name, output_seq);
        }

        for(int i=0; i<output_names.size(); ++i) {
            gate_library.get_OUTPUT_NAMES()[i] = output_names.get(i);
        }

    }


    /**
     * Actual experimental histograms are not able to be specified for inputs, so we just get a
     * default histogram for the input RPU geometric mean values.
     */
    public static void makeHistogramsforInputRPUs(GateLibrary gate_library, String file_name_default) {


        HistogramBins hbins = new HistogramBins();
        hbins.init();

        for(int i=0; i< gate_library.get_INPUT_NAMES().length; ++i) {

            Double log_OFF = Math.log10(gate_library.get_INPUTS_OFF().get(gate_library.get_INPUT_NAMES()[i]));
            Double log_ON  = Math.log10(gate_library.get_INPUTS_ON().get( gate_library.get_INPUT_NAMES()[i]));

            ArrayList<Double> hist_OFF = HistogramUtil.getDefaultHistgramAtSpecifiedMean(log_OFF, file_name_default);
            ArrayList<Double> hist_ON  = HistogramUtil.getDefaultHistgramAtSpecifiedMean(log_ON, file_name_default);

            //normalize: sum of all fractional counts equals 1.0
            double[] load_OFF = HistogramUtil.normalize(HistogramUtil.placeDataIntoBins(hist_OFF, hbins));
            double[] load_ON  = HistogramUtil.normalize(HistogramUtil.placeDataIntoBins(hist_ON, hbins));

            double[] shifted_OFF = HistogramUtil.normalizeHistogramToNewMedian(load_OFF, gate_library.get_INPUTS_OFF().get(gate_library.get_INPUT_NAMES()[i]), hbins);
            double[] shifted_ON  = HistogramUtil.normalizeHistogramToNewMedian(load_ON,  gate_library.get_INPUTS_ON().get( gate_library.get_INPUT_NAMES()[i]), hbins);

            gate_library.get_INPUTS_HIST_OFF().put(gate_library.get_INPUT_NAMES()[i], shifted_OFF);
            gate_library.get_INPUTS_HIST_ON().put( gate_library.get_INPUT_NAMES()[i], shifted_ON);

            //compare the input median value to the histogram median value.  Should be very close but not necessarily identical.
            //System.out.println("median " + gate_library.get_INPUT_NAMES()[i] + " OFF " + HistogramUtil.median(shifted_OFF, hbins));
            //System.out.println("median " + gate_library.get_INPUT_NAMES()[i] + " ON  " + HistogramUtil.median(shifted_ON, hbins));
        }

    }

}

