package org.cellocad.MIT.dnacompiler;
/**
 * Created by Bryan Der on 3/26/14.
 */


import org.cellocad.MIT.dnacompiler.Gate.GateType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;


/**
 * Netlist is a text representation of a wiring diagram
 */
public class Netlist{


    public static void setBioNetlist(LogicCircuit lc, boolean add_index) {

        String all_lines = "";

        for(int i = lc.get_Gates().size()-1; i>=0; --i) {
            Gate g = lc.get_Gates().get(i);

            String s = "";

            String name = g.name;

            if(add_index && g.type != GateType.INPUT)
                name += "_" + g.rIndex;

            name = name.replaceAll("~\\|", "NOR");
            name = name.replaceAll("~",    "NOT");

            s += String.format("%-16s", name);

            if(g.type == GateType.INPUT) {
                s += BooleanLogic.logicString(g.get_logics());
            }

            ArrayList<String> promoter_names = new ArrayList<String>();

            for(Gate child: g.getChildren()) {
                String promoter_name = child.get_regulable_promoter().get_name();
                promoter_names.add(promoter_name);
            }

            sortPromoterNames(promoter_names);

            for(String pname: promoter_names) {
                s += String.format("%-10s", pname);
            }

            all_lines += s + "\n";

        }

        lc.set_netlist(all_lines);
    }



    public static void sortPromoterNames(ArrayList<String> promoter_names) {
        Collections.sort(promoter_names,
                new Comparator<String>() {
                    public int compare(String p1, String p2) {
                        int result = 0;

                        if(!getPromoterPriorities().containsKey(p1)) {
                            return result;
                        }
                        if(!getPromoterPriorities().containsKey(p2)) {
                            return result;
                        }

                        if ( getPromoterPriorities().get(p1) < getPromoterPriorities().get(p2) ){
                            result = -1;
                        }
                        else {
                            result = 1;
                        }
                        return result;
                    }
                }
        );
    }

    public static HashMap<String, Integer> getPromoterPriorities() {
        HashMap<String, Integer> priorities = new HashMap<String, Integer>();

        priorities.put("pTac",     -6);
        priorities.put("pBAD",     -5);
        priorities.put("pSrpR",    -4);
        priorities.put("pPhlF",    -3);
        priorities.put("pBM3R1",   -2);
        priorities.put("pQacR",    -1);

        priorities.put("pTet",      2);
        priorities.put("pAmtR",     3);
        priorities.put("pBetI",     4);
        priorities.put("pIcaRA",    5);
        priorities.put("pHlyIIR",   6);
        priorities.put("pLitR",     7);
        priorities.put("pAmeR",     8);

        return priorities;
    }


    public static String getNetlist(LogicCircuit lc) {


        String net = "";

        for(Gate g: lc.get_Gates()) {
            net += String.format("%5s", g.name) + ": " + g.type + "(";

            for(Wire w: lc.get_Wires()) {

                if(w.to != null && w.to.name.trim() == g.name.trim()) {
                    net += w.name + ", ";
                    break;
                }
            }

            if ( (g.outgoing != null) && (g.outgoing.to != null)){
                net += g.outgoing.name + ", ";

                Wire w = g.outgoing;
                while(w.next != null && w.next.to != null) {
                    net += w.next.name + ", ";
                    w = w.next;
                }
            }

            if(net.endsWith(", ")) {
                net = net.substring(0, net.length() - 2);
            }

            net += ");\n";

        }

        return net;

    }

}
