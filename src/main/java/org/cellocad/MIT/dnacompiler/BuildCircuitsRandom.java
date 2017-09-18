package org.cellocad.MIT.dnacompiler;
/**
 * Created by Bryan Der on 3/26/14.
 */

import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;


public class BuildCircuitsRandom extends BuildCircuits {

    public BuildCircuitsRandom(Args options, GateLibrary gate_library, Roadblock roadblock) {
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
        logger.info("Enumerating logic circuits using random...");

        double max_score = 0.0;

        LogicCircuit lc = new LogicCircuit(get_unassigned_lc());

        for(int traj=0; traj<get_options().get_hill_trajectories(); ++traj) {

            set_best_score( 0.0 );


            String follow_best = "";

            String b = get_options().get_output_directory() + "/b" + String.format("%02d", traj) + ".txt";


            for (int iter = 0; iter < get_options().get_hill_iterations(); ++iter) {

                set_n_total_assignments(get_n_total_assignments() + 1);

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
                Toxicity.evaluateCircuitToxicity(lc, get_gate_library());



                Util.fileWriter(b, follow_best, true);

                follow_best = iter + " " + get_best_score() + "\n";


                Evaluate.evaluateCircuit(lc, get_gate_library(), get_options());
                Toxicity.evaluateCircuitToxicity(lc, get_gate_library());

                int B_rb = get_roadblock().numberRoadblocking(lc, get_gate_library());

                Double B_score = lc.get_scores().get_score();

                double B_growth = Toxicity.mostToxicRow(lc);

                //toxicity
                if(B_growth < get_options().get_toxicity_threshold()) {
                    //logger.info("toxic");
                    continue;
                }

                //roadblocking
                if(B_rb > 0) {
                    //logger.info("roadblock");
                    continue;
                }

                //noise margin
                Evaluate.evaluateCircuitNoiseMargin(lc, get_options());
                if(lc.get_scores().is_noise_margin_contract() == false) {
                    //logger.info("noise margin");
                    continue;
                }

                if(B_score > get_best_score()) {
                    set_best_score( B_score );

                    if(get_best_score() > max_score) {
                        //_logic_circuits.add(new LogicCircuit(lc));
                        max_score = get_best_score();
                        logger.info("RANDOM " + get_n_total_assignments() + " " + Util.sc(get_best_score()) + " ACCEPT ");
                    }
                }

            }

            logger.info(traj + " best score " + get_best_score());
            set_best_score( 0.0 );
            max_score = 0.0;
        }

    }


    /////////////////////////
    //
    // Private member data
    //
    /////////////////////////

    private Logger logger  = Logger.getLogger(getClass());
}
