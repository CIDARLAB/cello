package org.cellocad.MIT.logic_motif_synthesis;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.*;

//import org.json.JSONArray;
//import org.json.JSONObject;
//This is to write the results to a file.
public class JsonWriter{
	public static void writeToJson(HashMap<String,ArrayList<Circuit>> foundTruthValues, String dir,double runtime,Date d, double currCost, HashSet<String> allowedOps){
		ArrayList<JSONObject> content = new ArrayList<JSONObject> ();
		
		JSONObject details = new JSONObject();
		details.put("collection", "run_details");
		details.put("operations_used", allowedOps);
		details.put("operation_costs", ConstantProperties.costPerOp);
		details.put("number_found", Integer.toString(foundTruthValues.size()));
		details.put("runtime", runtime);
		details.put("date_started", d);
		details.put("max_cost", currCost);
		content.add(details);
		
		ArrayList<String> truthValues = new ArrayList<String>(foundTruthValues.keySet());
		Collections.sort(truthValues);
		for (String tv:truthValues){
			JSONObject tvCircPair = new JSONObject();
			JSONArray circList = new JSONArray();
			for(Circuit circ:foundTruthValues.get(tv)){
				circList.add(circ.toString());
			}
			tvCircPair.put("truthValue", tv);
			tvCircPair.put("circuits", circList);
			content.add(tvCircPair);
		}
		prettyPrintJSONArray(content,dir, false);
	}
	public static void prettyPrintJSONArray(ArrayList<JSONObject> objects, String filepath, boolean append) {


        String json_array = "";


        for(int i=0; i<objects.size(); ++i) {


            JSONObject obj = objects.get(i);


            Gson gson = new GsonBuilder().disableHtmlEscaping().setPrettyPrinting().create();
            String json = gson.toJson(obj);


            json_array += json;



            if(i < objects.size() - 1) {
                json_array += ",\n";
            }



        }

        fileWriter(filepath, "[\n" + json_array + "\n]\n", append);
    }
	//Used to save the results in a file.
    public static void fileWriter(String outfile, String contents, boolean append) {
        try{
            BufferedWriter bw  = new BufferedWriter(new FileWriter(new File(outfile), append)); //append
            bw.write(contents);
            bw.close();
        }catch(Exception e) {
        }
    }
}