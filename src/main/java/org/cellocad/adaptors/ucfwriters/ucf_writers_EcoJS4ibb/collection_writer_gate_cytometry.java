package org.cellocad.adaptors.ucfwriters.ucf_writers_EcoJS4ibb;


import org.cellocad.MIT.dnacompiler.Gate;
import org.cellocad.MIT.dnacompiler.GateLibrary;
import org.cellocad.MIT.dnacompiler.HistogramBins;
import org.cellocad.MIT.dnacompiler.HistogramUtil;
import org.cellocad.adaptors.ucfwriters.ucf_writers_Eco1C1G1T1.collection_writer;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

public class collection_writer_gate_cytometry extends collection_writer {

    @Override
    public ArrayList<Map> getObjects() {

        ArrayList<Map> objects = new ArrayList<>();

        Double unit_conversion = 2.5;
        ArrayList<Double> RPU_TITR = new ArrayList<Double>();
        RPU_TITR.add(unit_conversion * 0.072031467);
        RPU_TITR.add(unit_conversion * 0.102952385);
        RPU_TITR.add(unit_conversion * 0.162752905);
        RPU_TITR.add(unit_conversion * 0.364726018);
        RPU_TITR.add(unit_conversion * 0.597496403);
        RPU_TITR.add(unit_conversion * 0.91827082);
        RPU_TITR.add(unit_conversion * 1.381301322);
        RPU_TITR.add(unit_conversion * 2.249136864);
        RPU_TITR.add(unit_conversion * 3.637029455);
        RPU_TITR.add(unit_conversion * 6.607562751);
        RPU_TITR.add(unit_conversion * 9.144185977);
        RPU_TITR.add(unit_conversion * 14.19852438);

        HistogramBins hbins = new HistogramBins();
        hbins.init();
        GateLibrary gate_library = new GateLibrary(0, 0);

        ArrayList<String> gate_names = new ArrayList<String>();
        gate_names.add("P3_PhlF");
        gate_names.add("S4_SrpR");
        gate_names.add("B3_BM3R1");
        gate_names.add("A1_AmtR");
        gate_names.add("H1_HlyIIR");
        gate_names.add("Q2_QacR");
        gate_names.add("I1_IcaRA");
        gate_names.add("E1_BetI");
        gate_names.add("F1_AmeR");

        for(String gate_name: gate_names) {
            Gate g = new Gate();
            g.name = gate_name;
            gate_library.get_GATES_BY_NAME().put(g.name, g);
        }


        for(Gate g: gate_library.get_GATES_BY_NAME().values()) {
            String gate_name = g.name;

            String rootpath = collection_writer.getRootPath();
            String filepath = rootpath + "/resources/data/nor_gates/cytometry/";
            HistogramUtil.getTransferFunctionHistogramTitrations(gate_name, gate_library, hbins, filepath);

            LinkedHashMap obj = new LinkedHashMap();
            obj.put("collection", "gate_cytometry"); //tag

            obj.put("gate_name", gate_name);

            ArrayList<LinkedHashMap> titrations = new ArrayList<LinkedHashMap>();

            for(int i=0; i<RPU_TITR.size(); ++i) {
                Double inRPU = RPU_TITR.get(i);

                ArrayList<Double> out_rpu_bins = new ArrayList<Double>();
                ArrayList<Double> out_rpu_counts = new ArrayList<Double>();

                for(int j=0; j<hbins.get_NBINS(); ++j) {
                    double[] normalized = HistogramUtil.normalize(g.get_xfer_hist().get_xfer_binned().get(i));
                    out_rpu_bins.add(Math.pow(10, hbins.get_LOG_BIN_CENTERS()[j]));
                    out_rpu_counts.add(normalized[j]);
                }


                LinkedHashMap titration = new LinkedHashMap();
                titration.put("maps_to_variable", "x");
                titration.put("input", inRPU);
                titration.put("output_bins", out_rpu_bins);
                titration.put("output_counts", out_rpu_counts);
                titrations.add(titration);
            }

            obj.put("cytometry_data", titrations);


            objects.add(obj);
            //org.cellocad.ucf_writers_tetr.ConstraintFileWriter.JSONPrettyPrint(obj, "resources/UCF/Eco1C1G1T1.gate_cytometry.json", true);


        }

        return objects;
    }
}