package org.cellocad.adaptors.ucfwriters.ucf_writers_Eco1C1G1T1;


import org.cellocad.adaptors.ucfwriters.ucf_writers_Eco1C1G1T0.collection_writer;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

public class collection_writer_motif_library extends collection_writer {

    @Override
    public ArrayList<Map> getObjects() {

        ArrayList<Map> objects = new ArrayList<>();
        FileReader reader = null;
        try {
            reader = new FileReader("resources/netsynthResources/netlist_in3out1_OUTPUT_OR.json");

            try {
                JSONParser jsonParser = new JSONParser();
                JSONArray jsonArray = (JSONArray) jsonParser.parse(reader);
                for(int i=0; i<jsonArray.size(); ++i) {
                    Map obj = (Map) jsonArray.get(i);
                    objects.add(obj);
                }
            } catch (IOException e) {
                e.printStackTrace();
                return objects;
            } catch (ParseException e) {
                e.printStackTrace();
                return objects;
            }
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
            return objects;
        }


        return objects;
    }
}
