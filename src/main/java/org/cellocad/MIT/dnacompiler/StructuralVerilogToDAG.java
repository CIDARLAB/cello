package org.cellocad.MIT.dnacompiler;

import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.StringTokenizer;

/**
 * Created by Bryan Der on 8/5/15.
 */
public class StructuralVerilogToDAG {


    public static LogicCircuit createDAG(String fin_verilog) {


        ArrayList<Wire> wires = new ArrayList<>();
        LinkedHashMap<String, Gate> gate_map = new LinkedHashMap<>();

        ArrayList<String> input_names = new ArrayList<>();
        ArrayList<String> output_names = new ArrayList<>();


        ArrayList<String> verilog_lines = Util.fileLines(fin_verilog);

        ArrayList<String> gate_lines = new ArrayList<>();


        for(String line: verilog_lines) {


            if(line.startsWith("module") ) {
                ArrayList<String> tokens = new ArrayList<String>();
                StringTokenizer st = new StringTokenizer(line, " \t\n\r\f,();");
                while (st.hasMoreTokens()) {
                    tokens.add(st.nextToken());
                }

                boolean is_output = false;

                for(String token: tokens) {
                    if(is_output) {
                        if(token.equals("input")) {
                            break;
                        }
                        output_names.add(token);
                    }

                    if(token.equals("output")) {
                        is_output = true;
                    }
                }

                boolean is_input = false;

                for(String token: tokens) {
                    if(is_input) {
                        if(token.equals("output")) {
                            break;
                        }
                        input_names.add(token);
                    }

                    if(token.equals("input")) {
                        is_input = true;
                    }
                }

            }


            //Wire lines?
            if(line.contains("not") || line.contains("nor")) {
                gate_lines.add(line);
            }
        }


        for(String input_name: input_names) {
            Gate g = new Gate();
            g.Name = input_name;
            g.Type = Gate.GateType.INPUT;
            gate_map.put(input_name, g);
            g.set_unvisited(false);
        }
        for(String output_name: output_names) {
            Gate g = new Gate();
            g.Name = output_name;
            g.Type = Gate.GateType.OUTPUT;
            gate_map.put(output_name, g);
        }


        int wire_count = 0;

        for(int gi=0; gi<gate_lines.size(); ++gi) {

            String line = gate_lines.get(gi);
            String gate_name = "g" + (gi+1);
            Integer gate_index = (gi+1);


            ArrayList<String> tokens = new ArrayList<String>();
            StringTokenizer st = new StringTokenizer(line, " \t\n\r\f,()");
            while (st.hasMoreTokens()) {
                tokens.add(st.nextToken());
            }

            String gate_type = tokens.get(0);
            String fanout = tokens.get(1);
            ArrayList<String> fanin = new ArrayList<>();
            for(int i=2; i<tokens.size()-1; ++i) {
                fanin.add(tokens.get(i));
            }

            Gate g = new Gate();
            g.Name = gate_name;
            g.Index = gate_index;

            //Check only not and nor?
            if(gate_type.equals("not")) {
                g.Type = Gate.GateType.NOT;
            }
            if(gate_type.equals("nor")) {
                g.Type = Gate.GateType.NOR;
            }

            gate_map.put(g.Name, g);


            for(String output_name: output_names) {
                if(fanout.equals(output_name)) {

                }
            }



            Wire prev = null;

            for(int i=0; i<fanin.size(); ++i) {

                String fanin_name = fanin.get(i);

                Wire w = new Wire();
                w.Name = fanin_name;
                w.From = g;
                w.Index = wire_count;


                if(i==0) {
                    g.Outgoing = w;
                }
                else {
                    prev.Next = w;
                }

                prev = w;

                wire_count++;

                wires.add(w);
            }

        }





        for(String output_name: output_names) {
            Gate g = gate_map.get(output_name);

            for(Wire w: wires) {
                if(w.Name.equals(output_name)) {
                    g.Outgoing = w;
                }
            }
        }





        for(int gi=0; gi<gate_lines.size(); ++gi) {

            String line = gate_lines.get(gi);

            ArrayList<String> tokens = new ArrayList<String>();
            StringTokenizer st = new StringTokenizer(line, " \t\n\r\f,()");
            while (st.hasMoreTokens()) {
                tokens.add(st.nextToken());
            }

            String fanout_name = tokens.get(1);

            String gate_name = "g" + (gi+1);

            Gate g = gate_map.get(gate_name);


            for(Wire w: wires) {
                if(w.Name.equals(fanout_name)) {
                    w.To = g;
                }
                else {
                    for(String input_name: input_names) {
                        if(w.Name.equals(input_name)) {
                            w.To = gate_map.get(input_name);
                        }
                    }
                    for(String output_name: output_names) {
                        if(w.Name.equals(output_name)) {
                            w.From = gate_map.get(output_name);
                        }
                    }
                }
            }


            ArrayList<String> fanin = new ArrayList<>();
            for(int i=2; i<tokens.size()-1; ++i) {
                fanin.add(tokens.get(i));
            }

            for(int i=0; i<fanin.size(); ++i) {

                String fanin_name = fanin.get(i);

                for(String output_name: output_names) {
                    if(fanin_name.equals(output_name)) {
                        Wire w = new Wire();
                        w.Name = fanin_name;
                        w.Index = wire_count;
                        w.From = g;
                        w.To = gate_map.get(output_name).Outgoing.To;
                        wire_count++;
                        wires.add(w);
                    }
                }
            }

        }


        ArrayList<Gate> gates = new ArrayList<>(gate_map.values());

        for(int i=0; i<gates.size(); ++i) {
            Gate g = gates.get(i);
            g.Index = i;
        }

        LogicCircuit lc = new LogicCircuit(gates, wires);


        return lc;
    }

}
