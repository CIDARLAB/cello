package org.cellocad.adaptors.ucfwriters.ucf_writers_LacI_TetR;


import org.cellocad.adaptors.ucfwriters.ucf_writers_Eco3C3G3T3.collection_writer;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

public class collection_writer_eugene_rules extends collection_writer {

    @Override
    public ArrayList<Map> getObjects() {

        ArrayList<Map> objects = new ArrayList<>();

        Map obj = new LinkedHashMap();
        obj.put("collection", "eugene_rules");
        obj.put("eugene_part_rules", getPartRules());
        obj.put("eugene_gate_rules", getGateRules());

        objects.add(obj);

        return objects;
    }


    public static ArrayList<String> getGateRules() {
        ArrayList<String> gate_rules = new ArrayList<String>();

        gate_rules.add("ALL_FORWARD");
        gate_rules.add("gate_mxiE AFTER gate_TetR");
        gate_rules.add("gate_mxiE AFTER gate_LacI");
        gate_rules.add("gate_exsDA AFTER gate_TetR");
        gate_rules.add("gate_exsDA AFTER gate_LacI");
        return gate_rules;
    }


    public static ArrayList<String> getPartRules() {

        ArrayList<String> part_rules = new ArrayList<String>();

        return part_rules;
    }
}
