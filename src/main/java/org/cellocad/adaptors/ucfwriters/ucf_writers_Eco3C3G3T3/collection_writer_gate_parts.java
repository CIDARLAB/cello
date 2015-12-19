package org.cellocad.adaptors.ucfwriters.ucf_writers_Eco3C3G3T3;


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

            if(     gate_name.contains("LmrA") ||
                    gate_name.contains("PsrA") ||
                    gate_name.contains("QacR") ||
                    gate_name.contains("IcaRA") ||
                    gate_name.contains("LitR")
                    )
            {
                continue;
            }

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

        //Need to parse AND_gate_parts.txt
        ArrayList<ArrayList<String>> gate_part_tokens = Util.fileTokenizer(getRootPath() + "/resources/data/and_gates/AND_gate_parts.txt");
        ArrayList<String> sicA_gate_parts = gate_part_tokens.get(0);
        ArrayList<String> exsC_gate_parts = gate_part_tokens.get(1);

        String s_name        = sicA_gate_parts.get(1);
        String s_promoter    = sicA_gate_parts.get(2);
        String s1_ribozyme   = sicA_gate_parts.get(3);
        String s1_rbs        = sicA_gate_parts.get(4);
        String s1_cds        = sicA_gate_parts.get(5);
        String s1_terminator = sicA_gate_parts.get(6);
        String s2_ribozyme   = sicA_gate_parts.get(7);
        String s2_rbs        = sicA_gate_parts.get(8);
        String s2_cds        = sicA_gate_parts.get(9);
        String s2_terminator = sicA_gate_parts.get(10);

        String e_name        = exsC_gate_parts.get(1);
        String e_promoter    = exsC_gate_parts.get(2);
        String e1_ribozyme   = exsC_gate_parts.get(3);
        String e1_rbs        = exsC_gate_parts.get(4);
        String e1_cds        = exsC_gate_parts.get(5);
        String e1_terminator = exsC_gate_parts.get(6);
        String e2_ribozyme   = exsC_gate_parts.get(7);
        String e2_rbs        = exsC_gate_parts.get(8);
        String e2_cds        = exsC_gate_parts.get(9);
        String e2_terminator = exsC_gate_parts.get(10);


        ArrayList<String> s1_cassette_part_names = new ArrayList<String>();
        s1_cassette_part_names.add(s1_ribozyme);
        s1_cassette_part_names.add(s1_rbs);
        s1_cassette_part_names.add(s1_cds);
        s1_cassette_part_names.add(s1_terminator);

        ArrayList<String> s2_cassette_part_names = new ArrayList<String>();
        s2_cassette_part_names.add(s2_ribozyme);
        s2_cassette_part_names.add(s2_rbs);
        s2_cassette_part_names.add(s2_cds);
        s2_cassette_part_names.add(s2_terminator);

        ArrayList<LinkedHashMap> s_expression_cassettes = new ArrayList<LinkedHashMap>();

        LinkedHashMap s1_cassette = new LinkedHashMap();
        s1_cassette.put("maps_to_variable", "x1");
        s1_cassette.put("cassette_parts", s1_cassette_part_names);
        s_expression_cassettes.add(s1_cassette);

        LinkedHashMap s2_cassette = new LinkedHashMap();
        s2_cassette.put("maps_to_variable", "x2");
        s2_cassette.put("cassette_parts", s2_cassette_part_names);
        s_expression_cassettes.add(s2_cassette);

        Map s = new LinkedHashMap();
        s.put("collection", "gate_parts"); //tag
        s.put("gate_name", s_name);
        s.put("expression_cassettes", s_expression_cassettes);
        s.put("promoter", s_promoter);


        ArrayList<String> e1_cassette_part_names = new ArrayList<String>();
        e1_cassette_part_names.add(e1_ribozyme);
        e1_cassette_part_names.add(e1_rbs);
        e1_cassette_part_names.add(e1_cds);
        e1_cassette_part_names.add(e1_terminator);

        ArrayList<String> e2_cassette_part_names = new ArrayList<String>();
        e2_cassette_part_names.add(e2_ribozyme);
        e2_cassette_part_names.add(e2_rbs);
        e2_cassette_part_names.add(e2_cds);
        e2_cassette_part_names.add(e2_terminator);

        ArrayList<LinkedHashMap> e_expression_cassettes = new ArrayList<LinkedHashMap>();

        LinkedHashMap e1_cassette = new LinkedHashMap();
        e1_cassette.put("maps_to_variable", "x1");
        e1_cassette.put("cassette_parts", e1_cassette_part_names);
        e_expression_cassettes.add(e1_cassette);

        LinkedHashMap e2_cassette = new LinkedHashMap();
        e2_cassette.put("maps_to_variable", "x2");
        e2_cassette.put("cassette_parts", e2_cassette_part_names);
        e_expression_cassettes.add(e2_cassette);

        Map e = new LinkedHashMap();
        e.put("collection", "gate_parts"); //tag
        e.put("gate_name", e_name);
        e.put("expression_cassettes", e_expression_cassettes);
        e.put("promoter", e_promoter);


        objects.add(s);
        objects.add(e);


        return objects;

    }
}
