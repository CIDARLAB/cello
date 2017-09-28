package org.cellocad.adaptors.ucfwriters.ucf_writers_Eco1C1G1T0;


import org.cellocad.MIT.dnacompiler.Gate;
import org.cellocad.MIT.dnacompiler.GateLibrary;
import org.cellocad.MIT.dnacompiler.Pair;
import org.cellocad.MIT.dnacompiler.Util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class collection_writer_gate_toxicity extends collection_writer {

    @Override
    public ArrayList<Map> getObjects() {

        ArrayList<Map> objects = new ArrayList<>();

        GateLibrary gate_library = new GateLibrary(0, 0);

        String path = getRootPath() + "/resources/data/nor_gates/NOR_Gates.txt";

        ArrayList<ArrayList<String>> gates_tokens = Util.fileTokenizer(path);
        for(ArrayList<String> gate_tokens: gates_tokens) {

            String gate_name = gate_tokens.get(0);
            String repressor_name = gate_name.split("_")[1];

            if(     gate_name.contains("LmrA") ||
                    gate_name.contains("PsrA") ||
                    gate_name.contains("QacR") ||
                    gate_name.contains("IcaRA") ||
                    gate_name.contains("LitR")
                    )
            {
                continue;
            }

            Gate g = new Gate();
            g.name = gate_name;
            g.regulator = repressor_name;
            gate_library.get_GATES_BY_NAME().put(g.name, g);
        }

        readToxTable(gate_library);


        for(Gate g: gate_library.get_GATES_BY_NAME().values()) {
            ArrayList<Pair> toxtable = g.get_toxtable();

            ArrayList<LinkedHashMap> titrations = new ArrayList<LinkedHashMap>();

            ArrayList<Double> in_rpus = new ArrayList<Double>();
            ArrayList<Double> od600s = new ArrayList<Double>();

            for (int i = 0; i < toxtable.size(); ++i) {

                Double inRPU = toxtable.get(i).get_x() / 4.23; //adjustment for ribozyme-insulated YFP RPU standard
                Double cellgrowth_value = toxtable.get(i).get_y();

                in_rpus.add(inRPU);
                od600s.add(cellgrowth_value);
            }

            LinkedHashMap obj = new LinkedHashMap();
            obj.put("collection", "gate_toxicity");
            obj.put("gate_name", g.name);

            obj.put("input", in_rpus);
            obj.put("growth", od600s);

            objects.add(obj);
        }

        return objects;

    }

    public static void readToxTable(GateLibrary gate_library) {
        HashMap<String, ArrayList<Double>> TOX_TABLE = new HashMap<String, ArrayList<Double>>();
        ArrayList<Double> TOX_TITRATION = new ArrayList<Double>();

        String path = getRootPath() + "/resources/data/nor_gates/toxicity/ToxicityTable.txt";

        ArrayList< ArrayList<String>> tokenized_list = Util.fileTokenizer(path);

        ///////////////////////
        //  Read titration RPU's
        ///////////////////////
        for(int j=1; j<tokenized_list.get(0).size(); ++j) {
            Double od = Double.valueOf(tokenized_list.get(0).get(j));
            TOX_TITRATION.add(od);
        }
        System.out.println( "\nToxicity table: " + String.format("%-10s", "titration") + TOX_TITRATION.toString());

        ///////////////////////
        //  Read OD measurements for each repressor
        ///////////////////////
        for(int i=1; i<tokenized_list.size(); ++i) {
            ArrayList<Double> repressor_OD = new ArrayList<Double>();
            for(int j=1; j<tokenized_list.get(i).size(); ++j) {
                Double od = Double.valueOf(tokenized_list.get(i).get(j));
                repressor_OD.add(od);
            }
            String repressor_name = tokenized_list.get(i).get(0);
            TOX_TABLE.put(repressor_name, repressor_OD);
            System.out.println( "Toxicity table: " + String.format("%-10s",repressor_name) + repressor_OD.toString());
        }


        for(Gate g: gate_library.get_GATES_BY_NAME().values()) {

            System.out.println("looking for " + g.regulator);
            ArrayList<Double> ods = TOX_TABLE.get(g.regulator);

            ArrayList<Pair> toxtable = new ArrayList<Pair>();
            for(int i=0; i<ods.size(); ++i) {
                toxtable.add(new Pair(TOX_TITRATION.get(i), ods.get(i)));
            }

            g.set_toxtable(toxtable);
        }
    }

}
