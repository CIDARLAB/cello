package org.cellocad.MIT.dnacompiler;


import org.apache.log4j.Logger;

import java.util.ArrayList;

/***********************************************************************
 Synopsis    [ Default algorithm to assign repressors to gates.]

 Exhaustive search.  Intractable as circuit size grows, but guarantees finding the best score.

 Two permutations are required:
 1. Permute assignment of repressors to gates.
 2. Permute choice of all possible RBS-variants of the repressor assignment.

 ***********************************************************************/

public class BuildCircuitsPreset extends BuildCircuits {


    public BuildCircuitsPreset(Args options, GateLibrary gate_library, Roadblock roadblock) {
        super(options, gate_library, roadblock);
    }



    @Override
    public void buildCircuits() {
        logger = Logger.getLogger(getThreadDependentLoggername());

        LogicCircuit lc = get_unassigned_lc();

        ArrayList<String> gate_names = Util.fileLines(get_options().get_fin_preset());
        for(String gate_name: gate_names) {
            logger.info(gate_name);
        }

        for(int i=0; i<lc.get_logic_gates().size(); ++i) {
            Gate g = lc.get_logic_gates().get(i);
            g.name = gate_names.get(i);
            g.group = get_gate_library().get_GATES_BY_NAME().get(g.name).group;
        }

        Evaluate.evaluateCircuit(lc, get_gate_library(), get_options());
        for(Gate g: lc.get_Gates()) {
            Evaluate.evaluateGate(g, get_options());
        }
        logger.info(lc.printGraph());

        get_logic_circuits().add(lc);
    }



    private Logger logger  = Logger.getLogger(getClass());

}
