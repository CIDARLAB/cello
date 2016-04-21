if(!isRegistered()) {
    window.location.replace('index.html');
}

var res = {}; //data model for results page

$( document ).ready( function() {

        if(sessionStorage.results_page) {
            var results_page = JSON.parse(sessionStorage.results_page);
            res.jobID = sessionStorage.jobID;
            res.show_nA = results_page.show_nA;
            res.result_tab = results_page.result_tab;
            res.files = results_page.files;
            res.all_result_files = results_page.all_result_files;
        }
        else {
            res.jobID = sessionStorage.jobID;
            res.show_nA = "000";
        }

        setResults();
    }
);

function setResults() {
    set_result_pulldown();
}

if (typeof String.prototype.startsWith != 'function') {
    // see below for better implementation!
    String.prototype.startsWith = function (str){
        return this.indexOf(str) === 0;
    };
}
if (typeof String.prototype.endsWith != 'function') {
    String.prototype.endsWith = function (str){
        return this.slice(-str.length) == str;
    };
}

function set_filepaths() {

    var all_result_files = res.all_result_files;

    var prefix = res.jobID + "_";

    var files_obj = {};
    files_obj['verilog']  = prefix + "verilog.v";
    files_obj['dnac_log'] = prefix + "dnacompiler_output.txt";
    files_obj['nA_list']  = res.nA_list;

    for(var i=0; i<res.nA_list.length; ++i) {
        var nA = res.nA_list[i];

        //var pad = "000";
        //var n = $('#show_nA').val();
        //var nA = "A" + (pad + n).slice(-pad.length);

        var nA_files = {};
        nA_files.output_rpus   = [];
        nA_files.plasmids      = [];
        nA_files.sbol          = [];

        for(var j=0; j<all_result_files.length; ++j) {
            var file = all_result_files[j];

            if(file.indexOf(prefix+"A"+nA) != -1) {

                //single files
                if(file.endsWith("_wiring_grn.png")) {
                    nA_files.wiring_grn = file;
                }
                if(file.endsWith("_wiring_rpu.png")) {
                    nA_files.wiring_rpu = file;
                }
                if(file.endsWith("_wiring_xfer.png")) {
                    nA_files.wiring_xfer = file;
                }
                if(file.endsWith("_rputable.txt")) {
                    nA_files.rputable = file;
                }
                if(file.endsWith("_toxtable.txt")) {
                    nA_files.toxtable = file;
                }
                if(file.endsWith("_logic_circuit.txt")) {
                    nA_files.logic_circuit = file;
                }
                if(file.endsWith("_bionetlist.txt")) {
                    nA_files.bionetlist = file;
                }
                if(file.endsWith("_circuit_module_rules.eug")) {
                    nA_files.eugene = file;
                }
                if(file.endsWith("_dnaplotlib_Eu_out.png")) {
                    nA_files.dnaplotlib = file;
                }

                //list of files
                if(file.endsWith("_truth.png")) {
                    nA_files.output_rpus.push( file );
                }
                if(file.endsWith(".ape")) {
                    nA_files.plasmids.push( file );
                }
                if(file.endsWith(".sbol")) {
                    nA_files.sbol.push( file );
                }
            }
        }

        files_obj[nA] = nA_files;
    }

    res.files = files_obj;

    showResults();

}



$( "#result_pulldown" ).change(function() {
    res.jobID = $('#result_pulldown').val();
    set_nA_list();
});

$( "#show_nA" ).change(function() {
    res.show_nA = $('#show_nA').val();
    set_filepaths();
});


$( "#download_zip" ).on("click", function() {
    downloadZip();
});


function set_all_result_files() {
    $.ajax({
        url: "results/" + res.jobID,
        type: "GET",
        headers: {
            "Authorization": "Basic " + btoa(sessionStorage.username + ":" + sessionStorage.password)
        },
        data: {
            keyword: "",
            extension: ""
        },
        dataType: "json",
        success: function(response) {
            var filenames = response['files'];
            res.all_result_files = filenames;
            set_nA_list();
            return true;
        },
        error: function() {
            return true;
        }
    });
}


function set_nA_list() {

    var nA_list = [];

    for(var i=0; i<res.all_result_files.length; ++i) {
        var filename = res.all_result_files[i];
        if(filename.indexOf("_logic_circuit") > -1) {
            var l = filename.split('_logic_circuit')[0].length;
            var nA = filename.split('_logic_circuit')[0].substring(l-3,l);
            nA_list.push(nA);
        }
    }

    res.nA_list = nA_list;

    set_nA_pulldown();
}


function set_nA_pulldown() {

    var x = document.getElementById("show_nA");
    removeOptions(x);

    for(var i=0; i<res.nA_list.length; ++i) {
        var custom = document.createElement("option");
        var nA = res.nA_list[i];
        custom.text = nA;
        custom.value = nA;
        x.add(custom);

        var exists = false;
        $('#show_nA option').each(function () {
            if (this.value === res.show_nA) {
                exists = true;
                return false;
            }
        });

        if (!exists) {
            $("#show_nA option:first");
            res.show_nA = $('#show_nA').val();
        }
        else {
            $('#show_nA').val(res.show_nA);
        }
    }

    set_filepaths();
}


function set_result_pulldown() {

    var x = document.getElementById("result_pulldown");

    $.ajax({
        url: "results",
        type: "GET",
        headers: {
            "Authorization": "Basic " + btoa(sessionStorage.username + ":" + sessionStorage.password)
        },
        data: {
        },
        dataType: "json",
        success: function(response) {
            var dirnames = response['folders'];

            for(var i=0; i<dirnames.length; ++i) {
                if(dirnames[i] != '') {
                    var custom = document.createElement("option");
                    custom.text  = dirnames[i].replace(/(\r\n|\n|\r)/gm,"");
                    custom.value = dirnames[i].replace(/(\r\n|\n|\r)/gm,"");
                    x.add(custom);
                }
            }

            var exists = false;
            $('#result_pulldown option').each(function(){
                if (this.value == res.jobID) {
                    exists = true;
                    return false;
                }
            });

            if(!exists) {
                $("#result_pulldown option:first");
                res.jobID = $('#result_pulldown').val();
            }
            else {
                $('#result_pulldown').val(res.jobID);
            }

            set_all_result_files();
        },
        error: function() {
            return true;
        }
    });
}


$( "#delete_result" ).click(function() {

    if($('#result_pulldown').val() == null) {
        return false;
    }

    if (confirm("delete result " + $('#result_pulldown').val() + "?")) {
        var jobID = $('#result_pulldown').val()
        $.ajax({
            url: "results/"  + jobID,
            type: "DELETE",
            headers: {
                "Authorization": "Basic " + btoa(sessionStorage.username + ":" + sessionStorage.password)
            },
            data: {
            },
            dataType: "json",
            success: function(response) {
                location.reload();
            }
        });
    }
});


function removeOptions(selectbox)
{
    var i;
    for(i=selectbox.options.length-1;i>=0;i--)
    {
        selectbox.remove(i);
    }
}


$("#r1").mousedown(function () {
    show(1);
    res.result_tab = 1;
});
$("#r2").mousedown(function () {
    show(2);
    res.result_tab = 2;
});
$("#r3").mousedown(function () {
    show(3);
    res.result_tab = 3;
});
$("#r4").mousedown(function () {
    show(4);
    res.result_tab = 4;
});
$("#r5").mousedown(function () {
    show(5);
    res.result_tab = 5;
});
$("#r6").mousedown(function () {
    show(6);
    res.result_tab = 6;
});


function show(x) { //hide all, then display current mousedown block
    $('#view1').hide();
    $('#view2').hide();
    $('#view3').hide();
    $('#view4').hide();
    $('#view5').hide();
    $('#view6').hide();
    $('#view' + x).show();
}
function dontshow(x) {
    $(x).hide();
}

function downloadZip() {

    if($('#result_pulldown').val() == null) {
        return false;
    }

    var jobID = $('#result_pulldown').val();
    $.ajax({
        url: "downloadzip/" + jobID,
        type: "GET",
        headers: {
            "Authorization": "Basic " + btoa(sessionStorage.username + ":" + sessionStorage.password)
        },
        data: {
        },
        success: function (response) {
            var uriContent = "data:application/zip;base64," + response;
            var encodedUri = uriContent;

            var pom = document.createElement('a');
            pom.setAttribute('href', encodedUri);
            pom.setAttribute('download', $('#result_pulldown').val()+ ".zip");
            pom.style.display = 'none';
            document.body.appendChild(pom);
            pom.click();
            document.body.removeChild(pom);
        },
        error: function() {
        }
    });
}


function showFile(filename, div_id, element_id) {

    if(filename != null && filename != undefined && filename.length > 0) {

        if (filename.indexOf("png") > -1) {
            ajaxPNG(filename, div_id, element_id);
        }
        else {
            ajaxTXT(filename, div_id, element_id);
        }

    }
}



function showResults() {

    var nA_files = res.files[res.show_nA];

    //single files
    showFile(res.files.verilog,      "div1a", "file1a");
    showFile(res.files.dnac_log,     "div1b", "file1b");
    showFile(nA_files.wiring_grn,    "div2a", "file2a");
    showFile(nA_files.wiring_xfer,   "div2c", "file2c");
    showFile(nA_files.rputable,      "div2d", "file2d");
    showFile(nA_files.toxtable,      "div2e", "file2e");
    showFile(nA_files.logic_circuit, "div2f", "file2f");
    showFile(nA_files.bionetlist,    "div2g", "file2g");
    showFile(nA_files.wiring_rpu,    "div3a", "file3a");
    showFile(nA_files.eugene,        "div4a", "file4a");
    showFile(nA_files.dnaplotlib,    "div4b", "file4b");

    //file lists
    showImgSet(nA_files.output_rpus, "div3b", "img3set");
    showPlasmidFiles(nA_files.plasmids, 'plasmid_list');
    showSBOLFiles(nA_files.sbol, 'sbol_list');

    if(res.result_tab > 0) {
        show(res.result_tab);
    }
    else {
        show(1);
    }

    return;
}

function showImgSet(filenames, div_id, element_id) {
    //output RPU files
    $('#'+element_id).html("");

    for(var i=0; i<filenames.length; ++i) {
        var filename = filenames[i].trim();
        if(filename.length > 0) {
            $('#'+element_id).append("" +
                    "<p style='text-align: left'>"+filename+"</p>" +
                    "<img id='outrpu"+i+"' style='width:50%'>"
            );
            ajaxPNG(filename, div_id, 'outrpu'+i);
            $('#'+div_id).show();
        }
        else {
            $('#'+div_id).hide();
        }
    }
}

function showSBOLFiles(filenames, element_id) {
    var sbol_html = "";

    for (var i = 0; i < filenames.length; ++i) {
        if (filenames[i]) {
            filenames[i].trim();

            var tokens = filenames[i].split("/");
            var filename = tokens[tokens.length-1];

            sbol_html += "<p class='sbol_link' onclick='showPlasmid(this)'" + ">" + filename + "</p>";
        }
        else {
        }
    }
    $('#'+element_id).html(sbol_html);

}


function showPlasmidFiles(filenames, element_id) {

    //ape files
    var plasmids_html = "";

    for (var i = 0; i < filenames.length; ++i) {
        if (filenames[i]) {
            filenames[i].trim();

            var tokens = filenames[i].split("/");
            var filename = tokens[tokens.length-1];

            plasmids_html += "<p id=plasmid_link"+i+" class='plasmid_link' onclick='showPlasmid(this)'" + ">" + filename + "</p>";
            $('#div5a').show();
            $('#div5b').show();
        }

    }

    $('#'+element_id).html(plasmids_html);

    if (document.getElementsByClassName('plasmid_link').length > 0) {
        showPlasmid(document.getElementsByClassName('plasmid_link')[0]);
    }
}



function ajaxPNG(filename, div_id, element_id) {

    var jobID = res.jobID;
    $.ajax({
        url: "results/" + jobID + "/" + filename,
        type: "GET",
        headers: {
            "Authorization": "Basic " + btoa(sessionStorage.username + ":" + sessionStorage.password)
        },
        data: {
        },
        success: function (rawImageData) {
            $('#'+element_id).attr('src', "data:image/png;base64," + rawImageData);
            $('#'+div_id).show();
            return true;
        },
        error: function() {
            $('#'+div_id).hide();
            return true;
        }
    });
}

function ajaxTXT(filename, div_id, element_id) {

    var jobID = res.jobID;
    $.ajax({
        url: "results/" + jobID + "/" + filename,
        type: "GET",
        headers: {
            "Authorization": "Basic " + btoa(sessionStorage.username + ":" + sessionStorage.password)
        },
        data: {
        },
        success: function (response) {
            response = response.replace(/</g, '&lt;');
            $('#'+element_id).html(response);
            $('#'+div_id).show();
            return true;
        },
        error: function() {
            $('#'+div_id).hide();
            return true;
        }
    });
}


function showPlasmid(element) {

    var jobID = res.jobID;
    var filename = $(element).html();

    $.ajax({
        url: "results/" + jobID + "/" + filename,
        type: "GET",
        headers: {
            "Authorization": "Basic " + btoa(sessionStorage.username + ":" + sessionStorage.password)
        },
        data: {
        },
        success: function (response) {
            $('#plasmid_text').text(response);
        },
        error: function() {
        }
    });
}




if(findBootstrapEnvironment() === "xs" || findBootstrapEnvironment() === "sm" || findBootstrapEnvironment() === "md") {
    $('#results_well').css('margin-top', '140px');
}
else {
    $('#results_well').css('margin-top', '80px');
}

var mtop = Number($('#results_well').css('margin-top').replace('px', ''));

var pageHeight = jQuery(window).height();
var navHeight = pageHeight - 80 - mtop;
$( ".scrolling" ).css( "max-height", navHeight );
$( "#results_well" ).css( "max-height", navHeight );
$( "#results_well" ).css( "height", navHeight );

$( window ).resize(function() {
    mtop = Number($('#results_well').css('margin-top').replace('px', ''));
    pageHeight = jQuery(window).height();
    navHeight = pageHeight - 80 - mtop;
    $( ".scrolling" ).css( "max-height", navHeight );
    $( "#results_well" ).css( "max-height", navHeight );
    $( "#results_well" ).css( "height", navHeight );

    if(findBootstrapEnvironment() === "xs" || findBootstrapEnvironment() === "sm" || findBootstrapEnvironment() === "md") {
        $('#results_well').css('margin-top', '140px');
    }
    else {
        $('#results_well').css('margin-top', '80px');
    }

});


$(window).bind('beforeunload', function() {
    sessionStorage.jobID = $('#result_pulldown').val();
    sessionStorage.results_page = JSON.stringify(res);
});