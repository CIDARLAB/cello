package org.cellocad.adaptors.ucfwriters.ucf_writers_Eco3C3G3T3;


import org.cellocad.MIT.dnacompiler.Util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class collection_writer_gates extends collection_writer {

    @Override
    public ArrayList<Map> getObjects() {

        ArrayList<Map> objects = new ArrayList<>();

        String csv_file = getRootPath() + "/resources/csv_gate_libraries/gates_Eco1C1G1T1.csv";
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


            String regulator = gate_data.get(attributes_map.get("cds"));
            String group_name = gate_data.get(attributes_map.get("cds"));
            String gate_name = gate_data.get(attributes_map.get("name"));
            String gate_type = gate_data.get(attributes_map.get("type"));
            String system = "TetR";
            String color = "000000" + gate_data.get(attributes_map.get("color"));
            String hcolor = color.substring(color.length()-6, color.length());

            if(     gate_name.contains("LmrA") ||
                    gate_name.contains("PsrA") ||
                    gate_name.contains("QacR") ||
                    gate_name.contains("IcaRA") ||
                    gate_name.contains("LitR")
                    )
            {
                continue;
            }


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

        Map sicA_invF  = new LinkedHashMap();
        sicA_invF.put("collection", "gates");
        sicA_invF.put("regulator", "SicA_InvF");
        sicA_invF.put("group_name", "SicA_InvF");
        sicA_invF.put("gate_name", "SicA_InvF");
        sicA_invF.put("gate_type", "AND");
        sicA_invF.put("system", "Activator_Chaperone");
        sicA_invF.put("color_hexcode", "E83C96");

        Map exsC_exsDA = new LinkedHashMap();
        exsC_exsDA.put("collection", "gates");
        exsC_exsDA.put("regulator", "ExsC_ExsDA");
        exsC_exsDA.put("group_name", "ExsC_ExsDA");
        exsC_exsDA.put("gate_name", "ExsC_ExsDA");
        exsC_exsDA.put("gate_type", "AND");
        exsC_exsDA.put("system", "Activator_Chaperone");
        exsC_exsDA.put("color_hexcode", "66BC46");

        objects.add(sicA_invF);
        objects.add(exsC_exsDA);

        return objects;
    }
}
