package org.cellocad.MIT.dnacompiler;
/**
 * Created by Bryan Der on 3/26/14.
 */

import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.StringTokenizer;

/**Class*************************************************************

 Synopsis    [  ]

 ***********************************************************************/


public class BuildCircuitsReload extends BuildCircuits {


    public BuildCircuitsReload(Args options, GateLibrary gate_library, Roadblock roadblock) {
        super(options, gate_library, roadblock);
    }

    /**Function*************************************************************

     Synopsis    [ ]

     ***********************************************************************/
    @Override
    public void buildCircuits(){
        logger = Logger.getLogger(getThreadDependentLoggername());
        logger.info("Reloading logic circuit...");

        BuildCircuitsUtil.setGate_name_map();
        HashMap<String, String> gate_name_map = BuildCircuitsUtil._gate_name_map;

        ArrayList<Gate> gates = new ArrayList();
        ArrayList<Wire> wires = new ArrayList();

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


        LinkedHashMap<Integer, Gate> gate_map = new LinkedHashMap<>();
        LinkedHashMap<Integer, ArrayList<Integer>> fanin_map = new LinkedHashMap<>();


        for(String s: keep_lines) {



            String[] columns = s.split("\\s+");
            String gate_type = columns[0].trim();
            String gate_logic = columns[1].trim();
            String gate_name = columns[2].trim();
            String gate_index = columns[3].trim();
            String gate_child_indexes = "";

            if(gate_name_map.containsKey(gate_name)) {
                gate_name = gate_name_map.get(gate_name);
            }
            if(gate_name.contains("input_")) {
                gate_name = gate_name.split("input_")[1];
            }
            if(gate_name.contains("output_")) {
                gate_name = gate_name.split("output_")[1];
            }

            if(!gate_type.equals("INPUT")) {
                gate_child_indexes = columns[4].trim();
            }

            Gate g = new Gate();
            if(gate_type.equals("NOT")) {
                g.type = Gate.GateType.NOT;
            }
            if(gate_type.equals("NOR")) {
                g.type = Gate.GateType.NOR;
            }
            if(gate_type.equals("INPUT")) {
                g.type = Gate.GateType.INPUT;
                g.set_unvisited(false);
            }
            if(gate_type.equals("OUTPUT")) {
                g.type = Gate.GateType.OUTPUT;
            }
            if(gate_type.equals("OUTPUT_OR")) {
                g.type = Gate.GateType.OUTPUT_OR;
            }
            g.name = gate_name;
            g.index = Integer.valueOf(gate_index);


            g.set_logics(new ArrayList<Integer>());
            String[] logic_string = gate_logic.split("");
            for(String a: logic_string) {
                if(a.equals("0") || a.equals("1")) {
                    g.get_logics().add(Integer.valueOf(a));
                }
            }
            logger.info(g.get_logics().toString());



            gate_map.put(g.index, g);

            if(!gate_child_indexes.isEmpty()) {
                ArrayList<Integer> fanin_indexes = new ArrayList<>();
                StringTokenizer st = new StringTokenizer(gate_child_indexes, " \t\n\r\f,()");
                while (st.hasMoreTokens()) {
                    Integer index = Integer.valueOf(st.nextToken());
                    fanin_indexes.add(index);
                }
                fanin_map.put(g.index, fanin_indexes);
            }
        }

        for(Integer i: gate_map.keySet()) {
            Gate g = gate_map.get(i);

            if(fanin_map.containsKey(g.index)) {
                int counter = 0;

                for (Integer fanin_index : fanin_map.get(g.index)) {
                    Wire w = new Wire();
                    w.from = g;
                    w.to = gate_map.get(fanin_index);
                    w.index = wires.size();

                    if(counter > 0) {
                        w.next = wires.get(wires.size()-1);
                    }
                    g.outgoing = w;

                    wires.add(w);
                    counter++;
                }
            }
        }

        for(Gate g: gate_map.values()) {
            gates.add(g);

            if(get_gate_library().get_GATES_BY_NAME().containsKey(g.name)) {
                logger.info("Found " + g.name);
            }
            else {
                logger.info("Missing " + g.name);
            }
        }


        LogicCircuit lc = new LogicCircuit(gates, wires);
        Evaluate.simulateLogic(lc);

        LogicCircuitUtil.setInputRPU(lc, get_gate_library());
        Evaluate.evaluateCircuit(lc, get_gate_library(), get_options());

        get_logic_circuits().add(lc);

    }


    private Logger logger  = Logger.getLogger(getClass());

}
