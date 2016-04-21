function computeScore(circuit) {
    var output_gates  = circuit["output_gates"];
    var all_output_gate_scores = [];

    for(var i=0; i<output_gates.length; ++i) {
        var g = output_gates[i];
        var on_low = 1000;
        var off_high = 0.00001;

        for (var j = 0; j < 8; ++j) {
            if(g["logics"][j] == 0) {
                if(g["rpus"][j] > off_high) {
                    off_high = g["rpus"][j];
                }
            }
            if(g["logics"][j] == 1) {
                if(g["rpus"][j] < on_low) {
                    on_low = g["rpus"][j];
                }
            }
        }

        var score = log10( on_low / off_high);
        all_output_gate_scores.push(score);
    }

    var min_of_array = Math.min.apply(Math, all_output_gate_scores);

    var circuit_score = min_of_array;
    circuit["score"] = circuit_score
}