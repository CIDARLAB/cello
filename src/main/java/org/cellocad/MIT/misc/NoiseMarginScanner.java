package org.cellocad.MIT.misc;

import org.cellocad.MIT.dnacompiler.Gate;
import org.cellocad.MIT.dnacompiler.MathEval;

import java.util.HashMap;

public class NoiseMarginScanner {

    public static void main(String[] args) {
        Gate g = new Gate();
        g.name = "SicA_InvF";

        /*
        sicA_map_ymax.put("name", "ymax");
        sicA_map_ymax.put("value", 6.0);
        sicA_map_ymin.put("name", "ymin");
        sicA_map_ymin.put("value", 0.05);
        sicA_map_K1.put("name", "K1");
        sicA_map_K1.put("value", 1.0);
        sicA_map_K2.put("name", "K2");
        sicA_map_K2.put("value", 0.3);
        sicA_map_n.put("name", "n");
        sicA_map_n.put("value", 2.0);
         */

        HashMap<String, Double> params = new HashMap<>();
        params.put("ymax", 6.0);
        params.put("ymin", 0.05);
        params.put("K", 0.1);
        params.put("n", 2.0);

        g.set_params(params);

        getLowHighThresholds(g);
    }

    public static Double[] getLowHighThresholds(Gate g) {

        MathEval math = new MathEval();

        math.setVariable("ymax", g.get_params().get("ymax"));
        math.setVariable("ymin", g.get_params().get("ymin"));
        math.setVariable("K",   g.get_params().get("K"));
        math.setVariable("n",    g.get_params().get("n"));
        math.setVariable("pTac", 0.1);
        String hill_func  = "ymin+(ymax-ymin)/(1.0+(pTac/K)^n)"; //hill function

        double start = Math.log10(0.001);
        double end = Math.log10(100.0);

        double inc = (end - start) / 1000;

        double low_margin  = 0.001;
        double high_margin = 100.0;

        boolean found_low = false;
        boolean found_high = false;

        for(double i=start+inc; i<end; i+=inc) {
            double x1 = Math.pow(10, i-inc);
            math.setVariable("pTac", x1);
            double y1 = math.evaluate(hill_func);

            if(y1 < g.get_params().get("ymax") * 0.5 && found_low == false) {
                low_margin = x1;
                found_low = true;
            }
            if(g.get_params().get("ymin") > y1 * 0.5 && found_high == false) {
                high_margin = x1;
                found_high = true;
            }
        }


        System.out.println( g.name + "    " +
                String.format("%8.3f",g.get_params().get("ymax")) + "  " +
                String.format("%8.3f",g.get_params().get("ymin")) + "  " +
                String.format("%8.3f",g.get_params().get("K")) + "  " +
                String.format("%8.3f",g.get_params().get("n")) + "  " +
                String.format("%8.3f", low_margin) + "  " +
                String.format("%8.3f", high_margin));


        Double[] input_thresholds = new Double[]{low_margin, high_margin};

        return input_thresholds;
    }

}
