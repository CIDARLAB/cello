package org.cellocad.MIT.dnacompiler;


import lombok.Getter;
import org.cellocad.MIT.dnacompiler.Gate.GateType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;

public class GateLibrary {


    public GateLibrary(int n_in, int n_out) {

        //#inputs and #outputs are needed because _INPUT_NAMES and _OUTPUT_NAMES are 'final' data members
        //to prevent modification.
        _INPUT_NAMES = new String[n_in];
        _OUTPUT_NAMES = new String[n_out];
    }

    @Getter private final String[] _INPUT_NAMES;  //e.g. pTac, pTet, pBAD
    @Getter private final String[] _OUTPUT_NAMES; //e.g. YFP


    //logic gates have response functions, but inputs just have ON and OFF values.

    //map the input name to the ON RPU value for that input promoter
    //units of RPU (not logRPU)
    @Getter private final HashMap<String, Double> _INPUTS_ON    = new HashMap<String, Double>();

    //map the input name to the OFF RPU value for that input promoter
    //units of RPU (not logRPU)
    @Getter private final HashMap<String, Double> _INPUTS_OFF   = new HashMap<String, Double>();

    //map the input name to the K value for that input promoter
    @Getter private final HashMap<String, Double> _INPUTS_K    = new HashMap<String, Double>();

    //map the input name to the n value for that input promoter
    @Getter private final HashMap<String, Double> _INPUTS_n   = new HashMap<String, Double>();

    //map the input name to the a value for that input promoter
    @Getter private final HashMap<String, Double> _INPUTS_a    = new HashMap<String, Double>();

    //map the input name to the b value for that input promoter
    @Getter private final HashMap<String, Double> _INPUTS_b   = new HashMap<String, Double>();

    //map the input name to the DNA sequence for that input promoter
    @Getter private final HashMap<String, String> _INPUTS_SEQ = new HashMap<String, String>();

    //map the input name to the ON RPU histogram value for that input
    @Getter private final HashMap<String, double[] > _INPUTS_HIST_ON = new HashMap<String, double[] >();

    //map the input name to the OFF RPU histogram value for that input
    @Getter private final HashMap<String, double[] > _INPUTS_HIST_OFF = new HashMap<String, double[] >();

    //map the output name to the DNA sequence of the output cassette.
    //the output cassette might be, for example, a concatenation of ribozyme, RBS, CDS, Terminator.
    @Getter private final HashMap<String, String> _OUTPUTS_SEQ = new HashMap<String, String>();



    /**
     * The gate library is a list of gates, but these gates can be organized in more than one way.
     * 1. _GATES_BY_NAME: HashMap that maps a gate object to the gate name
     * 2. _GATES_BY_TYPE: HashMap that maps a list of gate objects to the gate type.  e.g. get all NOR gates.
     * 3. _GATES_BY_GROUP: HashMap that maps a group name (e.g. PhlF) to a list of gates in that group (e.g. rbs variants P1_PhlF, P2_PhlF)
     */

    //outer key is gate type, e.g. GateType.NOR
    //inner key is group name, PhlF
    @Getter private final HashMap<GateType, LinkedHashMap<String, ArrayList<Gate>>> _GATES_BY_GROUP = new HashMap<GateType, LinkedHashMap<String, ArrayList<Gate>>>();

    //outer key is gate type, e.g. GateType.NOR
    //inner key is gate name, P3_PhlF
    @Getter private final HashMap<GateType, LinkedHashMap<String, Gate>> _GATES_BY_TYPE = new HashMap<GateType, LinkedHashMap<String, Gate>>();

    //key is gate name, value is Gate object
    @Getter private final LinkedHashMap<String, Gate> _GATES_BY_NAME = new LinkedHashMap<String, Gate>();


    /**
     * Jonghyeon's tandem promoter data
     * Built from UCFAdaptor.setTandemPromoters
     */
    @Getter private final LinkedHashMap<String, double[][]> _TANDEM_PROMOTERS = new LinkedHashMap<>();


    /**
     * The gate library is a list of gates, but these gates can be organized in more than one way.
     * 1. _GATES_BY_NAME: HashMap that maps a gate object to the gate name
     * 2. _GATES_BY_TYPE: HashMap that maps a list of gate objects to the gate type.  e.g. get all NOR gates.
     * 3. _GATES_BY_GROUP: HashMap that maps a group name (e.g. PhlF) to a list of gates in that group (e.g. rbs variants P1_PhlF, P2_PhlF)
     */
    public void setHashMapsForGates() {

        //the same gate object is added to each data structure, where each data structure
        //provides a different organization of the gates in the library

        for (Gate g : _GATES_BY_NAME.values()) {

            //organization 1
            _GATES_BY_NAME.put(g.name, g);


            //organization 2
            if (!_GATES_BY_TYPE.containsKey(g.type)) {

                _GATES_BY_TYPE.put(g.type, new LinkedHashMap<String, Gate>());

            }

            LinkedHashMap<String, Gate> gate_type_map = _GATES_BY_TYPE.get(g.type);

            gate_type_map.put(g.name, g);


            //organization 3
            if (!_GATES_BY_GROUP.containsKey(g.type)) {

                _GATES_BY_GROUP.put(g.type, new LinkedHashMap<String, ArrayList<Gate>>());

            }

            LinkedHashMap<String, ArrayList<Gate>> gate_type_group_map = _GATES_BY_GROUP.get(g.type);

            if( ! gate_type_group_map.containsKey(g.group)) {

                gate_type_group_map.put(g.group, new ArrayList<Gate>());

            }

            gate_type_group_map.get(g.group).add(g); //add group member to arraylist

        }
    }

    //used in permutation algorithm for circuit assignment
    public ArrayList<Gate> getGatesByGroupByIndex(GateType type, int index) {

        LinkedHashMap<String, ArrayList<Gate>> map = _GATES_BY_GROUP.get(type);

        ArrayList<String> keySet = new ArrayList(map.keySet());

        //sort so that keySet returns the same order every time
        Collections.sort(keySet);

        if(index < keySet.size()) {
            return map.get(keySet.get(index));
        }
        else {
            return null;
        }
    }

}
