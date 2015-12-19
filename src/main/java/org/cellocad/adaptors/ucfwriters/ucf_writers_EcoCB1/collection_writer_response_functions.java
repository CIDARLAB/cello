package org.cellocad.adaptors.ucfwriters.ucf_writers_EcoCB1;

import org.cellocad.MIT.dnacompiler.Util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class collection_writer_response_functions extends collection_writer {

    @Override
    public ArrayList<Map> getObjects() {

        ArrayList<Map> objects = new ArrayList<>();

        String csv_file = getRootPath() + "/resources/csv_gate_libraries/gates_EcoCB1_v2.csv";
        System.out.println("reading " + csv_file);

        ArrayList<ArrayList<String>> csv_tokens = Util.fileTokenizer(csv_file);


        HashMap<String, Integer> attributes_map = new HashMap<>();

        for(int i=0; i<csv_tokens.get(0).size(); ++i) {
            String attribute = csv_tokens.get(0).get(i);
            attributes_map.put(attribute, i);
        }


        for(int i=1; i<csv_tokens.size(); ++i) {

            ArrayList<String> gate_data = csv_tokens.get(i);

            Map obj = new LinkedHashMap();
            obj.put("collection", "response_functions");

            String gate_name = gate_data.get(attributes_map.get("name"));
            String equation = gate_data.get(attributes_map.get("equation"));

            Double ymax = Double.valueOf(gate_data.get(attributes_map.get("ymax")));
            Double ymin = Double.valueOf(gate_data.get(attributes_map.get("ymin")));
            Double K    = Double.valueOf(gate_data.get(attributes_map.get("K")));
            Double n    = Double.valueOf(gate_data.get(attributes_map.get("n")));
            Double IL   = Double.valueOf(gate_data.get(attributes_map.get("IL")));
            Double IH   = Double.valueOf(gate_data.get(attributes_map.get("IH")));

            LinkedHashMap map_ymax = new LinkedHashMap();
            LinkedHashMap map_ymin = new LinkedHashMap();
            LinkedHashMap map_K = new LinkedHashMap();
            LinkedHashMap map_n = new LinkedHashMap();

            map_ymax.put("name", "ymax");
            map_ymax.put("value", ymax);
            map_ymin.put("name", "ymin");
            map_ymin.put("value", ymin);
            map_K.put("name", "K");
            map_K.put("value", K);
            map_n.put("name", "n");
            map_n.put("value", n);


            ArrayList<Map> parameters = new ArrayList<Map>();
            parameters.add(map_ymax);
            parameters.add(map_ymin);
            parameters.add(map_K);
            parameters.add(map_n);


            ArrayList<Map> variables = new ArrayList<Map>();
            LinkedHashMap map_var = new LinkedHashMap();
            map_var.put("name", "x");
            map_var.put("off_threshold", IL);
            map_var.put("on_threshold",  IH);
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
