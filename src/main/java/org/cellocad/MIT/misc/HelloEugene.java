package org.cellocad.MIT.misc;

import org.cellocad.MIT.dnacompiler.Args;
import org.cellocad.MIT.dnacompiler.Part;
import org.cidarlab.eugene.Eugene;
import org.cidarlab.eugene.dom.Device;
import org.cidarlab.eugene.dom.NamedElement;
import org.cidarlab.eugene.dom.imp.container.EugeneArray;
import org.cidarlab.eugene.dom.imp.container.EugeneCollection;
import org.cidarlab.eugene.exception.EugeneException;

import java.io.File;
import java.util.ArrayList;

public class HelloEugene {

    public static void main(String args[]){

        System.out.println("Hello Eugene");

        System.out.println(args[0]);

        callEugene(args[0], new Args());

        /*try {
            Eugene eugene = new Eugene();

            EugeneCollection ec = eugene.executeFile(new File(args[0]));

            EugeneArray variants = (EugeneArray)ec.get("allResults");

            //System.out.println(variants.getElements().size());

            /*for(NamedElement circuit : variants.getElements()) {
            //for(int i=0; i<1; ++i) {
                //NamedElement circuit = variants.getElement(i);

                if(circuit instanceof  Device) {

                    for(NamedElement gate: ((Device) circuit).getComponentList()) {

                        if(gate instanceof Device) {
                            Orientation o = ((Device) gate).getOrientations(0).get(0);
                            String gate_name = gate.getName();
                            //System.out.println(gate_name + " " + o);
                        }


                    }
                    //System.out.println(circuit);
                }

            }

        }
        catch (Exception e){
            e.printStackTrace();
        }*/
    }


    public static void callEugene(String name_Eug_file, Args options) {

        try {
            Eugene e = new Eugene();

            EugeneCollection ec = e.executeFile(new File(name_Eug_file));

            EugeneArray variants = (EugeneArray) ec.get("allResults");

            if (variants.getElements().size() < options.get_nP()) {

                options.set_nP( variants.getElements().size() );

            }

            //for (int i = 0; i < options.get_nP(); ++i) {
            for (int i = 0; i < 1; ++i) {

                NamedElement circuit = variants.getElement(i);

                if (circuit instanceof Device) {

                    ArrayList<Part> circuit_module = new ArrayList<Part>();

                    int g_index = 0;

                    for (NamedElement gate : ((Device) circuit).getComponentList()) {


                        if (gate instanceof org.cidarlab.eugene.dom.Device) {
                            System.out.println("Device " + gate.getName());
                        }
                        else if(gate instanceof org.cidarlab.eugene.dom.Part) {
                            System.out.println("Part " + gate.getName());
                        }
                            /*String gate_name = gate.getName();

                            String g_direction = "+";

                            String o = ((Device) circuit).getOrientations(g_index).toString();

                            if (o.equals("[REVERSE]")) {
                                g_direction = "-";
                                Device reverse_gate = DeviceUtils.flipAndInvert((Device) gate);
                                gate = reverse_gate;
                            }

                            String egate = g_direction + gate_name;

                            //System.out.println("egate " + egate);


                            ArrayList<Part> txn_unit = new ArrayList<Part>();

                            int p_index = 0;

                            for (NamedElement part : ((Device) gate).getComponentList()) {

                                String part_name = part.getName();

                                String p_direction = "+";

                                String op = ((Device) gate).getOrientations(p_index).toString();

                                if (op.equals("[REVERSE]")) {
                                    p_direction = "-";
                                }

                                p_index++;

                                String epart = p_direction + part_name;

                                //System.out.println("epart: " + epart);

                            }

                            circuit_module.addAll(txn_unit);

                        }*/

                        g_index++;

                    }

                }

            }

            System.out.println("Number of Eugene solutions " + variants.getElements().size());

        } catch (EugeneException exception) {
            exception.printStackTrace();
        }
    }
}
