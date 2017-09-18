package org.cellocad.adaptors.ucfwriters.ucf_writers_Eco1C1G1T0;


import org.cellocad.MIT.dnacompiler.*;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

public class collection_writer_gate_cytometry extends collection_writer {

    @Override
    public ArrayList<Map> getObjects() {

        ArrayList<Map> objects = new ArrayList<>();

        ArrayList<Double> RPU_TITR = new ArrayList<Double>();
        /*RPU_TITR.add(0.0094);
        RPU_TITR.add(0.0236);
        RPU_TITR.add(0.0496);
        RPU_TITR.add(0.1678);
        RPU_TITR.add(0.4609);
        RPU_TITR.add(1.3049);
        RPU_TITR.add(1.6501);
        RPU_TITR.add(1.6595);
        RPU_TITR.add(1.6808);
        RPU_TITR.add(1.9952);
        RPU_TITR.add(4.1418);
        RPU_TITR.add(4.8061);*/

        RPU_TITR.add(0.004843);
        RPU_TITR.add(0.007426);
        RPU_TITR.add(0.012530);
        RPU_TITR.add(0.034109);
        RPU_TITR.add(0.062700);
        RPU_TITR.add(0.099936);
        RPU_TITR.add(0.144093);
        RPU_TITR.add(0.247036);
        RPU_TITR.add(0.418091);
        RPU_TITR.add(0.739476);
        RPU_TITR.add(1.012582);
        RPU_TITR.add(2.078460);

        HistogramBins hbins = new HistogramBins();
        hbins.init();
        GateLibrary gate_library = new GateLibrary(0, 0);

        String path = getRootPath() + "/resources/data/nor_gates/NOR_Gates.txt";

        ArrayList<ArrayList<String>> gates_tokens = Util.fileTokenizer(path);
        for(ArrayList<String> gate_tokens: gates_tokens) {

            String gate_name = gate_tokens.get(0);

            if(     gate_name.contains("LmrA") ||
                    gate_name.contains("PsrA") ||
                    gate_name.contains("QacR") ||
                    gate_name.contains("IcaRA") ||
                    gate_name.contains("LitR")
                    )
            {
                continue;
            }


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