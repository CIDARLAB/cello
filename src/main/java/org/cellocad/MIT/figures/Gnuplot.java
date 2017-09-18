package org.cellocad.MIT.figures;


import org.cellocad.MIT.dnacompiler.*;
import org.cellocad.MIT.tandem_promoter.InterpolateTandemPromoter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class Gnuplot {

    public Gnuplot(String home, String output_directory, String dateID) {
        _home = home;
        _output_directory = output_directory;
        _dateID = dateID;
    }


    public void printGnuplotGateSNR(Gate g, String assignment_name, Args options) {

        String snr_gp_file  = _output_directory + assignment_name + "_snr_" + g.name + ".gp";
        String snr_eps_file = assignment_name + "_snr_" + g.name + ".eps";
        String snr_txt_file = assignment_name + "_snr_" + g.name + ".txt";

        String dsnr_gp_file  = _output_directory + assignment_name + "_dsnr_" + g.name + ".gp";
        String dsnr_eps_file = assignment_name + "_dsnr_" + g.name + ".eps";
        String dsnr_txt_file = assignment_name + "_dsnr_" + g.name + ".txt";

        Integer STEPS = 100;

        HistogramBins hbins = new HistogramBins();
        hbins.init();
        Double LOGMAX = hbins.get_LOGMAX();
        Double LOGMIN = hbins.get_LOGMIN();

        Double LOGINC = (LOGMAX - LOGMIN) / STEPS;


        Integer snr_score = 0;

        String snr_out = "";
        String dsnr_out = "";

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

                snr_out  += rpu_low + " " + rpu_high + " " + out_snr + "\n";
                dsnr_out += rpu_low + " " + rpu_high + " " + d_snr + "\n";


                if(d_snr > 0) {
                    snr_score++;
                }

            }

            Util.fileWriter(_output_directory + snr_txt_file, snr_out, false);
            Util.fileWriter(_output_directory + dsnr_txt_file, dsnr_out, false);
        }



        ArrayList<Double> child_ons = new ArrayList<>();
        ArrayList<Double> child_offs = new ArrayList<>();

        for(Gate child: g.getChildren()) {
            for (int i = 0; i < child.get_logics().size(); ++i) {
                if (child.get_logics().get(i) == 1) {
                    child_ons.add(child.get_outrpus().get(i));
                } else if (child.get_logics().get(i) == 0) {
                    child_offs.add(child.get_outrpus().get(i));
                }
            }
        }

        Double child_on_min  = Collections.min(child_ons);
        Double child_off_max = Collections.max(child_offs);

        Double axis_max = Math.pow(10, hbins.get_LOGMAX());
        Double axis_min = Math.pow(10, hbins.get_LOGMIN());

        String snr_gnuplot_xfer_lines = "";
        snr_gnuplot_xfer_lines += "\n" + "set output \"" + snr_eps_file + "\"";
        snr_gnuplot_xfer_lines += "\n" + "set terminal postscript eps enhanced color \"Helvetica, 35\" size 10,8.5";
        snr_gnuplot_xfer_lines += "\n" + "set palette defined (0 'dark-blue', 1 'blue', 2 'cyan', 3 'dark-green', 4 'yellow', 5 'orange', 6 'red', 7 'dark-red' )";
        snr_gnuplot_xfer_lines += "\n" + "set cbrange [-20:5]";
        snr_gnuplot_xfer_lines += "\n" + "set xrange ["+axis_min+":"+axis_max+"]";
        snr_gnuplot_xfer_lines += "\n" + "set yrange ["+axis_min+":"+axis_max+"]";
        snr_gnuplot_xfer_lines += "\n" + "set logscale x";
        snr_gnuplot_xfer_lines += "\n" + "set logscale y";
        snr_gnuplot_xfer_lines += "\n" + "plot '"+snr_txt_file+"' using 1:2:3 with points pt 5 ps 2 lt palette notitle,\\";
        snr_gnuplot_xfer_lines += "\n" + "'-' using 1:2 with points pt 7 ps 7 lc rgb 'black' notitle\n";
        snr_gnuplot_xfer_lines += "\n" + child_off_max + " " + child_on_min;
        snr_gnuplot_xfer_lines += "\n" + "EOF";

        Util.fileWriter(snr_gp_file, snr_gnuplot_xfer_lines, false);


        String dsnr_gnuplot_xfer_lines = "";
        dsnr_gnuplot_xfer_lines += "\n" + "set output \"" + dsnr_eps_file + "\"";
        dsnr_gnuplot_xfer_lines += "\n" + "set terminal postscript eps enhanced color \"Helvetica, 35\" size 10,8.5";
        dsnr_gnuplot_xfer_lines += "\n" + "set palette defined (0 'dark-blue', 1 'blue', 2 'cyan', 3 'dark-green', 4 'yellow', 5 'orange', 6 'red', 7 'dark-red' )";
        dsnr_gnuplot_xfer_lines += "\n" + "set cbrange [-20:5]";
        snr_gnuplot_xfer_lines += "\n" + "set xrange ["+axis_min+":"+axis_max+"]";
        snr_gnuplot_xfer_lines += "\n" + "set yrange ["+axis_min+":"+axis_max+"]";
        dsnr_gnuplot_xfer_lines += "\n" + "set logscale x";
        dsnr_gnuplot_xfer_lines += "\n" + "set logscale y";
        dsnr_gnuplot_xfer_lines += "\n" + "plot '"+dsnr_txt_file+"' using 1:2:3 with points pt 5 ps 2 lt palette notitle,\\";
        dsnr_gnuplot_xfer_lines += "\n" + "'-' using 1:2 with points pt 7 ps 7 lc rgb 'black' notitle\n";
        dsnr_gnuplot_xfer_lines += "\n" + child_off_max + " " + child_on_min;
        dsnr_gnuplot_xfer_lines += "\n" + "EOF";

        Util.fileWriter(dsnr_gp_file, dsnr_gnuplot_xfer_lines, false);

    }


    /***********************************************************************

     Synopsis    [  ]

     To generate a wiring diagram with transfer function images as nodes, we first need to make the transfer function images using gnuplot.
     This function puts .gp files in figs directory, which can be converted into images using scripts/make_gnuplot_rpu.pl

     ***********************************************************************/

    public void printGnuplotXfer1D(Gate g, String assignment_name, Args options) {

        String var = g.get_variable_names().get(0);

        String gp_file  = _output_directory + assignment_name + "_xfer_model_" + g.name + ".gp";
        String eps_file = assignment_name + "_xfer_model_" + g.name + ".eps";

        HistogramBins hbins = new HistogramBins();
        hbins.init();
        Double axis_max = Math.pow(10, hbins.get_LOGMAX());
        Double axis_min = Math.pow(10, hbins.get_LOGMIN());

        HashMap<String, Double> lowest_on_rpu_map   = GateUtil.getIncomingONlow(g);
        HashMap<String, Double> highest_off_rpu_map = GateUtil.getIncomingOFFhigh(g);

        String xfer_color = g.colorHex;

        String gnuplot_xfer_lines = "";
        gnuplot_xfer_lines += "\n" + "set output \"" + eps_file + "\"";
        gnuplot_xfer_lines += "\n" + "set terminal postscript eps enhanced color \"Helvetica, 35\" size 2,2";
        gnuplot_xfer_lines += "\n" + "set logscale x";
        gnuplot_xfer_lines += "\n" + "set logscale y";
        gnuplot_xfer_lines += "\n" + "set lmargin screen 0.0";
        gnuplot_xfer_lines += "\n" + "set rmargin screen 1.0";
        gnuplot_xfer_lines += "\n" + "set tmargin screen 1.0";
        gnuplot_xfer_lines += "\n" + "set bmargin screen 0.0";
        gnuplot_xfer_lines += "\n" + "set size ratio 1.0";
        gnuplot_xfer_lines += "\n" + "set border linewidth 2";
        gnuplot_xfer_lines += "\n" + "set tics scale 2";
        gnuplot_xfer_lines += "\n" + "set mxtics 10";
        gnuplot_xfer_lines += "\n" + "set mytics 10";
        gnuplot_xfer_lines += "\n" + "set key bottom left";
        gnuplot_xfer_lines += "\n" + "set key samplen -1";
        gnuplot_xfer_lines += "\n" + "set xrange ["+axis_min+":"+axis_max+"]";
        gnuplot_xfer_lines += "\n" + "set yrange ["+axis_min+":"+axis_max+"]";
        gnuplot_xfer_lines += "\n" + "set format y \"10^{%L}\"    ";
        gnuplot_xfer_lines += "\n" + "set format x \"10^{%L}\"    ";
        gnuplot_xfer_lines += "\n" + "set format x \"\"    ";
        gnuplot_xfer_lines += "\n" + "set xlabel '" + BooleanLogic.logicString(g.get_logics()) + "'";


        Double highest_off_rpu = highest_off_rpu_map.get(var);
        Double lowest_on_rpu = lowest_on_rpu_map.get(var);
        Double IL_x = 0.0;
        Double IH_x = 0.0;
        Double IL_y = 0.0;
        Double IH_y = 0.0;

        if(g.get_variable_thresholds().get(var) != null && options.is_noise_margin()) {

            IL_x = g.get_variable_thresholds().get(var)[0];
            IH_x = g.get_variable_thresholds().get(var)[1];

            HashMap<String, Double> variables_low = new HashMap<String, Double>();
            variables_low.put(var, IL_x);
            HashMap<String, Double> variables_high = new HashMap<String, Double>();
            variables_high.put(var, IH_x);

            IL_y = ResponseFunction.computeOutput(variables_low, g.get_params(), g.get_equation());
            IH_y = ResponseFunction.computeOutput(variables_high, g.get_params(), g.get_equation());

            String low_rect_color = "gold";
            String high_rect_color = "gold";


            if (highest_off_rpu > IL_x) {
                low_rect_color = "red";
            }
            if (lowest_on_rpu < IH_x) {
                high_rect_color = "red";
            }

            //gnuplot_xfer_lines += "\n" + "set object 1 rect from " + highest_off_rpu + "," + ""+axis_min+"" + " to " + IL_x + "," + "50" + " fc rgb '" + low_rect_color + "'";
            //gnuplot_xfer_lines += "\n" + "set object 2 rect from " + IH_x + "," + ""+axis_min+"" + " to " + lowest_on_rpu + "," + "50" + " fc rgb '" + high_rect_color + "'";
        }

        gnuplot_xfer_lines += "\n" + "set arrow from "+highest_off_rpu+","+axis_min+" to "+highest_off_rpu+","+axis_max+" nohead lw 10 lt 2 lc rgb '#000000'";
        gnuplot_xfer_lines += "\n" + "set arrow from "+lowest_on_rpu+","+axis_min+" to "+lowest_on_rpu+","+axis_max+" nohead lw 10 lt 2 lc rgb '#000000'";


        String title = "";
        title = g.name;
        title = title.replaceAll("_", "");

        gnuplot_xfer_lines += "\n";
        for(String param_name: g.get_params().keySet()) {
            gnuplot_xfer_lines += param_name + " = " + g.get_params().get(param_name) + "\n";
        }
        gnuplot_xfer_lines += "set dummy " + var + "\n";

        String equation = g.get_equation().replaceAll("\\^", "**");

        gnuplot_xfer_lines += "\n" + "plot "+ equation + " lw 25 lc rgb '#" + xfer_color +"' title '"+title+"',\\\n";

        if(g.get_variable_thresholds().get(var) != null) {
            gnuplot_xfer_lines += " \"<echo '1 2'\" using (" + IL_x + "):(" + IL_y + ")  with points pt 7 ps 4 lc rgb 'black' notitle,\\\n";
            gnuplot_xfer_lines += " \"<echo '1 2'\" using (" + IH_x + "):(" + IH_y + ")  with points pt 7 ps 4 lc rgb 'black' notitle\n";
        }

        Util.fileWriter(gp_file, gnuplot_xfer_lines, false);

    }


    public void printGnuplotXfer2D(Gate g, String assignment_name, Args options) {

        String gp_file  = _output_directory + assignment_name + "_xfer_model_" + g.name + ".gp";
        String eps_file = assignment_name + "_xfer_model_" + g.name + ".eps";

        HashMap<String, Double> lowest_on_rpu_map   = GateUtil.getIncomingONlow(g);
        HashMap<String, Double> highest_off_rpu_map = GateUtil.getIncomingOFFhigh(g);

        HistogramBins hbins = new HistogramBins();
        hbins.init();
        Double axis_max = Math.pow(10, hbins.get_LOGMAX());
        Double axis_min = Math.pow(10, hbins.get_LOGMIN());

        String xfer_color = g.colorHex;

        String gnuplot_xfer_lines = "";
        gnuplot_xfer_lines += "\n" + "set output \"" + eps_file + "\"";
        gnuplot_xfer_lines += "\n" + "set terminal postscript eps enhanced color \"Helvetica, 35\" size 2,2";
        gnuplot_xfer_lines += "\n" + "set logscale x";
        gnuplot_xfer_lines += "\n" + "set logscale y";
        gnuplot_xfer_lines += "\n" + "set logscale cb";
        gnuplot_xfer_lines += "\n" + "set key off";
        gnuplot_xfer_lines += "\n" + "set lmargin screen 0.0";
        gnuplot_xfer_lines += "\n" + "set rmargin screen 1.0";
        gnuplot_xfer_lines += "\n" + "set tmargin screen 1.0";
        gnuplot_xfer_lines += "\n" + "set bmargin screen 0.0";
        gnuplot_xfer_lines += "\n" + "set size ratio 1.0";
        gnuplot_xfer_lines += "\n" + "set border linewidth 2";
        gnuplot_xfer_lines += "\n" + "set tics scale 2";
        gnuplot_xfer_lines += "\n" + "set mxtics 10";
        gnuplot_xfer_lines += "\n" + "set mytics 10";
        gnuplot_xfer_lines += "\n" + "set key bottom left";
        gnuplot_xfer_lines += "\n" + "set key samplen -1";
        gnuplot_xfer_lines += "\n" + "set xrange ["+axis_min+":"+axis_max+"]";
        gnuplot_xfer_lines += "\n" + "set yrange ["+axis_min+":"+axis_max+"]";
        gnuplot_xfer_lines += "\n" + "set cbrange ["+axis_min+":"+axis_max+"]";
        gnuplot_xfer_lines += "\n" + "set format x ''";
        gnuplot_xfer_lines += "\n" + "set format y ''";
        gnuplot_xfer_lines += "\n" + "set format z ''";
        gnuplot_xfer_lines += "\n" + "set format cb ''";

        gnuplot_xfer_lines += "\n" + "set pm3d      #color";
        gnuplot_xfer_lines += "\n" + "set hidden3d  #no gridlines";
        gnuplot_xfer_lines += "\n" + "set view map  #flattens";
        gnuplot_xfer_lines += "\n" + "set isosamples 60";

        gnuplot_xfer_lines += "\n" + "set palette defined ( 0 '#FFFFFF',  1 '#"+xfer_color+"')";

        String title = "";
        title = g.name;
        title = title.replaceAll("_", "");

        gnuplot_xfer_lines += "\n";
        for(String param_name: g.get_params().keySet()) {
            gnuplot_xfer_lines += param_name + " = " + g.get_params().get(param_name) + "\n";
        }
        gnuplot_xfer_lines += "set dummy ";
        for(String var: g.get_variable_names()) {
            gnuplot_xfer_lines += var+",";
        }
        gnuplot_xfer_lines = gnuplot_xfer_lines.substring(0, gnuplot_xfer_lines.length()-1); //remove last comma
        gnuplot_xfer_lines += "\n";

        String equation = g.get_equation().replaceAll("\\^", "**");


        HashMap<String, Double> highest_off_rpus = GateUtil.getIncomingOFFhigh(g);
        HashMap<String, Double> lowest_on_rpus   = GateUtil.getIncomingONlow(g);


        String varX = g.get_variable_names().get(0);
        String varY = g.get_variable_names().get(1);

        Double offX = highest_off_rpus.get(varX);
        Double onX  = lowest_on_rpus.get(varX);

        Double offY = highest_off_rpus.get(varY);
        Double onY  = lowest_on_rpus.get(varY);

        gnuplot_xfer_lines += "\n" + "set arrow from "+offX+","+axis_min+" to "+offX+","+axis_max+" nohead front lw 10 lt 2 lc rgb 'black'";
        gnuplot_xfer_lines += "\n" + "set arrow from "+onX +","+axis_min+" to "+onX +","+axis_max+" nohead front lw 10 lt 2 lc rgb 'black'";

        gnuplot_xfer_lines += "\n" + "set arrow from "+axis_min+","+offY+" to "+axis_max+","+offY+" nohead front lw 10 lt 2 lc rgb 'black'";
        gnuplot_xfer_lines += "\n" + "set arrow from "+axis_min+","+onY +" to "+axis_max+","+onY +" nohead front lw 10 lt 2 lc rgb 'black'";

        if(options.is_noise_margin()) {
            Double ILX = g.get_variable_thresholds().get(varX)[0];
            Double IHX = g.get_variable_thresholds().get(varX)[1];

            Double ILY = g.get_variable_thresholds().get(varY)[0];
            Double IHY = g.get_variable_thresholds().get(varY)[1];

            gnuplot_xfer_lines += "\n" + "set arrow from " + ILX + ","+axis_min+" to " + ILX + ","+axis_max+" nohead front lw 2 lt 2 lc rgb 'black'";
            gnuplot_xfer_lines += "\n" + "set arrow from " + IHX + ","+axis_min+" to " + IHX + ","+axis_max+" nohead front lw 2 lt 2 lc rgb 'black'";

            gnuplot_xfer_lines += "\n" + "set arrow from "+axis_min+"," + ILY + " to "+axis_max+"," + ILY + " nohead front lw 2 lt 2 lc rgb 'black'";
            gnuplot_xfer_lines += "\n" + "set arrow from "+axis_min+"," + IHY + " to "+axis_max+"," + IHY + " nohead front lw 2 lt 2 lc rgb 'black'";
        }

        gnuplot_xfer_lines += "\n" + "splot " + equation + " title '" + title + "'\n";

        Util.fileWriter(gp_file, gnuplot_xfer_lines, false);

    }

    public void printGnuplotXfer(LogicCircuit lc, Args options) {

        for(Gate g: lc.get_logic_gates()) {

            if(g.get_variable_names().size() == 1) {
                printGnuplotXfer1D(g, lc.get_assignment_name(), options);
            }
            else if(g.get_variable_names().size() == 2) {
                printGnuplotXfer2D(g, lc.get_assignment_name(), options);
            }
        }
    }


    /***********************************************************************

     Synopsis    [  ]

     ***********************************************************************/
    public void makeCellGrowthFigure(LogicCircuit lc, String suffix) {

        ArrayList<Gate> gates = new ArrayList<Gate>();
        gates.addAll(lc.get_output_gates());
        gates.addAll(lc.get_logic_gates());

        for(int i=0; i<gates.size(); ++i) {

            String outname = gates.get(i).name;
            String name_logic_rpus = lc.get_assignment_name() + "_" + outname + "_" + suffix + ".txt";
            String logic_rpus_data = lc.printLogicRPU(gates.get(i));
            Util.fileWriter(_output_directory + name_logic_rpus, logic_rpus_data, false);

            String name_rpus = lc.get_assignment_name() + "_" + gates.get(i).name + "_" + suffix + ".txt";

            String logic_string = BooleanLogic.logicString(gates.get(i).get_logics());
            logic_string = logic_string.replaceAll("[^\\d.]", "");
            String cmd = "perl " + _home + "/resources/scripts/make_gnuplot_cellgrowth.pl " + _output_directory + " " + _dateID + " " + name_rpus;
            String command_result = Util.executeCommand(cmd);
        }
    }


    /***********************************************************************

     Synopsis    [  ]

     Bar graph with blue ON bars and red OFF bars for the output gate(s)

     ***********************************************************************/
    public void makeTruthtableBargraph(Gate g, String prefix, String suffix) {

        String name_rpu_data = prefix + "_" + g.name + "_" + suffix + ".txt";

        LogicCircuit lc = new LogicCircuit();
        String rpu_data = lc.printLogicRPU(g);
        Util.fileWriter(_output_directory + name_rpu_data, rpu_data, false);

        String logic_string = BooleanLogic.logicString(g.get_logics());
        logic_string = logic_string.replaceAll("[^\\d.]", "");
        String cmd = "perl " + _home + "/resources/scripts/make_gnuplot_truthtable.pl " + _output_directory + " " + _dateID + " " + name_rpu_data + " " + logic_string;
        Util.executeCommand(cmd);
    }


    public void makeTruthtableBargraph(LogicCircuit lc, String suffix) {

        for(int i=0; i<lc.get_output_gates().size(); ++i) {

            String name_rpu_data = lc.get_assignment_name() + "_" + lc.get_output_gates().get(i).name + "_" + suffix + ".txt";

            String rpu_data = lc.printLogicRPU(lc.get_output_gates().get(i));
            Util.fileWriter(_output_directory + name_rpu_data, rpu_data, false);

            String logic_string = BooleanLogic.logicString(lc.get_output_gates().get(i).get_logics());
            logic_string = logic_string.replaceAll("[^\\d.]", "");
            String cmd = "perl " + _home + "/resources/scripts/make_gnuplot_truthtable.pl " + _output_directory + " " + _dateID + " " + name_rpu_data + " " + logic_string;
            Util.executeCommand(cmd);
        }
    }


    /***********************************************************************

     Synopsis    [  ]


     Figure generation via Gnuplot to visualize distributions as histograms.

     ***********************************************************************/
    public void makeHistogramMultiplotGate(Gate g, String prefix, String suffix, String input_truth) {

        String h_datapoints = "";
        for(int h=0; h<g.get_histogram_bins().get_NBINS(); ++h) {
            h_datapoints += g.get_histogram_bins().get_LOG_BIN_CENTERS()[h] + " \t ";
            for(int row=0; row<g.get_histogram_rpus().size(); ++row) {
                h_datapoints += g.get_histogram_rpus().get(row)[h] + " \t ";
            }
            h_datapoints += "\n";
            //each row of the truth table is a column of fractional counts in the output text file
        }
        String name_conv_rpus = prefix + "_" + g.name + "_" + suffix + ".txt";

        //write data to file, where columns are truth table rows
        //data is not pre-binned, this is done in Gnuplot
        Util.fileWriter(_output_directory + name_conv_rpus, h_datapoints, false);

        String logic_string = BooleanLogic.logicString(g.get_logics());
        logic_string = logic_string.replaceAll("[^\\d.]", "");


        String input_truth_string = "";

        String cmd = "perl " + _home + "/resources/scripts/make_conv_multiplot.pl " + _output_directory + " " + _dateID + " " + name_conv_rpus + " " + logic_string + " " + input_truth;
        Util.executeCommand(cmd);
    }


    /***********************************************************************

     Synopsis    [  ]


     Figure generation via Gnuplot to visualize distributions as histograms.

     ***********************************************************************/
    public void makeHistogramMultiplot(LogicCircuit lc, String suffix, String input_truth) {

        for(int i=0; i<lc.get_output_gates().size(); ++i) {
            Gate g = lc.get_output_gates().get(i);
            String h_datapoints = "";
            for(int h=0; h<g.get_histogram_bins().get_NBINS(); ++h) {
                h_datapoints += g.get_histogram_bins().get_LOG_BIN_CENTERS()[h] + " \t ";
                for(int row=0; row<lc.get_output_gates().get(i).get_histogram_rpus().size(); ++row) {
                    h_datapoints += lc.get_output_gates().get(i).get_histogram_rpus().get(row)[h] + " \t ";
                }
                h_datapoints += "\n";
                //each row of the truth table is a column of fractional counts in the output text file
            }
            String name_conv_rpus = lc.get_assignment_name() + "_" + lc.get_output_gates().get(i).name + "_" + suffix + ".txt";

            //write data to file, where columns are truth table rows
            //data is not pre-binned, this is done in Gnuplot
            Util.fileWriter(_output_directory + name_conv_rpus, h_datapoints, false);

            String logic_string = BooleanLogic.logicString(lc.get_output_gates().get(i).get_logics());
            logic_string = logic_string.replaceAll("[^\\d.]", "");
            String cmd = "perl " + _home + "/resources/scripts/make_conv_multiplot.pl " + _output_directory + " " + _dateID + " " + name_conv_rpus + " " + logic_string + " " + input_truth;
            String command_result = Util.executeCommand(cmd);
        }
    }


    public void makeTandemPromoterHeatmaps(LogicCircuit lc, GateLibrary gate_library, Args options) {

        InterpolateTandemPromoter itp = new InterpolateTandemPromoter();

        HistogramBins hbins = new HistogramBins();
        hbins.init();

        for(Gate g: lc.get_Gates()) {
            if(g.type == Gate.GateType.INPUT) {
                continue;
            }


            boolean tp_exists = false;
            String tp_name = "";
            double[][] grid = new double[hbins.get_NBINS()][hbins.get_NBINS()];
            Gate child1 = new Gate();
            Gate child2 = new Gate();
            ArrayList<String> fanin_gate_names = new ArrayList<>();

            String var = "x";
            if(g.get_variable_names().size() == 1) {
                var = g.get_variable_names().get(0);
            }


            if (g.get_variable_wires().get(var).size() == 2) { //hard-coded

                child1 = g.getChildren().get(0);
                child2 = g.getChildren().get(1);

                if (child1.type == Gate.GateType.INPUT) {
                    fanin_gate_names.add("input_" + child1.name);
                } else {
                    fanin_gate_names.add(child1.name);
                }

                if (child2.type == Gate.GateType.INPUT) {
                    fanin_gate_names.add("input_" + child2.name);
                } else {
                    fanin_gate_names.add(child2.name);
                }


                String tandem_promoter_name_1 = fanin_gate_names.get(0) + "_" + fanin_gate_names.get(1);
                String tandem_promoter_name_2 = fanin_gate_names.get(1) + "_" + fanin_gate_names.get(0);
                tp_name = tandem_promoter_name_1;

                if (gate_library.get_TANDEM_PROMOTERS().containsKey(tandem_promoter_name_1)) {
                    grid = gate_library.get_TANDEM_PROMOTERS().get(tandem_promoter_name_1);
                    tp_name = tandem_promoter_name_1;
                    tp_exists = true;
                } else if (gate_library.get_TANDEM_PROMOTERS().containsKey(tandem_promoter_name_2)) {
                    grid = gate_library.get_TANDEM_PROMOTERS().get(tandem_promoter_name_2);
                    tp_name = tandem_promoter_name_2;
                    tp_exists = true;
                }
            }


            if(tp_exists) {

                String file_points_on  = "grid_tp_" + tp_name + "_points_on.txt";
                String file_points_off = "grid_tp_" + tp_name + "_points_off.txt";
                String file_interp = "grid_tp_" + tp_name + ".txt";
                String file_points_on_path  = options.get_output_directory() + "/" + file_points_on;
                String file_points_off_path = options.get_output_directory() + "/" + file_points_off;
                String file_interp_path = options.get_output_directory() + "/" + file_interp;

                itp.writeGridstoFiles(grid, file_interp_path, 5);

                String gate1_name = fanin_gate_names.get(0);
                String gate2_name = fanin_gate_names.get(1);

                String points_on  = "";
                String points_off = "";

                String v = "x";

                for(int row=0; row<g.get_logics().size(); ++row) {

                    Double in1 = 0.0;
                    Double in2 = 0.0;

                    if (child1.type == Gate.GateType.INPUT) {
                        if (child1.get_logics().get(row) == 0) {
                            in1 = Math.pow(10, hbins.get_LOGMIN());
                        } else if (child1.get_logics().get(row) == 1) {
                            in1 = Math.pow(10, hbins.get_LOGMAX());
                        }
                    } else {
                        in1 = child1.get_inrpus().get(v).get(row);
                    }


                    if (child2.type == Gate.GateType.INPUT) {
                        if (child2.get_logics().get(row) == 0) {
                            in2 = Math.pow(10, hbins.get_LOGMIN());
                        } else if (child2.get_logics().get(row) == 1) {
                            in2 = Math.pow(10, hbins.get_LOGMAX());
                        }
                    } else {
                        in2 = child2.get_inrpus().get(v).get(row);
                    }


                    if (tp_name.startsWith(gate1_name) && tp_name.endsWith(gate2_name)) {
                        //correct in1 and in2 order
                    } else if (tp_name.startsWith(gate2_name) && tp_name.endsWith(gate1_name)) {
                        Double temp = new Double(in1);
                        in1 = in2;
                        in2 = temp;
                    } else {
                        throw new IllegalStateException("Problem with tandem promoter lookup");
                    }

                    Integer bin1 = HistogramUtil.bin_of_logrpu(Math.log10(in1), hbins);
                    Integer bin2 = HistogramUtil.bin_of_logrpu(Math.log10(in2), hbins);

                    int logic = g.get_logics().get(row);

                    if (g.type == Gate.GateType.NOR) {
                        logic = BooleanLogic.computeNOT(logic);
                    }

                    if(logic == 0) {
                        points_off += bin1 + " " + bin2 + " 1\n";
                    }
                    if(logic == 1) {
                        points_on += bin1 + " " + bin2 + " 1\n";
                    }

                }

                Util.fileWriter(file_points_on_path, points_on, false);
                Util.fileWriter(file_points_off_path, points_off, false);


                String cmd = "perl " + options.get_home() + "/resources/scripts/make_tandem_promoter_heatmaps.pl " +
                        options.get_output_directory() + " " +
                        options.get_jobID() + " " +
                        options.get_home() + "/resources/scripts/" + " " +
                        file_interp + " " +
                        file_points_on + " " +
                        file_points_off + " " +
                        tp_name;

                String command_result = Util.executeCommand(cmd);
            }

        }
    }


    private String _home;
    private String _output_directory;
    private String _dateID;
}
