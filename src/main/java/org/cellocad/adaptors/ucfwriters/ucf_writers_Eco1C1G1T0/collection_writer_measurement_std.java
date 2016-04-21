package org.cellocad.adaptors.ucfwriters.ucf_writers_Eco1C1G1T0;


import org.cellocad.MIT.dnacompiler.Util;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

public class collection_writer_measurement_std extends collection_writer {

    @Override
    public ArrayList<Map> getObjects() {

        ArrayList<Map> objects = new ArrayList<>();

        ArrayList<String> plasmid_genbank = Util.fileLines("resources/plasmids/pAN1717.ape");
        String plasmid_genbank_string = "";
        for(String s: plasmid_genbank) {
            plasmid_genbank_string += s + "\n";
        }

        Map obj = new LinkedHashMap();
        obj.put("collection", "measurement_std");
        obj.put("signal_carrier_units", "RPU");
        obj.put("normalization_instructions", "The following equation converts the median YFP fluorescence to RPU.  RPU = (YFP – YFP0)/(YFPRPU – YFP0), where YFP is the median fluorescence of the cells of interest, YFP0 is the median autofluorescence, and YFPRPU is the median fluorescence of the cells containing the measurement standard plasmid");

        obj.put("plasmid_description", "p15A plasmid backbone with kanamycin resistance and a YFP expression cassette. Upstream isulation by terminator L3S3P21\n" +
                "and a 5’-promoter spacer.  Promoter BBa_J23101, ribozyme RiboJ, RBS BBa_B0064 drives constitutive YFP expression, with transcriptional termination by L3S3P21.");

        obj.put("plasmid_sequence", plasmid_genbank);


        objects.add(obj);

        return objects;
    }
}
