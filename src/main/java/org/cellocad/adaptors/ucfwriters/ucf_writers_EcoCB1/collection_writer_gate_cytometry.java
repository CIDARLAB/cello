package org.cellocad.adaptors.ucfwriters.ucf_writers_EcoCB1;


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

        ArrayList<Double> RPU_TITR = new ArrayList<Double>();
        RPU_TITR.add(0.018806664);
        RPU_TITR.add(0.028666538);
        RPU_TITR.add(0.051722065);
        RPU_TITR.add(0.14837545);
        RPU_TITR.add(0.261559417);
        RPU_TITR.add(0.447725869);
        RPU_TITR.add(0.687508549);
        RPU_TITR.add(1.131011516);
        RPU_TITR.add(1.891798237);
        RPU_TITR.add(3.27479675);
        RPU_TITR.add(4.549973162);
        RPU_TITR.add(8.687342616);

        HistogramBins hbins = new HistogramBins();
        hbins.init();
        GateLibrary gate_library = new GateLibrary(0, 0);

        ArrayList<String> gate_names = new ArrayList<String>();
        gate_names.add("an0_AmeR");
        gate_names.add("js2_AmtR");
        gate_names.add("js2_BM3R1");
        gate_names.add("js2_BetI");
        gate_names.add("js2_HlyIIR");
        gate_names.add("js2_IcaRA");
        gate_names.add("js2_LitR");
        gate_names.add("js2_PhlF");
        gate_names.add("js2_SrpR");
        gate_names.add("js_QacR");
        gate_names.add("js2_LitR");

        for(String gate_name: gate_names) {
            Gate g = new Gate();
            g.name = gate_name;
            gate_library.get_GATES_BY_NAME().put(g.name, g);
        }


        for(Gate g: gate_library.get_GATES_BY_NAME().values()) {
            String gate_name = g.name;

            String rootpath = collection_writer.getRootPath();
            String filepath = rootpath + "/resources/data/nor_gates/cytometry_cb1/";
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