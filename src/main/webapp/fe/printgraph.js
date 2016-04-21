function printGraph2(circuit) {

    var gates = circuit["gates"];

    var s = "";

    for(var i=0; i<gates.length; ++i) {

        for(var f=0; f<gates[i]["fanin"].length; ++f) {
            if(gates[i]["fanin"][f]["to"]["type"] == "NOR" || gates[i]["fanin"][f]["to"]["type"] == "NOT") {
                s += " " + format("p" + gates[i]["fanin"][f]["to"]["name"], 5) + " +";
            }
            else {
                s += " " + format(gates[i]["fanin"][f]["to"]["name"], 5) + " +";
            }
        }
        s = s.substring(0, s.length - 1);
        s += "->" + format(gates[i]["name"], 5) + "    ";
    }

    s += "\n";
    for(var j=0; j<8; ++j) {
        for(var i=0; i<gates.length; ++i) {

            for(var f=0; f<gates[i]["fanin"].length; ++f) {
                s += " " + format(gates[i]["fanin"][f]["to"]["rpus"][j].toFixed(2), 5) + " +";
            }
            s = s.substring(0, s.length - 1);
            s += "->" + format(gates[i]["rpus"][j].toFixed(2), 5) + "    ";
        }
        s += "\n";
    }
    s += "\n";
    console.log(s);

    return s;

}

function format(s, len) {

    if(s.length > len) {
        return s.substring(0, len);
    }

    var diff = len - s.length;

    var new_s = "";

    for(var i=0; i<diff; ++i) {
        new_s += " ";
    }
    new_s += s;

    return new_s;
}

