package org.cellocad.MIT.dnacompiler;

/**
 * Created by Bryan Der on 7/17/15.
 */

import lombok.Getter;
import lombok.Setter;
import org.codehaus.jackson.node.ObjectNode;

import java.util.ArrayList;


@Deprecated
public class Assignment {

    //LogicCircuit object
    @Getter @Setter private LogicCircuit _logic_circuit;

    //JSON object, infinite loops of Gate-Wire-Gate and Wire-Gate-Wire removed by using gate/wire indexes instead of objects
    @Getter @Setter private ObjectNode _logic_circuit_node;

    //text for the Eugene (.eug) file
    @Getter @Setter private String _eugene_file;

    //text for the SBOL (sbol.xml) file
    @Getter @Setter private String _sbol_file;

    //text for the plasmid files
    @Getter @Setter private ArrayList<String> _plasmid_files;
}
