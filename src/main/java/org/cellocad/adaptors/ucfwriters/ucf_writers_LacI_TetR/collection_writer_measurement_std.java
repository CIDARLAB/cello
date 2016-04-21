package org.cellocad.adaptors.ucfwriters.ucf_writers_LacI_TetR;


import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

public class collection_writer_measurement_std extends collection_writer {

    @Override
    public ArrayList<Map> getObjects() {

        ArrayList<Map> objects = new ArrayList<>();

        Map obj = new LinkedHashMap();
        obj.put("collection", "measurement_std");
        obj.put("signal_carrier_units", "RPU");
        obj.put("normalization_instructions", "");

        obj.put("plasmid_description", "");
        obj.put("plasmid_sequence", "");

        objects.add(obj);

        return objects;
    }
}
