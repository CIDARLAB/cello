package org.cellocad.MIT.tandem_promoter;

import org.cellocad.MIT.dnacompiler.Args;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;


public class TandemPromoterJSONReader {


    public static void main(String[] args) {
        Args options = new Args();

        String path = options.get_home() + "/resources/data/tandem_promoters/";

        /*
        String in2_json = path + "in2bb_tandem_promoters.json";
        String in4_json = path + "in4bb_tandem_promoters.json";

        JSONObject tp_in2 = get_json_object(in2_json);
        JSONObject tp_in4 = get_json_object(in4_json);

        if(tp_in2 != null) {
            System.out.println("read tp_in2");
        }
        if(tp_in4 != null) {
            System.out.println("read tp_in4");
        }
        */


        String json_file = path + "tandem_promoters.json";

        ArrayList<String> required_keys = new ArrayList<String>();
        required_keys.add("gateA");
        required_keys.add("gateB");
        required_keys.add("0x_equation");
        required_keys.add("1x_equation");
        required_keys.add("x0_equation");
        required_keys.add("x1_equation");
        required_keys.add("0x_params");
        required_keys.add("1x_params");
        required_keys.add("x0_params");
        required_keys.add("x1_params");

        JSONObject tp = get_json_object(json_file, required_keys);

        if(tp != null) {
            System.out.println("read tp");
        }

    }



    public static JSONObject get_json_object(String filename, ArrayList<String> required_keys) {

        try {
            FileReader reader = new FileReader(filename);

            JSONParser jsonParser = new JSONParser();
            try {
                JSONObject jsonObject = (JSONObject) jsonParser.parse(reader);


                //Gson gson = new GsonBuilder().disableHtmlEscaping().setPrettyPrinting().create();
                //String jsontext = gson.toJson(jsonObject);

                if(!valid_tandem_promoter_object(jsonObject, required_keys)) {
                    return null;
                }
                else {
                    return jsonObject;
                }


            } catch (IOException e) {
                e.printStackTrace();
            } catch (ParseException e) {
                e.printStackTrace();
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return null;
    }


    public static boolean valid_tandem_promoter_object(JSONObject jsonObject, ArrayList<String> required_keys) {

        for(Iterator iterator = jsonObject.keySet().iterator(); iterator.hasNext();) {
            String key = (String) iterator.next();

            JSONObject obj = (JSONObject) jsonObject.get(key);


            for(String k: required_keys) {
                if(!obj.containsKey(k)) {

                    System.out.println(obj.toJSONString() + " missing " + k);
                    return false;
                }
            }

        }

        return true;
    }
}
