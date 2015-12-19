
/**
 * Builds an HTML string for a clickable truthtable.  0 or 1 values can be chosen for the outputs.
 * The table is initialized with all combinations of input bits, and all 0's for the output bits.
 *
 * This is not currently in use, because we want to use Verilog code instead of a GUI.
 *
 * Note: Verilog code templates are generated to assist the user in 'verilog_helpers.js'
 *
 * @returns {boolean}
 */

function drawTable() {

    if (!sessionStorage.n_inputs > 1 || !sessionStorage.n_outputs > 0) {
        return false;
    }

    var tt_array = [];
    var n = sessionStorage.n_inputs;
    for (var i = 0; i != (1 << n); i++) {
        var s = i.toString(2);
        while (s.length != n) {
            s = '0' + s;
        }
        tt_array.push(s);
    }


    var table_html = "";
    for (var i = 0; i < tt_array.length; i++) {
        table_html += "<tr class='row'>";
        table_html += "<td>  </td>";

        //INPUTS
        for (var j = 0, len = tt_array[i].length; j < len; j++) {
            var on_off = "";
            if (tt_array[i][j] == 1) {
                on_off = "on";
            }
            else {
                on_off = "off";
            }
            table_html += "<td>";
            table_html += "<canvas class=\"" + on_off + "\" width=\"30\" height=\"30\"></canvas>";
            table_html += "</td>";
        }

        //OUTPUTS
        table_html += "<td> &nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp  ";
        for (var n_out = 1; n_out <= sessionStorage.n_outputs; n_out++) {
            table_html += "<td>";
            table_html += "<canvas oncontextmenu='dontCare(this);' onclick='toggleLabelLogic(this);' class=\"" + "off" + "\" width=\"30\" height=\"30\"></canvas>";
            table_html += "</td>";
        }
        table_html += "</tr>";
    }

    document.getElementById("truthtable").innerHTML = table_html;
    document.getElementById("truthtable").align = "center";
    setInputCanvas();
}

function setInputCanvas() { //called in drawTable
    var elements = document.getElementsByClassName("off");
    for (var i = 0; i < elements.length; i++) {
        var x = elements[i];
        var context = x.getContext("2d");
        context.fillStyle = "white";
        context.fillRect(0,0,x.width, x.height);
        context.fillStyle = "#555";
        context.font = "bold 16px Arial";
        context.fillText("0", 11, 20);
    }
    elements = document.getElementsByClassName("on");
    for (var i = 0; i < elements.length; i++) {
        var x = elements[i];
        var context = x.getContext("2d");
        context.fillStyle = "#555";
        context.fillRect(0,0,x.width, x.height);
        context.fillStyle = "white";
        context.font = "bold 16px Arial";
        context.fillText("1", 11, 20);
    }
}

//change 0 to 1, change 1 to 0
function toggleLabelLogic(canvas) {

    if(canvas.className == "off") {
        var context = canvas.getContext("2d");
        //canvas.clearRect(0,0,canvas.width,canvas.height);
        context.fillStyle = "#555";
        context.fillRect(0,0,canvas.width, canvas.height);
        context.fillStyle = "white";
        context.font = "bold 16px Arial";
        context.fillText("1", 11, 20);
        canvas.className="on";
    }
    else {
        var context = canvas.getContext("2d");
        context.fillStyle = "white";
        context.fillRect(0,0,canvas.width, canvas.height);
        //canvas.clearRect(0,0,canvas.width,canvas.height);
        context.fillStyle = "#555";
        context.font = "bold 16px Arial";
        context.fillText("0", 11, 20);
        canvas.className="off";
    }

    editor.getDoc().setValue(getVerilog());
}


//convert clicked truthtable to Verilog file case statement
function getVerilog() {

    var input_rows = [];
    var output_rows = [];
    $("table#truthtable tr").each(function( i ) {
        var input_row = "";
        var output_row = "";
        $("td", this).each(function( j ) {
            if(this.innerHTML.indexOf("canvas") !== -1) {
                var type = "input";
                if(this.innerHTML.indexOf("onclick") !== -1) {
                    type = "output";
                }
                var bit = "";
                if(this.innerHTML.indexOf("class=\"on\"") > 0) {
                    bit = 1;
                }
                else if(this.innerHTML.indexOf("class=\"off\"") > 0) {
                    bit = 0;
                }
                else if(this.innerHTML.indexOf("class=\"dc\"") > 0) {
                    bit = "x";
                }

                if(type == "input") {
                    input_row += String(bit);
                }
                if(type == "output") {
                    output_row += String(bit);
                    if(bit == 0) {
                        has0 = true;
                    }
                    if(bit == 1) {
                        has1 = true;
                    }
                }
            }
        });
        input_rows.push(input_row);
        output_rows.push(output_row);
    });

    var v = "";
    v += "module A(output ";
    for(var i = 0; i<sessionStorage.n_outputs; ++i) {
        var index = i+1;
        v += "out"+index+", ";
    }
    v += " input ";
    for(var i = 0; i<sessionStorage.n_inputs; ++i) {
        var index = i+1;
        v += "in"+index+", ";
    }
    v += ");\n";
    v += "  always@(";
    for(var i = 0; i<sessionStorage.n_inputs; ++i) {
        var index = i+1;
        v += "in"+index+",";
    }
    v += ")\n";
    v += "    begin\n";
    v += "      case({";
    for(var j=1; j<=sessionStorage.n_inputs; ++j) {
        v += "in"+j+",";
    }
    v += "})\n";

    for(var i=0; i<input_rows.length; ++i) {
        v += "        "+sessionStorage.n_inputs+"'b" + input_rows[i] + ": ";
        v += "{";
        for(var j=1; j<=sessionStorage.n_outputs; ++j) {
            v += "out"+j+",";
        }
        v += "} = " + sessionStorage.n_outputs + "'b" + output_rows[i] + ";\n";
    }

    v += "      endcase\n";
    v += "    end\n";
    v += "endmodule\n";

    v = v.replace(/,\ \)/g, ")");
    v = v.replace(/,\)/g, ")");
    v = v.replace(/,}/g, "}");

    //sessionStorage.verilogFile=v;
    return v;

}