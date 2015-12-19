package org.cellocad.adaptors.ucfwriters.ucf_writers_Eco1C1G1T1;


import org.cellocad.MIT.dnacompiler.Util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class collection_writer_gate_parts extends collection_writer {

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

            String gate_name = gate_data.get(attributes_map.get("name"));
            String ribozyme_name = gate_data.get(attributes_map.get("ribozyme"));
            String rbs_name = gate_data.get(attributes_map.get("rbs"));
            String cds_name = gate_data.get(attributes_map.get("cds"));
            String terminator_name = gate_data.get(attributes_map.get("terminator"));
            String promoter_name = gate_data.get(attributes_map.get("promoter"));

            ArrayList<String> cassette_part_names = new ArrayList<String>();
            cassette_part_names.add(ribozyme_name);
            cassette_part_names.add(rbs_name);
            cassette_part_names.add(cds_name);
            cassette_part_names.add(terminator_name);

            ArrayList<LinkedHashMap> expression_cassettes = new ArrayList<LinkedHashMap>();
            LinkedHashMap cassette = new LinkedHashMap();
            cassette.put("maps_to_variable", "x");
            cassette.put("cassette_parts", cassette_part_names);
            expression_cassettes.add(cassette);

            Map obj = new LinkedHashMap();
            obj.put("collection", "gate_parts"); //tag
            obj.put("gate_name", gate_name);
            obj.put("expression_cassettes", expression_cassettes);
            obj.put("promoter", promoter_name);

            objects.add(obj);
        }


        return objects;

    }
}
