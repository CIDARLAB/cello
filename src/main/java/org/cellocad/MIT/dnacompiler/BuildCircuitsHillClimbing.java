package org.cellocad.MIT.dnacompiler;
/**
 * Created by Bryan Der on 3/26/14.
 */

import org.apache.log4j.Logger;

import java.util.*;


public class BuildCircuitsHillClimbing extends BuildCircuits {


    public long SEED = 42000;
    public Integer COUNTER = 1;

    public BuildCircuitsHillClimbing(Args options, GateLibrary gate_library, Roadblock roadblock) {
        super(options, gate_library, roadblock);
    }

    private boolean currentlyAssignedGroup(LogicCircuit lc, String group_name) {

        for(Gate g: lc.get_logic_gates()) {

            if(g.Group.equals(group_name)) {

                return true;

            }

        }

        return false;

    }


    @Override
    public void buildCircuits(){
        logger = Logger.getLogger(getThreadDependentLoggername());
        logger.info("Enumerating logic circuits using hill climbing...");


        double max_score = 0.0;

        LogicCircuit lc = new LogicCircuit(get_unassigned_lc());

        for(int traj=0; traj<get_options().get_hill_trajectories(); ++traj) {


            if(! get_options().is_hill_climb_seed()) {
                SEED = System.currentTimeMillis();
            }


            set_best_score( 0.0 );

            //initial random

            for (int i = 0; i < lc.get_logic_gates().size(); ++i) {
                Gate g = lc.get_logic_gates().get(i);

                g.Name = "null";
            }

            for (int i = 0; i < lc.get_logic_gates().size(); ++i) {

                Gate g = lc.get_logic_gates().get(i);

                LinkedHashMap<String, ArrayList<Gate>> groups_of_type = get_gate_library().get_GATES_BY_GROUP().get(g.Type);

                ArrayList<String> group_names = new ArrayList<String>(groups_of_type.keySet());

                Collections.shuffle(group_names, new Random(SEED+COUNTER));
                COUNTER++;

                for (String group_name : group_names) {

                    if (!currentlyAssignedGroup(lc, group_name)) {

                        ArrayList<Gate> gates_of_group = new ArrayList<Gate>(groups_of_type.get(group_name));

                        Collections.shuffle(gates_of_group, new Random(SEED+COUNTER));
                        COUNTER++;

                        g.Name = gates_of_group.get(0).Name;
                    }
                }
            }

            //logger.info("Random assignment ");
            //logger.info(lc.printAssignment());


            Evaluate.evaluateCircuit(lc, get_gate_library(), get_options());
            for (Gate g : lc.get_logic_gates()) {
                Evaluate.evaluateGate(g, get_options());
            }

            if(get_options().is_toxicity()) {
                Toxicity.evaluateCircuitToxicity(lc, get_gate_library());
            }

            //logger.info(lc.printGraph());


            //next will be changed.  if rejected, LC next will be reset back to LC curr.

            //String follow_best = "";

            String b = get_options().get_output_directory() + "/b" + String.format("%02d", traj) + ".txt";

            for (int i = 0; i < get_options().get_hill_iterations(); ++i) {

                //Util.fileWriter(b, follow_best, true);

                LogicCircuit save_lc = new LogicCircuit(lc);

                int A_gate_index = new Random(SEED+COUNTER).nextInt(lc.get_logic_gates().size());
                COUNTER++;

                Gate A_gate = lc.get_logic_gates().get( A_gate_index );

                Gate B_gate = getNextGate(lc, A_gate); //Get a second gate, either used or unused.

                String A_gate_name  = new String(A_gate.Name);
                String B_gate_name  = new String(B_gate.Name);
                //String A_gate_group = new String(A_gate.Group);
                //String B_gate_group = new String(B_gate.Group);
                //String A_regulator  = new String(A_gate.Group);
                //String B_regulator  = new String(B_gate.Group);

                //1. if second gate is used, swap
                if(isNextGateCurrentlyUsed(lc, B_gate)) {

                    int B_gate_index = 0; //need to know the second gate index
                    for(int j=0; j<lc.get_logic_gates().size(); ++j) {
                        if(lc.get_logic_gates().get(j).Name.equals(B_gate.Name)) {
                            B_gate_index = j;
                        }
                    }

                    lc.get_logic_gates().get(A_gate_index).Name  = B_gate_name;
                    lc.get_logic_gates().get(B_gate_index).Name  = A_gate_name;
                    //lc.get_logic_gates().get(A_gate_index).Group = B_gate_group;
                    //lc.get_logic_gates().get(B_gate_index).Group = A_gate_group;
                    //lc.get_logic_gates().get(A_gate_index).Regulator = B_regulator;
                    //lc.get_logic_gates().get(B_gate_index).Regulator = A_regulator;

                }
                //2. if second gate is unused, substitute
                else {
                    lc.get_logic_gates().get(A_gate_index).Name      = B_gate_name;
                    //lc.get_logic_gates().get(A_gate_index).Group     = B_gate_group;
                    //lc.get_logic_gates().get(A_gate_index).Regulator = B_regulator;
                }


                set_n_total_assignments(get_n_total_assignments()+1);

                Evaluate.evaluateCircuit(lc, get_gate_library(), get_options());

                int B_rb = get_roadblock().numberRoadblocking(lc, get_gate_library());
                int A_rb = get_roadblock().numberRoadblocking(save_lc, get_gate_library());

                Double B_score = lc.get_scores().get_score();
                double A_score = save_lc.get_scores().get_score();


                //logger.info(_n_total_assignments + " " + B_score + " " + A_score);

                /*logger.info("out:"+ A_gate_name + " in:"+B_gate_name +
                                " prev_sc:" + String.format("%-6.2f", A_score)  + " B_sc:" + String.format("%-6.2f", B_score) +
                                " prev_rb:" + A_rb     + " B_rb:" + B_rb +
                                " prev_tx:" + String.format("%-5.4f", A_growth) + " B_tx:" + String.format("%-5.4f", B_growth)
                );*/


                //follow_best = i + " " + _best_score + "\n";

                if(get_options().is_check_roadblocking()) {
                    if (B_rb > A_rb) {
                        //logger.info("reject added roadblock");

                        revert(lc, save_lc);
                        continue;
                    } else if (B_rb < A_rb) {
                        //logger.info("accept removed roadblock");

                        continue; //accept, but don't proceed to evaluate based on score
                    }
                }


                if(get_options().is_toxicity()) {

                    Toxicity.evaluateCircuitToxicity(lc, get_gate_library());

                    double B_growth = Toxicity.mostToxicRow(lc);
                    double A_growth = Toxicity.mostToxicRow(save_lc);

                    if(A_growth < get_options().get_toxicity_threshold()) {
                        if( B_growth > A_growth) {
                            //logger.info("accept, curr fails growth threshold, next improved growth.");
                            continue;
                        }
                        else {
                            //logger.info("reject, curr fails growth threshold, next did not improve growth.");
                            revert(lc, save_lc);
                            continue;
                        }
                    }
                    else {
                        if(B_growth < get_options().get_toxicity_threshold()) {
                            //logger.info("reject, next went below growth threshold.");
                            revert(lc, save_lc);
                            continue;
                        }
                    }
                }


                if (B_score >= A_score) {

                    /////////////// Noise Margin filter //////////////
                    Evaluate.evaluateCircuitNoiseMargin(lc, get_options());
                    if(lc.get_scores().is_noise_margin_contract() == false) {
                        //logger.info("failed nm");
                        //don't revert to avoid getting stuck
                        continue;
                    }


                    if(B_score > get_best_score()) {

                        //recheck roadblock and toxicity... improvements will pass to avoid getting stuck,
                        //but we don't want to actually save an assignment that doesn't pass all filters.
                        if(get_options().is_check_roadblocking() == false || get_roadblock().numberRoadblocking(lc, get_gate_library()) == 0 ) {

                            if(get_options().is_toxicity() == false || Toxicity.mostToxicRow(lc) > get_options().get_toxicity_threshold()) {
                                get_logic_circuits().add(new LogicCircuit(lc));

                                set_best_score( B_score );

                                if(get_best_score() > max_score) {
                                    max_score = get_best_score();
                                    logger.info("  iteration " + String.format("%4s", i) + ": score = " + String.format("%6.2f", get_best_score()));
                                }
                            }
                        }
                    }
                }
                else { //score did not increase
                    revert(lc, save_lc);
                }
            }



            logger.info("Trajectory " + (traj+1) + " of " + get_options().get_hill_trajectories());
            set_best_score( 0.0 );
            max_score = 0.0;

        }
    }





    private boolean isNextGateCurrentlyUsed(LogicCircuit A_lc, Gate B_gate) {
        boolean is_used = false;
        for(int i=0; i<A_lc.get_logic_gates().size(); ++i) {
            String gate_name = A_lc.get_logic_gates().get(i).Name;
            if(B_gate.Name.equals(gate_name)) {
                is_used = true;
                break;
            }
        }

        return is_used;
    }


    private Gate getNextGate(LogicCircuit lc, Gate A_gate) {

        ArrayList<Gate> gates_of_type = new ArrayList<Gate>(get_gate_library().get_GATES_BY_TYPE().get(A_gate.Type).values());

        HashMap<String, Gate> allowed_B_gates = new HashMap<String, Gate>();

        for(Gate g: gates_of_type) {

            //disallow same gate
            if(g.Name.equals(A_gate.Name)) {
                continue;
            }

            //allow RBS variant
            if(g.Group.equals(A_gate.Group)) {
                //logger.info("allowing RBS variant " + A_gate.Name + ": " + g.Name);
                allowed_B_gates.put(g.Name, g);
            }

            //allow non-duplicate groups
            if (!currentlyAssignedGroup(lc, g.Group)) {
                allowed_B_gates.put(g.Name, g);
            }

            //allow swap
            if(currentlyAssigned(lc, g.Name)) {
                allowed_B_gates.put(g.Name, g);
            }

        }


        ArrayList<String> allowed_B_gate_names = new ArrayList<String>( allowed_B_gates.keySet());
        Collections.shuffle(allowed_B_gate_names, new Random(SEED+COUNTER));
        COUNTER++;
        String B_gate_name = allowed_B_gate_names.get(0);

        /*logger.info("allowed B: " + allowed_B_gate_names.toString());
        logger.info("Current assignment " + lc.printAssignment());
        logger.info("A_gate " + A_gate.Name);
        logger.info("B_gate " + B_gate_name);
        */

        return get_gate_library().get_GATES_BY_NAME().get(B_gate_name);

    }


    //debugging purposes.
    private void checkReuseError(LogicCircuit lc) {
        for(int i=0; i<lc.get_logic_gates().size()-1; ++i) {
            for(int j=i+1; j<lc.get_logic_gates().size(); ++j) {
                if(lc.get_logic_gates().get(i).Group.equals(lc.get_logic_gates().get(j).Group)) {
                    throw new IllegalStateException("Repressor reuse error in simulated annealing, \n" + lc.get_logic_gates().get(i).Name + " " + lc.get_logic_gates().get(j).Name);
                }
            }
        }
    }

    //if rejected, reset the Name for all logic gates.
    private void revert(LogicCircuit B_lc, LogicCircuit A_lc) {

        for(int i=0; i<A_lc.get_logic_gates().size(); ++i) {
            B_lc.get_logic_gates().get(i).Name      = A_lc.get_logic_gates().get(i).Name;
            B_lc.get_logic_gates().get(i).Group     = A_lc.get_logic_gates().get(i).Group;
            B_lc.get_logic_gates().get(i).Regulator = A_lc.get_logic_gates().get(i).Regulator;
        }

        Evaluate.evaluateCircuit(B_lc, get_gate_library(), get_options());
        Toxicity.evaluateCircuitToxicity(B_lc, get_gate_library());
    }


    private boolean currentlyAssigned(LogicCircuit lc, String gate_name) {

        for(Gate g: lc.get_logic_gates()) {

            if(g.Name.equals(gate_name)) {
                return true;
            }
        }

        return false;
    }

    /////////////////////////
    //
    // Private member data
    //
    /////////////////////////

    private Logger logger  = Logger.getLogger(getClass());
}
