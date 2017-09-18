package org.cellocad.MIT.dnacompiler;

import lombok.Getter;
import lombok.Setter;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by Bryan Der on 8/4/15.
 */
public class BuildCircuitsPermuteNOR extends BuildCircuits {


    public BuildCircuitsPermuteNOR(Args options, GateLibrary gate_library, Roadblock roadblock) {
        super(options, gate_library, roadblock);
    }
    /***********************************************************************

     Synopsis    [ ]

     Currently hard-coded to handle a single gate type (i.e. NOR/NOT).

     ***********************************************************************/
    @Override
    public void buildCircuits() {
        logger = Logger.getLogger(getThreadDependentLoggername());
        logger.info("Building circuits by permute");

        set_logic_circuits( new ArrayList<LogicCircuit>() );

        LogicCircuit lc = get_unassigned_lc();



        Integer n_gates_circuit = lc.get_logic_gates().size();
        Integer n_gates_library = get_gate_library().get_GATES_BY_NAME().keySet().size();

        permuteNORGateIndices(lc, get_gate_library());

        logger.info("enumerate assignments");
        enumerateAssignments();

        logger.info("evaluate assignments");
        evaluateAssignments();

    }





    /***********************************************************************

     Synopsis    [ ]

     Permute N-choose-K, where N = number of repressors and K = number of logic gates in the circuit.

     ***********************************************************************/
    public void permuteNORGateIndices(LogicCircuit lc, GateLibrary gate_library) {
        _NOR_indexes_set = new ArrayList<int[]>();

        int n_nor_gates = lc.get_logic_gates().size();
        int n_groups = gate_library.get_GATES_BY_GROUP().get(Gate.GateType.NOR).keySet().size();

        if(n_nor_gates <= n_groups) { //permute all combinations
            int n_available_gates=n_groups;
            int[] n = new int[n_nor_gates];
            int[] Nr = new int[n_available_gates];
            for (int i = 0; i<n_available_gates; ++i){
                Nr[i] = n_available_gates-1;
            }

            //calculate n-choose-k combinations (Nr choose n)
            int nchoosek_combinations = 1;
            for(int i=n_available_gates; i>n_available_gates-n_nor_gates; --i) {
                nchoosek_combinations *= i;
            }

            logger.info("n NOR gates:  " + n_nor_gates);
            logger.info("n repressors: " + n_available_gates);
            logger.info("n choose k:   " + nchoosek_combinations);


            Permute.getIndexProduct(_NOR_indexes_set, n, Nr, 0);  //_indexes_set gets populated here

            logger.info("Permuting repressor assignments: " + Arrays.toString(_NOR_indexes_set.get(0)) + " to " + Arrays.toString(_NOR_indexes_set.get(_NOR_indexes_set.size()-1)));

        }
        else {
            logger.info("not enough repressors for " + n_nor_gates + " NOR gates");
        }
    }


    /***********************************************************************

     Synopsis    [ ]

     For a given repressor assignment, permute all RBS variant combinations.


     Example: [PhlF-rbs1, Phlf-rbs2], [QacR-rbs1], [SrpR-rbs0, SrpR-rbs1, SrpR-rbs2, SrpR-rbs3]
     we want to permute from [0,0,0] to [1,0,3]

     1: indx = 0;
     0: indx = 1;
     3: indx = 2;

     asn.length = 3; (asn stands for assignment)

     rbs_assignment = ArrayList<int[]>:
     [0, 0, 0]
     [1, 0, 0]
     [0, 0, 1]
     [1, 0, 1]
     [0, 0, 2]
     [1, 0, 2]
     [0, 0, 3]
     [1, 0, 3]


     repr_assignment:
     used to figure out [1,0,3]: 3 repressors, and #variants per repressor

     ***********************************************************************/
    public static void permuteRBS(int[] asn, ArrayList< ArrayList<Gate>> repr_assignment, ArrayList<int[]> rbs_assignment ) {

        rbs_assignment.add(asn.clone());
        //logger.info(Arrays.toString(asn));

        //if max is [1,0,3] and asn=[1,0,3], permutation is complete.
        boolean complete = true;
        for(int i=0; i<asn.length; ++i) {
            if(asn[i] < repr_assignment.get(i).size() - 1) {
                complete = false;
            }
        }
        if(complete == true)
            return;


        asn[0] += 1;


        /*
        if a column has reached its max, increment the next column.

        0,0,0
        1,0,0
                2,0,0 -> 0,1,0 -> 0,0,1
        0,0,1
        1,0,1
                2,0,1 -> 0,1,1 -> 0,0,2
        0,0,2
        1,0,2
                2,0,2 -> 0,1,2 -> 0,0,3

         */
        for(int j=0; j<asn.length; ++j) {
            if(asn[j] > repr_assignment.get(j).size() - 1) {
                asn[j] = 0;
                asn[j+1]++;
            }
        }


        permuteRBS(asn, repr_assignment, rbs_assignment);

    }

    /***********************************************************************

     Synopsis    [ ]

     For each repressor assignment:
     check for roadblocking
     permute all RBS variant combinations
     score all RBS variant combinations

     ***********************************************************************/
    public void enumerateAssignments(){
        logger.info("Enumerating logic circuits...");
        logger.info("_NOR_indexes_set " + _NOR_indexes_set.size());


        for(int[] NOR_indexes: _NOR_indexes_set){

            LogicCircuit lc = get_unassigned_lc();

            ArrayList< ArrayList<Gate>> repr_assignment = new ArrayList< ArrayList<Gate>>();
            ArrayList<int[]> rbs_assignment = new ArrayList<int[]>();
            int asn[] = new int[NOR_indexes.length];

            for(int i = 0; i<NOR_indexes.length; ++i){    // assign DB gate to logic gate

                /**
                 * Warning: hard-coded NOR
                 */
                ArrayList<Gate> repressor = get_gate_library().getGatesByGroupByIndex( Gate.GateType.NOR, NOR_indexes[i] ); //get from hashmap (integer maps to arraylist of gates)
                repr_assignment.add(repressor);
                asn[i] = 0; //initialize to 0th element
            }


            /*if(get_options().is_check_roadblocking()) {

                //0th index is fine, don't care about RBS variants for roadblocking
                for(int i=0; i<lc.get_logic_gates().size(); ++i) {
                    lc.get_logic_gates().get(i).Name  = repr_assignment.get(i).get(0).Name;
                }

                //want to check roadblocking before permuting RBS variants
                boolean illegal_n_roadblocking = get_roadblock().illegalRoadblocking(lc, get_gate_library());
                if (illegal_n_roadblocking) {
                    continue;
                }
            }*/


            //0                permuteRBS is recursive which is why indx is needed, but start at 0
            //asn              permuteRBS is recursive and modifies asn[], but it starts initialized with all 0's
            //repr_assignment  this is the assignment of repressor proteins, but a repressor can have multiple transfer functions due to RBS strength
            //rbs_assignment   this is the assignment of repressor rbs variants... arraylist of arrays starts empty and is filled during recursion
            permuteRBS(asn, repr_assignment, rbs_assignment);


            for(int i=0; i<rbs_assignment.size(); ++i) {

                int rbs_asn[] = rbs_assignment.get(i);


                assignNORGates(lc, repr_assignment, rbs_asn);

                Evaluate.refreshGateAttributes(lc, get_gate_library());

                //want to check roadblocking before permuting RBS variants
                boolean illegal_n_roadblocking = get_roadblock().illegalRoadblocking(lc, get_gate_library());
                if (illegal_n_roadblocking) {
                    continue;
                }


                ArrayList<String> assignment_gates = new ArrayList<>();

                for(Gate g: lc.get_logic_gates()) {
                    assignment_gates.add(g.name);
                }

                this._assignment_gate_names.add(assignment_gates);

                _n_total_assignments++;
            }
        }
    }


    public void evaluateAssignments() {

        LogicCircuit lc = get_unassigned_lc();

        int counter = 0;

        for(ArrayList<String> gate_assignment: this._assignment_gate_names) {

            //logger.info(gate_assignment.toString());

            counter++;

            if (counter % 1000 == 0) {
                logger.info(counter);
            }

            //logger.info(gate_assignment.toString());

            for (int a = 0; a < gate_assignment.size(); ++a) {
                String gate_name = gate_assignment.get(a);
                String group_name = get_gate_library().get_GATES_BY_NAME().get(gate_name).group;

                Gate g = lc.get_logic_gates().get(a);
                g.name = gate_name;


                Evaluate.evaluateCircuit(lc, get_gate_library(), get_options());


                if(get_options().is_toxicity()) {
                    Toxicity.evaluateCircuitToxicity(lc, get_gate_library());
                    double growth_score = Toxicity.mostToxicRow(lc);

                    if (growth_score < get_options().get_toxicity_threshold()) {
                        continue;
                    }
                }


                Double score = lc.get_scores().get_score();

                if(score > this.get_best_score()) {
                    this.set_best_score(score);
                    get_logic_circuits().add(new LogicCircuit(lc));
                }
            }
        }
    }


    /***********************************************************************

     Synopsis    [ ]

     Set all logic gate names in the circuit, then evaluateCircuit(), return score

     ***********************************************************************/
    private void assignNORGates(LogicCircuit lc, ArrayList< ArrayList<Gate>> repr_assignment, int[] rbs_asn) {

        //String gates_assignment = "";
        for(int i=0; i<lc.get_logic_gates().size(); ++i) {
            lc.get_logic_gates().get(i).name  = repr_assignment.get(i).get(rbs_asn[i]).name;
            //gates_assignment += repr_assignment.get(i).get(rbs_asn[i]).Name + " ";
        }
        //logger.info(gates_assignment);

    }


    @Getter @Setter
    private ArrayList< ArrayList<String> > _assignment_gate_names = new ArrayList<>();


    public int _n_total_assignments = 0;

    public static ArrayList<int[]> _NOR_indexes_set;

    private Logger logger  = Logger.getLogger(getClass());
}
