package org.cellocad.MIT.figures;
/**
 * Created by Bryan Der on 3/26/14.
 */

import org.cellocad.BU.parseVerilog.Convert;
import org.cellocad.MIT.dnacompiler.*;
import org.cellocad.MIT.dnacompiler.Gate.GateType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class PlotLibAbstractTruthtableWriter {


    /***********************************************************************

     ***********************************************************************/
    public static void writeAbstractCircuitTruthtableForDNAPlotLib(LogicCircuit lc, Args options) {

        _lc = lc;

        String dna_designs      = get_dna_designs(options.get_jobID());
        String part_information = get_part_information();
        String reg_information  = get_reg_information();
        String plot_parameters  = get_plot_parameters();

        String prefix = options.get_jobID() + "_abstract_dnaplotlib_TT_";
        String params = options.get_output_directory() + prefix + "plot_parameters.csv";
        String parts  = options.get_output_directory() + prefix + "part_information.csv";
        String designs= options.get_output_directory() + prefix + "dna_designs.csv";
        String reg    = options.get_output_directory() + prefix + "reg_information.csv";
        String out    = options.get_output_directory() + prefix + "out.pdf";

        Util.fileWriter(params, plot_parameters, false);
        Util.fileWriter(parts, part_information, false);
        Util.fileWriter(designs, dna_designs, false);
        Util.fileWriter(reg, reg_information, false);

        String python_exe = "python";

        String cmd = python_exe + " -W ignore " + options.get_home() + "/resources/scripts/plot_SBOL_designs.py";
        cmd += " -params "  + params;
        cmd += " -parts "   + parts;
        cmd += " -designs " + designs;
        cmd += " -reg "     + reg;
        cmd += " -output "  + out;
        Util.executeCommand(cmd);

        ScriptCommands script_commands = new ScriptCommands(options.get_home(), options.get_output_directory(), options.get_jobID());
        script_commands.makePdf2Png(out);
    }

    private static String get_dna_designs(String name) {

        String dna_design = "design_name,parts,\n";

        int rows = _lc.get_input_gates().get(0).get_logics().size();

        for(int r=0; r<rows; ++r) {
            String A = "";

            dna_design += "Design " + A+"row"+(r+1) + ": " + name + "_" + A+"row"+(r+1) +",";

            for(int i= _lc.get_Gates().size()-1; i>=0; --i) {
                Gate g = _lc.get_Gates().get(i);
                if(g.type == GateType.INPUT)
                    continue;

                dna_design += "p" + g.outgoing.to.index + ",";

                Wire w = g.outgoing;
                while(w.next != null) {
                    dna_design += "p" + w.next.to.index + ",";
                    w = w.next;
                }

                dna_design += "i" + g.rIndex + ",";
                dna_design += "u" + g.rIndex + ",";

                boolean ON = false;
                ArrayList<Gate> children = g.getChildren();
                for(Gate child: children) {
                    if(child.get_logics().get(r) == 1) {
                        ON = true;
                    }
                }
                if(ON) {
                    dna_design += "g" + g.rIndex + "_ON,";
                }
                else {
                    dna_design += "g" + g.rIndex + "_OFF,";
                }

                dna_design += "t" + g.rIndex + ",";

            }
            dna_design += "\n";
        }


        return dna_design;
    }

    private static String rgb_color(int index) {

        Colors.setColors();
        String color = Colors._DEFAULTHEX.get(Integer.toString(index));

        String r_hex = color.substring(0, 2);
        String g_hex = color.substring(2, 4);
        String b_hex = color.substring(4, 6);

        String r_rgb = String.format("%.2f", Double.valueOf(Convert.HextoInt(r_hex)) / 255);
        String g_rgb = String.format("%.2f", Double.valueOf(Convert.HextoInt(g_hex)) / 255);
        String b_rgb = String.format("%.2f", Double.valueOf(Convert.HextoInt(b_hex)) / 255);

        String rgb_color = r_rgb + ";" + g_rgb + ";" + b_rgb;

        return rgb_color;
    }

    private static String get_part_information() {
        String part_information = "part_name,type,x_extent,y_extent,start_pad,end_pad,color,hatch,arrowhead_height,arrowhead_length,linestyle,linewidth\n";

        String gray  =  "0.5;0.5;0.5";
        String white =  "1.0;1.0;1.0";

        HashSet<String> unique_parts = new HashSet<String>();


        for(int i= _lc.get_Gates().size()-1; i>=0; --i) {
            Gate g = _lc.get_Gates().get(i);
            if (g.type == GateType.INPUT)
                continue;


            if(g.outgoing.to.type == GateType.INPUT)
                unique_parts.add("p"+g.outgoing.to.index + "," + "Promoter" + ",,,,," + gray + ",,,,,\n");
            else
                unique_parts.add("p"+g.outgoing.to.index + "," + "Promoter" + ",,,,," + rgb_color(g.outgoing.to.rIndex) + ",,,,,\n" );


            Wire w = g.outgoing;
            while(w.next != null) {
                if(w.next.to.type == GateType.INPUT)
                    unique_parts.add("p"+w.next.to.index + "," + "Promoter" + ",,,,," + gray + ",,,,,\n" );
                else
                    unique_parts.add("p"+w.next.to.index + "," + "Promoter" + ",,,,," + rgb_color(w.next.to.rIndex) + ",,,,,\n" );

                w = w.next;
            }

            unique_parts.add( "i" + g.rIndex + "," + "Ribozyme" + ",,,,," + rgb_color(g.rIndex) + ",,,,,\n" );
            unique_parts.add( "u" + g.rIndex + "," + "RBS" + ",,,,," + rgb_color(g.rIndex) + ",,,,,\n" );

            if(g.type == GateType.OUTPUT || g.type == GateType.OUTPUT_OR) {
                unique_parts.add("g" + g.rIndex + "_ON" + "," + "CDS" + ",,,,," + white + ",,,,,\n");
                unique_parts.add("g" + g.rIndex + "_OFF" + "," + "CDS" + ",,,,," + white + ",////,,,,\n");
            }
            else{
                unique_parts.add("g" + g.rIndex + "_ON" + "," + "CDS" + ",,,,," + rgb_color(g.rIndex) + ",,,,,\n");
                unique_parts.add("g" + g.rIndex + "_OFF" + "," + "CDS" + ",,,,," + rgb_color(g.rIndex) + ",////,,,,\n");
            }

            unique_parts.add( "t" + g.rIndex + "," + "Terminator" + ",,,,," + rgb_color(g.rIndex) + ",,,,,\n" );

        }


        for(String part_line: unique_parts) {
            part_information += part_line;
        }


        return part_information;
    }


    private static String get_reg_information() {
        String reg_information = "from_partname,type,to_partname,arrowhead_length,linestyle,linewidth,color\n";

        for(int i= _lc.get_Gates().size()-1; i>=0; --i) {
            Gate g = _lc.get_Gates().get(i);
            if (g.type == GateType.INPUT || g.type == GateType.OUTPUT || g.type == GateType.OUTPUT_OR)
                continue;

            String arc = "g"+g.rIndex+"_ON" + ",Repression," + "p"+g.rIndex + ",3,-,2.0,"+rgb_color(g.rIndex)+"\n";
            reg_information += arc;

        }
        return reg_information;
    }

    private static String get_plot_parameters() {
        String plot_parameters = "";

        plot_parameters += "parameter,value\n";
        plot_parameters += "linewidth,1\n";
        plot_parameters += "y_scale,1\n";
        plot_parameters += "fig_x,8.5\n";
        plot_parameters += "fig_y," + _lc.get_input_gates().get(0).get_logics().size() + "\n";
        plot_parameters += "show_title,N\n";
        plot_parameters += "backbone_pad_left,3\n";
        plot_parameters += "backbone_pad_right,3\n";
        plot_parameters += "axis_y,35\n";

        return plot_parameters;

    }

    private static HashMap<String, Part> unique_parts = new HashMap<String, Part>();

    private static LogicCircuit _lc;

}
