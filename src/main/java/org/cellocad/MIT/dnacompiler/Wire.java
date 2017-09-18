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

    public int Index;
    public String Name;


    public Gate From; //toward outputs
    public Gate To;   //toward inputs
    public Wire Next; //sibling wire in NOR or OUTPUT_OR


    public int From_index;
    public int To_index;
    public int Next_index;

    public DWire wire;

    public Wire()
    {
        this.Index = -1;
        this.Name = null;
        this.wire = null;
        this.From = null;
        this.To = null;
        this.Next = null;
        this.From_index = -1;
        this.To_index = -1;
        this.Next_index = -1;
    }
    public Wire(Wire w) {
        this.Index = w.Index;
        this.Name = w.Name;
        this.wire = w.wire;
        this.From = w.From;
        this.To = w.To;
        this.Next = w.Next;
        this.From_index = w.From_index;
        this.To_index = w.To_index;
        this.Next_index = w.Next_index;
    }
    public Wire(int indx,Gate dFrom,Gate dTo)
    {
        this.Index = indx;
        this.Name = null;
        this.wire = null;
        this.From = dFrom;
        this.To = dTo;
        this.Next = null;
    }
    public Wire(int indx,Gate dFrom,Gate dTo,Wire next)
    {
        this.Index = indx;
        this.Name = null;
        this.wire = null;
        this.From = dFrom;
        this.To = dTo;
        this.Next = next;
    }
    @Override
    public String toString()
    {
        String x= "Index:"+Index + " From:"+From.index + " To:"+To.index;
        return x;
    }
}
