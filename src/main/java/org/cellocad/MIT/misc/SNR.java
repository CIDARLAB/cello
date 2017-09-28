package org.cellocad.MIT.misc;

/**
 * Created by Bryan Der on 7/13/15.
 */

import org.cellocad.MIT.dnacompiler.*;
import org.cellocad.adaptors.ucfadaptor.UCFAdaptor;
import org.cellocad.adaptors.ucfadaptor.UCFReader;

import java.util.HashMap;

public class SNR {

    public static void main(String[] args) {

        Args _options = new Args();

        _options.set_UCFfilepath( _options.get_home() + "/resources/UCF/Eco1C1G1T1.UCF.json" );
        _options.set_histogram( false );

        GateLibrary gate_library = new GateLibrary(0, 0);
        PartLibrary part_library = new PartLibrary();

        UCFAdaptor ucf_adaptor = new UCFAdaptor();
        UCFReader ucf_reader = new UCFReader();

        UCF ucf = ucf_reader.readAllCollections(_options.get_UCFfilepath());


        Integer STEPS = 100;

        Double MAX = 50.0;
        Double MIN = 0.001;

        Double LOGMAX = Math.log10(MAX);
        Double LOGMIN = Math.log10(MIN);

        Double LOGINC = (LOGMAX - LOGMIN) / STEPS;

        for(Gate g: gate_library.get_GATES_BY_NAME().values()) {

            String filename = "rpu_triangle_" + g.name + ".txt";

            Util.fileWriter(_options.get_home() + "/src/test/resources/"+filename, "", false);


            Integer snr_score = 0;

            for (int i = 0; i < STEPS - 1; ++i) {
                for (int j = i + 1; j < STEPS; ++j) {

                    Double log_rpu_low = LOGMIN + i * LOGINC;
                    Double rpu_low = Math.pow(10, log_rpu_low);

                    Double log_rpu_high = LOGMIN + j * LOGINC;
                    Double rpu_high = Math.pow(10, log_rpu_high);

                    HashMap<String, Double> low_x = new HashMap<>();
                    low_x.put("x", rpu_low);

                    HashMap<String, Double> high_x = new HashMap<>();
                    high_x.put("x", rpu_high);

                    Double out_high = ResponseFunction.computeOutput(low_x, g.get_params(), g.get_equation());
                    Double out_low = ResponseFunction.computeOutput(high_x, g.get_params(), g.get_equation());

                    Double out_snr = 20 * Math.log10((Math.log10(out_high / out_low)) / (2 * Math.log10(3.2)));
                    Double in_snr = 20 * Math.log10((Math.log10(rpu_high / rpu_low)) / (2 * Math.log10(3.2)));

                    Double d_snr = out_snr - in_snr;

                    String out = rpu_low + " " + rpu_high + " " + d_snr + "\n";

                    Util.fileWriter(_options.get_home() + "/src/test/resources/"+filename, out, true);

                    if(d_snr > 0) {
                        snr_score++;
                    }

                }
            }

            Double on_off_ratio = g.get_params().get("ymax") / g.get_params().get("ymin");
            Double n = g.get_params().get("n");
            Double ratio_n = on_off_ratio * n;
            System.out.println(g.name + " dsnr_score: " + snr_score + " " + on_off_ratio + " " + n + " " + ratio_n);

        }
    }
}
