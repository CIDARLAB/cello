
function setVerilogMethod() {
    if(vio.inputs.length == null || vio.inputs.length == 0) {
        $('#method').val('choose');
        $('#dialog_pre').html('<div class="alert alert-warning"> Inputs not yet specified </div>');
        $('#dialog').dialog({title:"Verilog generator"});
        $('#dialog').dialog( 'open' );
        return;
    }
    if(vio.outputs.length == null || vio.outputs.length == 0) {
        $('#method').val('choose');
        $('#dialog_pre').html('<div class="alert alert-warning"> Outputs not yet specified </div>');
        $('#dialog').dialog({title:"Verilog generator"});
        $('#dialog').dialog( 'open' );
        return;
    }

    var verilog_method = $('#method').val();

    if(verilog_method == "truth") {
        editor.getDoc().setValue(getVerilogCase());
        drawTable();
        $('#truthtable').show();
    }
    else {
        $('#truthtable').hide();
    }

    if(verilog_method == "previous") {
        editor.getDoc().setValue(vio.verilog);
    }

    if(verilog_method == "case") {
        editor.getDoc().setValue(getVerilogCase());
    }

    if(verilog_method == "assign") {
        editor.getDoc().setValue(getVerilogAssign());
    }

    if(verilog_method == "structural") {
        editor.getDoc().setValue(getVerilogStructural());
    }

    if(verilog_method == "upload") {
        $('#upload_verilog').show();
    }
    else {
        $('#upload_verilog').hide();
    }

    $('#method').val('choose');
}

function processUploadFile(that) {

    if(that.files && that.files[0])
    {
        var reader = new FileReader();
        reader.readAsText(that.files[0]);
        reader.onload = function (e) {
            //var result = e.target.result;
            editor.getDoc().setValue(e.target.result);
        }

    }

    $('#upload_verilog').css('display','none');
}

function getVerilogStructural() {

    var v = "";
    v += "module A(output ";
    for(var i = 0; i<vio.outputs.length; ++i) {
        var index = i+1;
        v += "out"+index+", ";
    }
    v += " input ";
    for(var i = 0; i<vio.inputs.length; ++i) {
        var index = i+1;
        v += "in"+index+", ";
    }
    v += ");\n";

    v += "\n";

    v += "/*example: \n";
    v += "  wire w1,w2,w3;\n";
    v += "\n";
    v += "  not (w1, in1);\n";
    v += "  not (w2, in2);\n";
    v += "  nor (out1, w2, w1);\n";
    v += "*/";

    v += "\n\n";

    v += "endmodule\n";


    v = v.replace(/,\ \)/g, ")");
    v = v.replace(/,\)/g, ")");
    v = v.replace(/,}/g, "}");

    return v;
}

function getVerilogAssign() {

    var v = "";

    v += "module A(";
    v += "output ";
    for(var i = 0; i<vio.outputs.length; ++i) {
        var index = i+1;
        v += "out"+index+", ";
    }

    v += "input ";
    for(var i = 0; i<vio.inputs.length; ++i) {
        var index = i+1;
        v += "in"+index+", ";
    }
    v += ");\n\n";

    v += "// NOT: ~\n";
    v += "// AND: &\n";
    v += "// OR:  |\n";
    v += "// order of operations: ()\n\n";

    v += "/*example: \n";
    v += "  wire w0;\n";
    v += "  assign w0 = in1 & ~in2;\n";
    v += "  assign out1 = w0 | (in1 & in3);\n";
    v += "*/\n";

    for(var i=0; i<vio.inputs.length; ++i) {
        var index = i+1;
        //v += "  assign out"+index + " = (in1 & ~in2) | in3";
        //v += ";\n";
    }

    v += "\n";
    v += "endmodule\n";

    v = v.replace(/,\ \)/g, ")");
    v = v.replace(/,\)/g, ")");
    v = v.replace(/,}/g, "}");
    v = v.replace(/,\ ;/g, ";");
    v = v.replace(/in1/g, "a");
    v = v.replace(/in2/g, "b");
    v = v.replace(/in3/g, "c");
    v = v.replace(/in4/g, "d");

    return v;
}

function getVerilogCase() {

    var v = "";

    var tt_array = [];
    var n = vio.inputs.length;
    for (var i = 0; i != (1 << n); i++) {
        var s = i.toString(2);
        while (s.length != n) {
            s = '0' + s;
        }
        tt_array.push(s);
    }


    v += "module A(output ";
    for(var i = 0; i<vio.outputs.length; ++i) {
        var index = i+1;
        v += "out"+index+", ";
    }
    v += " input ";
    for(var i = 0; i<vio.inputs.length; ++i) {
        var index = i+1;
        v += "in"+index+", ";
    }
    v += ");\n";
    v += "  always@(";
    for(var i = 0; i<vio.inputs.length; ++i) {
        var index = i+1;
        v += "in"+index+",";
    }
    v += ")\n";
    v += "    begin\n";
    v += "      case({";
    for(var j=1; j<=vio.inputs.length; ++j) {
        v += "in"+j+",";
    }
    v += "})\n";


    var out_bits = "";
    for(var i=0; i<vio.outputs.length; ++i) {
        out_bits += "0";
    }

    for(var i=0; i<tt_array.length; ++i) {
        v += "        "+vio.inputs.length+"'b" + tt_array[i] + ": ";
        v += "{";
        for(var j=1; j<=vio.outputs.length; ++j) {
            v += "out"+j+",";
        }
        v += "} = " + vio.outputs.length + "'b" + out_bits + ";\n";
    }

    v += "      endcase\n";
    v += "    end\n";
    v += "endmodule\n";

    v = v.replace(/,\ \)/g, ")");
    v = v.replace(/,\)/g, ")");
    v = v.replace(/,}/g, "}");

    return v;
}



$('#info_verilog').click(function() {
    var rhtml = "<p>"+
        "Verilog is a hardware description language used in electronic design.  A program "+
        "starts with a module definition, which has a module name, and input/output names. "+
        "Note that the order of the promoters specified above corresponds to the order "+
        "of input names specified in the module definition."+
        "</p>"+

        "<table class='table table-bordered' style='table-layout: fixed'>"+
        "<tr>"+
        "<td width='20%'>Case</td>"+
        "<td width='80%'>Useful format to specify a truth table. "+
        "Each input combination is a unique case, "+
        "and a desired output can be defined for each input combination.</td>"+
        "</tr>"+
        "<tr>"+
        "<td width='20%'>Assign</td>"+
        "<td width='80%'>Operators AND (&), OR (|), NOT (~), and parentheses for order of operations"+
        "can be used to specify any logic function.  Internal wires can carry intermediate values"+
        "to build more complex assign statements.</td>"+
        "</tr>"+
        "<tr>"+
        "<td width='20%'>Structural</td>"+
        "<td width='80%'>Can be used to specify the wiring diagram through a netlist, "+
        "a list of connected gates.</td>"+
        "</tr>"+
        "</table>"+

        "<p>"+
        "The Verilog code must be validated before a design run.  A netlist (list of connected gates) " +
        "for a NOR-Inverter Graph will appear if the Verilog is valid.  Note that " +
        "this does not necessarily reflect the final circuit diagram, because gate types and logic motifs " +
        "specified in the UCF will be applied during logic synthesis. "+
        "</p>";
    $('#dialog_pre').html(rhtml);
    $('#dialog').dialog({title: "Verilog language"});
    $('#dialog').dialog('open');
});


$('#info_inputs').click(function() {
    var rhtml = "<p>" +
        "Cello designs transcriptional logic circuits that relate input promoter states " +
        "to the expression of one or more output genes.  This table lists the promoters that " +
        "will act as circuit inputs, where ON/OFF promoter activities are specified in relative expression units (REU). " +
        "The sequence of each promoter is also specified because Cello will output the full DNA sequence " +
        "of the circuit, which includes the promoters specified here.  Note that the order of promoters "
    "corresponds to the order of inputs specified in the Verilog module definition. " +
    "</p>";
    $('#dialog_pre').html(rhtml);
    $('#dialog').dialog({title: "Input promoters"});
    $('#dialog').dialog('open');
});

$('#info_outputs').click(function() {
    var rhtml = "<p>" +
        "The output gene does not affect the circuit designed by Cello.  However, to allow Cello to "+
        "generate the full DNA sequence for the circuit, the DNA sequence of the output gene(s) can be "+
        "specified in this table.  Typical output sequences should concatenate a "+
        "ribozyme, RBS, CDS, and terminator sequence. Note that the order of output genes "+
        "corresponds to the order of outputs specified in the Verilog module definition."+
        "</p>";
    $('#dialog_pre').html(rhtml);
    $('#dialog').dialog({title: "Output genes"});
    $('#dialog').dialog('open');
});

$('#instructions').click(function() {
    var rhtml = "";
    rhtml += "<div class='alert alert-success'> Step 1: Define/choose input promoters and output genes. </div>";
    rhtml += "<div class='alert alert-success'> Step 2: Choose a Verilog method, and edit the code to specify the desired logic function.  This logic function will relate the input promoter states to output gene expression (ON/OFF).  </div>";
    rhtml += "<div class='alert alert-success'> Step 3: Click 'Validate Verilog'. </div>";
    rhtml += "<div class='alert alert-success'> Step 4: Choose a design name and click 'Run'. </div>";
    rhtml += "<div class='alert alert-info'> Note: The default gate library is specified in a user constraint file (UCF), which can be viewed in the Options tab.  A custom gate library can be uploaded in the Options tab. </div>";
    $('#dialog_pre').html(rhtml);
    $('#dialog').dialog({title: "Instructions"});
    $('#dialog').dialog('open');
});

