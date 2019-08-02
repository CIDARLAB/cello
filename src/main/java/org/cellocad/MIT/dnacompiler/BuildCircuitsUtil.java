package org.cellocad.MIT.dnacompiler;

/**
 * Created by Bryan Der on 6/26/15.
 */

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class BuildCircuitsUtil {


    /**
     *
     * @param unique_lcs
     * @param gate_library
     * @param part_library
     * @return list of assigned circuits each of which has a different repressor-to-gate assignment (removes assignments that only vary the RBS)
     */
    public static ArrayList<LogicCircuit> getUniqueRepressorAssignments(ArrayList<LogicCircuit> unique_lcs, GateLibrary gate_library, PartLibrary part_library, Args _options) {

        HashMap<String, LogicCircuit> unique_repressor_assignments = new HashMap<>();
        for(int i=0; i<unique_lcs.size(); ++i) {

            LogicCircuit lc = unique_lcs.get(i);

            //bug fix for sequential circuit
            for(Gate g: lc.get_Gates()) {
                if(g.type == Gate.GateType.INPUT) {
                    continue;
                }

                for(Wire w: g.get_variable_wires().get("x")) {

                    for(Wire wi: lc.get_Wires()) {
                        if(w.index == wi.index) {
                            w.to = wi.to;
                        }
                    }
                }
            }



            //unique assignments are determined by the alphabetical ordering of transcription units

            PlasmidUtil.setGateParts(lc, gate_library, part_library);

            PlasmidUtil.setTxnUnits(lc, gate_library, _options);



            ArrayList<String> tus = new ArrayList<String>();

            String asn = "";

            for(Gate g: lc.get_logic_gates()) {
                Evaluate.refreshGateAttributes(g, gate_library);

                ArrayList<String> promoter_names = new ArrayList<>();
                String cds = "";

                //WARNING: hard-coded for NOR gates, which only have 1 txn unit per gate
                for(Part p: g.get_txn_units().get(0)) {
                    if(p.get_type().equalsIgnoreCase("promoter")) {
                        promoter_names.add( p.get_name() );
                    }
                    if(p.get_type().equalsIgnoreCase("cds")) {
                        cds = p.get_name();
                    }
                }

                Collections.sort(promoter_names);

                String tu = "";
                for(String s: promoter_names) {
                    tu += s;
                }
                tu += cds;


                tus.add(tu);
            }

            Collections.sort(tus);

            //txn unit string is determined by promoter names followed by CDS name
            asn = "";
            for(String s: tus) {
                asn += s + "_";
            }


            if(!unique_repressor_assignments.containsKey(asn)) {
                if(!asn.contains("LmrA") && !asn.contains("PsrA")) {
                    //System.out.println("Unique Assignment Name: " + asn);
                    unique_repressor_assignments.put(asn, lc);
                }
            }

        }


        return new ArrayList<>(unique_repressor_assignments.values());

    }


    /**
     * The txn unit names are promoter(s), RBS, CDS... RBS variants count as unique assignments
     *
     * @param unique_lcs
     * @param gate_library
     * @param part_library
     * @return
     */
    public static ArrayList<LogicCircuit> removeIdenticalTUs(ArrayList<LogicCircuit> unique_lcs, GateLibrary gate_library, PartLibrary part_library, Args _options) {

        HashMap<String, LogicCircuit> unique_repressor_assignments = new HashMap<>();
        for(int i=0; i<unique_lcs.size(); ++i) {

            LogicCircuit lc = unique_lcs.get(i);


            //bug fix for sequential circuit
            for(Gate g: lc.get_Gates()) {
                if(g.type == Gate.GateType.INPUT) {
                    continue;
                }

                for(Wire w: g.get_variable_wires().get("x")) {

                    for(Wire wi: lc.get_Wires()) {
                        if(w.index == wi.index) {
                            w.to = wi.to;
                        }
                    }
                }
            }


            PlasmidUtil.setGateParts(lc, gate_library, part_library);

            PlasmidUtil.setTxnUnits(lc, gate_library, _options);

            ArrayList<String> tus = new ArrayList<String>();

            String asn = "";

            for(Gate g: lc.get_logic_gates()) {
                Evaluate.refreshGateAttributes(g, gate_library);
                asn += g.name;

                ArrayList<String> promoter_names = new ArrayList<>();
                String rbs = "";
                String cds = "";

                for(Part p: g.get_txn_units().get(0)) {
                    if(p.get_type().equalsIgnoreCase("promoter")) {
                        promoter_names.add( p.get_name() );
                    }
                    if(p.get_type().equalsIgnoreCase("rbs")) {
                        rbs = p.get_name();
                    }
                    if(p.get_type().equalsIgnoreCase("cds")) {
                        cds = p.get_name();
                    }
                }

                Collections.sort(promoter_names);

                String tu = "";
                for(String s: promoter_names) {
                    tu += s;
                }
                tu += rbs;
                tu += cds;


                tus.add(tu);
            }

            Collections.sort(tus);

            asn = "";
            for(String s: tus) {
                asn += s + "_";
            }


            if(!unique_repressor_assignments.containsKey(asn)) {

                if(!asn.contains("LmrA") && !asn.contains("PsrA")) {

                    //System.out.println("Unique Assignment Name: " + asn);
                    unique_repressor_assignments.put(asn, lc);
                }
            }

        }



        return new ArrayList<>(unique_repressor_assignments.values());

    }


    public static void setGate_name_map() {
        _gate_name_map.put("NOR_an0-AmeR", "F1_AmeR");
        _gate_name_map.put("NOR_js2-AmtR", "A1_AmtR");
        _gate_name_map.put("NOR_an1-AmtR", "A1_AmtR");
        _gate_name_map.put("NOR_js2-BetI", "E1_BetI");
        _gate_name_map.put("NOR_an0-BM3R1", "B1_BM3R1");
        _gate_name_map.put("NOR_an1-BM3R1", "B2_BM3R1");
        _gate_name_map.put("NOR_js2-BM3R1", "B3_BM3R1");
        _gate_name_map.put("NOR_js2-HlyIIR", "H1_HlyIIR");
        _gate_name_map.put("NOR_an1-IcaRA", "I1_IcaRA");
        _gate_name_map.put("NOR_js2-LitR", "L1_LitR");
        _gate_name_map.put("NOR_x-LmrA", "N1_LmrA");
        _gate_name_map.put("NOR_an0-PhlF", "P1_PhlF");
        _gate_name_map.put("NOR_an1-PhlF", "P2_PhlF");
        _gate_name_map.put("NOR_js2-PhlF", "P3_PhlF");
        _gate_name_map.put("NOR_x-PsrA", "R1_PsrA");
        _gate_name_map.put("NOR_an2-QacR", "Q1_QacR");
        _gate_name_map.put("NOR_an1-QacR", "Q2_QacR");
        _gate_name_map.put("NOR_an1-SrpR", "S1_SrpR");
        _gate_name_map.put("NOR_an2-SrpR", "S2_SrpR");
        _gate_name_map.put("NOR_an3-SrpR", "S3_SrpR");
        _gate_name_map.put("NOR_an0-SrpR", "S4_SrpR");
        _gate_name_map.put("NOR_js2-SrpR", "S2_SrpR");
    }



    public static HashMap<String, String> _gate_name_map = new HashMap();


}
