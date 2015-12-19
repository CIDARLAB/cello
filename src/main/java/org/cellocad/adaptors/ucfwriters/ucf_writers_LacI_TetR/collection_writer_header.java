package org.cellocad.adaptors.ucfwriters.ucf_writers_LacI_TetR;


import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

public class collection_writer_header extends collection_writer {

    @Override
    public ArrayList<Map> getObjects() {

        ArrayList<Map> objects = new ArrayList<>();

        Date date = new Date();

        Map obj = new LinkedHashMap();
        obj.put("collection", "header");
        obj.put("description", "LacI and TetR for IWBDA AND4 demo");
        obj.put("version", "v1");
        obj.put("date", date.toString());
        obj.put("author", new String[]{"Bryan Der"});
        obj.put("organism", "Escherichia coli");
        obj.put("genome", "");
        obj.put("media", "");
        obj.put("temperature", "37");
        obj.put("growth", "");


        objects.add(obj);

        return objects;
    }
}
