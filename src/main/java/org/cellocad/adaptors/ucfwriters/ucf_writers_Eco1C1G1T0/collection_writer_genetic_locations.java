package org.cellocad.adaptors.ucfwriters.ucf_writers_Eco1C1G1T0;


//import net.sf.json.JSONArray;
//import net.sf.json.JSONObject;

import org.cellocad.MIT.dnacompiler.Util;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

public class collection_writer_genetic_locations extends collection_writer {

    @Override
    public ArrayList<Map> getObjects() {

        ArrayList<Map> objects = new ArrayList<>();

        JSONObject l1 = new JSONObject();
        l1.put("name", "pAN1201");
        l1.put("file", Util.fileLines(getRootPath()+"/resources/plasmids/pAN1201.ape"));

        JSONObject l2 = new JSONObject();
        l2.put("name", "pAN4020");
        l2.put("file", Util.fileLines(getRootPath()+"/resources/plasmids/pAN4020.ape"));


        JSONArray locations = new JSONArray();
        locations.add(l1);
        locations.add(l2);

        JSONArray sensor_module_locations = new JSONArray();
        JSONObject sensor_obj = new JSONObject();
        sensor_obj.put("location_name", "pAN1201");
//        sensor_obj.put("bp_range", new Integer[]{560, 560});
        sensor_obj.put("bp_start", 560);
        sensor_obj.put("bp_end", 560);
        sensor_module_locations.add(sensor_obj);

        JSONArray circuit_module_locations = new JSONArray();
        JSONObject circuit_obj = new JSONObject();
        circuit_obj.put("location_name", "pAN1201");
//        circuit_obj.put("bp_range", new Integer[]{54, 560});
        circuit_obj.put("bp_start", 54);
        circuit_obj.put("bp_end", 560);
        circuit_module_locations.add(circuit_obj);

        JSONArray output_module_locations = new JSONArray();
        JSONObject output_obj = new JSONObject();
        output_obj.put("location_name", "pAN4020");
//        output_obj.put("bp_range", new Integer[]{953, 953});
        output_obj.put("bp_start", 953);
        output_obj.put("bp_end", 953);
        output_obj.put("unit_conversion", 0.40);
        output_module_locations.add(output_obj);


        Map obj = new LinkedHashMap();
        obj.put("collection", "genetic_locations");
        obj.put("locations", locations);
        obj.put("sensor_module_location", sensor_module_locations);
        obj.put("circuit_module_location", circuit_module_locations);
        obj.put("output_module_location", output_module_locations);

        objects.add(obj);

        return objects;
    }

}
