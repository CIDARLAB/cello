package org.cellocad.MIT.tandem_promoter;

/**
 * Created by Bryan Der on 9/1/15.
 */

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.cellocad.MIT.dnacompiler.Args;
import org.cellocad.MIT.dnacompiler.Util;
import org.json.simple.JSONObject;

import java.util.ArrayList;



public class SinglePromoterCSV2JSON {



        public static void main(String[] args) {

            Args options = new Args();

            String path = options.get_home() + "/resources/data/tandem_promoters/";

            String in4_csv  = path + "single_promoters.csv";
            String out_json = path + "single_promoters.json";

            ArrayList<ArrayList<String>> in4_tokens = Util.fileTokenizer(in4_csv);


            /**
             * 4-input tandem promoter data trumps 2-input tandem promoter data.
             */


            tokens2json(in4_tokens, "in4bb"); //will overwrite



            /**
             * Write a single json file for tandem promoter data from both 4-input and 2-input backbones.
             */
            Gson gson = new GsonBuilder().disableHtmlEscaping().setPrettyPrinting().create();
            String jsontext = gson.toJson(all_fns);
            Util.fileWriter(out_json, jsontext, false);
        }


        public static void tokens2json(ArrayList<ArrayList<String>> tokens_list, String source) {

            String hill_activation = "ymin+(ymax-ymin)/(1.0+(K/x)^n)";
            String hill_repression = "ymin+(ymax-ymin)/(1.0+(x/K)^n)";


            for(ArrayList<String> tokens: tokens_list) {

                String promoter_name = tokens.get(0);
                Double ymax = Double.valueOf(tokens.get(1));
                Double ymin = Double.valueOf(tokens.get(2));
                Double K = Double.valueOf(tokens.get(3));
                Double n = Double.valueOf(tokens.get(4));

                String equation_name = "equation";
                String params_name = "params";

                String equation = "";
                if(promoter_name.equals("pTac") || promoter_name.equals("pTet") || promoter_name.equals("pBAD") || promoter_name.equals("pLuxStar")) {
                    equation = hill_activation;
                }
                else {
                    equation = hill_repression;
                }

                JSONObject params = new JSONObject();
                params.put("ymax", ymax);
                params.put("ymin", ymin);
                params.put("K", K);
                params.put("n", n);



                JSONObject tp_obj = new JSONObject();

                tp_obj.put("source", source);
                tp_obj.put("promoter", promoter_name);
                tp_obj.put(params_name, params);
                tp_obj.put(equation_name, equation);

                all_fns.put(promoter_name, tp_obj);
            }


        }

        public static JSONObject all_fns = new JSONObject();

}
