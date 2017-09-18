package org.cellocad.MIT.dnacompiler;

import org.cellocad.BU.dom.DWire;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

/**
 *
 * @author prashantvaidyanathan
 */


/**
 * Synopsis    [ Wires connect gates in the circuit. ]
 *
 * From gate  (toward outputs)
 * To gate    (toward inputs)
 * Next wire  (sibling wire in NOR or OUTPUT_OR, points to child2 gate)
 */
@JsonIgnoreProperties({"From", "To", "Next", "wire"})
public class Wire {

    public int index;
    public String name;


    public Gate from; //toward outputs
    public Gate to;   //toward inputs
    public Wire next; //sibling wire in NOR or OUTPUT_OR


    public int from_index;
    public int to_index;
    public int next_index;

    public DWire wire;

    public Wire()
    {
        this.index = -1;
        this.name = null;
        this.wire = null;
        this.from = null;
        this.to = null;
        this.next = null;
        this.from_index = -1;
        this.to_index = -1;
        this.next_index = -1;
    }
    public Wire(Wire w) {
        this.index = w.index;
        this.name = w.name;
        this.wire = w.wire;
        this.from = w.from;
        this.to = w.to;
        this.next = w.next;
        this.from_index = w.from_index;
        this.to_index = w.to_index;
        this.next_index = w.next_index;
    }
    public Wire(int indx,Gate dFrom,Gate dTo)
    {
        this.index = indx;
        this.name = null;
        this.wire = null;
        this.from = dFrom;
        this.to = dTo;
        this.next = null;
    }
    public Wire(int indx,Gate dFrom,Gate dTo,Wire next)
    {
        this.index = indx;
        this.name = null;
        this.wire = null;
        this.from = dFrom;
        this.to = dTo;
        this.next = next;
    }
    @Override
    public String toString()
    {
        String x= "Index:"+index + " From:"+from.index + " To:"+to.index;
        return x;
    }
}
