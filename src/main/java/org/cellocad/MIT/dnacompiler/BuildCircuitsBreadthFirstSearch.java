package org.cellocad.MIT.dnacompiler;


import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.Arrays;

/***********************************************************************
 Synopsis    [ Default algorithm to assign repressors to gates.]

 Exhaustive search.  Intractable as circuit size grows, but guarantees finding the best score.

 Two permutations are required:
 1. Permute assignment of repressors to gates.
 2. Permute choice of all possible RBS-variants of the repressor assignment.

 ***********************************************************************/


/**
 *
 */
public class BuildCircuitsBreadthFirstSearch extends BuildCircuits {


    /**
     * Constructor.  Search algorithm needs to know about:
     *
     * options:
     *   Search settings: iterations,
     *   Score settings: noise margin, roadblocking, toxicity
     *
     * gate_library:
     *   Needs to know about the gate library in order to update response functions during assignment.
     *
     * roadblock:
     *   Needs to know which input and logic gate promoters are roadblocking
     *
     * @param options
     * @param gate_library
     * @param roadblock
     */
    public BuildCircuitsBreadthFirstSearch(Args options, GateLibrary gate_library, Roadblock roadblock) {
        super(options, gate_library, roadblock);
    }


    /**
     *  returns void, but sets the list of assigned circuits, which can be accessed through 'get_logic_circuits()'
     */
    @Override
    public void buildCircuits() {
        logger = Logger.getLogger(getThreadDependentLoggername());
        logger.info("Building circuits by breadth first search");


        set_logic_circuits( new ArrayList<LogicCircuit>() );

        LogicCircuit lc = get_unassigned_lc();

        LogicCircuitUtil.sortGatesByStage(lc);

        for(Gate g: lc.get_output_gates()) {
            Evaluate.refreshGateAttributes(g, get_gate_library());
        }

        ArrayList<ArrayList<String>> curr_asns = new ArrayList<ArrayList<String>>();

        //needs to start with one element in the array
        curr_asns.add(new ArrayList<String>());



        for(int gi=0; gi<get_unassigned_lc().get_logic_gates().size(); gi++) {


            ArrayList<ArrayList<String>> next_asns = new ArrayList<ArrayList<String>>();


            //foreach current branch
            for (ArrayList<String> curr_asn : curr_asns) {


                /**
                 * Update the circuit simulation for the gates that have been assigned thus far
                 */
                for(int i=0; i<curr_asn.size(); ++i) {

                    Gate g = lc.get_logic_gates().get(i);

                    g.name = curr_asn.get(i);

                    Evaluate.refreshGateAttributes(g, get_gate_library());

                    g.set_unvisited(true);

                    //simulates the RPU's
                    Evaluate.simulateRPU(g, get_gate_library(), get_options());

                    //computes the gate score based on ON/OFF ratio
                    Evaluate.evaluateGate(g, get_options());

                }


                /**
                 * Gate gc, 'current gate', is the gate that we want to assign now.
                 */
                Gate gc = lc.get_logic_gates().get(gi);



                //adding branches
                for (Gate libgate : get_gate_library().get_GATES_BY_TYPE().get(gc.type).values()) {

                    gc.name = "null";

                    //setting to null then refreshing clears the attributes assocated with the gate
                    Evaluate.refreshGateAttributes(gc, get_gate_library());


                    if (!currentlyAssignedGroup(lc, libgate.group)) {

                        gc.name = libgate.name;

                        Evaluate.refreshGateAttributes(gc, get_gate_library());

                        gc.set_unvisited(true);

                        Evaluate.simulateRPU(gc, get_gate_library(), get_options());

                        Evaluate.evaluateGate(gc, get_options());




                        if (!get_options().is_tpmodel()) {

                            if(get_options().is_check_roadblocking()) {

                                if (get_roadblock().numberRoadblocking(lc, get_gate_library()) > 0) {
                                    //logger.info("rb");
                                    continue;
                                }
                            }
                        }

                        if(get_options().is_toxicity()) {

                            gc.set_toxtable(get_gate_library().get_GATES_BY_NAME().get(gc.name).get_toxtable());

                            Toxicity.evaluateGateToxicity(gc);

                            if (Toxicity.mostToxicRow(gc) < get_options().get_toxicity_threshold()) {
                                //logger.info("tox");
                                continue;
                            }
                        }

                        if(get_options().is_noise_margin()) {

                            if (!gc.get_scores().is_noise_margin_contract()) {
                                //logger.info("nm");
                                continue;
                            }
                        }

                        if(gc.get_scores().get_onoff_ratio() < get_options().get_gate_onoff_threshold()) {
                            //continue;
                        }

                        //'continue' will skip these steps, which add the passing assignment for 'gc' to the 'current assignment' list

                        ArrayList<String> pass_asn = new ArrayList<String>(curr_asn);

                        //pass_asn is a list of gate names...
                        //the index order matches the index order of the gates in the 'logic_gates' array
                        pass_asn.add(gc.name);

                        //curr_asns is a list of passing assignments
                        next_asns.add(new ArrayList<String>(pass_asn));

                        //logger.info("child: " + gc.Outgoing.To.Name + " gate: " + gc.Name);

                    }

                }

            }

            curr_asns.clear();


            // we are about to loop to the next round, so we update 'assignments' with the current assignments.
            curr_asns = next_asns;



            Integer[] gate_indexes_assigned = new Integer[gi+1];

            for(int i=0; i<(gi+1); ++i) {

                gate_indexes_assigned[i] = (i+1);

            }

            /**
             * Assignments for gates [1]: 16
             * Assignments for gates [1, 2]: 201
             * Assignments for gates [1, 2, 3]: 1943
             * Assignments for gates [1, 2, 3, 4]: 3961
             * Assignments for gates [1, 2, 3, 4, 5]: 4401
             * Assignments for gates [1, 2, 3, 4, 5, 6]: 2815
             *
             * Notice that the number of passing assignments can actually decrease from one round to the next
             */
            logger.info("Assignments for gates " + Arrays.toString(gate_indexes_assigned) + ": " + curr_asns.size());

        }


        //logger.info("Final stage assignments: " + assignments.size() + " Gates: " + lc.get_logic_gates().size());
        //System.exit(-1);

        logger.info("\nScoring all assignments...");


        /**
         * Loop through the passing complete assignments (as opposed to partial assignments)
         * and resimulate/rescore the circuit, then add each circuit to the array of assigned circuits.
         */
        for(ArrayList<String> assignment: curr_asns) {

            LogicCircuit temp_deep_copy = new LogicCircuit(lc);
            lc = temp_deep_copy;

            LogicCircuitUtil.sortGatesByStage(lc);


            for(int i=0; i<assignment.size(); ++i) {

                String gate_name = assignment.get(i);

                Gate g = lc.get_logic_gates().get(i);

                g.name = gate_name;
            }

            Evaluate.evaluateCircuit(lc, get_gate_library(), get_options());

            Toxicity.evaluateCircuitToxicity(lc, get_gate_library());

            //logger.info("score_v_growth: " + Util.sc(lc.get_scores().get_score()) + " " + Util.sc(Toxicity.mostToxicRow(lc)));

            LogicCircuitUtil.sortGatesByIndex(lc);

            get_logic_circuits().add(lc);

        }

    }


    /**
     * If group name already exists in the current circuit assignment, return true;
     * This prevents the assignment of genetic gates belonging to the same group (such as RBS variants or crosstalkers)
     *
     * @param lc
     * @param group_name
     * @return
     */
    private boolean currentlyAssignedGroup(LogicCircuit lc, String group_name) {

        for(Gate g: lc.get_logic_gates()) {

            if(g.group.equals(group_name)) {
                return true;
            }
        }

        return false;
    }


    private Logger logger  = Logger.getLogger(getClass());
}
