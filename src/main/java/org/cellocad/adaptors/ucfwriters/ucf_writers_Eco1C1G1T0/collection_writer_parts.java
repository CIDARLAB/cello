package org.cellocad.adaptors.ucfwriters.ucf_writers_Eco1C1G1T0;


import org.cellocad.MIT.dnacompiler.Util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class collection_writer_parts extends collection_writer {

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

            if(     gate_name.contains("LmrA") ||
                    gate_name.contains("PsrA") ||
                    gate_name.contains("QacR") ||
                    gate_name.contains("IcaRA") ||
                    gate_name.contains("LitR")
                    )
            {
                continue;
            }

            String promoter_name = gate_data.get(attributes_map.get("promoter"));
            String promoter_seq = gate_data.get(attributes_map.get("promoterDNA"));
            String insulator_name = gate_data.get(attributes_map.get("ribozyme"));
            String insulator_seq = gate_data.get(attributes_map.get("ribozymeDNA"));
            String rbs_name = gate_data.get(attributes_map.get("rbs"));
            String rbs_seq = gate_data.get(attributes_map.get("rbsDNA"));
            String cds_name = gate_data.get(attributes_map.get("cds"));
            String cds_seq = gate_data.get(attributes_map.get("cdsDNA"));
            String terminator_name = gate_data.get(attributes_map.get("terminator"));
            String terminator_seq = gate_data.get(attributes_map.get("terminatorDNA"));

            Map ribozyme = new LinkedHashMap();
            ribozyme.put("collection", "parts"); //tag
            ribozyme.put("type", "ribozyme");
            ribozyme.put("name", insulator_name);
            ribozyme.put("dnasequence", insulator_seq);

            Map rbs = new LinkedHashMap();
            rbs.put("collection", "parts"); //tag
            rbs.put("type", "rbs");
            rbs.put("name", rbs_name);
            rbs.put("dnasequence", rbs_seq);

            Map cds = new LinkedHashMap();
            cds.put("collection", "parts"); //tag
            cds.put("type", "cds");
            cds.put("name", cds_name);
            cds.put("dnasequence", cds_seq);

            Map terminator = new LinkedHashMap();
            terminator.put("collection", "parts"); //tag
            terminator.put("type", "terminator");
            terminator.put("name", terminator_name);
            terminator.put("dnasequence", terminator_seq);



            //promoter_name = "p"+rbs_name.substring(0,1);
            Map promoter = new LinkedHashMap();
            promoter.put("collection", "parts"); //tag
            promoter.put("type", "promoter");
            promoter.put("name", promoter_name);
            promoter.put("dnasequence", promoter_seq);

            objects.add(ribozyme);
            objects.add(rbs);
            objects.add(cds);
            objects.add(terminator);
            objects.add(promoter);
        }

        String scar_file = getRootPath() + "/resources/csv_gate_libraries/scars.csv";
        ArrayList<ArrayList<String>> scar_tokens = Util.fileTokenizer(scar_file);

        for(int i=1; i<scar_tokens.size(); ++i) {
            ArrayList<String> scar_data = scar_tokens.get(i);
            String scar_name = scar_data.get(2);
            String scar_seq = scar_data.get(3);

            Map scar = new LinkedHashMap();
            scar.put("collection", "parts"); //tag
            scar.put("type", "scar");
            scar.put("name", scar_name);
            scar.put("dnasequence", scar_seq);

            objects.add(scar);
        }

    return objects;

}

}
