package org.cellocad.adaptors.ucfwriters.ucf_writers_Eco1C1G1T0;


import org.cellocad.MIT.dnacompiler.Util;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

public class collection_writer_motif_library extends collection_writer {

    @Override
    public ArrayList<Map> getObjects() {

        ArrayList<Map> objects = new ArrayList<>();

        String path = getRootPath() + "/resources/netsynthResources/netlists_3in1out";

        File file = new File(path);
        String[] directories = file.list(new FilenameFilter() {
            @Override
            public boolean accept(File current, String name) {
                return new File(current, name).isDirectory();
            }
        });

        for(String directory: directories) {
            ArrayList<String> netlist = Util.fileLines(path + "/" + directory + "/netlist.txt");
            ArrayList<String> netlist_renamed = new ArrayList<String>();
            for(int i=0; i<netlist.size(); ++i) {
                String s = netlist.get(i);
                s = s.replaceAll("in1", "a");
                s = s.replaceAll("in2", "b");
                s = s.replaceAll("in3", "c");
                s = s.replaceAll("out", "y");
                netlist_renamed.add(s);
            }
            LinkedHashMap obj = new LinkedHashMap();
            obj.put("collection", "motif_library");
            //motif.put("expression", previous_expression);

            ArrayList<String> input_names = new ArrayList<String>();
            input_names.add("a");
            input_names.add("b");
            input_names.add("c");

            ArrayList<String> output_names = new ArrayList<String>();
            output_names.add("y");

            obj.put("inputs",  input_names);
            obj.put("outputs", output_names);
            obj.put("netlist", netlist_renamed);

            objects.add(obj);
        }

        return objects;

    }


}
