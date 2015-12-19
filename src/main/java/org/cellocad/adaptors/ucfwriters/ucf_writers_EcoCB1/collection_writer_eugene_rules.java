package org.cellocad.adaptors.ucfwriters.ucf_writers_EcoCB1;


import org.cellocad.adaptors.ucfwriters.ucf_writers_Eco1C1G1T1.collection_writer;

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
        return gate_rules;
    }


    public static ArrayList<String> getPartRules() {

        ArrayList<String> part_rules = new ArrayList<String>();

        ArrayList<String> roadblock_promoters = new ArrayList<String>();
        roadblock_promoters.add("pTac");
        roadblock_promoters.add("pBAD");
        roadblock_promoters.add("pPhlF");
        roadblock_promoters.add("pSrpR");
        roadblock_promoters.add("pBM3R1");
        roadblock_promoters.add("pQacR");

        for(String p: roadblock_promoters) {
            part_rules.add("STARTSWITH " + p);
        }
        
        return part_rules;
    }
}
