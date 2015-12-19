package org.cellocad.adaptors.ucfwriters.ucf_writers_Eco1C1G1T1;


import org.cellocad.MIT.dnacompiler.Util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class collection_writer_gates extends collection_writer {

    @Override
    public ArrayList<Map> getObjects() {

        ArrayList<Map> objects = new ArrayList<>();

        String csv_file = getRootPath() + "/resources/csv_gate_libraries/gates_Eco1C1G1T1b.csv";
        System.out.println("reading " + csv_file);

        ArrayList<ArrayList<String>> csv_tokens = Util.fileTokenizer(csv_file);
        System.out.println(csv_tokens.size());


        HashMap<String, Integer> attributes_map = new HashMap<>();

        for(int i=0; i<csv_tokens.get(0).size(); ++i) {
            String attribute = csv_tokens.get(0).get(i);
            attributes_map.put(attribute, i);
        }


        for(int i=1; i<csv_tokens.size(); ++i) {

            ArrayList<String> gate_data = csv_tokens.get(i);

//            System.out.println(gate_data);
            String regulator = gate_data.get(attributes_map.get("cds"));
            String group_name = gate_data.get(attributes_map.get("cds"));
            String gate_name = gate_data.get(attributes_map.get("name"));
            String gate_type = gate_data.get(attributes_map.get("type"));
            String system = "TetR";
            String color = "000000" + gate_data.get(attributes_map.get("color"));
            String hcolor = color.substring(color.length()-6, color.length());

            Map obj = new LinkedHashMap();
            obj.put("collection", "gates");
            obj.put("regulator", regulator);
            obj.put("group_name", group_name);
            obj.put("gate_name", gate_name);
            obj.put("gate_type", "NOR");
            obj.put("system", "TetR");
            obj.put("color_hexcode", hcolor);

            objects.add(obj);
        }


        return objects;
    }
}
