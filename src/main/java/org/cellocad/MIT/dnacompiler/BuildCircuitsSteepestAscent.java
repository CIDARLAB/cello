package org.cellocad.MIT.dnacompiler;
/**
 * Created by Bryan Der on 3/26/14.
 */

import org.apache.log4j.Logger;

import java.util.*;


public class BuildCircuitsSteepestAscent extends BuildCircuits {

    public BuildCircuitsSteepestAscent(Args options, GateLibrary gate_library, Roadblock roadblock) {
        super(options, gate_library, roadblock);
    }

    private boolean currentlyAssignedGroup(LogicCircuit lc, String group_name) {

        for(Gate g: lc.get_logic_gates()) {

            if(g.group.equals(group_name)) {
                return true;
            }
        }

        return false;
    }


    @Override
    public void buildCircuits(){
        logger = Logger.getLogger(getThreadDependentLoggername());
        logger.info("Enumerating logic circuits using steepest ascent...");

        Random generator = new Random();


        double max_score = 0.0;

        LogicCircuit lc = new LogicCircuit(get_unassigned_lc());


        for(int traj=0; traj<get_options().get_hill_trajectories(); ++traj) {

            set_best_score( 0.0 );

            //initial random

            for (int i = 0; i < lc.get_logic_gates().size(); ++i) {
                Gate g = lc.get_logic_gates().get(i);

                g.name = "null";
            }

            for (int i = 0; i < lc.get_logic_gates().size(); ++i) {

                Gate g = lc.get_logic_gates().get(i);

                LinkedHashMap<String, ArrayList<Gate>> groups_of_type = get_gate_library().get_GATES_BY_GROUP().get(g.type);

                ArrayList<String> group_names = new ArrayList<String>(groups_of_type.keySet());

                Collections.shuffle(group_names);

                for (String group_name : group_names) {

                    if (!currentlyAssignedGroup(lc, group_name)) {

                        ArrayList<Gate> gates_of_group = new ArrayList<Gate>(groups_of_type.get(group_name));

                        Collections.shuffle(gates_of_group);

                        g.name = gates_of_group.get(0).name;
                    }
                }
            }

            Evaluate.evaluateCircuit(lc, get_gate_library(), get_options());
            for (Gate g : lc.get_logic_gates()) {
                Evaluate.evaluateGate(g, get_options());
            }

            //logger.info(lc.printGraph());


            for (int i = 0; i < get_options().get_hill_iterations(); ++i) {

                //choose Gate A

                LogicCircuit save_lc = new LogicCircuit(lc);

                int A_gate_index = generator.nextInt(lc.get_logic_gates().size());

                Gate A_gate = lc.get_logic_gates().get(A_gate_index);




                HashMap<String, Double> B_gate_choices = getGateBChoices(lc, A_gate.name);

                String best_B_gate_name = "";
                Double best_B_gate_score = 0.0;

                for(String B_gate_name: B_gate_choices.keySet()) {

                    if(B_gate_choices.get(B_gate_name) > best_B_gate_score) {
                        best_B_gate_name  = B_gate_name;
                        best_B_gate_score = B_gate_choices.get(B_gate_name);
                    }

                }



                if(get_gate_library().get_GATES_BY_NAME().containsKey(best_B_gate_name)) {

                    Gate B_gate = get_gate_library().get_GATES_BY_NAME().get(best_B_gate_name);

                    String A_gate_name  = new String(A_gate.name);
                    String B_gate_name  = new String(B_gate.name);
                    String A_gate_group = new String(A_gate.group);
                    String B_gate_group = new String(B_gate.group);
                    String A_regulator  = new String(A_gate.group);
                    String B_regulator  = new String(B_gate.group);


                    //1. if second gate is used, swap
                    if(isNextGateCurrentlyUsed(lc, B_gate)) {

                        int B_gate_index = 0;
                        for(int j=0; j<lc.get_logic_gates().size(); ++j) {
                            if(lc.get_logic_gates().get(j).name.equals(B_gate.name)) {
                                B_gate_index = j;
                            }
                        }

                        lc.get_logic_gates().get(A_gate_index).name  = B_gate_name;
                        lc.get_logic_gates().get(B_gate_index).name  = A_gate_name;
                        lc.get_logic_gates().get(A_gate_index).group = B_gate_group;
                        lc.get_logic_gates().get(B_gate_index).group = A_gate_group;
                        lc.get_logic_gates().get(A_gate_index).regulator = B_regulator;
                        lc.get_logic_gates().get(B_gate_index).regulator = A_regulator;

                    }

                    //2. if second gate is unused, substitute
                    else {
                        lc.get_logic_gates().get(A_gate_index).name      = B_gate_name;
                        lc.get_logic_gates().get(A_gate_index).group     = B_gate_group;
                        lc.get_logic_gates().get(A_gate_index).regulator = B_regulator;
                    }


                    set_n_total_assignments( get_n_total_assignments() + 1 );

                    Evaluate.evaluateCircuit(lc, get_gate_library(), get_options());

                    if(get_options().is_toxicity()) {
                        Toxicity.evaluateCircuitToxicity(lc, get_gate_library());
                    }


                    if(get_options().is_check_roadblocking() == false || get_roadblock().numberRoadblocking(lc, get_gate_library()) == 0 ) {

                        if(get_options().is_toxicity() == false || Toxicity.mostToxicRow(lc) > get_options().get_toxicity_threshold()) {

                            if(get_options().is_noise_margin() && lc.get_scores().is_noise_margin_contract() == true) {

                                set_best_score( lc.get_scores().get_score() );
                                get_logic_circuits().add(new LogicCircuit(lc));

                            }
                        }

                    }

                }

            }

            logger.info("STEEPEST_ASCENT " + String.format("%-8s", "#" + traj) + " score:" + Util.sc(get_best_score()) + " tox:" + Util.sc(Toxicity.mostToxicRow(lc)) + " nm:" + Util.sc(lc.get_scores().get_noise_margin()) + " rb:" + get_roadblock().numberRoadblocking(lc, get_gate_library()));
            set_best_score ( 0.0 );
        }

    }



    private boolean isNextGateCurrentlyUsed(LogicCircuit A_lc, Gate B_gate) {
        boolean is_used = false;
        for(int i=0; i<A_lc.get_logic_gates().size(); ++i) {
            String gate_name = A_lc.get_logic_gates().get(i).name;
            if(B_gate.name.equals(gate_name)) {
                is_used = true;
                break;
            }
        }

        return is_used;
    }



    private HashMap<String, Double> getGateBChoices(LogicCircuit lc, String A_gate_name) {

        LogicCircuit save_lc = new LogicCircuit(lc);
        Evaluate.evaluateCircuit(save_lc, get_gate_library(), get_options());

        HashMap<String, Double> gate_B_choices = new HashMap<>();

        HashSet<String> allowed_B_gates = new HashSet<>();

        Gate A_gate = get_gate_library().get_GATES_BY_NAME().get(A_gate_name);

        int A_gate_index = 0;
        for(int j=0; j<lc.get_logic_gates().size(); ++j) {
            if(lc.get_logic_gates().get(j).name.equals(A_gate.name)) {
                A_gate_index = j;
            }
        }

        ArrayList<Gate> gates_of_type = new ArrayList<Gate>(get_gate_library().get_GATES_BY_TYPE().get(A_gate.type).values());


        for(Gate g: gates_of_type) {

            //disallow same gate
            if(g.name.equals(A_gate.name)) {
                continue;
            }

            //allow RBS variant
            if(g.group.equals(A_gate.group)) {
                allowed_B_gates.add(g.name);
            }

            //allow non-duplicate groups
            if (!currentlyAssignedGroup(lc, g.group)) {
                allowed_B_gates.add(g.name);
            }

        }

        int A_rb = get_roadblock().numberRoadblocking(save_lc, get_gate_library());
        Double A_growth = Toxicity.mostToxicRow(save_lc);
        Double A_score = save_lc.get_scores().get_score();



        for(String B_gate_name: allowed_B_gates) {

            Gate B_gate = get_gate_library().get_GATES_BY_NAME().get(B_gate_name);

            A_gate_name  = new String(A_gate.name);
            B_gate_name  = new String(B_gate.name);
            String A_gate_group = new String(A_gate.group);
            String B_gate_group = new String(B_gate.group);
            String A_regulator  = new String(A_gate.group);
            String B_regulator  = new String(B_gate.group);

            //1. if second gate is used, swap
            if(isNextGateCurrentlyUsed(lc, B_gate)) {

                int B_gate_index = 0;
                for(int j=0; j<lc.get_logic_gates().size(); ++j) {
                    if(lc.get_logic_gates().get(j).name.equals(B_gate.name)) {
                        B_gate_index = j;
                    }
                }

                lc.get_logic_gates().get(A_gate_index).name  = B_gate_name;
                lc.get_logic_gates().get(B_gate_index).name  = A_gate_name;
                lc.get_logic_gates().get(A_gate_index).group = B_gate_group;
                lc.get_logic_gates().get(B_gate_index).group = A_gate_group;
                lc.get_logic_gates().get(A_gate_index).regulator = B_regulator;
                lc.get_logic_gates().get(B_gate_index).regulator = A_regulator;

            }

            //2. if second gate is unused, substitute
            else {
                lc.get_logic_gates().get(A_gate_index).name      = B_gate_name;
                lc.get_logic_gates().get(A_gate_index).group     = B_gate_group;
                lc.get_logic_gates().get(A_gate_index).regulator = B_regulator;
            }


            Evaluate.evaluateCircuit(lc, get_gate_library(), get_options());

            if(get_options().is_toxicity()) {
                Toxicity.evaluateCircuitToxicity(lc, get_gate_library());
            }

            int B_rb = get_roadblock().numberRoadblocking(lc, get_gate_library());
            Double B_growth = Toxicity.mostToxicRow(lc);
            Double B_score = new Double(lc.get_scores().get_score());


            revert(lc, save_lc);



            /*if(B_score > A_score) {
                gate_B_choices.put(B_gate_name, B_score);
            }*/

            if(A_rb > 0) {
                if(B_rb < A_rb) {
                    //logger.info("roadblocking, adding " + B_gate_name);
                    gate_B_choices.put(B_gate_name, B_score);
                }
            }

            else if(A_growth < get_options().get_toxicity_threshold()) {
                if(B_growth > A_growth) {
                    //logger.info("toxic, adding " + B_gate_name);
                    gate_B_choices.put(B_gate_name, B_growth);
                }
            }

            else {
                if(B_score > A_score) {

                    if(B_growth > get_options().get_toxicity_threshold() && B_rb == 0) {
                        //logger.info("regular, adding " + B_gate_name);
                        gate_B_choices.put(B_gate_name, B_score);
                    }
                }
            }


        }

        //System.exit(-1);


        return gate_B_choices;

    }



    //debugging purposes.
    private void checkReuseError(LogicCircuit lc) {
        for(int i=0; i<lc.get_logic_gates().size()-1; ++i) {
            for(int j=i+1; j<lc.get_logic_gates().size(); ++j) {
                if(lc.get_logic_gates().get(i).group.equals(lc.get_logic_gates().get(j).group)) {
                    throw new IllegalStateException("Repressor reuse error in simulated annealing, \n" + lc.get_logic_gates().get(i).name + " " + lc.get_logic_gates().get(j).name);
                }
            }
        }
    }

    //if rejected, reset the Name for all logic gates.
    private void revert(LogicCircuit B_lc, LogicCircuit A_lc) {

        for(int i=0; i<A_lc.get_logic_gates().size(); ++i) {
            B_lc.get_logic_gates().get(i).name      = A_lc.get_logic_gates().get(i).name;
            B_lc.get_logic_gates().get(i).group     = A_lc.get_logic_gates().get(i).group;
            B_lc.get_logic_gates().get(i).regulator = A_lc.get_logic_gates().get(i).regulator;
        }

        Evaluate.evaluateCircuit(B_lc, get_gate_library(), get_options());
    }

    private Logger logger  = Logger.getLogger(getClass());
}
