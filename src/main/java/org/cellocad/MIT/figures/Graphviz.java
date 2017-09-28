package org.cellocad.MIT.figures;


import org.cellocad.MIT.dnacompiler.*;

public class Graphviz {


    public Graphviz(String home, String output_directory, String dateID) {
        _home = home;
        _output_directory = output_directory;
        _dateID = dateID;
    }

    /***********************************************************************

     Synopsis    [  ]

     Wiring diagram with xfer functions (Gnuplot) as node images.
     This function was originally written to make animated GIFs to illustrate assignment algorithms.
     Now it's used to generate one graphviz file with transfer function images as nodes

     example file name: 1404317484362_A000_wiring_rpu.dot

     ***********************************************************************/
    public void printGraphvizXferPNG(LogicCircuit lc, String outfile) { //single output only

        String gvText = "#GRAPHVIZ_OUTPUT \n";
        gvText += "digraph{ \n";
        gvText += "rankdir=LR; \n";
        gvText += "splines=ortho; \n";

        for(int i=0; i<lc.get_output_gates().size(); ++i) {
            gvText += lc.get_output_gates().get(i).name + "[shape=none,label=\"" + lc.get_output_gates().get(i).name + "\"]; \n";
        }

        for(int i=lc.get_Gates().size()-1; i>=0; --i) {

            Gate g = lc.get_Gates().get(i);

            if(g.type == Gate.GateType.OUTPUT || g.type == Gate.GateType.OUTPUT_OR ) { continue; }
            String shape = g.name;
            if(g.type == Gate.GateType.INPUT) {
                gvText += g.name + "[shape=none,label=\"" + shape + "\"]; \n";
            }
            else if(g.type == Gate.GateType.NOT || g.type == Gate.GateType.NOR || g.type == Gate.GateType.AND || g.type == Gate.GateType.NAND || g.type == Gate.GateType.OR || g.type == Gate.GateType.XOR || g.type == Gate.GateType.XNOR) {
                String image_location = "\"" + lc.get_assignment_name() + "_xfer_model_" + g.name +".png"+ "\"";
                gvText += g.name + "[fixedsize=true,height=1.0,width=1.0,label=\"\",shape=none,image="+image_location+"]; \n";

            }
            else {
                String g_logics = "\\n" + BooleanLogic.logicString(g.get_logics());
                String labelscore = "";
                if(g.get_scores().get_score() != -1.0) {
                    labelscore = "\\n" + String.format("%5.4f", g.get_scores().get_score());
                }
                gvText += g.name + "[shape=box,label=\"" + shape + labelscore + g_logics + "\"]; \n";
            }
        }

        for(int i=0; i<lc.get_Wires().size(); ++i) {
            Wire w = lc.get_Wires().get(i);

            String child = w.to.name;
            String parent = w.from.name;

            gvText += child + " ->" + parent + " ; \n";
        }

        gvText += "} \n";


        Util.fileWriter(_output_directory + outfile, gvText, false);
    }


    /***********************************************************************

     Synopsis    [  ]

     ***********************************************************************/
    public void printGraphvizDistrPNG(LogicCircuit lc, String outfile) { //single output only

        String gvText = "#GRAPHVIZ_OUTPUT \n";
        gvText += "digraph{ \n";
        gvText += "rankdir=LR; \n";
        gvText += "splines=ortho; \n";

        for(int i=0; i<lc.get_output_gates().size(); ++i) {
            Gate g = lc.get_output_gates().get(i);
            gvText += g.name + "[shape=none,label=\"" + lc.get_output_gates().get(i).name + "\"]; \n";
        }

        for(int i=lc.get_Gates().size()-1; i>=0; --i) {

            Gate g = lc.get_Gates().get(i);

            if(g.type == Gate.GateType.OUTPUT || g.type == Gate.GateType.OUTPUT_OR) { continue; }
            String shape = g.name;
            if(g.type == Gate.GateType.INPUT) {
                gvText += g.name + "[shape=none,label=\"" + shape + "\"]; \n";
            }
            else {
                //String image_location = "\"" + _output_directory + prefixA + "_" + g.Name +"_gate.png"+ "\"";
                String image_location = "\"" + lc.get_assignment_name() + "_" + g.name +"_gate.png"+ "\"";
                gvText += g.name + "[fixedsize=true,height=1.0,width=1.0,label=\"\",shape=none,image="+image_location+"]; \n";
            }
        }

        for(int i=0; i<lc.get_Wires().size(); ++i) {
            Wire w = lc.get_Wires().get(i);

            String child = w.to.name;
            String parent = w.from.name;

            gvText += child + " ->" + parent + " ; \n";
        }

        gvText += "} \n";


        Util.fileWriter(_output_directory + outfile, gvText, false);
    }


    /***********************************************************************

     Synopsis    [  ]


     write .dot file for Graphviz.  Works for AGRN and GRN.

     /////////////////// AGRN ////////////////////
     #GRAPHVIZ_OUTPUT
     digraph{
     rankdir=LR;
     splines=ortho;
     out1[shape=none,label="out1\n0001"];
     in2[shape=none,label="in2\n0101"];
     in1[shape=none,label="in1\n0011"];
     3[shape=box,style=filled,fillcolor=gray100,label="3\n1100"];
     2[shape=box,style=filled,fillcolor=gray100,label="2\n1010"];
     1[shape=box,style=filled,fillcolor=gray100,label="1\n0001"];
     1->out1;
     2->1;
     3->1;
     in2->2;
     in1->3;
     }


     /////////////////// GRN ////////////////////
     #GRAPHVIZ_OUTPUT
     digraph{
     rankdir=LR;
     splines=ortho;
     output_YFP[shape=none,label="output_YFP\n2.6691\n0001"];
     pTet[shape=none,label="pTet\n2.1083\n0101"];
     pTac[shape=none,label="pTac\n2.6344\n0011"];
     PhlF[shape=box,style=filled,fillcolor=gray99,label="PhlF\nrbs0\n2.4254\n1100"];
     QacR[shape=box,style=filled,fillcolor=gray98,label="QacR\nrbs2\n2.2019\n1010"];
     SrpR[shape=box,style=filled,fillcolor=gray99,label="SrpR\nrbs3\n2.6691\n0001"];
     SrpR->output_YFP;
     QacR->SrpR;
     PhlF->SrpR;
     pTet->QacR;
     pTac->PhlF;
     }
     ***********************************************************************/
    public void printGraphvizDotText(LogicCircuit lc, String outfile) {

        String gvText = "#GRAPHVIZ_OUTPUT \n";
        gvText += "digraph{ \n";
        gvText += "rankdir=LR; \n";
        gvText += "splines=ortho; \n";

        for(int i=0; i<lc.get_output_gates().size(); ++i) {
            String output_score = "";
            if(lc.get_output_gates().get(i).get_scores().get_score() != -1.0000)
                output_score = "\\n" + String.format("%8.2f", lc.get_output_gates().get(i).get_scores().get_score());
            String output_logics = "\\n" + BooleanLogic.logicString(lc.get_output_gates().get(i).get_logics());
            gvText += lc.get_output_gates().get(i).name + "[shape=none,label=\"" + lc.get_output_gates().get(i).name + output_score + output_logics + "\"]; \n";
        }

        for(int i=lc.get_Gates().size()-1; i>=0; --i) {

            Gate g = lc.get_Gates().get(i);
            String g_logics = "\\n" + BooleanLogic.logicString(g.get_logics());

            String graycolor = "100";

            String labelscore = "";
            if(g.get_scores().get_score() != -1.0) {
                labelscore = "\\n" + String.format("%8.2f", g.get_scores().get_score());
            }

            if(g.type == Gate.GateType.OUTPUT || g.type == Gate.GateType.OUTPUT_OR) { continue; }

            if(!g.name.matches("[A-Za-z0-9_]+")) {
                g.name = g.type + "" + Integer.toString(g.rIndex); //abstract gate
            }

            String shape = g.type + " " + g.name  + " " + g.get_distance_to_input();

            if(g.type == Gate.GateType.INPUT) {
                shape = g.type + " " + g.name + " " + g.get_distance_to_input();
                gvText += g.name + "[shape=none,label=\"" + shape +labelscore + g_logics + "\"]; \n";
            }
            else {
                String shape_type = "box";
                if(g.type == Gate.GateType.OR)
                    shape_type = "none";
                if(g.type == Gate.GateType.AND)
                    shape_type = "oval";
                gvText += g.name + "[shape="+shape_type+",style=filled,fillcolor=gray"+graycolor+",label=\"" + shape + labelscore + g_logics + "\"]; \n";
            }
        }

        for(int i=0; i<lc.get_Wires().size(); ++i) {
            Wire w = lc.get_Wires().get(i);

            String child = w.to.name;
            String parent = w.from.name;

            gvText += child + "->" + parent + "; \n";
        }

        gvText += "} \n";

        Util.fileWriter(_output_directory + outfile, gvText, false);
    }


    private String _home;
    private String _output_directory;
    private String _dateID;
}
