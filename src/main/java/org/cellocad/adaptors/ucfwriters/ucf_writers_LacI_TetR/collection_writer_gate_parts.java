package org.cellocad.adaptors.ucfwriters.ucf_writers_LacI_TetR;


import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

public class collection_writer_gate_parts extends collection_writer {

    @Override
    public ArrayList<Map> getObjects() {

        ArrayList<Map> objects = new ArrayList<>();

        ArrayList<String> laci_cassette_part_names = new ArrayList<String>();
        laci_cassette_part_names.add("rbs_LacI");
        laci_cassette_part_names.add("LacI");
        laci_cassette_part_names.add("terminator_LacI");

        ArrayList<LinkedHashMap> laci_expression_cassettes = new ArrayList<LinkedHashMap>();
        LinkedHashMap laci_cassette = new LinkedHashMap();
        laci_cassette.put("maps_to_variable", "x");
        laci_cassette.put("cassette_parts", laci_cassette_part_names);
        laci_expression_cassettes.add(laci_cassette);

        Map laci_obj = new LinkedHashMap();
        laci_obj.put("collection", "gate_parts"); //tag
        laci_obj.put("gate_name", "const_LacI");
        laci_obj.put("expression_cassettes", laci_expression_cassettes);
        laci_obj.put("promoter", "pLac");


        ArrayList<String> tetr_cassette_part_names = new ArrayList<String>();
        tetr_cassette_part_names.add("rbs_TetR");
        tetr_cassette_part_names.add("TetR");
        tetr_cassette_part_names.add("terminator_TetR");

        ArrayList<LinkedHashMap> tetr_expression_cassettes = new ArrayList<LinkedHashMap>();
        LinkedHashMap tetr_cassette = new LinkedHashMap();
        tetr_cassette.put("maps_to_variable", "x");
        tetr_cassette.put("cassette_parts", tetr_cassette_part_names);
        tetr_expression_cassettes.add(tetr_cassette);

        Map tetr_obj = new LinkedHashMap();
        tetr_obj.put("collection", "gate_parts"); //tag
        tetr_obj.put("gate_name", "const_TetR");
        tetr_obj.put("expression_cassettes", tetr_expression_cassettes);
        tetr_obj.put("promoter", "pTet");


        objects.add(laci_obj);
        objects.add(tetr_obj);


        return objects;

    }

}
