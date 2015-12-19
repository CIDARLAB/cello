package org.cellocad.adaptors.ucfwriters.ucf_writers_Eco2C2G2T2;

import org.cellocad.MIT.dnacompiler.Util;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

public class collection_writer_response_functions extends collection_writer {

    @Override
    public ArrayList<Map> getObjects() {

        ArrayList<Map> objects = new ArrayList<>();

        ArrayList<ArrayList<String>> gates_tokens = Util.fileTokenizer(getRootPath() + "/resources/csv_gate_libraries/gates_Eco2C2G2T2.csv");


        int counter = 0;
        for(ArrayList<String> gate_tokens: gates_tokens) {

            if(counter == 0) {
                counter++;
                continue;
            }

            Map obj = new LinkedHashMap();
            obj.put("collection", "response_functions");

            String promoter_name = gate_tokens.get(1);
            String promoter_seq = gate_tokens.get(2);
            String sgRNA_name = gate_tokens.get(3);
            String sgRNA_seq = gate_tokens.get(4);
            String terminator_name = gate_tokens.get(5);
            String terminator_seq = gate_tokens.get(6);


            String equation = gate_tokens.get(7);
            Double a = Double.valueOf(gate_tokens.get(8));
            Double b = Double.valueOf(gate_tokens.get(9));

            String gate_name = sgRNA_name;

            LinkedHashMap map_a = new LinkedHashMap();
            LinkedHashMap map_b = new LinkedHashMap();

            map_a.put("name", "a");
            map_a.put("value", a);
            map_b.put("name", "b");
            map_b.put("value", b);


            ArrayList<Map> parameters = new ArrayList<Map>();
            parameters.add(map_a);
            parameters.add(map_b);


            ArrayList<Map> variables = new ArrayList<Map>();
            LinkedHashMap map_var = new LinkedHashMap();
            map_var.put("name", "x");
            map_var.put("off_threshold", null);
            map_var.put("on_threshold",  null);
            variables.add(map_var);

            obj.put("gate_name", gate_name);
            obj.put("equation", equation);
            obj.put("variables", variables);
            obj.put("parameters", parameters);


            objects.add(obj);
        }

        return objects;

    }
}
