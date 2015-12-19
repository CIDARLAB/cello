function simulateREU(circuit) {
    var gates  = circuit["gates"];
    var output_gates  = circuit["output_gates"];

    for(var i=0; i<gates.length; ++i) {
        var g = gates[i];

        g["reus"] = [];

        for (var j = 0; j < 8; ++j) {
            var fanin_reu = 0;

            for (var k = 0; k < g["fanin"].length; ++k) {
                fanin_reu += g["fanin"][k]["to"]["reus"][j];
            }

            var pmax = g["params"]["pmax"];
            var pmin = g["params"]["pmin"];
            var k = g["params"]["k"];
            var n = g["params"]["n"];

            var output_reu = pmin+(pmax-pmin) / (1.0 + Math.pow( (fanin_reu/k), n ) );

            g["reus"].push(output_reu);
        }
    }

    for(var i=0; i<output_gates.length; ++i) {
        var g = output_gates[i];

        g["reus"] = [];

        for (var j = 0; j < 8; ++j) {
            var fanin_reu = 0;

            for (var k = 0; k < g["fanin"].length; ++k) {
                fanin_reu += g["fanin"][k]["to"]["reus"][j];
            }

            var output_reu = fanin_reu;

            g["reus"].push(output_reu);
        }
    }
}