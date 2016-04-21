 function plotCircuit(circuit) {

    $('#circuit_plot').html("");

    var gates       = circuit["gates"];
    var input_gates = circuit["input_gates"];
    var output_gates= circuit["output_gates"];

    $('#circuit_plot').append("<div id='in1_div' class='window' style='height: 175px; width: 90px;'><pre id='in1_pre'></pre></div>");
    $('#circuit_plot').append("<div id='in2_div' class='window' style='height: 175px; width: 90px;'><pre id='in2_pre'></pre></div>");
    $('#circuit_plot').append("<div id='in3_div' class='window' style='height: 175px; width: 90px;'><pre id='in3_pre'></pre></div>");
    $('#circuit_plot').append("<div id='out_div' class='window' style='height: 175px; width: 90px;'><pre id='out_pre'></pre></div>");

    for(var i=0; i<gates.length; ++i) {
        var g = gates[i];
        $('#circuit_plot').append("<div id=gate"+i+" class='flot_div window' style='height: 200px; width: 210px; float: left'></div>");
        plotTransferFunction(g, i);
    }
}

function updateTransferFunctions(circuit) {
    var gates       = circuit["gates"];
    for(var i=0; i<gates.length; ++i) {
        var g = gates[i];
        plotTransferFunction(g, i);
    }
}


function plotAllScores() {
    var d1 = all_scores;
    var d2 = best_scores;

    var options = {
        legend: {position: "nw"},
        lines: { show: true, lineWidth:5 },
        xaxis: { axisLabel: "assignment #", min: 0, max:indexes_set.length },
        yaxis: { axisLabel: "score = log(ON/OFF)", min: 0.0, max:3.0 },
        grid: { hoverable: true, clickable: true , color: "#999", backgroundColor: 'white'}
    };


    plot1 = $.plot($("#best_score"),
        [
            { label: "all",  data: d1, color:'gray'},
            { label: "best", data: d2, color:'black'}
        ], options);

    plot1.getData()[0].lines.lineWidth = 2;
    plot1.getData()[1].lines.lineWidth = 5;
    plot1.draw();

}

function plotTransferFunction(g, i) {
    var pmax = g["params"]["pmax"];
    var pmin = g["params"]["pmin"];
    var kd =   g["params"]["k"];
    var n =    g["params"]["n"];
    //var pmax = 10.0;
    //var pmin = 0.01;
    //var kd = 0.3;
    //var n = 3.0;

    var options = {
        lines: { show: true, lineWidth:5 },
        xaxis: { ticks: [0.001,0.01,0.1,1,10,100], min: 0.01, max:50,
            transform:  function(v) {return Math.log(v+0.0001); }, tickDecimals:2 },
        yaxis: { ticks: [0.001,0.01,0.1,1,10,100], min: 0.01, max:50,
            transform:  function(v) {return Math.log(v+0.0001); }, tickDecimals:2 },
        grid: { hoverable: true, clickable: true , color: "#999", backgroundColor: 'white'}
    };


    var on_low_x = 10000.0;
    var off_high_x = 0.00001;

    for(var j=0; j<8; ++j) {

        var fanin_rpu = 0;

        for (var k = 0; k < g["fanin"].length; ++k) {
            fanin_rpu += g["fanin"][k]["to"]["rpus"][j];
        }

        if(g["logics"][j] == 0) {
            //inputs should be high
            if(fanin_rpu < on_low_x) {
                on_low_x = fanin_rpu;
            }
        }
        if(g["logics"][j] == 1) {
            //inputs should be low
            if(fanin_rpu > off_high_x) {
                off_high_x = fanin_rpu;
            }
        }
    }

    var on_low_y   = pmin + (pmax-pmin)/(1+Math.pow((on_low_x/kd),n ));
    var off_high_y = pmin + (pmax-pmin)/(1+Math.pow((off_high_x/kd),n ));


    var data1 = sampleFunction( 0.1, 1, function(x){ return pmin + (pmax-pmin)/( 1+Math.pow((x/kd),n ) ) } );

    plot1 = $.plot($("#gate"+i),
        [
            { label: g["name"], data: data1, color:g["color"]},
            { data: lineFunctionX(off_high_x, off_high_y),  color:'gray'},
            { data: lineFunctionX(on_low_x, on_low_y), color:'gray'},
            { data: lineFunctionY(off_high_x, off_high_y),  color:'gray'},
            { data: lineFunctionY(on_low_x, on_low_y), color:'gray'}
        ], options);

    plot1.getData()[0].lines.lineWidth = 5;
    plot1.getData()[1].lines.lineWidth = 3;
    plot1.getData()[2].lines.lineWidth = 3;
    plot1.getData()[3].lines.lineWidth = 3;
    plot1.getData()[4].lines.lineWidth = 3;
    plot1.draw();

}
function sampleFunction(x1, x2, func) {
    var d = [ ];
    var logx1 = Math.log(x1);
    var logx2 = Math.log(x2);

    var step = (logx2-logx1)/300;
    for (var i = logx1; i < logx2; i += step ) {
        var pow_i = Math.pow(i,10);
        d.push([pow_i, func( pow_i ) ]);
    }
    return d;
}
function lineFunctionX(x1, y1) {
    var d = [ ];
    d.push([x1,0.001]);
    d.push([x1,y1]);
    return d;
}
function lineFunctionY(x1, y1) {
    var d = [ ];
    d.push([100.0,y1]);
    d.push([x1,y1]);
    return d;
}


function updateOutputTable(circuit) {

    var in1_pre_html = "pBAD\n";
    var in2_pre_html = "pTac\n";
    var in3_pre_html = "pTet\n";
    var out_pre_html = "out\n";

    for(var i=0; i<8; ++i) {
        in1_pre_html += format(circuit["input_gates"][0]["rpus"][i].toFixed(2), 5) + " : " + circuit["input_gates"][0]["logics"][i] + '\n';
        in2_pre_html += format(circuit["input_gates"][1]["rpus"][i].toFixed(2), 5) + " : " + circuit["input_gates"][1]["logics"][i] + '\n';
        in3_pre_html += format(circuit["input_gates"][2]["rpus"][i].toFixed(2), 5) + " : " + circuit["input_gates"][2]["logics"][i] + '\n';
        out_pre_html += format(circuit["output_gates"][0]["rpus"][i].toFixed(2), 5) + " : " + circuit["output_gates"][0]["logics"][i] + '\n';
    }
    $('#in1_pre').html(in1_pre_html);
    $('#in2_pre').html(in2_pre_html);
    $('#in3_pre').html(in3_pre_html);
    $('#out_pre').html(out_pre_html);
}