package org.cellocad.MIT.dnacompiler;
/**
 * Created by Bryan Der on 3/26/14.
 */

import lombok.Getter;
import lombok.Setter;
import org.cellocad.BU.dom.DWire;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Connected Gates form a LogicCircuit
 *
 * Gate is a node in a Directed Acyclic Graph.
 *
 */

/**
 * these properties are ignored in order to convert a DAG to a JSON object.  Otherwise,
 * the Gson conversion encounters an infinite loop because Gates point to Wires and Wires point to Gates.
 */
@JsonIgnoreProperties({"Outgoing", "outW", "children"})
public class Gate {

    //a list of allowed gate types.
    public enum GateType{
        INPUT,
        OUTPUT,
        OUTPUT_OR,
        OR,
        AND,
        NAND,
        NOR,
        XOR,
        XNOR,
        NOT,
        NORCDS;
    }


    /**
     *
     * Wires just connect gates, there are not data associated with Wire objects.
     * Gate objects have all the data, such as RPUs, etc.
     *
     * Potential point of confusion: RPUs are properties of promoters, and promoters are 'wires',
     * but by associating RPUs with gates, the idea is that all fan-out promoters have the same RPU values
     * for the gate.
     *
     */


     /**
     * Explanation of the DAG data structure.*
     * g1.Outgoing refers to the first input wire for g1
     * g1.Outgoing.Next refers to the second input wire for g1 (if any)
     * g1.Outgoing.Next.Next refers to the third input wire for g1 (if any)
     *
     * g1.Outgoing.To is the gate on the other end of the Outgoing wire
     * g1.Outgoing.Next.To is the gate on the other end of the Outgoing.Next wire
     *
     * The .From property is not really used because the graphs are typically traversed from output to input
     *
     * Input gates have no Outgoing wire.
     */


    //DAG
    public DWire outW = new DWire();
    public int index = -1;
    public int rIndex= -1; //a renumbered index so that the first logic gate has index 1.
    public String name = "";
    public String group = "";
    public String regulator = "";
    public String inducer = ""; //added for an IWBDA demo, not really used in Cello.
    public String colorHex = "";//six character hexidecimal color.
    public String system = "";  //currently only used to check for CRIPSRi system to design NOR gates with separate txn units instead of tandem promoters
    public GateType type = null;

    public Wire outgoing = null; //toward INPUTS. note that a gate is not aware of its wire(s) going toward OUTPUT

    public int outgoing_wire_index = -1;
    public int stage = 0; //longest path, depth of gate


    public Gate(){}

    //constructor used in NetSynth
    //When creating a DAGW object, NetSynth uses the MIT/dnacompiler Gate and Wire objects.
    public Gate(int ind, GateType dType)
    {
        index = ind;
        rIndex = ind;
        type = dType;
        stage = 0;

        if(dType == GateType.NOR)
        {
            name = "~|";
        }
        else if(dType == GateType.NOT)
        {
            name = "~";
        }
        else if(dType == GateType.AND)
        {
            name = "&";
        }
        else if(dType == GateType.OR)
        {
            name = "|";
        }
        else if(dType == GateType.NAND){
            name = "~&";
        }
        else if(dType == GateType.XOR){
            name = "*"; 
        }
        else if(dType == GateType.XNOR){
            name = "~*"; 
        }
        else
        {
            name = "";
        }

        outW = new DWire();
        this.outgoing = null;
    }

    //constructor used in NetSynth
    public Gate(int ind, GateType dType, Wire de)
    {
        index = ind;
        rIndex = ind;
        type = dType;
        stage = 0;

        if(dType == GateType.NOR)
        {
            name = "~|";
        }
        else if(dType == GateType.NOT)
        {
            name = "~";
        }
        else if(dType == GateType.AND)
        {
            name = "&";
        }
        else if(dType == GateType.OR)
        {
            name = "|";
        }
        else if(dType == GateType.NAND){
            name = "~&";
        }
        else if(dType == GateType.XOR){
            name = "*"; 
        }
        else if(dType == GateType.XNOR){
            name = "~*"; 
        }
        else
        {
            name = "";
        }
        this.outgoing = de;

        outW = new DWire();
    }

    //constructor used in NetSynth
    public Gate(int ind, String dType)
    {
        index = ind;
        rIndex = ind;
        type = GateType.valueOf(dType);
        stage = 0;

        if(type == GateType.NOR)
        {
            name = "~|";
        }
        else if(type == GateType.NOT)
        {
            name = "~";
        }
        else if(type == GateType.AND)
        {
            name = "&";
        }
        else if(type == GateType.OR)
        {
            name = "|";
        }
        else if(type == GateType.NAND){
            name = "~&";
        }
        else if(type == GateType.XOR){
            name = "*"; 
        }
        else if(type == GateType.XNOR){
            name = "~*"; 
        }
        else
        {
            name = "";
        }

        outW = new DWire();
        this.outgoing = null;
    }

    //constructor used in NetSynth
    public Gate(int ind, String dType,Wire de)
    {
        index = ind;
        rIndex = ind;
        type = GateType.valueOf(dType);
        stage = 0;

        if(type == GateType.NOR)
        {
            name = "~|";
        }
        else if(type == GateType.NOT)
        {
            name = "~";
        }
        else if(type == GateType.AND)
        {
            name = "&";
        }
        else if(type == GateType.OR)
        {
            name = "|";
        }
        else if(type == GateType.NAND){
            name = "~&";
        }
        else if(type == GateType.XOR){
            name = "*"; 
        }
        else if(type == GateType.XNOR){
            name = "~*"; 
        }
        else
        {
            name = "";
        }
        this.outgoing = de;

        outW = new DWire();
    }

    /**
     *
     * Copy constructor.
     *
     */
    public Gate(Gate gate){
        _unvisited = gate._unvisited;
        _scores = new Scores(gate.get_scores());
        _distance_to_input = gate._distance_to_input;
        _params  = gate._params;
        _variable_names = new ArrayList<String>(gate._variable_names);
        _variable_thresholds = gate._variable_thresholds;
        _variable_wires = gate._variable_wires;
        _equation  = gate._equation;
        _downstream_parts = gate._downstream_parts;
        _regulable_promoter = gate._regulable_promoter;

        _toxtable = gate._toxtable;
        _toxicity = gate._toxicity;
        _logics   = gate._logics;
        _histogram_rpus = gate._histogram_rpus;
        _histogram_bins = gate._histogram_bins;
        _xfer_hist = gate._xfer_hist;
        _unit_conversion = gate._unit_conversion;

        // Deep copy of rpus.  Other data are not deep-copied to save memory.
        //_outrpus = gate._outrpus; //this would be the shallow copy of _outrpus
        if(!gate._outrpus.isEmpty()) {
            _outrpus = new ArrayList<Double>();
           for(Double d: gate._outrpus) {
               _outrpus.add(new Double(d));
           }
        }
        _inrpus = gate._inrpus;

        stage = gate.stage;
        outW = gate.outW;
        index = gate.index;
        rIndex = gate.rIndex;
        name = gate.name;
        group = gate.group;
        regulator = gate.regulator;
        colorHex = gate.colorHex;
        system = gate.system;
        type = gate.type;
        outgoing = gate.outgoing;
        outgoing_wire_index = gate.outgoing_wire_index;
    }


    @Override
    public String toString(){
        return index + " " + type + " " + name;
    }


    /**
     *
     * Returns array of child gates
     *
     */
    public ArrayList<Gate> getChildren(){

        /**
         * It's called 'Outgoing' because the DAG is traversed from the output(s) toward the input(s).
         * This can cause confusion because most people think about circuits from the inputs toward the outputs.
         *
         * Inputs have no Outgoing wires.
         * Logic/Output gates have at least one Outgoing wire.
         * If a gate has more than one input, these would be accessed using:
         * Outgoing.Next (second input), Outgoing.Next.Next (third input).
         */

        ArrayList<Gate> children = new ArrayList<Gate>();

        if ( (this.outgoing != null) && (this.outgoing.to != null)){
            children.add(this.outgoing.to);

            Wire w = this.outgoing;
            while(w.next != null && w.next.to != null) {
                children.add(w.next.to);
                w = w.next;
            }
        }

        return children;
    }


    /////////////////////////
    //
    // Private member data
    //
    /////////////////////////
    
//    @Getter @Setter private boolean _tp_exists = false;

    @Getter @Setter private int _distance_to_input = -1;

    //TODO get rid of this one
    @Getter @Setter private int _farthest_dist2in = 1;

    //computed values

    //arraylist represents rows of truth table, Integer is either 0 or 1
    @Getter @Setter private ArrayList<Integer> _logics = new ArrayList<Integer>();

    //gate promoter order
    @Getter @Setter private int[] _porder;
    @Getter @Setter private String _promoter;

    //arraylist represents rows of truth table, Double is the RPU value.
    //The RPU value should be low if the logic value is 0, the RPU value should be high if the logic value is 1.
    @Getter @Setter private ArrayList<Double> _outrpus = new ArrayList<Double>();

    //map input RPUs to variable name, "x"
    @Getter @Setter private HashMap<String, ArrayList<Double>> _inrpus = new HashMap<>();

    /**
     * if the output module is on a different plasmid than the circuit module,
     * the RPU units must be adjusted for the change in plasmid copy number.
     * In our work, gates were characterized on p15A, but the output module was on pSC101.
     * The conversion factor is specified in the 'genetic_locations' collection of the UCF.
     */
    @Getter @Setter private Double _unit_conversion = 1.0;

    //predicted growth scores: arraylist is rows of truth table, Double is predicted relative growth value
    //based on the RPU value in that row for this gate.
    @Getter @Setter private ArrayList<Double>  _toxicity = new ArrayList<Double>();

    //arraylist represents the rows in the truth table, the double[] represents the histogram
    //the length of the double[] is the same length as HistogramBin._LOG_BIN_CENTERS[]
    @Getter @Setter private ArrayList< double[] > _histogram_rpus = new ArrayList< double[] >();
    @Getter @Setter private ArrayList< double[] > _in_histogram_rpus = new ArrayList< double[] >();
    

    //min, max, bin width, bin centers
    @Getter @Setter private HistogramBins _histogram_bins = new HistogramBins();


    /////////// response function //////////

    //for example, ymax:5.0, ymin:0.02, K:0.8, n:2.5
    @Getter @Setter private HashMap<String, Double> _params = new HashMap<>();

    //for example, "x" would be the variable name in "ymin+(ymax-ymin)/(1.0+(x/K)^n)"
    @Getter @Setter private ArrayList<String> _variable_names = new ArrayList<>();

    //threshold analysis defines a forbidden zone of input values for a gate
    /**
     * "x": [0.4, 3.0]
     * "x" is the independent variable in the hill function, for example.
     * index 0 is the low threshold
     * index 1 is the high threshold
     */
    @Getter @Setter private HashMap<String, Double[]> _variable_thresholds = new HashMap<>();


    /**
     * Mapping a variable name to wires.
     *
     * for a 2-input gate such as AND, it matters which wire maps to which variable name in the response function equation.
     * for a 1-input gate such as NOT, which can actually be implemented as a NOR via tandem promoters,
     * multiple wires will map to the same variable name in the response function equation.
     * This is what allows a 1-dimensional hill equation to serve as as the response function for a 2-input NOR gate.
     * RPU values from both wires in a NOR gate will contribute to the input RPU value that is applied to the Hill equation.
     */
    @Getter @Setter private HashMap<String, ArrayList<Wire>> _variable_wires = new HashMap<>();




    //for example, "ymin+(ymax-ymin)/(1.0+(x/K)^n)"
    @Getter @Setter private String _equation;

    //from the UCF
    /**
     * HistogramXfer is just a container for _xfer_interp,
     * which is the interpolated square matrix representing a probabilistic transfer function.
     */
    @Getter @Setter private HistogramXfer _xfer_hist = new HistogramXfer();
    
    //from the UCF
    /**
     * List of x,y value pairs.  x is the input RPU, y is the normalized growth value
     */
    @Getter @Setter private ArrayList<Pair> _toxtable = new ArrayList<Pair>();

    //parts
    /**
     * gates have one or more expression cassettes: ribozyme, rbs, cds, terminator.
     * an AND gate will have two expression cassettes, for example.
     */
    @Getter @Setter private HashMap<String, ArrayList<Part>> _downstream_parts = new HashMap<>();

    //primitive gates have a single output, thus a single regulable promoter
    @Getter @Setter private Part _regulable_promoter;

    //a transcription unit is an array of Parts.
    //the set of transcriptional units for a circuit is an array of an array of Parts.
    @Getter @Setter private ArrayList<ArrayList<Part>> _txn_units = new ArrayList<ArrayList<Part>>();

    //score
    /**
     * it's an object instead of a simple value because there are a few different types of scores to choose from.
     * the default is the ON/OFF ratio, where ON is the lowest ON and OFF is the highest OFF.
     */
    @Getter @Setter private Scores _scores = new Scores();

    //used to visit each gate once during recursive traversal of a graph data structure
    @Getter @Setter private boolean _unvisited = true;

}
