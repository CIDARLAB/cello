package org.cellocad.MIT.tandem_promoter;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.cellocad.MIT.dnacompiler.Args;
import org.cellocad.MIT.dnacompiler.Util;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;

public class TandemPromoterAdditiveJSONWriter {


    public static void main(String[] args) {

        Args options = new Args();
        String path = options.get_home() + "/resources/data/tandem_promoters/";

        //use the response functions of the NOT gates
        String sp_filename = "single_promoters.json";
        String sp_filepath = path + sp_filename;

        //need to know which tandem promoters we are interested in
        String tp_filename = "tandem_promoters.json";
        String tp_filepath = path + tp_filename;


        JSONObject sps = TandemPromoterJSONReader.get_json_object(sp_filepath, new ArrayList<String>());
        JSONObject tps = TandemPromoterJSONReader.get_json_object(tp_filepath, new ArrayList<String>());

        JSONObject additive_tps = new JSONObject();

        for (Iterator iterator = tps.keySet().iterator(); iterator.hasNext();) {
            String tp_name = (String) iterator.next();
            System.out.println(tp_name);


            JSONObject tp = (JSONObject) tps.get(tp_name);
            String gateA = (String) tp.get("gateA");
            String gateB = (String) tp.get("gateB");

            JSONObject spA = (JSONObject) sps.get(gateA);
            JSONObject spB = (JSONObject) sps.get(gateB);

            if(!sps.containsKey(gateA) || !sps.containsKey(gateB)) {
                System.out.println("missing " + tp_name);
                System.exit(-1);
            }



            JSONObject spA_params = (JSONObject) spA.get("params");
            JSONObject spB_params = (JSONObject) spB.get("params");

            Double ymax_A = (Double) spA_params.get("ymax");
            Double ymin_A = (Double) spA_params.get("ymin");
            Double K_A = (Double) spA_params.get("K");
            Double n_A = (Double) spA_params.get("n");

            Double ymax_B = (Double) spB_params.get("ymax");
            Double ymin_B = (Double) spB_params.get("ymin");
            Double K_B = (Double) spB_params.get("K");
            Double n_B = (Double) spB_params.get("n");



            JSONObject params_x0 = new JSONObject();
            JSONObject params_x1 = new JSONObject();
            JSONObject params_0x = new JSONObject();
            JSONObject params_1x = new JSONObject();


            String typeA = "";
            String typeB = "";

            if(gateA.contains("pTac") || gateA.contains("pTet") || gateA.contains("pBAD") || gateA.contains("pLuxStar")) {
                typeA = "INPUT";
            }
            else {
                typeA = "NOT";
            }

            if(gateB.contains("pTac") || gateB.contains("pTet") || gateB.contains("pBAD") || gateB.contains("pLuxStar")) {
                typeB = "INPUT";
            }
            else {
                typeB = "NOT";
            }


            boolean add_min = false;
            if(add_min) {
                Double ymax_x0 = ymax_A + ymin_B;
                Double ymin_x0 = ymin_A + ymin_B;
                Double K_x0 = K_A;
                Double n_x0 = n_A;

                Double ymax_x1 = ymax_A + ymax_B;
                Double ymin_x1 = ymin_A + ymax_B;
                Double K_x1 = K_A;
                Double n_x1 = n_A;

                Double ymax_0x = ymax_B + ymin_A;
                Double ymin_0x = ymin_B + ymin_A;
                Double K_0x = K_B;
                Double n_0x = n_B;

                Double ymax_1x = ymax_A + ymax_B;
                Double ymin_1x = ymax_A + ymin_B;
                Double K_1x = K_B;
                Double n_1x = n_B;

                params_x0.put("ymax", ymax_x0);
                params_x0.put("ymin", ymin_x0);
                params_x0.put("K", K_x0);
                params_x0.put("n", n_x0);

                params_x1.put("ymax", ymax_x1);
                params_x1.put("ymin", ymin_x1);
                params_x1.put("K", K_x1);
                params_x1.put("n", n_x1);

                params_0x.put("ymax", ymax_0x);
                params_0x.put("ymin", ymin_0x);
                params_0x.put("K", K_0x);
                params_0x.put("n", n_0x);

                params_1x.put("ymax", ymax_1x);
                params_1x.put("ymin", ymin_1x);
                params_1x.put("K", K_1x);
                params_1x.put("n", n_1x);
            }
            else {
                Double ymax_x0 = ymax_A;
                Double ymin_x0 = ymin_A;
                Double K_x0 = K_A;
                Double n_x0 = n_A;

                Double ymax_x1 = ymax_A + ymax_B;
                Double ymin_x1 = ymax_B;
                Double K_x1 = K_A;
                Double n_x1 = n_A;

                Double ymax_0x = ymax_B;
                Double ymin_0x = ymin_B;
                Double K_0x = K_B;
                Double n_0x = n_B;

                Double ymax_1x = ymax_A + ymax_B;
                Double ymin_1x = ymax_A;
                Double K_1x = K_B;
                Double n_1x = n_B;

                params_x0.put("ymax", ymax_x0);
                params_x0.put("ymin", ymin_x0);
                params_x0.put("K", K_x0);
                params_x0.put("n", n_x0);

                params_x1.put("ymax", ymax_x1);
                params_x1.put("ymin", ymin_x1);
                params_x1.put("K", K_x1);
                params_x1.put("n", n_x1);

                params_0x.put("ymax", ymax_0x);
                params_0x.put("ymin", ymin_0x);
                params_0x.put("K", K_0x);
                params_0x.put("n", n_0x);

                params_1x.put("ymax", ymax_1x);
                params_1x.put("ymin", ymin_1x);
                params_1x.put("K", K_1x);
                params_1x.put("n", n_1x);
            }

            JSONObject add_tp = new JSONObject();

            add_tp.put("gateA", gateA);
            add_tp.put("gateB", gateB);

            String hill_activation = "ymin+(ymax-ymin)/(1.0+(K/x)^n)";
            String hill_repression = "ymin+(ymax-ymin)/(1.0+(x/K)^n)";

            String equationA = "";
            if(typeA.equals("INPUT")) {
                equationA = hill_activation;
            }
            else if(typeA.equals("NOT")){
                equationA = hill_repression;
            }

            String equationB = "";
            if(typeB.equals("INPUT")) {
                equationB = hill_activation;
            }
            else if(typeB.equals("NOT")){
                equationB = hill_repression;
            }


            add_tp.put("x0_equation", equationA);
            add_tp.put("x1_equation", equationA);
            add_tp.put("0x_equation", equationB);
            add_tp.put("1x_equation", equationB);

            add_tp.put("x0_params", params_x0);
            add_tp.put("x1_params", params_x1);
            add_tp.put("0x_params", params_0x);
            add_tp.put("1x_params", params_1x);

            additive_tps.put(tp_name, add_tp);

        }

        String out_json = path + "additive_promoters.json";

        Gson gson = new GsonBuilder().disableHtmlEscaping().setPrettyPrinting().create();
        String jsontext = gson.toJson(additive_tps);
        Util.fileWriter(out_json, jsontext, false);

        System.out.println("finished");






    }

}
