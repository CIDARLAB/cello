

function assignCircuit(circuit, biogates, index_set) {
    var gates  = circuit["gates"];

    for(var i=0; i<gates.length; ++i) {
        var g = gates[i];
        g["name"]   = biogates[index_set[i]]["name"];
        g["params"] = biogates[index_set[i]]["params"];
        g["color"]  = biogates[index_set[i]]["color"];
    }

    for(var i=0; i<gates.length; ++i) {
        var bg = gates[i];
    }
}



function assignInputs(circuit) {

    var input_reus = {};
    input_reus["pBAD"] = {};
    input_reus["pTac"] = {};
    input_reus["pTet"] = {};
    input_reus["pBAD"]["OFF"] = 0.01;
    input_reus["pBAD"]["ON"]  = 7.72;
    input_reus["pTac"]["OFF"] = 0.091;
    input_reus["pTac"]["ON"]  =10.1;
    input_reus["pTet"]["OFF"] = 0.073;
    input_reus["pTet"]["ON"]  =15.7;

    var input_gates  = circuit["input_gates"];
    input_gates[0]["name"] = "pBAD";
    input_gates[1]["name"] = "pTac";
    input_gates[2]["name"] = "pTet";

    input_gates[0]["reus"] = [];
    input_gates[1]["reus"] = [];
    input_gates[2]["reus"] = [];


    for (var j = 0; j < 8; ++j) {
        for (var k = 0; k < input_gates.length; ++k) {
            if(input_gates[k]["logics"][j] == 0) {
                input_gates[k]["reus"].push(input_reus[input_gates[k]["name"]]["OFF"]);
            }
            else {
                input_gates[k]["reus"].push(input_reus[input_gates[k]["name"]]["ON"]);
            }
        }
    }
}


function firstAssignment() {

    assignment++;
    $('#assignment_number').html("<h4>assignment "+assignment+"</h4>");

    assignCircuit(circuit, biogates, indexes_set[assignment]);

    simulateREU(circuit);

    computeScore(circuit);

    if(circuit["score"] > best_score) {
        best_score = circuit["score"];
    }
    all_scores.push([assignment, circuit["score"]]);
    best_scores.push([assignment, best_score]);
    plotAllScores();

    var sc = circuit["score"].toFixed(2);
    $('#score').html("<h4 style='float: right; margin-right: 50px;'>Score: "+sc+"</h4><br style='clear: both'>");


    plotCircuit(circuit);

    placeDivs(circuit);

    wiresJSPLUMB(circuit);

    $('#print_graph').html(printGraph2(circuit));
}


function nextAssignment() {

    assignment++;
    $('#assignment_number').html("<h4>assignment "+assignment+"</h4>");

    assignCircuit(circuit, biogates, indexes_set[assignment]);

    simulateREU(circuit);

    computeScore(circuit);

    if(circuit["score"] > best_score) {
        best_score = circuit["score"];
    }
    all_scores.push([assignment, circuit["score"]]);
    best_scores.push([assignment, best_score]);
    plotAllScores();

    var sc = circuit["score"].toFixed(2);
    $('#score').html("<h4 style='float: right; margin-right: 50px;'>Score: "+sc+"</h4><br style='clear: both'>");

    updateTransferFunctions(circuit);
    updateOutputTable(circuit);

    $('#print_graph').html(printGraph2(circuit));
}