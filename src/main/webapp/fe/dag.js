function getDAG(netlist_string, inputs, outputs) {
    var netlist_lines = netlist_string.split("\n");

    var input_gates = [];
    var output_gates = [];

    var gates = [];
    var wires = [];

    //make input gates
    for(var i=0; i<inputs.length; ++i) {
        var g = {};
        g["type"] = "INPUT";
        g["name"] = inputs[i];
        g["index"]= (i+1)*-1;
        input_gates.push(g);
    }
    //make output gates
    for(var i=0; i<outputs.length; ++i) {
        var g = {};
        g["type"] = "OUTPUT";
        g["name"] = outputs[i];
        g["index"]= 999;
        output_gates.push(g);
    }

    //gate: index, name, type, fanin, fanout
    //wire:        name, from, to

    //for each line
    //add gates
    //assign "from"
    for(var i=0; i<netlist_lines.length; ++i) {
        var g = {};

        var gate_type = netlist_lines[i].match(/[^()]+/g)[0];
        var wire_list = netlist_lines[i].match(/[^()]+/g)[1].split(",");

        g["type"] = gate_type;
        g["index"]= i;

        for(var j=0; j<wire_list.length; ++j) {
            var w = {};
            w["name"] = wire_list[j];

            if(j>0) {
                w["from"] = g;
                wires.push(w);
            }
        }

        gates.push(g);
    }

    //for each wire
    //assign "to"
    for(var j=0; j<wires.length; ++j) {

        var w = wires[j];

        var is_internal_wire = false;

        for(var i=0; i<netlist_lines.length; ++i) {
            var g = gates[i];
            var wire_list = netlist_lines[i].match(/[^()]+/g)[1].split(",");

            if(w["name"] == wire_list[0]) {
                w["to"] = g;
                is_internal_wire = true;
            }
        }

        if(! is_internal_wire) {
            for (var i = 0; i < inputs.length; ++i) {
                if(w["name"] == inputs[i]) {
                    w["to"] = input_gates[i];
                }

            }
        }
    }

    for(var i=0; i<netlist_lines.length; ++i) {

        var wire_list = netlist_lines[i].match(/[^()]+/g)[1].split(",");

        for(var j=0; j<output_gates.length; ++j) {
            if(wire_list[0] == output_gates[j]["name"]) {
                var w = {};
                w["name"] = wire_list[0];
                w["to"] = gates[i];
                w["from"] = output_gates[j];
                wires.push(w);
            }
        }
    }


    for(var i=0; i<gates.length; ++i) {
        var g = gates[i];
        g["fanout"] = [];
        g["fanin"]  = [];
    }
    for(var i=0; i<input_gates.length; ++i) {
        var g = input_gates[i];
        g["fanout"] = [];
        g["fanin"]  = [];
    }
    for(var i=0; i<output_gates.length; ++i) {
        var g = output_gates[i];
        g["fanout"] = [];
        g["fanin"]  = [];
    }


    for(var j=0; j<wires.length; ++j) {
        var w = wires[j];

        for(var i=0; i<gates.length; ++i) {
            var g = gates[i];
            if(w["to"]["index"] == g["index"]) {
                g["fanout"].push(w);
            }
            if(w["from"]["index"] == g["index"]) {
                g["fanin"].push(w);
            }
        }
        for(var i=0; i<input_gates.length; ++i) {
            var g = input_gates[i];
            if(w["to"]["index"] == g["index"]) {
                g["fanout"].push(w);
            }
        }
        for(var i=0; i<output_gates.length; ++i) {
            var g = output_gates[i];
            if(w["from"]["index"] == g["index"]) {
                g["fanin"].push(w);
            }
        }
    }

    var circuit = {};
    circuit["input_gates"] = input_gates;
    circuit["output_gates"] = output_gates;
    circuit["gates"] = gates;
    circuit["wires"] = wires;
    return circuit;

}



var w1 = {};
var w2 = {};

var g1_fanin = [w1, w2];

//w1["to"] = ???


var g1 = {};
g1["name"] = "g1";
g1["index"] = 1;
g1["fanin"] = g1_fanin;