package org.cellocad.MIT.dnacompiler;

import lombok.Getter;
import lombok.Setter;
import org.apache.log4j.Logger;
import org.cellocad.MIT.dnacompiler.Gate.GateType;
import org.cellocad.adaptors.eugeneadaptor.EugeneAdaptor;

import java.util.ArrayList;

/**
 * Created by Bryan Der on 3/26/14.
 */



/**
 * Given a circuit, is there illegal roadblocking?
 *
 * Roadblocking occurs for some repressors, and not others.  NOR gates are assumed to have additive tandem promoter activities,
 * but when the downstream repressed promoter does not allow RNAP to pass through, the assumed additivity becomes non-additive.
 * A repressor assignment is illegal if two roadblocking promoters are inputs to the same repressor
 * because a downstream roadblocker is unavoidable.
 *
 */
public class Roadblock {

    /**
     * Based on Alec's experiments, these promoters are roadblocking
     *
     * input roadblockers are separate from logic roadblockers.
     * A circuit with input:input roadblocking will still be output but with a warning.
     * A circuit with input:logic or logic:logic roadblocking will be illegal.
     *
     */
    public void set_roadblockers(ArrayList<String> eugene_part_rules, GateLibrary gate_library) {
        logger = Logger.getLogger(threadDependentLoggername);
        _input_roadblockers = new ArrayList<String>();
        //_input_roadblockers.add("pTac");
        //_input_roadblockers.add("pBAD");

        _logic_roadblockers = new ArrayList<String>();
        //_logic_roadblockers.add("pSrpR");
        //_logic_roadblockers.add("pPhlF");
        //_logic_roadblockers.add("pBM3R1");
        //_logic_roadblockers.add("pQacR");


        for(String rule: eugene_part_rules) {
            if(rule.contains("STARTSWITH") || rule.contains("startswith")) {

                EugeneAdaptor eugene_adaptor = new EugeneAdaptor();
                ArrayList<String> device_names = eugene_adaptor.getDeviceNamesFromRule(rule);
                String name = device_names.get(0);

                boolean is_input = false;

                for(String input_name: gate_library.get_INPUT_NAMES()) {
                    if(name.equals(input_name)) {
                        is_input = true;
                    }
                }

                if(is_input) {
                    _input_roadblockers.add(name);
                }
                else {
                    _logic_roadblockers.add(name);
                }


            }
        }

        logger.info("Roadblocking input promoters: " + _input_roadblockers.toString());
        logger.info("Roadblocking logic promoters: " + _logic_roadblockers.toString());
    }


    /***********************************************************************

     Synopsis: [   ]



     ***********************************************************************/
    /**
     * Count the number of roadblocked gates.
     *
     * In hill climbing, if the number of roadblocked gates decreases, accept the change.
     *
     */
    public int numberRoadblocking(LogicCircuit lc, GateLibrary gate_library) {

        if(_roadblocking_option == false) {
            return 0;
        }

        int n_roadblocking = 0;
        for(Gate g: lc.get_Gates()) {

            if(numberRoadblocking(g, gate_library) > 0) {
                n_roadblocking++;
            }
        }

        return n_roadblocking;
    }


    public int numberRoadblocking(Gate g, GateLibrary gate_library) {

        if(_roadblocking_option == false) {
            return 0;
        }

        int n_roadblocking = 0;


        GateUtil.mapWiresToVariables(g, g.get_variable_names());

        for(String var: g.get_variable_wires().keySet()) {

            Integer n_child_roadblockers_input = 0;
            Integer n_child_roadblockers_logic = 0;

            ArrayList<Wire> wires = g.get_variable_wires().get(var);
            for(Wire w: wires) {

                if(_input_roadblockers.contains(w.to.name)) {
                    n_child_roadblockers_input++;
                }


                if(gate_library.get_GATES_BY_NAME().containsKey(w.to.name)) {

                    if (_logic_roadblockers.contains(gate_library.get_GATES_BY_NAME().get(w.to.name).get_regulable_promoter().get_name())) {
                        n_child_roadblockers_logic++;
                    }
                }
            }

            int total_roadblockers = n_child_roadblockers_input + n_child_roadblockers_logic;

            if(n_child_roadblockers_logic > 0 && total_roadblockers > 1) {
                n_roadblocking++;
            }
        }

        return n_roadblocking;
    }


    /***********************************************************************

     Synopsis: [   ]


     ***********************************************************************/

    /**
     * If two roadblocking promoters are tandem inputs to NOR or OUTPUT_OR, this is an illegal assignment.
     * However, if both roadblockers are inducible promoters, this is not illegal but a warning will be given.
     *
     */
    public boolean illegalRoadblocking(LogicCircuit lc, GateLibrary gate_library) {

        if(_roadblocking_option == false) {
            return false;
        }

        if(numberRoadblocking(lc, gate_library) > 0) {
            return true;
        }
        else {
            return false;
        }

    }



    /**
     *
     * roadblocking by inducible promoters only, throw a warning if true
     */
    public boolean illegalInputRoadblocking(LogicCircuit lc) {

        if(_roadblocking_option == false) {
            return false;
        }

        for(Gate g: lc.get_Gates()) {

            Integer n_child_roadblockers_input = 0;

            if(g.type == GateType.NOR || g.type == GateType.OUTPUT_OR) {

                for(Gate child: g.getChildren()) {

                    if(_input_roadblockers.contains(child.name)) {
                        n_child_roadblockers_input++;
                    }

                }

                if(n_child_roadblockers_input > 1) {
                    return true;
                }
            }
        }

        return false;
    }



    /////////////////////////
    //
    // Private member data
    //
    /////////////////////////
    @Getter @Setter private ArrayList<String> _input_roadblockers = new ArrayList<String>();
    @Getter @Setter private ArrayList<String> _logic_roadblockers = new ArrayList<String>();

    @Getter @Setter private boolean _roadblocking_option = true;

    @Getter @Setter private String threadDependentLoggername;

    private Logger logger  = Logger.getLogger(getClass());
}
