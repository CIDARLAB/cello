package org.cellocad.MIT.dnacompiler;
/**
 * Created by Bryan Der on 3/26/14.
 */

import lombok.Getter;
import lombok.Setter;
import org.cellocad.MIT.dnacompiler.Gate.GateType;

import java.util.*;

//import org.sbolstandard.core2.ComponentDefinition;
//import org.sbolstandard.core2.ModuleDefinition;

/**
 *
 * LogicCircuit is used as the DAG data structure, containing Gates connected by Wires.
 *
 * An abstract circuit is a Boolean circuit.
 * An assigned circuit has repressors assigned to gates.
 * Assigned LogicCircuits are ranked by score.
 *
 */
public class LogicCircuit{

    /**
     *
     * Default constructor
     *
     */
    public LogicCircuit(){
        _Wires = new ArrayList<Wire>();
        _Gates = new ArrayList<Gate>();
    }


    /**
     *
     * Constructor to make LogicCircuit from DAGW (dag = abstract circuit)
     *
     */
    public LogicCircuit(ArrayList<Gate> Gates, ArrayList<Wire> Wires){ // used to make abstract circuit from frontend handoff

        _Gates = new ArrayList<Gate>();
        _Wires = new ArrayList<Wire>();
        for(Gate g:Gates) { _Gates.add(g); }
        for(Wire w:Wires) { _Wires.add(w); }

        reconnectCircuit();
        categorizeGates();
        setGateTypes();
    }


    /**
     *
     * Copy constructor
     *
     */
    public LogicCircuit(LogicCircuit lc){ // copy constructor
        _Gates = lc.get_Gates();
        _Wires = lc.get_Wires();

        _index = _number_of_logic_circuits;
        _number_of_logic_circuits++;
        _scores = new Scores(lc.get_scores());
        _truthtable = lc._truthtable;
        _netlist = lc._netlist;
        _gate_types = lc._gate_types;
        _assignment_name = lc._assignment_name;

        reconnectCircuit();
        categorizeGates();
        setGateTypes();
        LogicCircuitUtil.sortGatesByStage(this);

        // re-arrange the _Gates in the order of output, logic, and input
        ArrayList<Gate> all_Gates = new ArrayList<Gate>();
        all_Gates.addAll(_output_gates);
        all_Gates.addAll(_logic_gates);
        all_Gates.addAll(_input_gates);
        _Gates = all_Gates;
    }


    /**
     *
     * Gate and Wire objects must "point" to each other in memory in a connected circuit
     *
     */
    public void reconnectCircuit() {
        //Copy _Gates
        ArrayList<Gate> new_Gates = new ArrayList<Gate>();
        ArrayList<Wire> new_Wires = new ArrayList<Wire>();

        if (_Gates != null) {
            for (int i=0; i<_Gates.size(); i++) {
                Gate g = new Gate(_Gates.get(i));
                new_Gates.add(g);
            }
        }
        //Copy _Wires
        if (_Wires != null) {
            for (int i = 0; i< _Wires.size(); i++) {
                Wire w = new Wire(_Wires.get(i));
                new_Wires.add(w);
            }
        }

        //after making deep copies of _Gates/_Wires, the _Gates/_Wires have the proper data but the new objects need to be reconnected in memory
        for (int i = 0; i < new_Gates.size(); i++) {
            if (_Gates.get(i).outgoing != null) {                     //Outgoing is a wire
                int index = _Gates.get(i).outgoing.index;
                for(Wire w: new_Wires) {
                    if(w.index == index) { new_Gates.get(i).outgoing = w; }
                }
            }
        }
        for (int i = 0; i < new_Wires.size(); i++) {
            if (_Wires.get(i).from != null) {                        //From is a gate
                int index = _Wires.get(i).from.index;
                for(Gate g: new_Gates) {
                    if(g.index == index) { new_Wires.get(i).from = g; }
                }
            }
            if (_Wires.get(i).to != null) {                          //To is a gate
                int index = _Wires.get(i).to.index;
                for(Gate g: new_Gates) {
                    if(g.index == index) { new_Wires.get(i).to = g; }
                }
            }
            if (_Wires.get(i).next != null) {                        //Next is a wire
                int index = _Wires.get(i).next.index;
                for(Wire w: new_Wires) {
                    if(w.index == index) { new_Wires.get(i).next = w; }
                }
            }
        }

        _Gates = new_Gates;
        _Wires = new_Wires;
    }
    
    public void reconnectCircuitByIndexes() {

        for(Gate g: this.get_Gates()) {

            for(Wire w: this.get_Wires()) {
                if (g.outgoing_wire_index == w.index) {
                    g.outgoing = w;
                }
            }

            for(String x: g.get_variable_names()) {
                for(Wire w: g.get_variable_wires().get(x)) {

                    for(Gate g2: this.get_Gates()) {
                        if(w.from_index == g2.index) {
                            w.from = g2;
                        }
                        if(w.to_index == g2.index) {
                            w.to = g2;
                        }
                    }

                    for(Wire w2: this.get_Wires()) {
                        if(w.next_index == w2.index) {
                            w.next = w2;
                        }
                    }
                }
            }

        }

        for(Wire w: this.get_Wires()) {
            for(Gate g: this.get_Gates()) {
                if(w.from_index == g.index) {
                    w.from = g;
                }
                if(w.to_index == g.index) {
                    w.to = g;
                }
            }
        }

        for(Wire w1: this.get_Wires()) {
            for(Wire w2: this.get_Wires()) {
                if(w1.next_index == w2.index && w1.next_index != -1) {
                    w1.next = w2;
                }
            }
        }

    }


    public String toString(){
        String s = "";
        s += printGraph();
        s += LogicCircuit.this.printLogicRPU();

        return s;
    }


    /**
     *
     * prints a list of gates, and the children for each gate.
     * if it's an assigned circuit, also print the score and toxicity of each gate.
     *
     * Example:
     * ----- Logic Circuit #552 -----
     * OUTPUT_OR   0110              output_YFP        0  (2,1)   1.5905  tox:0.02
     * NOR         0100              NOT_rbs2-QacR     1  (4,3)           tox:0.05
     * NOR         0010              NOT_rbs0-SrpR     2  (5,3)           tox:0.93
     * NOR         1000              NOT_rbs2-LitR     3  (4,5)           tox:0.52
     * INPUT       0011              input_pTac        4
     * INPUT       0101              input_pTet        5
     *
     */
    public String printGraph(){
        String s = String.format("\n----- Logic Circuit #%d -----\n", _index);
        for (int i = 0; i < _Gates.size(); ++i) {
            Gate gi = _Gates.get(i);

            s += String.format("%-12s", gi.type);
            s += String.format("%-18s", BooleanLogic.logicString(gi.get_logics()));
            s += String.format("%-18s", gi.name);
            s += String.format("%-3d", gi.index);

            String child_indx = "(";

            int[] porder = gi.get_porder();

            String promoter_names = "P";

            if(porder != null) {
                for (int k = 0; k< porder.length; k++) {
                    promoter_names += "-" + gi.getChildren().get(porder[k]).name;
                }
            }


            for(Gate child: gi.getChildren()) {
                child_indx += child.index +",";
            }
            child_indx = child_indx.substring(0,child_indx.length()-1);
            if(gi.type != GateType.INPUT)
                child_indx += ")";

            s += String.format("%-12s", child_indx);

            s += String.format("%-30s", promoter_names);

            if(gi.get_scores().get_score() != -1.0000) {
                s += String.format("%6.2f", gi.get_scores().get_score()); //onoff_ratio or noise_margin
            }

            if(gi.get_toxicity().size() > 0) {
                s += "  tox:" + String.format("%-3.2f", Toxicity.mostToxicRow(gi));
            }

            s += "\n";
        }
        s += "\n";

        return s;
    }


    public String printRPUTable() {
        String s = "\n";

        s += String.format("%14s", "truth table") + "\t";
        for(int j=this.get_Gates().size()-1; j>=0; --j) {
            Gate g = this.get_Gates().get(j);

            String name;
            if(g.group.equals("") || g.group == null) {
               name = g.name;
            }
            else{
                name = g.group;
            }

            s += String.format("%7s", name) + "\t";
        }
        s += "\n";

        for(int i=0; i<get_Gates(GateType.OUTPUT, GateType.OUTPUT_OR).get(0).get_logics().size(); ++i){
            s += String.format("%14s", this.getLogicRow(i)) + "\t";

            for(int j=this.get_Gates().size()-1; j>=0; --j) {
                Gate g = this.get_Gates().get(j);

                if (g.get_logics().size() > 0 && g.get_outrpus().size() > 0){
                    s += String.format("%7s", String.format("%4.2f",g.get_outrpus().get(i))) + "\t";
                }
            }
            s += "\n";
        }

        return s;
    }

    public String printAssignment(){
        String s = "";
        for (int i = 0; i < _Gates.size(); ++i) {
            Gate gi = _Gates.get(i);

            s += String.format("%-3d", gi.get_distance_to_input());
            s += String.format("%-12s", gi.type);
            s += String.format("%-18s", BooleanLogic.logicString(gi.get_logics()));
            s += String.format("%-18s", gi.name);
            s += String.format("%-3d", gi.index);

            String child_indx = "(";
            for(Gate child: gi.getChildren()) {
                child_indx += child.index +",";
            }
            child_indx = child_indx.substring(0,child_indx.length()-1);
            if(gi.type != GateType.INPUT)
                child_indx += ")";

            s += String.format("%-12s", child_indx);

            s += "\n";
        }
        s += "\n";

        return s;
    }


    /**
     *
     *
     * print RPUs and incoming RPUs for all rows of all gates.
     * print scores for all gates.
     * print toxicity values if available.
     *
     * Example:
     *
     * Circuit_score = 1.59047     maxRPU = 51.79068    avgRPU = 28.63099    maxTox = 0.02418     avgTox = 0.02998
     *
     * output_YFP  Gate=1.59047
     * _OUTPUT_OR    [ 0 0 ]: 0        0.038   0.056:  0.094    multiplied_tox: 0.05
     * _OUTPUT_OR    [ 0 1 ]: 1        0.031   3.628:  3.659    multiplied_tox: 0.02
     * _OUTPUT_OR    [ 1 0 ]: 1        4.503   0.053:  4.557    multiplied_tox: 0.03
     * _OUTPUT_OR    [ 1 1 ]: 0        0.031   0.053:  0.084    multiplied_tox: 0.02
     *
     * NOT_rbs2-QacR  Gate=1.81409
     * _NOR    2     [ 0 0 ]: 0        0.020   4.886:  0.056    individual_tox: 0.05
     * _NOR    2     [ 0 1 ]: 1        0.020   0.269:  3.628    individual_tox: 0.05
     * _NOR    2     [ 1 0 ]: 0        8.792   0.279:  0.053    individual_tox: 0.05
     * _NOR    2     [ 1 1 ]: 0        8.792   0.267:  0.053    individual_tox: 0.05
     *
     */
    public String printLogicRPU() {


        //header
        String s = String.format("Circuit_score = %-8.5f", _scores.get_score());

        //if(Args.toxicity) {
            s += "    " + String.format("Cell_growth = %-8.5f", Toxicity.mostToxicRow(this));
        //}
        s += "\n\n";

        //body
        for(Gate gate: _Gates) {
            s += printLogicRPU(gate);
        }

        return s;
    }


    public String printLogicRPU(Gate g) {  //my improved output format

        String s = "";

        if (g.get_logics().size() > 0 && g.get_outrpus().size() > 0){

            s += g.name + "  Gate=" + String.format("%-6.5f",g.get_scores().get_score()) + "\n";

            for(int i=0; i<g.get_outrpus().size(); ++i){

                String child_rpu = "";
                if(g.type != GateType.INPUT) {
                    for(Gate child: g.getChildren()) {
                        child_rpu += String.format("%8.3f", child.get_outrpus().get(i));
                    }

                    child_rpu += String.format("%8.3f", g.get_inrpus().get("x").get(i));
                    //child_rpu = String.format("%4.3f", sum_incoming_rpus.get(i));
                }

                String dist2in = "    ";
                if(g.type != GateType.INPUT && g.type != GateType.OUTPUT_OR && g.type != GateType.OUTPUT) {
                    dist2in = String.format("%4s", g.get_distance_to_input());
                }
                //String tagged_gate_type = "_" + g.Type; // for grepping purposes to make truth table figure

                String logic_i = Integer.toString(g.get_logics().get(i));
                if(g.get_logics().get(i) == 2) {
                    logic_i = "-"; //dontcare
                }

                s += String.format("%11s", g.type) + " " + dist2in + " " + String.format("%11s: %-6s", getLogicRow(i), logic_i);
                //s += String.format("%11s: %6.3f", child_rpu, g.get_outrpus().get(i));
                s += String.format("%18s: %6.3f", child_rpu, g.get_outrpus().get(i));

                if(!g.get_toxicity().isEmpty()) {
                    if (g.get_toxicity().size() > 0 && g.type != GateType.OUTPUT && g.type != GateType.OUTPUT_OR)
                        s += String.format("%18s: %3.2f", "individual_tox", g.get_toxicity().get(i));
                    else if (g.get_toxicity().size() > 0 && (g.type == GateType.OUTPUT || g.type == GateType.OUTPUT_OR))
                        s += String.format("%18s: %3.2f", "multiplied_tox", g.get_toxicity().get(i));
                }

                s += "\n";
            }
        }
        s += "\n";
        return s;
    }


    public String printNetlist() {

        String s = "";

        for (int i = 0; i < _Gates.size(); ++i) {
            Gate gi = _Gates.get(i);

            s += String.format("%-3d", gi.index);
            s += String.format("%-12s", gi.type);

            String child_indx = "(";
            for(Gate child: gi.getChildren()) {
                child_indx += child.index +",";
            }
            child_indx = child_indx.substring(0,child_indx.length()-1);
            if(gi.type != GateType.INPUT)
                child_indx += ")";

            s += String.format("%-12s", child_indx);

            s += "\n";
        }


        return s;
    }

    /**
     *
     * Example:
     *
     * returned string for row=0 of a 3-input circuit: [ 0 0 0 ]
     * returned string for row=7 of a 3-input circuit: [ 1 1 1 ]
     */
    public String getLogicRow(int row) {
        String logic_row = "[ ";
        for(Gate g: _input_gates) {
            logic_row += g.get_logics().get(row) + " ";
        }
        logic_row += "]";

        logic_row = logic_row.replace("2","-"); //dont-care

        return logic_row;
    }

    /**
     *
     * Categories: Input, Output, Logic(NOR), Logic(AND)
     *
     * sorted by distance to input
     *
     */
    public void categorizeGates() {

        ArrayList<Gate> all_gates = new ArrayList<Gate>();
        ArrayList<Gate> input_gates = new ArrayList<Gate>();
        ArrayList<Gate> logic_gates = new ArrayList<Gate>();
        ArrayList<Gate> output_gates = new ArrayList<Gate>();

        for(Gate g: _Gates) {

            if(g.type == GateType.INPUT) {

                input_gates.add(g);

            }

            else if(g.type == GateType.OUTPUT || g.type == GateType.OUTPUT_OR) {

                output_gates.add(g);

            }

            else {

                GateUtil.calculateDistanceToFarthestInput(g);

                g.rIndex = logic_gates.size() + 1;

                logic_gates.add(g);

            }
        }


        // sort _logic_gates by their distance_to_input, from far to close

        Collections.sort(logic_gates,
                new Comparator<Gate>() {
                    public int compare(Gate g1, Gate g2) {
                        int result = 0;
                        if ( (g1.get_distance_to_input() - g2.get_distance_to_input()) > 1e-10 ){
                            result = -1;
                        } else if ( (g1.get_distance_to_input() - g2.get_distance_to_input()) < -1.0e-10){
                            result = 1;
                        }
                        else if ( (g1.get_distance_to_input() - g2.get_distance_to_input() == 0) ) {
                            if(g1.index < g2.index) {
                                return -1;
                            }
                            else {
                                return 1;
                            }
                        }
                        return result;
                    }
                }
        );

        _output_gates = output_gates;
        _logic_gates  = logic_gates;
        _input_gates  = input_gates;

        all_gates.addAll(output_gates);
        all_gates.addAll(logic_gates);
        all_gates.addAll(input_gates);

    }

    public String assignment() {
        String assignment = "";
        for(Gate g: this._logic_gates) {
            assignment += g.name + " ";
        }
        return assignment;
    }



    /**
     *
     * Order the logic gates according to the given eugene construct design.
     *
     * Done prior to generating a plasmid.
     *
     */
    /*public void reorderLogicGates(ArrayList<String> eugene_construct) {

        ArrayList<Gate> gates = new ArrayList<Gate>();
        gates.addAll(_logic_gates);
        gates.addAll(_output_gates);

        ArrayList<Gate> ordered_Gates = new ArrayList<Gate>();
        for(String eugene_gate_name: eugene_construct) {

            String gate_name = eugene_gate_name.substring(1,eugene_gate_name.length());
            String orientation = eugene_gate_name.substring(0, 1);

            for(Gate g: gates) {
                if(g.Name.equals(gate_name)) {
                    if(orientation.equals("f") || orientation.equals("+")) {
                        g.set_direction("+");
                    }
                    else if(orientation.equals("r") || orientation.equals("-")) {
                        g.set_direction("-");
                    }
                    ordered_Gates.add(g);
                }
            }
        }
        gates = ordered_Gates;
        Collections.reverse(gates);

        ArrayList<Gate> all_Gates = new ArrayList<Gate>();
        all_Gates.addAll(gates);
        all_Gates.addAll(_input_gates);

        _Gates = all_Gates;
    }*/


    public void setGateTypes() {

        HashMap<GateType, ArrayList<Gate>> gate_types = new HashMap<GateType, ArrayList<Gate>>();

        for(Gate g: this.get_Gates()) {

            if(!gate_types.containsKey(g.type)) {

                gate_types.put(g.type, new ArrayList<Gate>());

            }

            gate_types.get(g.type).add(g);

        }

        _gate_types = gate_types;
    }



    /////////////////////////
    //
    // Getters and Setters
    //
    /////////////////////////



    public ArrayList<Gate> get_Gates(GateType gtype) {

        ArrayList<Gate> gates = new ArrayList<Gate>();

        for(Gate g: _Gates) {

            if(g.type == gtype) {

                gates.add(g);

            }
        }

        return gates;

    }

    public ArrayList<Gate> get_Gates(GateType gtype1, GateType gtype2) {

        ArrayList<Gate> gates = new ArrayList<Gate>();

        for(Gate g: _Gates) {

            if(g.type == gtype1 || g.type == gtype2) {

                gates.add(g);

            }

        }

        return gates;
    }


    public HashMap<GateType, ArrayList<Gate>> get_logic_gate_types() {

        HashMap<GateType, ArrayList<Gate>> logic_gate_types = new HashMap<GateType, ArrayList<Gate>>();

        for(GateType gtype: _gate_types.keySet()) {

            if(gtype != GateType.INPUT && gtype != GateType.OUTPUT && gtype != GateType.OUTPUT_OR ) {

                logic_gate_types.put(gtype, _gate_types.get(gtype));

            }

        }

        return logic_gate_types;
    }


    /////////////////////////
    //
    // Private member data
    //
    /////////////////////////
    @Getter @Setter private static int _number_of_logic_circuits;
    @Getter @Setter private int _index;

    @Getter @Setter private String _assignment_name = "";

    @Getter @Setter private ArrayList<Wire> _Wires = new ArrayList<Wire>();
    @Getter @Setter private ArrayList<Gate> _Gates = new ArrayList<Gate>();

    @Getter @Setter private ArrayList<Gate> _input_gates = new ArrayList<Gate>();
    @Getter @Setter private ArrayList<Gate> _logic_gates = new ArrayList<Gate>();
    @Getter @Setter private ArrayList<Gate> _output_gates = new ArrayList<Gate>();

    @Getter @Setter private List<String> _truthtable = new ArrayList<String>();

    @Getter @Setter private HashMap<GateType, ArrayList<Gate>> _gate_types = new HashMap<GateType, ArrayList<Gate>>();

    @Getter @Setter private Scores _scores = new Scores();
    @Getter @Setter private String _netlist = new String();


    @Getter @Setter private ArrayList<ArrayList<Part>> _sensor_module_parts  = new ArrayList<ArrayList<Part>>();
    @Getter @Setter private ArrayList<ArrayList<Part>> _sensor_plasmid_parts = new ArrayList<ArrayList<Part>>();
    @Getter @Setter private ArrayList<ArrayList<Part>> _circuit_module_parts  = new ArrayList<ArrayList<Part>>();
    @Getter @Setter private ArrayList<ArrayList<Part>> _circuit_plasmid_parts = new ArrayList<ArrayList<Part>>();
    @Getter @Setter private ArrayList<ArrayList<Part>> _output_module_parts   = new ArrayList<ArrayList<Part>>();
    @Getter @Setter private ArrayList<ArrayList<Part>> _output_plasmid_parts  = new ArrayList<ArrayList<Part>>();

}
