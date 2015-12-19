package org.cellocad.adaptors.ucfwriters.ucf_writers_LacI_TetR;


import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

public class collection_writer_gates extends collection_writer {

    @Override
    public ArrayList<Map> getObjects() {

        ArrayList<Map> objects = new ArrayList<>();


        Map lacI = new LinkedHashMap();
        lacI.put("collection", "gates");
        lacI.put("regulator", "LacI");
        lacI.put("inducer", "IPTG");
        lacI.put("group_name", "LacI");
        lacI.put("gate_name", "const_LacI");
        lacI.put("gate_type", "NOT");
        lacI.put("system", "sensor");


        Map tetR = new LinkedHashMap();
        tetR.put("collection", "gates");
        tetR.put("regulator", "TetR");
        tetR.put("inducer", "aTc");
        tetR.put("group_name", "TetR");
        tetR.put("gate_name", "const_TetR");
        tetR.put("gate_type", "NOT");
        tetR.put("system", "sensor");

        objects.add(lacI);
        objects.add(tetR);

        return objects;

    }


}
