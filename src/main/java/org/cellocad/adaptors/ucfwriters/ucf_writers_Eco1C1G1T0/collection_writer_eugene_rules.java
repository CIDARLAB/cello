package org.cellocad.adaptors.ucfwriters.ucf_writers_Eco1C1G1T0;


import java.util.ArrayList;
import java.util.HashMap;
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

        ArrayList<String> repressors = new ArrayList<String>();
        repressors.add("PhlF");
        repressors.add("SrpR");
        repressors.add("BM3R1");
        repressors.add("BetI");
        repressors.add("AmeR");
        repressors.add("HlyIIR");
        repressors.add("AmtR");


        for(int i=0; i<repressors.size() - 1; ++i) {
            for(int j=i+1; j<repressors.size(); ++j) {
                gate_rules.add("gate_" + repressors.get(i) + " BEFORE " + "gate_" + repressors.get(j));
            }
        }

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

        for(String p: roadblock_promoters) {
            part_rules.add("STARTSWITH " + p);
        }


        HashMap<String, Integer> priorities = new HashMap<String, Integer>();
        priorities.put("pTac",    -6);
        priorities.put("pBAD",    -5);
        priorities.put("pSrpR",    -4);
        priorities.put("pPhlF",    -3);
        priorities.put("pBM3R1",   -2);
        priorities.put("pTet",     2);
        priorities.put("pAmtR",     3);
        priorities.put("pBetI",     4);
        priorities.put("pHlyIIR",   6);
        priorities.put("pAmeR",     8);



        for(String p1: priorities.keySet()) {
            for(String p2: priorities.keySet()) {

                if(p1.equals(p2)) {
                    continue;
                }

                if(priorities.get(p1) < priorities.get(p2)) {
                    //part_rules.add(p1 + " BEFORE " + p2);
                }

            }


        }
        return part_rules;
    }
}
