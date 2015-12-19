package org.cellocad.adaptors.ucfwriters.ucf_writers_Eco2C2G2T2;


import org.cellocad.MIT.dnacompiler.Util;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

public class collection_writer_gate_parts extends collection_writer {

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

            String promoter_name = gate_tokens.get(1);
            String promoter_seq = gate_tokens.get(2);
            String sgRNA_name = gate_tokens.get(3);
            String sgRNA_seq = gate_tokens.get(4);
            String terminator_name = gate_tokens.get(5);
            String terminator_seq = gate_tokens.get(6);

            String gate_name = sgRNA_name;

            ArrayList<String> cassette_part_names = new ArrayList<String>();
            cassette_part_names.add(sgRNA_name);
            cassette_part_names.add(terminator_name);

            ArrayList<LinkedHashMap> expression_cassettes = new ArrayList<LinkedHashMap>();
            LinkedHashMap cassette = new LinkedHashMap();
            cassette.put("maps_to_variable", "x");
            cassette.put("cassette_parts", cassette_part_names);
            expression_cassettes.add(cassette);


            //promoter_name = "p"+rbs_name.substring(0,1);
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
