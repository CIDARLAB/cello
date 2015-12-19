package org.cellocad.adaptors.ucfwriters.ucf_writers_LacI_TetR;

import org.cellocad.MIT.dnacompiler.Util;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

public class collection_writer_parts extends collection_writer {

    @Override
    public ArrayList<Map> getObjects() {

        ArrayList<Map> objects = new ArrayList<>();

        ArrayList<ArrayList<String>> gates_tokens = Util.fileTokenizer(getRootPath() + "/resources/csv_gate_libraries/parts_LacI_TetR.csv");

        for(ArrayList<String> gate_tokens: gates_tokens) {

            if(gate_tokens.isEmpty()) {
                continue;
            }

            Map obj = new LinkedHashMap();
            obj.put("collection", "parts");

            String name = gate_tokens.get(0);
            String type = gate_tokens.get(1);
            String seq = gate_tokens.get(2);

            Map part = new LinkedHashMap();
            part.put("collection", "parts"); //tag
            part.put("name", name);
            part.put("type", type);
            part.put("dnasequence", seq);


            objects.add(part);
        }


        return objects;
    }

}
