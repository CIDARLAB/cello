package org.cellocad.MIT.dnacompiler;
/**
 * Created by Bryan Der on 3/26/14.
 */


import lombok.Getter;
import lombok.Setter;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import java.util.HashMap;

/**
 * Part contains data: name, type, direction, sequence, etc.
 *
 * Gate has gate_parts, which are concatenated to build a plasmid.
 *
 */
@JsonIgnoreProperties({"_parent_gate"})
public class Part {

    /**
     * Default constructor
     */
    public Part(){
        _name = new String();
        _type = new String();
        _direction = new String();
        _ape_color = new String();
        _color = new String();
        _seq = new String();
        _start = 0;
        _end = 0;
        _gate_index = 0;
        _parent_gate = null;
    }

    /**
     * Copy constructor
     */
    public Part(Part p){

        _name = new String(p._name); //deep copy of string
        _type = new String(p._type);
        _direction = new String(p._direction);
        _ape_color = new String(p._ape_color);
        _color = new String(p._color);
        _seq = new String(p._seq);
        _start = p._start;
        _end = p._end;
        _gate_index = p._gate_index;
        _parent_gate = p._parent_gate;
        setPartTypeColors();
    }

    /**
     * Construct from name, type, seq
     */
    public Part(String name, String type, String seq){
        setPartTypeColors();
        _name = name;
        _type = type;
        _direction = "+";
        if(PART_TYPE_COLORS.containsKey(type)) {
            _ape_color = PART_TYPE_COLORS.get(type);
        }
        _color = "888888"; //gray
        _seq = seq;
        _start = 0;
        _end = 0;
        _gate_index = 0;
    }


    /**
     * default part colors for Ape file.
     * Note: the color is not for DNAplotlib or gnuplot figures, just for the ApE plasmid viewer.
     */
    public void setPartTypeColors() {
        PART_TYPE_COLORS.put("promoter", "green");
        PART_TYPE_COLORS.put("insulator", "magenta");
        PART_TYPE_COLORS.put("ribozyme", "magenta");
        PART_TYPE_COLORS.put("rbs", "blue");
        PART_TYPE_COLORS.put("cds", "cyan");
        PART_TYPE_COLORS.put("sgRNA", "cyan");
        PART_TYPE_COLORS.put("terminator", "red");
        PART_TYPE_COLORS.put("output", "yellow");
        PART_TYPE_COLORS.put("scar", "gray");
        PART_TYPE_COLORS.put("backbone", "pink");
        PART_TYPE_COLORS.put("spacer", "white");
    }


    @Override
    public String toString(){
        String s = "part:\n";
        s += "    name: " + _name + "\n";
        s += "    type: " + _type + "\n";
        s += "    seq: " + _seq + "\n";

        return s;
    }


    /////////////////////////
    //
    // Private member data
    //
    /////////////////////////

    @Getter @Setter private String _name = "";
    @Getter @Setter private String _type = "";

    //"+" or "-"
    @Getter @Setter private String _direction = "";

    @Getter @Setter private String _seq = "";
    @Getter @Setter private String _ape_color = "";
    @Getter @Setter private String _color = "";
    @Getter @Setter private int _start; // start from 1, not 0;
    @Getter @Setter private int _end;
    @Getter @Setter private int _gate_index;
    @Getter @Setter private Gate _parent_gate;

    //for the Ape plasmid viewer
    @Getter @Setter private HashMap<String, String> PART_TYPE_COLORS = new HashMap<String, String>();

}
