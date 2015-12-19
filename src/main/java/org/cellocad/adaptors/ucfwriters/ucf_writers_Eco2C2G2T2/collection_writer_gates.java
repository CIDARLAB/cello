package org.cellocad.adaptors.ucfwriters.ucf_writers_Eco2C2G2T2;


import org.cellocad.MIT.dnacompiler.Util;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

public class collection_writer_gates extends collection_writer {

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
            //System.out.println(gate_tokens.toString());

            Map obj = new LinkedHashMap();
            obj.put("collection", "gates");

            String promoter_name = gate_tokens.get(1);
            String promoter_seq = gate_tokens.get(2);
            String sgRNA_name = gate_tokens.get(3);
            String sgRNA_seq = gate_tokens.get(4);
            String terminator_name = gate_tokens.get(5);
            String terminator_seq = gate_tokens.get(6);
            String equation = gate_tokens.get(7);
            String a = gate_tokens.get(8);
            String b = gate_tokens.get(9);

            String group_name = sgRNA_name.substring(0, 8);

            System.out.println(group_name);

            obj.put("regulator", sgRNA_name);
            obj.put("group_name", group_name);
            obj.put("gate_name", sgRNA_name);
            obj.put("gate_type", "NOR");
            obj.put("system", "CRISPRi");

            objects.add(obj);


        }

        return objects;

    }


}
