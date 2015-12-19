function setLogic(circuit) {
    var input_gates  = circuit["input_gates"];

    input_gates[2]["logics"] = [0,1,0,1,0,1,0,1];
    input_gates[1]["logics"] = [0,0,1,1,0,0,1,1];
    input_gates[0]["logics"] = [0,0,0,0,1,1,1,1];

    var gates = circuit["gates"];
    for(var i=0; i<gates.length; ++i) {
        var g = gates[i];

        g["logics"] = [];

        for (var j = 0; j < 8; ++j) {
            var total_ons = 0;

            for (var k = 0; k < g["fanin"].length; ++k) {
                total_ons += g["fanin"][k]["to"]["logics"][j];
            }
            if (total_ons > 0) {
                g["logics"].push(0);
            }
            else {
                g["logics"].push(1);
            }
        }
    }

    var output_gates = circuit["output_gates"];
    for(var i=0; i<output_gates.length; ++i) {
        var g = output_gates[i];

        g["logics"] = [];

        for (var j = 0; j < 8; ++j) {
            var total_ons = 0;

            for (var k = 0; k < g["fanin"].length; ++k) {
                total_ons += g["fanin"][k]["to"]["logics"][j];
            }
            if (total_ons > 0) {
                g["logics"].push(1);
            }
            else {
                g["logics"].push(0);
            }
        }
    }

}