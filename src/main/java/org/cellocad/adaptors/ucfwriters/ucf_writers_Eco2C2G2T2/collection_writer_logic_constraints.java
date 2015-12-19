package org.cellocad.adaptors.ucfwriters.ucf_writers_Eco2C2G2T2;


import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

public class collection_writer_logic_constraints extends collection_writer {

    @Override
    public ArrayList<Map> getObjects() {

        ArrayList<Map> objects = new ArrayList<>();


        Map nor = new LinkedHashMap();
        nor.put("type", "NOR");
        nor.put("max_instances", 5);


        ArrayList<Map> gate_type_constraints = new ArrayList<Map>();
        gate_type_constraints.add(nor);


        Map obj = new LinkedHashMap();
        obj.put("collection", "logic_constraints");
        obj.put("available_gates", gate_type_constraints);

        objects.add(obj);

        return objects;
    }
}
