function simulateRPU(circuit) {
    var gates  = circuit["gates"];
    var output_gates  = circuit["output_gates"];

    for(var i=0; i<gates.length; ++i) {
        var g = gates[i];

        g["rpus"] = [];

        for (var j = 0; j < 8; ++j) {
            var fanin_rpu = 0;

            for (var k = 0; k < g["fanin"].length; ++k) {
                fanin_rpu += g["fanin"][k]["to"]["rpus"][j];
            }

            var pmax = g["params"]["pmax"];
            var pmin = g["params"]["pmin"];
            var k = g["params"]["k"];
            var n = g["params"]["n"];

            var output_rpu = pmin+(pmax-pmin) / (1.0 + Math.pow( (fanin_rpu/k), n ) );

            g["rpus"].push(output_rpu);
        }
    }

    for(var i=0; i<output_gates.length; ++i) {
        var g = output_gates[i];

        g["rpus"] = [];

        for (var j = 0; j < 8; ++j) {
            var fanin_rpu = 0;

            for (var k = 0; k < g["fanin"].length; ++k) {
                fanin_rpu += g["fanin"][k]["to"]["rpus"][j];
            }

            var output_rpu = fanin_rpu;

            g["rpus"].push(output_rpu);
        }
    }
}