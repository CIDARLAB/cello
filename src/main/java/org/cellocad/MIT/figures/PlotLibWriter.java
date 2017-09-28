package org.cellocad.MIT.figures;
/**
 * Created by Bryan Der on 3/26/14.
 */

import org.cellocad.BU.parseVerilog.Convert;
import org.cellocad.MIT.dnacompiler.Args;
import org.cellocad.MIT.dnacompiler.Gate;
import org.cellocad.MIT.dnacompiler.Part;
import org.cellocad.MIT.dnacompiler.Util;

import java.util.ArrayList;
import java.util.HashMap;


public class PlotLibWriter {


    public static HashMap<String, Part> unique_parts = new HashMap<String, Part>();

    /***********************************************************************

     ***********************************************************************/
    public static void writeCircuitsForDNAPlotLib(ArrayList<ArrayList<Part>> plasmids, int assignment_index, Args options) {

        _plasmids = plasmids;
        _a = assignment_index;
        String An = String.format("%03d", _a);

        String dna_designs      = get_dna_designs(options.get_jobID());
        String part_information = get_part_information();
        String reg_information  = get_reg_information();
        String plot_parameters  = get_plot_parameters();

        String prefix = options.get_jobID() + "_A" + An + "_dnaplotlib_Eu_";
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

        //ScriptCommands script_commands = new ScriptCommands(options.get_home(), options.get_output_directory(), options.get_jobID());
        //script_commands.makePdf2Png(out);
    }

    private static String get_dna_designs(String name) {

        String dna_design = "design_name,parts,\n";

        for(int np=0; np<_plasmids.size(); ++np) {
            String A = "A" + String.format("%03d", _a);
            String P = "P" + String.format("%03d", np);

            dna_design += "Design " + A+P + ": " + name + "_" + A+P +",";

            for (Part p : _plasmids.get(np)) {

                if (p.get_type().equalsIgnoreCase("promoter")
                        || p.get_type().equalsIgnoreCase("ribozyme")
                        || p.get_type().equalsIgnoreCase("rbs")
                        || p.get_type().equalsIgnoreCase("cds")
                        || p.get_type().equalsIgnoreCase("sgRNA")
                        || p.get_type().equalsIgnoreCase("terminator")
                        || p.get_type().equalsIgnoreCase("scar")
                        || p.get_type().equalsIgnoreCase("output")) {
                    if (p.get_direction().equals("+")) {
                        dna_design += p.get_name() + ",";
                    } else if (p.get_direction().equals("-")) {
                        dna_design += "-" + p.get_name() + ",";
                    }

                    unique_parts.put(p.get_name(), p);
                }
            }

            dna_design = dna_design.substring(0, dna_design.length() - 1) + "\n";
        }
        return dna_design;
    }


    private static String get_part_information() {
        String part_information = "part_name,type,x_extent,y_extent,start_pad,end_pad,color,hatch,arrowhead_height,arrowhead_length,linestyle,linewidth\n";

        for(String part_name: unique_parts.keySet()) {

            Part p = unique_parts.get(part_name);

            String color = "000000";

            if(p.get_parent_gate() != null && p.get_parent_gate().colorHex.length() == 6) {
                color = p.get_parent_gate().colorHex;
            }

            String r_hex = color.substring(0, 2);
            String g_hex = color.substring(2, 4);
            String b_hex = color.substring(4, 6);

            String r_rgb = String.format("%.2f", Double.valueOf(Convert.HextoInt(r_hex)) / 255);
            String g_rgb = String.format("%.2f", Double.valueOf(Convert.HextoInt(g_hex)) / 255);
            String b_rgb = String.format("%.2f", Double.valueOf(Convert.HextoInt(b_hex)) / 255);

            String colors = r_rgb + ";" + g_rgb + ";" + b_rgb;
            p.set_color(colors);

            String part_type = "";
            if (p.get_type().equalsIgnoreCase("promoter")) {
                part_type = "Promoter";
                part_information += p.get_name() + "," + part_type + ",,,,," + colors + ",,,,,\n";
            }
            if (p.get_type().equalsIgnoreCase("cds")) {
                part_type = "CDS";
                part_information += p.get_name() + "," + part_type + ",,,,," + colors + ",,,,,\n";
            }
            if (p.get_type().equalsIgnoreCase("output")) {
                part_type = "CDS";
                part_information += p.get_name() + "," + part_type + ",,,,," + colors + ",,,,,\n";
            }
            if (p.get_type().equalsIgnoreCase("rbs")) {
                part_type = "RBS";
                part_information += p.get_name() + "," + part_type + ",,,,," + colors + ",,,,,\n";
            }
            if (p.get_type().equalsIgnoreCase("ribozyme")) {
                part_type = "Ribozyme";
                part_information += p.get_name() + "," + part_type + ",,,,," + colors + ",,,,,\n";
            }
            if (p.get_type().equalsIgnoreCase("terminator")) {
                part_type = "Terminator";
                part_information += p.get_name() + "," + part_type + ",,,,," + colors + ",,,,,\n";
            }
            if (p.get_type().equalsIgnoreCase("scar")) {
                part_type = "Scar";
                part_information += p.get_name() + "," + part_type + ",,,,," + colors + ",,,,,\n";
            }
            if (p.get_type().equalsIgnoreCase("sgRNA")) {
                part_type = "UserDefined";
                part_information += p.get_name() + "," + part_type + ",,,,," + colors + ",,,,,\n";
            }
            //"pTac,Promoter,,,,,0;0;0,,,,,";
        }

        return part_information;

    }


    private static String get_reg_information() {
        String reg_information = "from_partname,type,to_partname,arrowhead_length,linestyle,linewidth,color\n";

        for(Part p: unique_parts.values()) {

            Gate g = p.get_parent_gate();

            if(p.get_type().equalsIgnoreCase("cds") || p.get_type().equalsIgnoreCase("sgRNA")) {

                for(Part p2: unique_parts.values()) {

                    if(g.type != Gate.GateType.INPUT && g.type != Gate.GateType.OUTPUT && g.type != Gate.GateType.OUTPUT_OR) {

                        if (p2.get_name().equals(g.get_regulable_promoter().get_name())) {
                            String regulation_type = "Repression";
                            if (g.type == Gate.GateType.AND || g.type == Gate.GateType.OR) {
                                regulation_type = "Activation";
                            }

                            String arc = p.get_name() + "," + regulation_type + "," + p2.get_name() + ",3,-,," + p.get_color() + "\n";
                            reg_information += arc;

                        }
                    }
                }
            }
        }

        //System.exit(-1);
        return reg_information;
    }

    private static String get_plot_parameters() {
        String plot_parameters = "";

        plot_parameters += "parameter,value\n";
        plot_parameters += "linewidth,1\n";
        plot_parameters += "y_scale,1\n";
        plot_parameters += "fig_x,8.5\n";
        plot_parameters += "fig_y," + _plasmids.size() + "\n";
        plot_parameters += "show_title,N\n";
        plot_parameters += "backbone_pad_left,3\n";
        plot_parameters += "backbone_pad_right,3\n";
        plot_parameters += "axis_y,45\n";

        return plot_parameters;

    }



    private static ArrayList<ArrayList<Part>> _plasmids = new ArrayList<ArrayList<Part>>();
    private static int _a = 0;

}
