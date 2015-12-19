package org.cellocad.MIT.dnacompiler;
/**
 * Created by Bryan Der on 3/26/14.
 */

import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.HashMap;

/**Class*************************************************************

 Synopsis    [  ]

 ***********************************************************************/


public class BuildCircuitsByReloading extends BuildCircuits {


    public BuildCircuitsByReloading(Args options, GateLibrary gate_library, Roadblock roadblock) {
        super(options, gate_library, roadblock);
    }

    /**Function*************************************************************

     Synopsis    [ ]

     ***********************************************************************/
    @Override
    public void buildCircuits(){

        log.info("build circuit by reloading " + get_options().get_fin_reload());
        setGate_name_map();

        LogicCircuit lc = get_unassigned_lc();

        ArrayList<String> file_lines = Util.fileLines(get_options().get_fin_reload());
        ArrayList<String> keep_lines = new ArrayList<>();
        for(String s: file_lines) {

            if(s.contains("Logic Circuit")) {
                continue;
            }

            if(s.contains("Circuit_score")) {
                break;
            }

            keep_lines.add(s);
        }

        for(String s: keep_lines) {
            String[] tokens = s.split("\\s+");
            String gate_type = tokens[0].trim();
            String gate_logic = tokens[1].trim();
            String gate_name = tokens[2].trim();
            String gate_index = tokens[3].trim();
            String gate_child_indexes = "";

            if(gate_name_map.containsKey(gate_name)) {
                gate_name = gate_name_map.get(gate_name);
            }

            //System.out.println("line s: " + s);
            //System.out.println("  " + gate_type + " " + gate_logic + " " + gate_name);

            if(!gate_type.equals("INPUT")) {
                gate_child_indexes = tokens[4].trim();
            }


            for(Gate g: lc.get_Gates()) {
                if(BooleanLogic.logicString(g.get_logics()).equals(gate_logic) && gate_type.equals(g.Type.toString())) {
                    System.out.println("matching gate " + g.Name + " with ");
                    System.out.println(gate_type + ".\n" + gate_logic + ".\n" + gate_name + ".\n" + gate_index + ".\n" + gate_child_indexes + ".\n");

                    g.Name = gate_name;
                }

            }

        }


        log.info(lc.printGraph());

        //fixInputOrder(lc);

        Evaluate.simulateLogic(lc);
        LogicCircuitUtil.setInputREU(lc, get_gate_library());
        System.out.println(lc.toString());

        //Evaluate.setNORGateParams(lc);

        for(Gate g: lc.get_logic_gates()) {
            if (get_gate_library().get_GATES_BY_NAME().containsKey(g.Name)) {
                System.out.println("Found " + g.Name);
            }
            else {
                System.out.println("did not find " + g.Name);
            }
        }


        Evaluate.evaluateCircuit(lc, get_gate_library(), get_options());
        for(Gate g: lc.get_Gates()) {
            Evaluate.evaluateGate(g, get_options());
        }


        Toxicity.evaluateCircuitToxicity(lc, get_gate_library());

        get_logic_circuits().add(lc);
        set_n_total_assignments(get_n_total_assignments() + 1);

    }




    private void setGate_name_map() {
        gate_name_map.put("NOR_an0-AmeR", "F1_AmeR");
        gate_name_map.put("NOR_js2-AmtR", "A1_AmtR");
        gate_name_map.put("NOR_an1-AmtR", "A1_AmtR");
        gate_name_map.put("NOR_js2-BetI", "E1_BetI");
        gate_name_map.put("NOR_an0-BM3R1", "B1_BM3R1");
        gate_name_map.put("NOR_an1-BM3R", "B2_BM3R1");
        gate_name_map.put("NOR_js2-BM3R", "B3_BM3R1");
        gate_name_map.put("NOR_js2-HlyIIR", "H1_HlyIIR");
        gate_name_map.put("NOR_an1-IcaRA", "I1_IcaRA");
        gate_name_map.put("NOR_js2-LitR", "L1_LitR");
        gate_name_map.put("NOR_x-LmrA", "N1_LmrA");
        gate_name_map.put("NOR_an0-PhlF", "P1_PhlF");
        gate_name_map.put("NOR_an1-PhlF", "P2_PhlF");
        gate_name_map.put("NOR_js2-PhlF", "P3_PhlF");
        gate_name_map.put("NOR_x-PsrA", "R1_PsrA");
        gate_name_map.put("NOR_an2-QacR", "Q1_QacR");
        gate_name_map.put("NOR_an1-QacR", "Q2_QacR");
        gate_name_map.put("NOR_an1-SrpR", "S1_SrpR");
        gate_name_map.put("NOR_an2-SrpR", "S2_SrpR");
        gate_name_map.put("NOR_an3-SrpR", "S3_SrpR");
        gate_name_map.put("NOR_an0-SrpR", "S4_SrpR");
    }



    private void fixInputOrder(LogicCircuit lc) {

        for(Gate g: lc.get_input_gates()) {

            if(g.Name.equals("pTac")) {
                ArrayList<Integer> i = new ArrayList<>();
                i.add(0);
                i.add(1);
                i.add(0);
                i.add(1);
                i.add(0);
                i.add(1);
                i.add(0);
                i.add(1);
                g.set_logics(i);
            }
            if(g.Name.equals("pTet")) {
                ArrayList<Integer> i = new ArrayList<>();
                i.add(0);
                i.add(0);
                i.add(1);
                i.add(1);
                i.add(0);
                i.add(0);
                i.add(1);
                i.add(1);
                g.set_logics(i);
            }
            if(g.Name.equals("pBAD")) {
                ArrayList<Integer> i = new ArrayList<>();
                i.add(0);
                i.add(0);
                i.add(0);
                i.add(0);
                i.add(1);
                i.add(1);
                i.add(1);
                i.add(1);
                g.set_logics(i);
            }

        }


        for(Gate g: lc.get_logic_gates()) {
            g.set_unvisited(true);
        }
        for(Gate g: lc.get_output_gates()) {
            g.set_unvisited(true);
        }

        Evaluate.simulateLogic(lc);

    }

    private HashMap<String, String> gate_name_map = new HashMap();

    private Logger log = Logger.getLogger( this.getClass().getPackage().getName() );

}
