package org.cellocad.adaptors.ucfwriters.ucf_writers_Eco2C2G2T2;

import org.cellocad.MIT.dnacompiler.Util;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

public class collection_writer_parts extends collection_writer {

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
            obj.put("collection", "parts");

            String promoter_name = gate_tokens.get(1);
            String promoter_seq = gate_tokens.get(2);
            String sgRNA_name = gate_tokens.get(3);
            String sgRNA_seq = gate_tokens.get(4);
            String terminator_name = gate_tokens.get(5);
            String terminator_seq = gate_tokens.get(6);


            Map sgrna = new LinkedHashMap();
            sgrna.put("collection", "parts"); //tag
            sgrna.put("type", "sgRNA");
            sgrna.put("name", sgRNA_name);
            sgrna.put("dnasequence", sgRNA_seq);

            Map terminator = new LinkedHashMap();
            terminator.put("collection", "parts"); //tag
            terminator.put("type", "terminator");
            terminator.put("name", terminator_name);
            terminator.put("dnasequence", terminator_seq);

            Map promoter = new LinkedHashMap();
            promoter.put("collection", "parts"); //tag
            promoter.put("type", "promoter");
            promoter.put("name", promoter_name);
            promoter.put("dnasequence", promoter_seq);


            objects.add(sgrna);
            objects.add(terminator);
            objects.add(promoter);


        }


        return objects;

        /*String sql_scars = "SELECT * FROM Basic WHERE type = '" + "scar" + "'";
        DBI dbi = new DBI(sql_scars);

        try {

            while (dbi.get_rs().next()) {

                String scar_name = dbi.get_rs().getString("name");
                String scar_seq = dbi.get_rs().getString("sequence");

                Map scar = new LinkedHashMap();
                scar.put("collection", "parts"); //tag
                scar.put("type", "scar");
                scar.put("name", scar_name);
                scar.put("dnasequence", scar_seq);
                ConstraintFileWriter.JSONPrettyPrint(scar, "resources/UCF/Eco2C2G2T2.parts.json", true);
            }

        }
        catch (SQLException se) {
            se.printStackTrace();
        }*/

    }

}
