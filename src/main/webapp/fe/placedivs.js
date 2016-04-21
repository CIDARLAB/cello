function placeDivs(circuit) {

    var x_off = 50;
    var y_off = 0;

    var d0 = document.getElementById('gate0');
    d0.style.position = "absolute";
    d0.style.left = 150+x_off+'px';
    d0.style.top  = 50+y_off+'px';

    var d1 = document.getElementById('gate1');
    d1.style.position = "absolute";
    d1.style.left =  150+x_off+'px';
    d1.style.top  = 265+y_off+'px';

    var d3 = document.getElementById('gate3');
    d3.style.position = "absolute";
    d3.style.left =  150+x_off+'px';
    d3.style.top  = 470+y_off+'px';

    var d2 = document.getElementById('gate2');
    d2.style.position = "absolute";
    d2.style.left = 500+x_off+'px';
    d2.style.top  = 120+y_off+'px';

    var d4 = document.getElementById('gate4');
    d4.style.position = "absolute";
    d4.style.left = 750+x_off+'px';
    d4.style.top  = 220+y_off+'px';

    var i1 = document.getElementById('in1_div');
    i1.style.position = "absolute";
    i1.style.left = 10+'px';
    i1.style.top  = 50+y_off+'px';

    var i2 = document.getElementById('in2_div');
    i2.style.position = "absolute";
    i2.style.left =  10+'px';
    i2.style.top  = 250+y_off+10+'px';

    var i3 = document.getElementById('in3_div');
    i3.style.position = "absolute";
    i3.style.left =  10+'px';
    i3.style.top  = 460+y_off+10+'px';

    var o1 = document.getElementById('out_div');
    o1.style.position = "absolute";
    o1.style.left = 1025+x_off+'px';
    o1.style.top  = 200+y_off+10+'px';

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