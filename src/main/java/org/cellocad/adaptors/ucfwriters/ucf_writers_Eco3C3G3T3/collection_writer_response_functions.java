package org.cellocad.adaptors.ucfwriters.ucf_writers_Eco3C3G3T3;

import org.cellocad.MIT.dnacompiler.Util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class collection_writer_response_functions extends collection_writer {

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

            Map obj = new LinkedHashMap();
            obj.put("collection", "response_functions");

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

            String equation = gate_data.get(attributes_map.get("equation"));

            Double ymax = Double.valueOf(gate_data.get(attributes_map.get("ymax")));
            Double ymin = Double.valueOf(gate_data.get(attributes_map.get("ymin")));
            Double K    = Double.valueOf(gate_data.get(attributes_map.get("K")));
            Double n    = Double.valueOf(gate_data.get(attributes_map.get("n")));
            Double IL   = Double.valueOf(gate_data.get(attributes_map.get("IL")));
            Double IH   = Double.valueOf(gate_data.get(attributes_map.get("IH")));

            LinkedHashMap map_ymax = new LinkedHashMap();
            LinkedHashMap map_ymin = new LinkedHashMap();
            LinkedHashMap map_K = new LinkedHashMap();
            LinkedHashMap map_n = new LinkedHashMap();

            map_ymax.put("name", "ymax");
            map_ymax.put("value", ymax);
            map_ymin.put("name", "ymin");
            map_ymin.put("value", ymin);
            map_K.put("name", "K");
            map_K.put("value", K);
            map_n.put("name", "n");
            map_n.put("value", n);


            ArrayList<Map> parameters = new ArrayList<Map>();
            parameters.add(map_ymax);
            parameters.add(map_ymin);
            parameters.add(map_K);
            parameters.add(map_n);


            ArrayList<Map> variables = new ArrayList<Map>();
            LinkedHashMap map_var = new LinkedHashMap();
            map_var.put("name", "x");
            map_var.put("off_threshold", IL);
            map_var.put("on_threshold",  IH);
            variables.add(map_var);


            obj.put("gate_name", gate_name);
            obj.put("equation", equation);
            obj.put("variables", variables);
            obj.put("parameters", parameters);

            objects.add(obj);
        }



        //SicA sicA_
        LinkedHashMap sicA_map_ymax = new LinkedHashMap();
        LinkedHashMap sicA_map_ymin = new LinkedHashMap();
        LinkedHashMap sicA_map_K1 = new LinkedHashMap();
        LinkedHashMap sicA_map_K2 = new LinkedHashMap();
        LinkedHashMap sicA_map_n = new LinkedHashMap();

        sicA_map_ymax.put("name", "ymax");
        sicA_map_ymax.put("value", 6.0);
        sicA_map_ymin.put("name", "ymin");
        sicA_map_ymin.put("value", 0.02);
        sicA_map_K1.put("name", "K1");
        sicA_map_K1.put("value", 0.5);
        sicA_map_K2.put("name", "K2");
        sicA_map_K2.put("value", 0.5);
        sicA_map_n.put("name", "n");
        sicA_map_n.put("value", 2.0);

        ArrayList<Map> sicA_parameters = new ArrayList<Map>();
        sicA_parameters.add(sicA_map_ymax);
        sicA_parameters.add(sicA_map_ymin);
        sicA_parameters.add(sicA_map_K1);
        sicA_parameters.add(sicA_map_K2);
        sicA_parameters.add(sicA_map_n);

        ArrayList<Map> sicA_variables = new ArrayList<Map>();
        LinkedHashMap sicA_map_var_x1 = new LinkedHashMap();
        sicA_map_var_x1.put("name", "x1");
        sicA_map_var_x1.put("off_threshold", 1.0);
        sicA_map_var_x1.put("on_threshold",  1.5);
        sicA_variables.add(sicA_map_var_x1);
        LinkedHashMap sicA_map_var_x2 = new LinkedHashMap();
        sicA_map_var_x2.put("name", "x2");
        sicA_map_var_x2.put("off_threshold", 1.0);
        sicA_map_var_x2.put("on_threshold",  1.5);
        sicA_variables.add(sicA_map_var_x2);


        Map sicA_invF = new LinkedHashMap();
        sicA_invF.put("collection", "response_functions");
        sicA_invF.put("gate_name", "SicA_InvF");
        sicA_invF.put("equation", "ymin + (ymax-ymin) * (x1^n / (K1^n + x1^n)) * (x2^n / (K2^n + x2^n))");
        sicA_invF.put("variables",  sicA_variables);
        sicA_invF.put("parameters", sicA_parameters);



        LinkedHashMap exsC_map_ymax = new LinkedHashMap();
        LinkedHashMap exsC_map_ymin = new LinkedHashMap();
        LinkedHashMap exsC_map_K1 = new LinkedHashMap();
        LinkedHashMap exsC_map_K2 = new LinkedHashMap();
        LinkedHashMap exsC_map_n = new LinkedHashMap();

        exsC_map_ymax.put("name", "ymax");
        exsC_map_ymax.put("value", 2.0);
        exsC_map_ymin.put("name", "ymin");
        exsC_map_ymin.put("value", 0.008);
        exsC_map_K1.put("name", "K1");
        exsC_map_K1.put("value", 0.2);
        exsC_map_K2.put("name", "K2");
        exsC_map_K2.put("value", 0.03);
        exsC_map_n.put("name", "n");
        exsC_map_n.put("value", 2.0);

        ArrayList<Map> exsC_parameters = new ArrayList<Map>();
        exsC_parameters.add(exsC_map_ymax);
        exsC_parameters.add(exsC_map_ymin);
        exsC_parameters.add(exsC_map_K1);
        exsC_parameters.add(exsC_map_K2);
        exsC_parameters.add(exsC_map_n);

        ArrayList<Map> exsC_variables = new ArrayList<Map>();
        LinkedHashMap exsC_map_var_x1 = new LinkedHashMap();
        exsC_map_var_x1.put("name", "x1");
        exsC_map_var_x1.put("off_threshold", 1.0);
        exsC_map_var_x1.put("on_threshold",  2.0);
        exsC_variables.add(exsC_map_var_x1);
        LinkedHashMap exsC_map_var_x2 = new LinkedHashMap();
        exsC_map_var_x2.put("name", "x2");
        exsC_map_var_x2.put("off_threshold", 0.1);
        exsC_map_var_x2.put("on_threshold",  0.3);
        exsC_variables.add(exsC_map_var_x2);

        Map exsC_exsDA = new LinkedHashMap();
        exsC_exsDA.put("collection", "response_functions");
        exsC_exsDA.put("gate_name", "ExsC_ExsDA");
        exsC_exsDA.put("equation", "ymin + (ymax-ymin) * (x1^n / (K1^n + x1^n)) * (x2^n / (K2^n + x2^n))");
        exsC_exsDA.put("variables",  exsC_variables);
        exsC_exsDA.put("parameters", exsC_parameters);

        objects.add(sicA_invF);
        objects.add(exsC_exsDA);




        return objects;
    }
}
