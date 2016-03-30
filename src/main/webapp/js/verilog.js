if(!isRegistered()) {
    window.location.replace('index.html');
}

//these variables are used in 'input_output.js'

var vio = {}; //verilog, input, output
var previous_designs = [];

if(sessionStorage.verilog_page) {
    var verilog_page = JSON.parse(sessionStorage.verilog_page);

    vio.verilog      = verilog_page.verilog;
    vio.inputs_list  = verilog_page.inputs_list;
    vio.outputs_list = verilog_page.outputs_list;
    vio.inputs       = verilog_page.inputs;
    vio.outputs      = verilog_page.outputs;
}

$( document ).ready( function() {

    setPreviousDesignNames();
    loadVerilogPageData();
});



function loadVerilogPageData() {

    //load the previous Verilog code when returning to this page
    if(vio.verilog !== undefined && vio.verilog !== null) {
        editor.getDoc().setValue(vio.verilog); //load verilog
    }

    vio.inputs_list = new Array();
    vio.outputs_list = new Array();
    getInputFiles();
    getOutputFiles();
}






//////////////// Verilog syntax highlighting via CodeMirror /////////////
var editor = CodeMirror.fromTextArea(document.getElementById("verilogArea"), {
    styleActiveLine: false,
    gutter: true,
    lineNumbers: true,
    lineWrapping: true,
    theme: "neat",  //specifies colors for codemirror classes, see css/neat.css
    mode: "verilog" //adds codemirror classes based on syntax rules, see js/verilog_codemirror.js
});

//every time the Verilog code changes, it will have to be validated again for whether or not
//it produces a valid 'netlist' (list of connected gates) by NetSynth.
editor.on("change", function(cm, change) {
    requireValidation();
});


//hide the 'submit' button, show the 'validate' button.  This prevents the user from submitting a
//if the Verilog code has not yet been validated.
function requireValidation() {
// // This code was used when a Validate Verilog button had to be clicked before the Run button could be clicked.
//    $('#validate').show();
//    $('#submit').hide();
//    $('#running').hide();
//    $('#design_name').hide();
//    $('#design_name_label').hide();
//    $('#netlist').html("");
}


//server-side call to NetSynth to see if the Verilog was valid.
//I'm validating Verilog empirically, rather than using syntax checking.
//The validation is based on the String representation of the 'netlist', which must contain
//one or more 'INPUT', 'OUTPUT' keywords, and must have an ( and ) parenthesis in each line.
//TODO This is not a rigorous way to validate a netlist.
function getNetlist() {

    var v = editor.getDoc().getValue();

    console.log('get netlist');

    return $.ajax({
        url: "netsynth", //see org.cellocad.springcontrollers.MainController
        type: "POST",
        headers: {
            "Authorization": "Basic " + btoa(sessionStorage.username + ":" + sessionStorage.password)
        },
        data: {
            verilog_text: String(v)
        },
        success: function(response) {
//            console.log('valid netlist');

// // This code was used when a Validate Verilog button had to be clicked before the Run button could be clicked.
//            //show the 'submit' button if the Verilog was valid.
//            $('#validate').hide();
//            $('#submit').show();
//            $('#design_name').show();
//            $('#design_name_label').show();
//
//            //show the netlist.  This is not necessarily the final netlist, it's just the NOR-Inverter Graph.
//            //the netlist might change upon subgraph swapping.
//            $('#dialog_pre').html(response);
//            $('#dialog').dialog({title:"NOR-Inverter Graph"});
//            $('#dialog').dialog( 'open' );
        },
        error: function() {
            $('#dialog_pre').html("The Verilog code did not produce a valid netlist.");
            $('#dialog').dialog({title:"NOR-Inverter Graph"});
            $('#dialog').dialog( 'open' );
        }
    });
}



//This is what actually launches a Cello design run.
//1. set the design name
//2. set the Verilog code
//3. set the input promoter text
//4. set the output gene text
//5. set other options from sessionStorage.options (see options.js).  Other options are specified using
//   dashes and spaces, which is split into a String array to call a java main function.
// (see Args.java for how options are parsed).
// (see DNACompiler.java, which executes each step in the Cello design workflow).


function failCallback(result) {
    console.log('Failed result callback');
}

function submitCello() {

    $.when(
        getNetlist()
    ).then(
        function (result) {
            console.log('submit cello');

            runDnaCompiler();
        },
        failCallback
    );
}

function runDnaCompiler() {

    //1. set teh design name (no whitespace)
    var design_name = $('#design_name').val().replace(/ /g,"_");
    $('#design_name').val(design_name);
    sessionStorage.jobID = design_name;

    //do not allow overwriting of a previous result
    if(previous_designs.indexOf(design_name) != -1) {
        $('#dialog_pre').html("'"+design_name+"' already exists in your results.");
        $('#dialog').dialog({title:"Design name."});
        $('#dialog').dialog( 'open' );

        return;
    }

    //2. set the Verilog code.
    var v = editor.getDoc().getValue();
    vio.verilog = v;

    //3. save the input promoters and output genes, will repopulate the HTML tables when the user
    //   returns to the verilog.html page.

    //   table of 4 columns for each input promoter (as a single string)
    var user_input_table = makeInputTableText(vio.inputs);

    //   table of 2 columns for each output gene (as a single string)
    var user_output_table = makeOutputTableText(vio.outputs);


    //Check to see that the Verilog code does not require more inputs/outputs than specified in the HTML tables
    var v_inputs  = get_v_inputs();  //# inputs specified in Verilog
    var v_outputs = get_v_outputs(); //# outputs specified in Verilog

    if(v_inputs > vio.inputs.length) {
        $('#dialog_pre').html(v_inputs + ' inputs required, ' + vio.inputs.length + ' defined.');
        $('#dialog').dialog({title:"Not enough input promoters specified in the table."});
        $('#dialog').dialog( 'open' );
        return;
    }
    if(v_outputs > vio.outputs.length) {
        $('#dialog_pre').html(v_outputs + ' outputs required, ' + vio.outputs.length + ' defined.');
        $('#dialog').dialog({title:"Not enough output promoters specified in the table."});
        $('#dialog').dialog( 'open' );
        return;
    }


    //show that Cello is 'Running'
    $('#wiring_div').hide();
    $('#dnaplot_div').hide();
    $('#validate').hide();
    $('#submit').hide();
    $('#running').show();
    $('#input_output_div').hide();
    $('#dnac').show();

    // single string of options.
    // For example: "-nA 1 -nP 5".
    // Will be converted to a String array and parsed in Args.java

    var options_string = "";

    if(sessionStorage.options_page) {
        var options_page = JSON.parse(sessionStorage.options_page);
        options_string = options_page.options_string;
    }

    // runtimeLoop() will stop when the status != 'running'
    status = "running";

    // automatically scroll to the bottom of the text area as content is added,
    // unless the user's manual scroll is detected.
    scrolled = false;

    // while Cello is running, update the textarea with the verbose program text output,
    // which allows the user to view the design progress.
    runtimeLoop(100000);


    // server-side call to DNACompiler.run(String[] args)
    // This controller will write text files to the design directory:
    // Files to be written: jobID_verilog.v, jobID_inputs.txt, jobID_outputs.txt,

    $.ajax({
        url: "submit",
        type: "POST",
        headers: {
            "Authorization": "Basic " + btoa(sessionStorage.username + ":" + sessionStorage.password)
        },
        data: {
            id: $('#design_name').val(),
            verilog_text: String(v),
            input_promoter_data:  user_input_table,
            output_gene_data: user_output_table,
            options: options_string
        },
        dataType: "json",
        success: function(response) { //response text is based on DNACompiler.get_result_status()
            var message = "Job did not finish, email cellohelp@gmail.com with bug reports";

            if ('message' in response) {
                message = response['message'];
            }

            status = "finished"; // stops the runtimeLoop.
            if(message.indexOf('SUCCESS') != -1) {
                var rhtml = "<div class='alert alert-success'>"+ message + "</div>";
                $('#dialog_pre').html(rhtml);
                $('#dialog').dialog({title:"Run finished"});
                $('#dialog').dialog('open');
            }
            else if(message.indexOf('WARNING') != -1) {
                var rhtml = "<div class='alert alert-warning'>"+ message + "</div>";
                $('#dialog_pre').html(rhtml);
                $('#dialog').dialog({title:"Run finished"});
                $('#dialog').dialog('open');
            }
            else {
                var rhtml = "<div class='alert alert-danger'>"+ message + "</div>";
                $('#dialog_pre').html(rhtml);
                $('#dialog').dialog({title:"Run finished"});
                $('#dialog').dialog('open');
            }
            $('#dialog').on('dialogclose', function(event) {
                requireValidation();
                sessionStorage.jobID = $('#design_name').val();
                window.location.href="result.html"; // open the results page when the job finishes.
            });

        },
        error: function(response) { //response text is based on DNACompiler.get_result_status()
            status = "error"; // stops the runtimeLoop.
            var rhtml = "<div class='alert alert-danger'>"+ "Job did not finish, email cellohelp@gmail.com with bug reports" + "</div>";
            $('#dialog_pre').html(rhtml);
            $('#dialog').dialog({title:"Run finished"});
            $('#dialog').dialog('open');

            $('#dialog').on('dialogclose', function(event) {
                requireValidation();
            });
        }
    });
}


// check directory names for a match to the design_name (String comparison).
// do not allow overwriting of a previous design name
function setPreviousDesignNames() {
    $.ajax({
        url: "/results",
        type: "GET",
        headers: {
            "Authorization": "Basic " + btoa(sessionStorage.username + ":" + sessionStorage.password)
        },
        data:{},
        dataType: "json",
        success: function(response) {
            previous_designs = response['folders'];
        }
    });
}


var status = "idle";
var seconds = 0;
function runtimeLoop (i) { //while Cello is running...
    if(status == "running") {
        setTimeout(function () {

            //display runtime elapsed
            seconds++;
            var float_seconds = seconds/10;
            $('#runtime').html("<small> Design in progress. Results will load automatically.</small>");


            var jobid = $('#design_name').val();
            var filename = $('#design_name').val() + "_" + "dnacompiler_output.txt";

            //get text from the textfile that the Cello logger writes to, update the HTML textarea
            $.ajax({
                url: "/results/" + jobid + "/" + filename,
                type: "GET",
                headers: {
                    "Authorization": "Basic " + btoa(sessionStorage.username + ":" + sessionStorage.password)
                },
                success: function(response) {
                    response = response.replace(/</g, '&lt;');
                    $('#dnac_txt').html("<small>"+ response + "</small>");

                    //keep scrolling to bottom, unless the user scrolls at least once
                    if(!scrolled) {
                        $("#dnac").scrollTop($("#dnac")[0].scrollHeight);
                    }
                }
            });

            if (--i) runtimeLoop(i); //  decrement i and call runtimeLoop again if i > 0
        }, 100) // update every 0.1 seconds (100 milliseconds)
    }
}(10000);//indefinite


var scrolled = false;

// if the user scrolls in the textarea that's auto-updating as Cello runs,
// disable the autoscroll to the bottom.  This will allow the user to see the text of interest.
$(window).bind('mousewheel', function(event) {
    if (event.originalEvent.wheelDelta >= 0) {
        scrolled = true; //scrollup
    }
    else {
        //scrolldown
    }
});


//initialize the dimensions of the divs according to the page size.
var pageHeight = jQuery(window).height();
var navHeight = pageHeight - 80;
$( ".dynamic_height_div" ).css( "max-height", navHeight );
$( ".dynamic_height_div" ).css( "height", navHeight );

//update the dimensions of the divs as the page size changes.
$( window ).resize(function() {
    pageHeight = jQuery(window).height();
    navHeight = pageHeight - 80;
    $( ".dynamic_height_div" ).css( "max-height", navHeight );
    $( ".dynamic_height_div" ).css( "height", navHeight );
});


//save before leaving page
$(window).bind('beforeunload', function(e) {
    vio.verilog = editor.getDoc().getValue();
    saveInputs();
    saveOutputs();
    sessionStorage.verilog_page = JSON.stringify(vio);

    if(status == "running") {
        var message = "Leaving page before run is complete.";
        e.returnValue = message;
        return message;
    }
});

$('#test_click').click(function() {
});

