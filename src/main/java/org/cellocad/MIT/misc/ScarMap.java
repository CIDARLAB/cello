package org.cellocad.MIT.misc;

import org.cellocad.MIT.dnacompiler.Util;

import java.util.*;

public class ScarMap {

    public static void main(String[] args) {

        HashSet<String> all_txn_units = new HashSet<String>();

        HashSet<String> output_txn_units = new HashSet<String>();

        ArrayList<String> lines = Util.fileLines(args[0]);

        for(String line: lines) {

            List<String> tokens = Arrays.asList(line.split(","));

            //System.out.println("current: " + tokens.toString());


            ArrayList<String> txn_units = new ArrayList<String>();
            ArrayList<String> this_output_txn_units = new ArrayList<String>();

            String txn_unit = "";

            for(int i=0; i<tokens.size(); ++i) {
                String s = tokens.get(i);
                if(s.contains("FP")) {
                    continue;
                }
                if(!s.startsWith("p")) {
                    txn_unit += s;
                    //all_txn_units.add(txn_unit);
                    txn_units.add(txn_unit);

                    //System.out.println("txn_unit " + txn_unit);

                    txn_unit = "";
                }
                else {
                    txn_unit += s + ".";
                }
            }



            sortGatesByCloningSlot(txn_units);


            ArrayList<String> txn_units_with_scars = addScars(txn_units);


            String tu_order = "";
            for(String tu: txn_units_with_scars) {
                tu_order += tu + "\t";
            }
            //System.out.println(tu_order);

            for(String tu: txn_units_with_scars) {
                all_txn_units.add(tu);
            }

            String construct_tus = "CONSTRUCT_TUS ";
            for(String tu: txn_units_with_scars) {
                construct_tus += tu + ",";
            }
            for(String tu: this_output_txn_units) {
                construct_tus += tu + ",";
            }
            System.out.println(construct_tus);


        }



        System.exit(-1);


        for(String s: all_txn_units) {
            System.out.println(s);
        }

        for(String s: output_txn_units) {
            System.out.println(s);
        }

        System.out.println(all_txn_units.size() + " " + output_txn_units.size());

    }


    public static ArrayList<String> addScars(ArrayList<String> txn_units) {



        ArrayList<String> txn_units_with_5scar = new ArrayList<String>();
        ArrayList<String> txn_units_with_scars = new ArrayList<String>();

        for(int i=0; i<txn_units.size(); ++i) {

            String tu = txn_units.get(i);

            String repressor = tu.split("_")[1];

            if(i==0) {
                String ws = "1_" + tu;
                txn_units_with_5scar.add(ws);
            }
            else {
                String ws = getCloningSlotPriorities().get(repressor) + "_" + tu;
                txn_units_with_5scar.add(ws);
            }
        }


        for(int i=0; i<txn_units_with_5scar.size()-1; ++i) {
            String tu      = txn_units_with_5scar.get(i);
            String tu_next = txn_units_with_5scar.get(i+1);


            String scar3 = tu_next.split("_")[0];


            String ws = tu + "_" + scar3;
            txn_units_with_scars.add(ws);
        }

        String last_tu = txn_units_with_5scar.get(txn_units_with_5scar.size()-1) + "_11";
        txn_units_with_scars.add(last_tu);

        return txn_units_with_scars;
    }


    public static void sortGatesByCloningSlot(ArrayList<String> txn_units) {

        final HashMap<String, Integer> priorities = getCloningSlotPriorities();

        Collections.sort(txn_units,
                new Comparator<String>() {
                    public int compare(String g1, String g2){

                        if(!g1.contains("_") || !g2.contains("_")) {
                            return 0;
                        }

                        String repressor1 = g1.split("_")[1];
                        String repressor2 = g2.split("_")[1];

                        if(!priorities.containsKey(repressor1) || !priorities.containsKey(repressor2)) {
                            return 0;
                        }

                        Integer index1 = priorities.get(repressor1);
                        Integer index2 = priorities.get(repressor2);

                        if ( index1 > index2 ){
                            return 1;
                        }else {
                            return -1;
                        }
                    }
                }
        );
    }

    public static HashMap<String, Integer> getCloningSlotPriorities() {
        HashMap<String, Integer> priorities = new HashMap<String, Integer>();

        priorities.put("AmeR",   1);
        priorities.put("HlyIIR", 2);
        priorities.put("AmtR",   3);
        priorities.put("LitR",   4);
        priorities.put("IcaRA",  5);
        priorities.put("BetI",   6);
        priorities.put("QacR",   7);
        priorities.put("BM3R1",  8);
        priorities.put("PhlF",   9);
        priorities.put("SrpR",  10);

        return priorities;
    }
}
