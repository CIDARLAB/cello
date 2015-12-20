package org.cellocad.MIT.dnacompiler;
/**
 * Created by Bryan Der on 3/26/14.
 */

import lombok.Getter;
import lombok.Setter;
import org.cellocad.MIT.dnacompiler.Gate.GateType;

import java.util.ArrayList;
import java.util.HashMap;
/**
 * Base class for the logic gate assignment algorithms.
 *
 * 1. set unassigned circuit (if multiple, this is because Args.permute_inputs == true
 * 2. build circuits
 * 3. add the good circuits to the list
 *
 */

public class BuildCircuits {

    public BuildCircuits(){

    }

    public BuildCircuits(Args options, GateLibrary gate_library, Roadblock roadblock) {
        _options = options;
        _gate_library = gate_library;
        _roadblock = roadblock;
    }


    //classes that extend BuildCircuits should @Override the buildCircuits() method
    public void buildCircuits(){
    }


    //list of available assignment algorithms
    public enum AssignmentAlgorithm{
        abstract_only,
        breadth_first,
        permute,
        random,
        sim_annealing,
        hill_climbing,
        steepest_ascent,
        reload,
        preset,
        sequential
    }

    //the string parsed from the command-line arguments must match one of the algorithms in the 'enum'
    public static AssignmentAlgorithm getAssignmentAlgorithm(String assignment_algorithm) {

        for (AssignmentAlgorithm a : AssignmentAlgorithm.values()) {
            if (a.name().equals(assignment_algorithm)) {
                return a;
            }
        }

        return null;
    }


    /////////////////////////
    //
    // Private member data
    //
    /////////////////////////

    //permutation for each gate type.
    //if there are 3 NOR gates and 10 in the library, 10*9*8 total permutations
    //if there are 2 AND gates and 2 in the library, 2*1 total permutation
    @Getter @Setter private static HashMap<GateType, ArrayList<int[]>> _indexes_map = new HashMap<GateType, ArrayList<int[]>>();

    //starting circuit: input and output gates assigned, but logic gates unassigned
    @Getter @Setter private LogicCircuit _unassigned_lc;

    //the good circuits are added to this list
    @Getter @Setter private ArrayList<LogicCircuit> _logic_circuits = new ArrayList<LogicCircuit>();

    //track the best circuit for accept/reject decisions
    @Getter @Setter private double _best_score = 0.0;
    @Getter @Setter private LogicCircuit _best_lc = new LogicCircuit();

    //statistics
    @Getter @Setter private int _n_total_assignments = 0;
    @Getter @Setter private int _n_roadblocking = 0;
    @Getter @Setter private int _n_toxic = 0;


    //values set in constructor
    @Getter @Setter private Args _options; // options such as noise_margin, toxicity, etc. are needed during assignment
    @Getter @Setter private GateLibrary _gate_library; // gates from the library are assigned to Boolean gates in the circuit
    @Getter @Setter private Roadblock _roadblock; //

    @Getter @Setter private String threadDependentLoggername;
}
