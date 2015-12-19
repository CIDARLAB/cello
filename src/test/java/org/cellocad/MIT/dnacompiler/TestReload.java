package org.cellocad.MIT.dnacompiler;


import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.StringTokenizer;

public class TestReload {

    String file = "/Users/peng/Dropbox (MIT)/people/alec/SCIENCE/reload/0xFE/0xFE_A000_logic_circuit.txt";

    @Test
    public void testReload() {
        BuildCircuitsUtil.setGate_name_map();
        HashMap<String, String> gate_name_map = BuildCircuitsUtil._gate_name_map;

        ArrayList<Gate> gates = new ArrayList();
        ArrayList<Wire> wires = new ArrayList();

        ArrayList<String> file_lines = Util.fileLines(file);
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

            if(!gate_type.equals("INPUT")) {
                gate_child_indexes = columns[4].trim();
            }

            Gate g = new Gate();
            if(gate_type.equals("NOT")) {
                g.Type = Gate.GateType.NOT;
            }
            if(gate_type.equals("NOR")) {
                g.Type = Gate.GateType.NOR;
            }
            if(gate_type.equals("INPUT")) {
                g.Type = Gate.GateType.INPUT;
                g.set_unvisited(false);
            }
            if(gate_type.equals("OUTPUT")) {
                g.Type = Gate.GateType.OUTPUT;
            }
            if(gate_type.equals("OUTPUT_OR")) {
                g.Type = Gate.GateType.OUTPUT_OR;
            }
            g.Name = gate_name;
            g.Index = Integer.valueOf(gate_index);

            g.set_logics(new ArrayList<Integer>());
            String[] logic_string = gate_logic.split("");
            for(String a: logic_string) {
                g.get_logics().add(Integer.valueOf(a));
            }

            gate_map.put(g.Index, g);

            if(!gate_child_indexes.isEmpty()) {
                ArrayList<Integer> fanin_indexes = new ArrayList<>();
                StringTokenizer st = new StringTokenizer(gate_child_indexes, " \t\n\r\f,()");
                while (st.hasMoreTokens()) {
                    Integer index = Integer.valueOf(st.nextToken());
                    fanin_indexes.add(index);
                }
                fanin_map.put(g.Index, fanin_indexes);
            }
        }

        for(Integer i: gate_map.keySet()) {
            Gate g = gate_map.get(i);
//            System.out.println(g.Name + " " + fanin_map.get(g.Index));

            if(fanin_map.containsKey(g.Index)) {
                int counter = 0;

                for (Integer fanin_index : fanin_map.get(g.Index)) {
                    Wire w = new Wire();
                    w.From = g;
                    w.To = gate_map.get(fanin_index);
                    w.Index = wires.size();

                    if(counter > 0) {
                        w.Next = wires.get(wires.size()-1);
                    }
                    g.Outgoing = w;

                    wires.add(w);
                    counter++;
                }
            }
        }

        for(Gate g: gate_map.values()) {
            gates.add(g);
        }


        LogicCircuit lc = new LogicCircuit(gates, wires);
        Evaluate.simulateLogic(lc);

    }



}
