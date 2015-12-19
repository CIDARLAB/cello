package org.cellocad.MIT.tandem_promoter;

import org.cellocad.MIT.dnacompiler.Args;
import org.cellocad.MIT.dnacompiler.HistogramBins;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;


public class TestInterpolateTandemPromoter {

    public static void main(String[] args) {


        Args options = new Args();
        String path = options.get_home() + "/resources/data/tandem_promoters/";

        //String fin_additive_promoters = path + "additive_promoters.json";

        String fin_tandem_promoters = path + "tandem_promoter_params_100515.json";

        ArrayList<String> required_keys = new ArrayList<String>();
        required_keys.add("gateA");
        required_keys.add("gateB");
        required_keys.add("0x_equation");
        required_keys.add("1x_equation");
        required_keys.add("x0_equation");
        required_keys.add("x1_equation");
        required_keys.add("0x_params");
        required_keys.add("1x_params");
        required_keys.add("x0_params");
        required_keys.add("x1_params");

        _tps = TandemPromoterJSONReader.get_json_object(fin_tandem_promoters, required_keys);

        //_tps_additive = TandemPromoterJSONReader.get_json_object(fin_additive_promoters, required_keys);


        if(_tps == null) {
            System.out.println("null");
            System.exit(-1);
        }

        _hbins.init();



        //double[][] grid_lr = new double[_hbins.get_NBINS()][_hbins.get_NBINS()];
        //double[][] grid_tb = new double[_hbins.get_NBINS()][_hbins.get_NBINS()];

        InterpolateTandemPromoter itp = new InterpolateTandemPromoter();



        for(Iterator iterator = _tps.keySet().iterator(); iterator.hasNext();) {
            String tandem_promoter_name = (String) iterator.next();

            if(!tandem_promoter_name.equals("P3_PhlF_H1_HlyIIR")) {
                continue;
            }

            System.out.println(tandem_promoter_name);

            JSONObject tp = (JSONObject) _tps.get(tandem_promoter_name);
            //JSONObject tp_additive = (JSONObject) _tps_additive.get(tandem_promoter_name);


            double[][] grid_avg = itp.interpolateTandemPromoter(tp, tandem_promoter_name);

            //double[][] grid_add = itp.numericalAddition(tp_additive);

            //double[][] grid_diff = itp.get_difference_grid(grid_avg, grid_add);


            String file_interp = "grid_interp_" + tandem_promoter_name + ".txt";
            itp.writeGridstoFiles(grid_avg, file_interp, 5);


            //String file_add = "grid_add_" + tandem_promoter_name + ".txt";
            //itp.writeGridstoFiles(grid_add, file_add, 5);


            //String file_diff = "grid_diff_" + tandem_promoter_name + ".txt";
            //itp.writeGridstoFiles(grid_diff, file_diff, 5);
        }

    }

    public static HistogramBins _hbins = new HistogramBins();
    public static JSONObject _tps;
    //public static JSONObject _tps_additive;
}
