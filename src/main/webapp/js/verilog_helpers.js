
function setVerilogMethod() {
    var verilog_method = $('#method').val();

    if(verilog_method == "demo") {

        resetInputList();
        resetOutputList();

        vio.inputs = [];
        vio.outputs = [];

        var input_obj1 = {};
        input_obj1.prom_name = "pTac";
        input_obj1.lowrpu = 0.0034;
        input_obj1.highrpu = 2.8;
        input_obj1.dnaseq = "AACGATCGTTGGCTGTGTTGACAATTAATCATCGGCTCGTATAATGTGTGGAATTGTGAGCGCTCACAATT";

        var input_obj2 = {};
        input_obj2.prom_name = "pTet";
        input_obj2.lowrpu = 0.0013;
        input_obj2.highrpu = 4.4;
        input_obj2.dnaseq = "TACTCCACCGTTGGCTTTTTTCCCTATCAGTGATAGAGATTGACATCCCTATCAGTGATAGAGATAATGAGCAC";

        vio.inputs.push(input_obj1);
        vio.inputs.push(input_obj2);

        var output_obj1 = {}
        output_obj1.gene_name = "YFP";
        output_obj1.dnaseq = "CTGAAGCTGTCACCGGATGTGCTTTCCGGTCTGATGAGTCCGTGAGGACGAAACAGCCTCTACAAATAATTTTGTTTAATACTAGAGAAAGAGGGGAAATACTAGATGGTGAGCAAGGGCGAGGAGCTGTTCACCGGGGTGGTGCCCATCCTGGTCGAGCTGGACGGCGACGTAAACGGCCACAAGTTCAGCGTGTCCGGCGAGGGCGAGGGCGATGCCACCTACGGCAAGCTGACCCTGAAGTTCATCTGCACCACAGGCAAGCTGCCCGTGCCCTGGCCCACCCTCGTGACCACCTTCGGCTACGGCCTGCAATGCTTCGCCCGCTACCCCGACCACATGAAGCTGCACGACTTCTTCAAGTCCGCCATGCCCGAAGGCTACGTCCAGGAGCGCACCATCTTCTTCAAGGACGACGGCAACTACAAGACCCGCGCCGAGGTGAAGTTCGAGGGCGACACCCTGGTGAACCGCATCGAGCTGAAGGGCATCGACTTCAAGGAGGACGGCAACATCCTGGGGCACAAGCTGGAGTACAACTACAACAGCCACAACGTCTATATCATGGCCGACAAGCAGAAGAACGGCATCAAGGTGAACTTCAAGATCCGCCACAACATCGAGGACGGCAGCGTGCAGCTCGCCGACCACTACCAGCAGAACACCCCAATCGGCGACGGCCCCGTGCTGCTGCCCGACAACCACTACCTTAGCTACCAGTCCGCCCTGAGCAAAGACCCCAACGAGAAGCGCGATCACATGGTCCTGCTGGAGTTCGTGACCGCCGCCGGGATCACTCTCGGCATGGACGAGCTGTACAAGTAACTCGGTACCAAATTCCAGAAAAGAGGCCTCCCGAAAGGGGGGCCTTTTTTCGTTTTGGTCC";

        vio.outputs.push(output_obj1);

        loadInputs();
        loadOutputs();


        var verilog_demo_text = "" +
        "module A(output out1,  input in1, in2);\n" +
        " always@(in1,in2)\n" +
        "  begin\n" +
        "   case({in1,in2})\n" +
        "    2'b00: {out1} = 1'b0;\n" +
        "    2'b01: {out1} = 1'b0;\n" +
        "    2'b10: {out1} = 1'b0;\n" +
        "    2'b11: {out1} = 1'b1;\n" +
        "   endcase\n" +
        "  end\n" +
        "endmodule\n";

        editor.getDoc().setValue(verilog_demo_text);

        $('#method').val('choose');

        return;
    }


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
