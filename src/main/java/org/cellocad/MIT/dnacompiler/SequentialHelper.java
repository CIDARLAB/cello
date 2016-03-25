package org.cellocad.MIT.dnacompiler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Bryan Der on 9/22/15.
 */
public class SequentialHelper {

    public static boolean convergeREUs(LogicCircuit lc, GateLibrary gate_library, Args options, HashMap<String, ArrayList<ArrayList<Double>>> track_reus) {

        Integer cycles = 0;

        while(! allGatesConverged(track_reus)) {

            if(cycles > 1000) {
                return false;
            }

            updateREUs(lc, gate_library, options, track_reus);
            cycles++;
        }

        return true;

    }

    public static boolean allGatesConverged(HashMap<String, ArrayList<ArrayList<Double>>> track_reus) {

        Double CONVERGENCE_THRESHOLD = 0.000000001;

        boolean all_converged = true;

        for(String gate_name: track_reus.keySet()) {

            ArrayList<ArrayList<Double>> reu_series = track_reus.get(gate_name);

            if(reu_series.size() < 5) {
                return false;
            }


            int i = reu_series.size() - 1;

            ArrayList<Double> reus_i = reu_series.get(i);
            ArrayList<Double> reus_iminus1 = reu_series.get(i-1);

            //for each row in the truth table, check for convergence
            for(int j=0; j<reus_i.size(); ++j) {
                Double diff = reus_i.get(j) - reus_iminus1.get(j);
                Double abs_diff = Math.abs(diff);
                if(abs_diff > CONVERGENCE_THRESHOLD) {
                    //System.out.println("not converged " + gate_name);
                    return false;
                }
            }
        }

        return all_converged;
    }


    public static void updateREUs(LogicCircuit lc, GateLibrary gate_library, Args options, HashMap<String, ArrayList<ArrayList<Double>>> track_reus) {

        ArrayList<Gate> gates = new ArrayList<Gate>();
        gates.addAll( lc.get_logic_gates() );
        gates.addAll( lc.get_output_gates() );

        for(Gate g: gates) {

            g.set_unvisited(true);
            Evaluate.simulateREU(g, gate_library, options);
        }

        //update the list of cycles
        for(Gate g: lc.get_Gates()) {
            ArrayList<Double> copy_reus = new ArrayList<Double>(g.get_outreus());
            track_reus.get(g.Name).add(copy_reus);
        }
    }


    public static void setInitialREUs(LogicCircuit lc, GateLibrary gate_library) {

        Evaluate.refreshGateAttributes(lc, gate_library);

        for(Gate g: lc.get_input_gates()) {

            ArrayList<Double> reus = new ArrayList<Double>();

            for(int i=0; i<g.get_logics().size(); ++i) {

                Integer logic = g.get_logics().get(i);
                if (logic == 0) {
                    Double reu = gate_library.get_INPUTS_OFF().get(g.Name);
                    reus.add(reu);
                } else {
                    Double reu = gate_library.get_INPUTS_ON().get(g.Name);
                    reus.add(reu);
                }
            }

            g.set_outreus(reus);

        }

        for(Gate g: lc.get_logic_gates()) {

            ArrayList<Double> reus = new ArrayList<Double>();

            for (int i = 0; i < g.get_logics().size(); ++i) {

                Integer logic = g.get_logics().get(i);

                if (logic == 0) {

                    Double output_reu = 0.0001;

                    if(g.get_params().containsKey("ymin")) {
                        output_reu = g.get_params().get("ymin");
                    }

                    reus.add(output_reu);
                }

                else {

                    Double output_reu = 100.0;

                    if(g.get_params().containsKey("ymax")) {
                        output_reu = g.get_params().get("ymax");
                    }

                    reus.add(output_reu);
                }
            }

            g.set_outreus(reus);

        }

        //TODO not sufficient for OUTPUT_OR
        for(Gate g: lc.get_output_gates()) {

            ArrayList<Double> reus = new ArrayList<Double>(g.Outgoing.To.get_outreus());
            g.set_outreus(reus);

        }

    }


    public static void printTruthTable(LogicCircuit lc) {

        String tt = "\n";

        for(Gate g: lc.get_Gates()) {

            tt += String.format("%7s", g.Name);
        }

        tt += "\n";

        for(int i=0; i<lc.get_input_gates().get(0).get_logics().size(); ++i) {

            for(Gate g: lc.get_Gates()) {
                tt += String.format("%7s", g.get_logics().get(i));
            }

            tt += "\n";
        }

        System.out.println(tt);
    }


    public static void setInitialLogics(LogicCircuit lc, HashMap<String, List<Integer>> initial_logics, Integer nrows) {

        for (Gate g : lc.get_Gates()) {
            ArrayList<Integer> logics = new ArrayList<>();

            for(int i=0; i<nrows; ++i) {
                logics.add(0);
            }

            g.set_logics(logics);
        }



        for(String gate_name: initial_logics.keySet()) {

            List<Integer> logics = initial_logics.get(gate_name);

            //g.Group: for S, R, Qa, Qb latch naming

            for(Gate g: lc.get_input_gates()) {
                if(g.Name.equals(gate_name)) {
                    g.set_logics(new ArrayList<Integer>(logics));
                }
            }

            for(Gate g: lc.get_logic_gates()) {
                if(g.Name.equals(gate_name)) {
                    g.set_logics(new ArrayList<Integer>(logics));
                }
            }

            for(Gate g: lc.get_output_gates()) {
                if(g.Name.equals(gate_name)) {
                    g.set_logics(new ArrayList<Integer>(logics));

                    Gate child = g.Outgoing.To;
                    child.set_logics(new ArrayList<Integer>(logics));
                }
            }
        }

    }


    public static void updateLogics(LogicCircuit lc) {

        for(Gate g: lc.get_Gates()) {

            if(g.Type == Gate.GateType.INPUT) {
                continue;
            }

            else {
                ArrayList<Integer> output_logics = GateUtil.computeGateLogics(g);

                g.set_logics(output_logics);
            }

        }

    }


    public static boolean validLogic(LogicCircuit lc) {

        for(Gate g: lc.get_Gates()) {

            if(g.Type == Gate.GateType.INPUT) {
                continue;
            }

            for(int i=0; i<g.get_logics().size(); ++i) {
                Integer logic = g.get_logics().get(i);

                ArrayList<Integer> child_logics = new ArrayList<>();
                for(Gate child: g.getChildren()) {
                    child_logics.add(child.get_logics().get(i));
                }

                Integer expected_logic = BooleanLogic.computeLogic(g.Type, child_logics);

                if(expected_logic != logic) {
                    System.out.println(g.Group + " " + logic + " " + expected_logic + " : from " + child_logics.toString());
                    return false;
                }
            }

        }

        return true;
    }



    public static int loadInitialLogicsFromTruthtable(HashMap<String, List<Integer>> initial_logics, String fin_sequential_waveform) {

        ArrayList<String> file_lines = Util.fileLines(fin_sequential_waveform);

        for(String line: file_lines) {
            System.out.println(line);
        }


        String first_line = file_lines.get(0);


        HashMap<String, ArrayList<Integer>> io_logics_map = new HashMap();

        ArrayList<String> labels = new ArrayList<String>(Util.lineTokenizer(first_line));

        for(int i=0; i<labels.size(); ++i) {
            io_logics_map.put(labels.get(i), new ArrayList<Integer>());
        }


        int nrows = 0;

        for(int j=1; j<file_lines.size(); ++j) {

            if(!file_lines.get(j).isEmpty()) {

                nrows++;

                ArrayList<String> tokens = new ArrayList<String>(Util.lineTokenizer(file_lines.get(j)));
                for (int i = 0; i < tokens.size(); ++i) {

                    String label = labels.get(i);
                    Integer val = Integer.valueOf(tokens.get(i));
                    io_logics_map.get(label).add(val);

                }

            }
        }

        for(String label: labels) {
            initial_logics.put(label, io_logics_map.get(label));

            System.out.println("Initial logics " + label + " " + io_logics_map.get(label));
        }


        return nrows;
    }





    public static void setInitialHistogramREUs(LogicCircuit lc, GateLibrary gate_library) {

        Evaluate.refreshGateAttributes(lc, gate_library);

        for(Gate g: lc.get_input_gates()) {

            ArrayList<double[]> hist_reus = new ArrayList<double[]>();

            for(int i=0; i<g.get_logics().size(); ++i) {

                Integer logic = g.get_logics().get(i);
                if (logic == 0) {
                    double[] hist_reu = gate_library.get_INPUTS_HIST_OFF().get(g.Name);
                    hist_reus.add(hist_reu);
                } else {
                    double[] hist_reu = gate_library.get_INPUTS_HIST_ON().get(g.Name);
                    hist_reus.add(hist_reu);
                }
            }

            g.set_histogram_reus(hist_reus);
        }


        for(Gate g: lc.get_logic_gates()) {

            ArrayList<double[]> hist_reus = new ArrayList<double[]>();

            for (int i = 0; i < g.get_logics().size(); ++i) {

                Integer logic = g.get_logics().get(i);

                if (logic == 0) {

                    Integer last_index = g.get_xfer_hist().get_xfer_interp().size() - 1;

                    double[] hist_reu = g.get_xfer_hist().get_xfer_interp().get(last_index);

                    hist_reus.add(hist_reu);
                }

                else {

                    double[] hist_reu = g.get_xfer_hist().get_xfer_interp().get(0);

                    hist_reus.add(hist_reu);
                }
            }

            g.set_histogram_reus(hist_reus);

        }

        //TODO not sufficient for OUTPUT_OR
        for(Gate g: lc.get_output_gates()) {

            ArrayList<double[]> hist_reus = new ArrayList<double[]>(g.Outgoing.To.get_histogram_reus());
            g.set_histogram_reus(hist_reus);

        }
    }


    public static void convergeHistogramREUs(LogicCircuit lc, GateLibrary gate_library, Args options, HashMap<String, ArrayList<ArrayList<double[]>>> track_reus) {

        Integer cycles = 0;

        while(! allGateHistogramsConverged(track_reus)) {
            updateHistogramREUs(lc, gate_library, options, track_reus);
            cycles++;
        }

    }


    public static boolean allGateHistogramsConverged(HashMap<String, ArrayList<ArrayList<double[]>>> track_reus) {

        HistogramBins hbins = new HistogramBins();
        hbins.init();

        Double CONVERGENCE_THRESHOLD = 0.000000001;

        boolean all_converged = true;

        for(String gate_name: track_reus.keySet()) {

            ArrayList<ArrayList<double[]>> reu_series = track_reus.get(gate_name);

            if(reu_series.size() < 5) {
                return false;
            }


            int i = reu_series.size() - 1;

            ArrayList<double[]> reus_i = reu_series.get(i);
            ArrayList<double[]> reus_iminus1 = reu_series.get(i-1);

            //for each row in the truth table, check for convergence
            for(int j=0; j<reus_i.size(); ++j) {

                Double diff = HistogramUtil.median(reus_i.get(j), hbins) - HistogramUtil.median(reus_iminus1.get(j), hbins);
                Double abs_diff = Math.abs(diff);
                if(abs_diff > CONVERGENCE_THRESHOLD) {
                    return false;
                }
            }
        }

        return all_converged;
    }


    public static void updateHistogramREUs(LogicCircuit lc, GateLibrary gate_library, Args options, HashMap<String, ArrayList<ArrayList<double[]>>> track_reus) {

        ArrayList<Gate> gates = new ArrayList<>();
        gates.addAll( lc.get_logic_gates() );
        gates.addAll( lc.get_output_gates() );

        Evaluate.simulateHistogramREU(lc, gate_library, options);

        //update the list of cycles
        for(Gate g: lc.get_Gates()) {
            ArrayList<double[]> copy_reus = new ArrayList<double[]>(g.get_histogram_reus());
            track_reus.get(g.Name).add(copy_reus);
        }
    }


}
