package org.cellocad.MIT.dnacompiler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Bryan Der on 9/22/15.
 */
public class SequentialHelper {

    public static boolean convergeRPUs(LogicCircuit lc, GateLibrary gate_library, Args options, HashMap<String, ArrayList<ArrayList<Double>>> track_rpus) {

        Integer cycles = 0;

        while(! allGatesConverged(track_rpus)) {

            if(cycles > 1000) {
                return false;
            }

            updateRPUs(lc, gate_library, options, track_rpus);
            cycles++;
        }

        return true;

    }

    public static boolean allGatesConverged(HashMap<String, ArrayList<ArrayList<Double>>> track_rpus) {

        Double CONVERGENCE_THRESHOLD = 0.000000001;

        boolean all_converged = true;

        for(String gate_name: track_rpus.keySet()) {

            ArrayList<ArrayList<Double>> rpu_series = track_rpus.get(gate_name);

            if(rpu_series.size() < 5) {
                return false;
            }


            int i = rpu_series.size() - 1;

            ArrayList<Double> rpus_i = rpu_series.get(i);
            ArrayList<Double> rpus_iminus1 = rpu_series.get(i-1);

            //for each row in the truth table, check for convergence
            for(int j=0; j<rpus_i.size(); ++j) {
                Double diff = rpus_i.get(j) - rpus_iminus1.get(j);
                Double abs_diff = Math.abs(diff);
                if(abs_diff > CONVERGENCE_THRESHOLD) {
                    //System.out.println("not converged " + gate_name);
                    return false;
                }
            }
        }

        return all_converged;
    }


    public static void updateRPUs(LogicCircuit lc, GateLibrary gate_library, Args options, HashMap<String, ArrayList<ArrayList<Double>>> track_rpus) {

        ArrayList<Gate> gates = new ArrayList<Gate>();
        gates.addAll( lc.get_logic_gates() );
        gates.addAll( lc.get_output_gates() );

        for(Gate g: gates) {

            g.set_unvisited(true);
            Evaluate.simulateRPU(g, gate_library, options);
        }

        //update the list of cycles
        for(Gate g: lc.get_Gates()) {
            ArrayList<Double> copy_rpus = new ArrayList<Double>(g.get_outrpus());
            track_rpus.get(g.name).add(copy_rpus);
        }
    }


    public static void setInitialRPUs(LogicCircuit lc, GateLibrary gate_library) {

        Evaluate.refreshGateAttributes(lc, gate_library);

        for(Gate g: lc.get_input_gates()) {

            ArrayList<Double> rpus = new ArrayList<Double>();

            for(int i=0; i<g.get_logics().size(); ++i) {

                Integer logic = g.get_logics().get(i);
                if (logic == 0) {
                    Double rpu = gate_library.get_INPUTS_OFF().get(g.name);
                    rpus.add(rpu);
                } else {
                    Double rpu = gate_library.get_INPUTS_ON().get(g.name);
                    rpus.add(rpu);
                }
            }

            g.set_outrpus(rpus);

        }

        for(Gate g: lc.get_logic_gates()) {

            ArrayList<Double> rpus = new ArrayList<Double>();

            for (int i = 0; i < g.get_logics().size(); ++i) {

                Integer logic = g.get_logics().get(i);

                if (logic == 0) {

                    Double output_rpu = 0.0001;

                    if(g.get_params().containsKey("ymin")) {
                        output_rpu = g.get_params().get("ymin");
                    }

                    rpus.add(output_rpu);
                }

                else {

                    Double output_rpu = 100.0;

                    if(g.get_params().containsKey("ymax")) {
                        output_rpu = g.get_params().get("ymax");
                    }

                    rpus.add(output_rpu);
                }
            }

            g.set_outrpus(rpus);

        }

        //TODO not sufficient for OUTPUT_OR
        for(Gate g: lc.get_output_gates()) {

            ArrayList<Double> rpus = new ArrayList<Double>(g.outgoing.to.get_outrpus());
            g.set_outrpus(rpus);

        }

    }


    public static void printTruthTable(LogicCircuit lc) {

        String tt = "\n";

        for(Gate g: lc.get_Gates()) {

            tt += String.format("%7s", g.name);
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
                if(g.name.equals(gate_name)) {
                    g.set_logics(new ArrayList<Integer>(logics));
                }
            }

            for(Gate g: lc.get_logic_gates()) {
                if(g.name.equals(gate_name)) {
                    g.set_logics(new ArrayList<Integer>(logics));
                }
            }

            for(Gate g: lc.get_output_gates()) {
                if(g.name.equals(gate_name)) {
                    g.set_logics(new ArrayList<Integer>(logics));

                    Gate child = g.outgoing.to;
                    child.set_logics(new ArrayList<Integer>(logics));
                }
            }
        }

    }


    public static void updateLogics(LogicCircuit lc) {

        for(Gate g: lc.get_Gates()) {

            if(g.type == Gate.GateType.INPUT) {
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

            if(g.type == Gate.GateType.INPUT) {
                continue;
            }

            for(int i=0; i<g.get_logics().size(); ++i) {
                Integer logic = g.get_logics().get(i);

                ArrayList<Integer> child_logics = new ArrayList<>();
                for(Gate child: g.getChildren()) {
                    child_logics.add(child.get_logics().get(i));
                }

                Integer expected_logic = BooleanLogic.computeLogic(g.type, child_logics);

                if(expected_logic != logic) {
                    System.out.println(g.group + " " + logic + " " + expected_logic + " : from " + child_logics.toString());
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





    public static void setInitialHistogramRPUs(LogicCircuit lc, GateLibrary gate_library) {

        Evaluate.refreshGateAttributes(lc, gate_library);

        for(Gate g: lc.get_input_gates()) {

            ArrayList<double[]> hist_rpus = new ArrayList<double[]>();

            for(int i=0; i<g.get_logics().size(); ++i) {

                Integer logic = g.get_logics().get(i);
                if (logic == 0) {
                    double[] hist_rpu = gate_library.get_INPUTS_HIST_OFF().get(g.name);
                    hist_rpus.add(hist_rpu);
                } else {
                    double[] hist_rpu = gate_library.get_INPUTS_HIST_ON().get(g.name);
                    hist_rpus.add(hist_rpu);
                }
            }

            g.set_histogram_rpus(hist_rpus);
        }


        for(Gate g: lc.get_logic_gates()) {

            ArrayList<double[]> hist_rpus = new ArrayList<double[]>();

            for (int i = 0; i < g.get_logics().size(); ++i) {

                Integer logic = g.get_logics().get(i);

                if (logic == 0) {

                    Integer last_index = g.get_xfer_hist().get_xfer_interp().size() - 1;

                    double[] hist_rpu = g.get_xfer_hist().get_xfer_interp().get(last_index);

                    hist_rpus.add(hist_rpu);
                }

                else {

                    double[] hist_rpu = g.get_xfer_hist().get_xfer_interp().get(0);

                    hist_rpus.add(hist_rpu);
                }
            }

            g.set_histogram_rpus(hist_rpus);

        }

        //TODO not sufficient for OUTPUT_OR
        for(Gate g: lc.get_output_gates()) {

            ArrayList<double[]> hist_rpus = new ArrayList<double[]>(g.outgoing.to.get_histogram_rpus());
            g.set_histogram_rpus(hist_rpus);

        }
    }


    public static void convergeHistogramRPUs(LogicCircuit lc, GateLibrary gate_library, Args options, HashMap<String, ArrayList<ArrayList<double[]>>> track_rpus) {

        Integer cycles = 0;

        while(! allGateHistogramsConverged(track_rpus)) {
            updateHistogramRPUs(lc, gate_library, options, track_rpus);
            cycles++;
        }

    }


    public static boolean allGateHistogramsConverged(HashMap<String, ArrayList<ArrayList<double[]>>> track_rpus) {

        HistogramBins hbins = new HistogramBins();
        hbins.init();

        Double CONVERGENCE_THRESHOLD = 0.000000001;

        boolean all_converged = true;

        for(String gate_name: track_rpus.keySet()) {

            ArrayList<ArrayList<double[]>> rpu_series = track_rpus.get(gate_name);

            if(rpu_series.size() < 5) {
                return false;
            }


            int i = rpu_series.size() - 1;

            ArrayList<double[]> rpus_i = rpu_series.get(i);
            ArrayList<double[]> rpus_iminus1 = rpu_series.get(i-1);

            //for each row in the truth table, check for convergence
            for(int j=0; j<rpus_i.size(); ++j) {

                Double diff = HistogramUtil.median(rpus_i.get(j), hbins) - HistogramUtil.median(rpus_iminus1.get(j), hbins);
                Double abs_diff = Math.abs(diff);
                if(abs_diff > CONVERGENCE_THRESHOLD) {
                    return false;
                }
            }
        }

        return all_converged;
    }


    public static void updateHistogramRPUs(LogicCircuit lc, GateLibrary gate_library, Args options, HashMap<String, ArrayList<ArrayList<double[]>>> track_rpus) {

        ArrayList<Gate> gates = new ArrayList<>();
        gates.addAll( lc.get_logic_gates() );
        gates.addAll( lc.get_output_gates() );

        Evaluate.simulateHistogramRPU(lc, gate_library, options);

        //update the list of cycles
        for(Gate g: lc.get_Gates()) {
            ArrayList<double[]> copy_rpus = new ArrayList<double[]>(g.get_histogram_rpus());
            track_rpus.get(g.name).add(copy_rpus);
        }
    }


}
