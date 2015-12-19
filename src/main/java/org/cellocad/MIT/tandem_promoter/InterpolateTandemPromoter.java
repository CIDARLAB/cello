package org.cellocad.MIT.tandem_promoter;

import org.cellocad.MIT.dnacompiler.HistogramBins;
import org.cellocad.MIT.dnacompiler.MathEval;
import org.cellocad.MIT.dnacompiler.Util;
import org.json.simple.JSONObject;

import java.util.HashMap;

/**
 * Created by Bryan on 8/26/15.
 */
public class InterpolateTandemPromoter {


    public InterpolateTandemPromoter() {
        _hbins.init();
    }


    public void writeGridstoFiles(double[][] grid, String filename, Integer mod) {

        String three_column_content = "";

        for(int i=0; i<_hbins.get_NBINS(); ++i) {

            if(i%mod!=0) {
                continue;
            }

            for (int j = 0; j < _hbins.get_NBINS(); ++j) {

                if(j%mod!=0) {
                    continue;
                }

                three_column_content += i + " " + j + " " + Util.sc(grid[i][j]) + "\n";
            }
        }

        Util.fileWriter(filename, three_column_content, false);
        System.out.println("wrote file " + filename);
    }


    public double[][] numericalAddition(JSONObject tp) {

        double[][] grid = new double[_hbins.get_NBINS()][_hbins.get_NBINS()];

        String eqA = (String) tp.get("x0_equation");
        String eqB = (String) tp.get("0x_equation");

        HashMap<String, Double> paramsA = (HashMap<String, Double>) tp.get("x0_params");
        HashMap<String, Double> paramsB = (HashMap<String, Double>) tp.get("0x_params");


        for(int i=0; i<_hbins.get_NBINS(); ++i) {
            for (int j = 0; j < _hbins.get_NBINS(); ++j) {

                Double log_inA = _hbins.get_LOG_BIN_CENTERS()[i];
                Double log_inB = _hbins.get_LOG_BIN_CENTERS()[j];

                Double inA = Math.pow(10, log_inA);
                Double inB = Math.pow(10, log_inB);

                _matheval.clear();
                for (String param_name : paramsA.keySet()) {
                    Double param_value = paramsA.get(param_name);
                    _matheval.setVariable(param_name, param_value);
                }
                _matheval.setVariable("x", inA);
                double yA = _matheval.evaluate(eqA);

                _matheval.clear();
                for (String param_name : paramsB.keySet()) {
                    Double param_value = paramsB.get(param_name);
                    _matheval.setVariable(param_name, param_value);
                }
                _matheval.setVariable("x", inB);
                double yB = _matheval.evaluate(eqB);

                double y_sum = yA + yB;

                Double out = Math.log10(y_sum);
                grid[i][j] = out;
               

            }

        }

        return grid;

    }



    public double[][] interpolateTandemPromoter(JSONObject tp, String tandem_promoter_name) {

        System.out.println("interpolating " + tandem_promoter_name);

        double[][] grid_tb = get_grid_from_top_bottom_interpolation(tp);

        double[][] grid_lr = get_grid_from_left_right_interpolation(tp);

        double[][] grid_avg =  get_averaged_grid(grid_tb, grid_lr);


        return grid_avg;
    }


    public double[][] get_grid(

            HashMap<String, Double> params1,
            HashMap<String, Double> params2,
            String eq1,
            String eq2,
            
            HashMap<String, Double> params_weight_schedule_0,
            HashMap<String, Double> params_weight_schedule_1,
            String weight_eq1,
            String weight_eq2

            ) {

        double[][] grid = new double[_hbins.get_NBINS()][_hbins.get_NBINS()];

        for(int i=0; i<_hbins.get_NBINS(); ++i) {

            Double weight_0 = 0.0;
            Double weight_1 = 0.0;
            Double weight = 0.0;
            
            Double log_val = _hbins.get_LOG_BIN_CENTERS()[i];


            //Non-linear
            _matheval.clear();
            for (String param_name : params_weight_schedule_0.keySet()) {
                Double param_value = params_weight_schedule_0.get(param_name);
                _matheval.setVariable(param_name, param_value);
            }
            _matheval.setVariable("ymax", 1.0);
            _matheval.setVariable("ymin", 0.0);
            _matheval.setVariable("x", Math.pow(10, log_val));
            //weight_0 = _matheval.evaluate(weight_eq1);
            weight_0 = _matheval.evaluate(this.hill_activation);

            _matheval.clear();
            for (String param_name : params_weight_schedule_1.keySet()) {
                Double param_value = params_weight_schedule_1.get(param_name);
                _matheval.setVariable(param_name, param_value);
            }
            _matheval.setVariable("ymax", 1.0);
            _matheval.setVariable("ymin", 0.0);
            _matheval.setVariable("x", Math.pow(10, log_val));
            //weight_1 = _matheval.evaluate(weight_eq2);
            weight_1 = _matheval.evaluate(this.hill_activation);

            for (int j = 0; j < _hbins.get_NBINS(); ++j) {
            	
            	// linear interpolation of two weights
                double ww = (j * 1.0) / (_hbins.get_NBINS() - 1);
     
                weight = weight_0 * (1 - ww)  +  weight_1 * ww;
            	
                Double log_x = _hbins.get_LOG_BIN_CENTERS()[j];
                Double x = Math.pow(10, log_x);


                _matheval.clear();
                for (String param_name : params1.keySet()) {
                    Double param_value = params1.get(param_name);
                    _matheval.setVariable(param_name, param_value);
                }
                _matheval.setVariable("x", x);
                double y1 = _matheval.evaluate(eq1);

                _matheval.clear();
                for (String param_name : params2.keySet()) {
                    Double param_value = params2.get(param_name);
                    _matheval.setVariable(param_name, param_value);
                }
                _matheval.setVariable("x", x);
                double y2 = _matheval.evaluate(eq2);


                /*double log_y1 = Math.log10(y1);
                double log_y2 = Math.log10(y2);

                double log_y = log_y2 * (1 - weight) + log_y1 * weight;*/


                double y = y2 * (1 - weight) + y1 * weight;

                double log_y = Math.log10(y);






                /*if(
                                (i == 0 && j == 0) ||
                                (i == 249 && j == 0) ||
                                (i == 0 && j == 249) ||
                                (i == 249 && j == 249)



                        ) {
                    HashMap<String, Double> test = new HashMap();
                    test.put("i", i*1.0);
                    test.put("j", j*1.0);

                    test.put("params1_max", params1.get("ymax"));
                    test.put("params2_max", params2.get("ymax"));

                    test.put("params1_min", params1.get("ymin"));
                    test.put("params2_min", params2.get("ymin"));

                    test.put("w0", weight_0);
                    test.put("w1", weight_1);
                    test.put("w", weight);
                    test.put("log_y1", log_y1);
                    test.put("log_y2", log_y2);
                    test.put("log_y", log_y);
                    System.out.println(test.toString());

                }*/


                grid[j][i] = log_y;
            }

        }
        
        return grid;
    }


    public double[][] get_grid_from_top_bottom_interpolation(JSONObject tp) {

        HashMap<String, Double> params1 = (HashMap<String, Double>) tp.get("x1_params");
        HashMap<String, Double> params2 = (HashMap<String, Double>) tp.get("x0_params");

        HashMap<String, Double> params_weight_schedule_0 = (HashMap<String, Double>) tp.get("0x_params");
        HashMap<String, Double> params_weight_schedule_1 = (HashMap<String, Double>) tp.get("1x_params");

        String eq1 = (String) tp.get("x1_equation");
        String eq2 = (String) tp.get("x0_equation");

        String weight_eq1 = (String) tp.get("0x_equation");
        String weight_eq2 = (String) tp.get("1x_equation");

        double[][] grid = get_grid(params1, params2, eq1, eq2, params_weight_schedule_0, params_weight_schedule_1, weight_eq1, weight_eq2);

        return grid;

    }

    public double[][] get_grid_from_left_right_interpolation(JSONObject tp) {

        HashMap<String, Double> params1 = (HashMap<String, Double>) tp.get("1x_params");
        HashMap<String, Double> params2 = (HashMap<String, Double>) tp.get("0x_params");

        HashMap<String, Double> params_weight_schedule_0 = (HashMap<String, Double>) tp.get("x0_params");
        HashMap<String, Double> params_weight_schedule_1 = (HashMap<String, Double>) tp.get("x1_params");


        String eq1 = (String) tp.get("1x_equation");
        String eq2 = (String) tp.get("0x_equation");
//        String eq_weight_schedule = (String) tp.get("x0_equation");

        String weight_eq1 = (String) tp.get("x0_equation");
        String weight_eq2 = (String) tp.get("x1_equation");


        double[][] grid = get_grid(params1, params2,  eq1, eq2, params_weight_schedule_0, params_weight_schedule_1, weight_eq1, weight_eq2);

        
        //transforming data only for from left to right, not from top to bottom
        double[][] temp_grid = new double[_hbins.get_NBINS()][_hbins.get_NBINS()];

        for(int i=0; i<_hbins.get_NBINS(); ++i) {
            for (int j = 0; j < _hbins.get_NBINS(); ++j) {

                temp_grid[i][j] = grid[j][i];
            }
        }
        grid = temp_grid;

        return grid;

    }

    public double[][] get_averaged_grid(double[][] grid1, double[][] grid2) {

        double[][] grid_avg = new double[_hbins.get_NBINS()][_hbins.get_NBINS()];

        for(int i=0; i<_hbins.get_NBINS(); ++i) {
            for (int j = 0; j < _hbins.get_NBINS(); ++j) {
                //grid_avg[i][j] = (grid1[i][j] + grid2[i][j]) / 2;

                grid_avg[i][j] = Math.log10((Math.pow(10, grid1[i][j]) + Math.pow(10, grid2[i][j])) / 2);
            }
        }

        return grid_avg;

    }

    public double[][] get_difference_grid(double[][] grid, double[][] grid_additive) {

        double[][] grid_diff = new double[_hbins.get_NBINS()][_hbins.get_NBINS()];

        for(int i=0; i<_hbins.get_NBINS(); ++i) {
            for (int j = 0; j < _hbins.get_NBINS(); ++j) {
                grid_diff[i][j] = (grid[i][j] - grid_additive[i][j]);
            }
        }

        return grid_diff;

    }

    public HistogramBins _hbins = new HistogramBins();

    public MathEval _matheval = new MathEval();
    public String hill_activation = "ymin+(ymax-ymin)/(1.0+(K/x)^n)";
    public String hill_repression = "ymin+(ymax-ymin)/(1.0+(x/K)^n)";


}
