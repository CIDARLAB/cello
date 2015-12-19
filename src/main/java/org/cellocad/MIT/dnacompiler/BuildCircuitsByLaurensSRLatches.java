package org.cellocad.MIT.dnacompiler;

import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;



public class BuildCircuitsByLaurensSRLatches extends BuildCircuits {


    public BuildCircuitsByLaurensSRLatches(Args options, GateLibrary gate_library, Roadblock roadblock) {
        super(options, gate_library, roadblock);
    }


    public ArrayList<ArrayList<String>> get_assignments() {

        ArrayList<ArrayList<String>> assignments = new ArrayList<>();

        setInputMap();
        setRBSMap();

        ArrayList<String> file_lines = Util.fileLines(get_options().get_fin_reload());

        ArrayList<String> design_names = new ArrayList<>();

        ArrayList<String> t1 = new ArrayList<>();
        ArrayList<String> t2 = new ArrayList<>();
        ArrayList<String> t3 = new ArrayList<>();
        ArrayList<String> t4 = new ArrayList<>();

        ArrayList<String> in1 = new ArrayList<>();
        ArrayList<String> in2 = new ArrayList<>();
        ArrayList<String> gateA = new ArrayList<>();
        ArrayList<String> gateB = new ArrayList<>();

        for(String line: file_lines) {

            String[] tokens = line.split(",");
            t1.add(tokens[1]);
            t2.add(tokens[2]);
            t3.add(tokens[3]);
            t4.add(tokens[4]);
        }

        for(String t: t1) {
            if(!_input_map.containsKey(t)) {
                System.out.println("does not contain " + t);
                System.exit(-1);
            }
            else {
                in1.add(_input_map.get(t));
            }
        }
        for(String t: t2) {
            if(!_input_map.containsKey(t)) {
                System.out.println("does not contain " + t);
                System.exit(-1);
            }
            else {
                in2.add(_input_map.get(t));
            }
        }
        for(String t: t3) {
            if(!_rbs_map.containsKey(t)) {
                System.out.println("does not contain " + t);
                System.exit(-1);
            }
            else {
                gateA.add(_rbs_map.get(t));
            }
        }
        for(String t: t4) {
            if(!_rbs_map.containsKey(t)) {
                System.out.println("does not contain " + t);
                System.exit(-1);
            }
            else {
                gateB.add(_rbs_map.get(t));
            }
        }


        for(int i=0; i<in1.size(); ++i) {
            ArrayList<String> asn = new ArrayList<>();
            asn.add(in1.get(i));
            asn.add(in2.get(i));
            asn.add(gateA.get(i));
            asn.add(gateB.get(i));

            assignments.add(asn);
        }

        return assignments;

    }

    @Override
    public void buildCircuits() {

        log.info("build circuit by reloading SR latches" + get_options().get_fin_reload());

        set_logic_circuits( new ArrayList<LogicCircuit>() );

        LogicCircuit lc = get_unassigned_lc();


        // reorder gates
        LogicCircuitUtil.sortGatesByStage(lc);
        Collections.reverse(lc.get_Gates());

        ArrayList<Gate> new_gate_order = new ArrayList<>();
        new_gate_order.addAll(lc.get_input_gates());
        new_gate_order.addAll(lc.get_logic_gates());
        new_gate_order.addAll(lc.get_output_gates());
        lc.set_Gates(new_gate_order);


        //load initial logics from truthtable.txt
        HashMap<String, List<Integer>> initial_logics = new HashMap<>();

        int nrows = SequentialHelper.loadInitialLogicsFromTruthtable( initial_logics, get_options().get_fin_sequential_waveform() );


        System.out.println("Initial");
        SequentialHelper.setInitialLogics(lc, initial_logics, nrows);
        SequentialHelper.printTruthTable(lc);

        System.out.println("Cycle 1");
        SequentialHelper.updateLogics(lc);
        SequentialHelper.printTruthTable(lc);

        System.out.println("Cycle 2");
        SequentialHelper.updateLogics(lc);
        SequentialHelper.printTruthTable(lc);

        System.out.println("Cycle 3");
        SequentialHelper.updateLogics(lc);
        SequentialHelper.printTruthTable(lc);


        //assert logic is valid
        if(! SequentialHelper.validLogic(lc)) {

            System.out.println("Invalid logic.  Exiting.");
            System.exit(-1);
        }

        for(Gate g: lc.get_Gates()) {
            System.out.println(g.Group + " " + g.get_logics());
        }



        ArrayList<LogicCircuit> all_assigned_lcs = new ArrayList<>();



        String stdout = "";

        ArrayList<ArrayList<String>> assignments = get_assignments();

        for(ArrayList<String> asn: assignments) {

            String in1 = asn.get(0);
            String in2 = asn.get(1);
            String A = asn.get(2);
            String B = asn.get(3);

            Gate gate_in1 = lc.get_input_gates().get(0);
            Gate gate_in2 = lc.get_input_gates().get(1);

            Gate gate_A = lc.get_logic_gates().get(1);
            Gate gate_B = lc.get_logic_gates().get(0);

            gate_in1.Group = "R";
            gate_in2.Group = "S";


            gate_in1.Name = in1;
            gate_in2.Name = in2;
            gate_A.Name = A;
            gate_B.Name = B;


            if(!get_gate_library().get_GATES_BY_NAME().containsKey(gate_A.Name)) {
                System.out.println(A + " missing");
                System.exit(-1);
            }
            if(!get_gate_library().get_GATES_BY_NAME().containsKey(gate_B.Name)) {
                System.out.println(B + " missing");
                System.exit(-1);
            }

            LogicCircuitUtil.setInputREU(lc, get_gate_library());

            SequentialHelper.setInitialREUs(lc, get_gate_library());

            HashMap<String, ArrayList<ArrayList<Double>>> track_reus = new HashMap<>();


            for (Gate g : lc.get_Gates()) {
                track_reus.put(g.Name, new ArrayList<ArrayList<Double>>());

                ArrayList<Double> copy_reus = new ArrayList<Double>(g.get_outreus());
                track_reus.get(g.Name).add(copy_reus);
            }


            ////////////////////////////////////////////////////
            // simulate REUs until they converge
            ////////////////////////////////////////////////////
            SequentialHelper.convergeREUs(lc, get_gate_library(), get_options(), track_reus);


            Evaluate.evaluateCircuitONOFFRatio(lc);

            Evaluate.evaluateCircuitNoiseMargin(lc, get_options());

            Toxicity.evaluateCircuitToxicity(lc, get_gate_library());
            double growth_score = Toxicity.mostToxicRow(lc);




            boolean pass_rb = ! get_roadblock().illegalRoadblocking(lc, get_gate_library());
            boolean pass_nm = lc.get_scores().is_noise_margin_contract();
            boolean pass_tox = (growth_score > get_options().get_toxicity_threshold());

            Double nm = lc.get_scores().get_noise_margin();
            Double tox = growth_score;

            Double score = lc.get_scores().get_score();

            ArrayList<Double> Qa_reus = lc.get_logic_gates().get(1).get_outreus();
            ArrayList<Double> Qb_reus = lc.get_logic_gates().get(0).get_outreus();

            String Qa_reu_string = "";
            String Qb_reu_string = "";

            for(Double d: Qa_reus) {
                Qa_reu_string += Util.sc(d*0.4) + ",";
            }
            for(Double d: Qb_reus) {
                Qb_reu_string += Util.sc(d*0.4) + ",";
            }

            //stdout += asn + ":: " + " nm:" + pass_nm + " tox:" + pass_tox + "\n";
            stdout += asn + "," + Util.sc(score) + "," + Util.sc(nm) + "," + pass_nm + "," + Util.sc(tox) + "," + pass_tox + "," + Qa_reu_string + "," + Qb_reu_string + "\n";


        }


        String rename_stdout = revert_names(stdout);


        System.out.println(rename_stdout);



        //LogicCircuit lc = get_unassigned_lc();
        //System.out.println(lc.printGraph());

        System.exit(-1);

    }

    private String revert_names(String out) {

        String s = out;
        s = s.replace("pTet", "PTet");
        s = s.replace("pTac", "PTac");
        s = s.replace("pBAD", "PBAD");


        s = s.replace("F1_AmeR",        "F0");
        s = s.replace("A1_AmtR",        "Aj2");
        s = s.replace("A1_AmtR",        "A1");
        s = s.replace("E1_BetI",        "Ej2");
        s = s.replace("B1_BM3R1",       "B0");
        s = s.replace("B2_BM3R1",       "B1");
        s = s.replace("B3_BM3R1",       "Bj2");
        s = s.replace("H1_HlyIIR",      "Hj2");
        s = s.replace("I1_IcaRA",       "I1");
        s = s.replace("L1_LitR",        "Lj2");
        s = s.replace("N1_LmrA",        "N0");
        s = s.replace("P1_PhlF",        "P0");
        s = s.replace("P2_PhlF",        "P1");
        s = s.replace("P3_PhlF",        "Pj2");
        s = s.replace("R1_PsrA",        "R0");
        s = s.replace("Q1_QacR",        "Q2");
        s = s.replace("Q2_QacR",        "Q0");
        s = s.replace("S1_SrpR",        "S1");
        s = s.replace("S2_SrpR",        "S2");
        s = s.replace("S3_SrpR",        "S3");
        s = s.replace("S4_SrpR",        "S0");

        return s;

    }

    private void setInputMap() {

        _input_map = new HashMap<>();
        _input_map.put("PTac", "pTac");
        _input_map.put("PTet", "pTet");
        _input_map.put("PBAD", "pBAD");
    }

    private void setRBSMap()
    {
        _rbs_map = new HashMap<>();
        _rbs_map.put("F0",  "F1_AmeR");
        _rbs_map.put("Aj2", "A1_AmtR");
        _rbs_map.put("A1",  "A1_AmtR");
        _rbs_map.put("Ej2", "E1_BetI");
        _rbs_map.put("B0",  "B1_BM3R1");
        _rbs_map.put("B1",  "B2_BM3R1");
        _rbs_map.put("Bj2", "B3_BM3R1");
        _rbs_map.put("Hj2", "H1_HlyIIR");
        _rbs_map.put("I1",  "I1_IcaRA");
        _rbs_map.put("Lj2", "L1_LitR");
        _rbs_map.put("N0",  "N1_LmrA");
        _rbs_map.put("P0",  "P1_PhlF");
        _rbs_map.put("P1",  "P2_PhlF");
        _rbs_map.put("Pj2", "P3_PhlF");
        _rbs_map.put("R0",  "R1_PsrA");
        _rbs_map.put("Q2",  "Q1_QacR");
        _rbs_map.put("Q0",  "Q2_QacR");
        _rbs_map.put("S1",  "S1_SrpR");
        _rbs_map.put("S2",  "S2_SrpR");
        _rbs_map.put("S3",  "S3_SrpR");
        _rbs_map.put("S0",  "S4_SrpR");

    }   
    
    private HashMap<String, String> _rbs_map = new HashMap();
    private HashMap<String, String> _input_map = new HashMap();
    
    private Logger log = Logger.getLogger( this.getClass().getPackage().getName() );

}