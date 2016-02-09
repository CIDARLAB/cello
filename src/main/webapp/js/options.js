if(!isRegistered()) {
    window.location.replace('index.html');
}

var opt = {}; //data model for options page

if(sessionStorage.options_page) {
    var options_page = JSON.parse(sessionStorage.options_page);

    opt.nA = options_page.nA;
    opt.nP = options_page.nP;
    opt.ucf_name = options_page.ucf_name;
    opt.options_string = options_page.options_string;
}

$( document ).ready( function() {
    loadOptionsPageData();
});

var default_ucfs = ["Eco1C1G1T1.UCF.json"];
var user_ucfs = [];
var jsonUCF = null;
var resultRoot = "";


function loadOptionsPageData() {
    if (opt.nA == null || opt.nA == undefined) {
        opt.nA = 1;
    }
    if (opt.nP == null || opt.nP == undefined) {
        opt.nP = 5;
    }
    if (opt.options_string == null || opt.options_string == undefined) {
        opt.options_string = "";
    }

    getResultRoot();
    loadSettings();
    findUCFs();
}

function getResultRoot() {
    $.ajax({
        url: "resultsroot",
        type: "GET",
        headers: {
            "Authorization": "Basic " + btoa(sessionStorage.username + ":" + sessionStorage.password)
        },
        data: {
        },
        success: function (response) {
            resultRoot = response;
        }
    });
}


function deleteUCF() {

    var className = $('#ucf_pulldown').find('option:selected').attr("class");

    if(className == "user_ucf") {

        var filename = $('#ucf_pulldown').val();
        if (confirm("delete UCF " + filename + "?")) {
            $.ajax({
                url: "ucf/" + filename,
                type: "DELETE",
                headers: {
                    "Authorization": "Basic " + btoa(sessionStorage.username + ":" + sessionStorage.password)
                },
                data: {
                },
                dataType:"json",
                success: function (response) {
                    opt.ucf_name = "null";
                    findUCFs();
                },
                error: function(response) {
                    var text = response.responseText;
                    console.log(text);
                }
            });
        }

    }
    else {
        $('#dialog_pre').html('<div class="alert alert-danger"> cannot delete default UCF </div>');
        $('#dialog').dialog({title:"UCF"});
        $('#dialog').dialog( 'open' );
    }

}



function downloadBase64File(response, filename) {
    console.log('download ' + filename);
    var Base64={_keyStr:"ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/=",encode:function(e){var t="";var n,r,i,s,o,u,a;var f=0;e=Base64._utf8_encode(e);while(f<e.length){n=e.charCodeAt(f++);r=e.charCodeAt(f++);i=e.charCodeAt(f++);s=n>>2;o=(n&3)<<4|r>>4;u=(r&15)<<2|i>>6;a=i&63;if(isNaN(r)){u=a=64}else if(isNaN(i)){a=64}t=t+this._keyStr.charAt(s)+this._keyStr.charAt(o)+this._keyStr.charAt(u)+this._keyStr.charAt(a)}return t},decode:function(e){var t="";var n,r,i;var s,o,u,a;var f=0;e=e.replace(/[^A-Za-z0-9\+\/\=]/g,"");while(f<e.length){s=this._keyStr.indexOf(e.charAt(f++));o=this._keyStr.indexOf(e.charAt(f++));u=this._keyStr.indexOf(e.charAt(f++));a=this._keyStr.indexOf(e.charAt(f++));n=s<<2|o>>4;r=(o&15)<<4|u>>2;i=(u&3)<<6|a;t=t+String.fromCharCode(n);if(u!=64){t=t+String.fromCharCode(r)}if(a!=64){t=t+String.fromCharCode(i)}}t=Base64._utf8_decode(t);return t},_utf8_encode:function(e){e=e.replace(/\r\n/g,"\n");var t="";for(var n=0;n<e.length;n++){var r=e.charCodeAt(n);if(r<128){t+=String.fromCharCode(r)}else if(r>127&&r<2048){t+=String.fromCharCode(r>>6|192);t+=String.fromCharCode(r&63|128)}else{t+=String.fromCharCode(r>>12|224);t+=String.fromCharCode(r>>6&63|128);t+=String.fromCharCode(r&63|128)}}return t},_utf8_decode:function(e){var t="";var n=0;var r=c1=c2=0;while(n<e.length){r=e.charCodeAt(n);if(r<128){t+=String.fromCharCode(r);n++}else if(r>191&&r<224){c2=e.charCodeAt(n+1);t+=String.fromCharCode((r&31)<<6|c2&63);n+=2}else{c2=e.charCodeAt(n+1);c3=e.charCodeAt(n+2);t+=String.fromCharCode((r&15)<<12|(c2&63)<<6|c3&63);n+=3}}return t}}

    var decodedString = Base64.decode(response);

    var blob = new Blob([decodedString], { type: 'plain/text' });
    var url = URL.createObjectURL(blob);

    var pom = document.createElement('a');
    pom.setAttribute('href', url);
    pom.setAttribute('download', filename);
    pom.style.display = 'none';
    document.body.appendChild(pom);
    pom.click();
    document.body.removeChild(pom);

    console.log('done');
}



function downloadUCF() {

    var filename = $('#ucf_pulldown').val();
    var className = $('#ucf_pulldown').find('option:selected').attr("class");

    var owner = "default";

    if(className == "user_ucf") {
        owner = sessionStorage.username;
    }

    $.ajax({
        url: "downloaducf/" + filename,
        type: "GET",
        headers: {
            "Authorization": "Basic " + btoa(sessionStorage.username + ":" + sessionStorage.password)
        },
        data: {
            owner: owner
        },
        success: function (response) {
            downloadBase64File( response.data, $('#ucf_pulldown').val() );
        }
    });
}


function findUCFs() {

    $('#ucf_collection').text("loading UCF data...");

    var x = document.getElementById("ucf_pulldown");
    removeOptions(x);

    //Default UCF's
    for(var i in default_ucfs) {
        var ucf_name = default_ucfs[i];

        var ucf = document.createElement("option");
        ucf.text = ucf_name;
        ucf.value = ucf_name;
        ucf.className = "default_ucf";
        x.add(ucf);
    }

    // User custom UCF's only
    $.ajax({
        url: "ucf",
        type: "GET",
        headers: {
            "Authorization": "Basic " + btoa(sessionStorage.username + ":" + sessionStorage.password)
        },
        data: {
            keyword: "UCF",
            extension: "json"
        },
        dataType: "json",
        success: function (response) {
            var filenames = response['files'];
            for(var i=0; i<filenames.length; ++i) {
                filenames[i] = filenames[i].replace(/(\r\n|\n|\r)/gm,"");
                if(filenames[i]) {
                    filenames[i].trim();
                    var custom = document.createElement("option");
                    custom.text  = filenames[i];
                    custom.value = filenames[i];
                    custom.className = "user_ucf";
                    x.add(custom);
                    user_ucfs.push(filenames[i]);
                }
            }

            if(!opt.ucf_name) {
                opt.ucf_name = default_ucfs[0];
            }

            if(opt.ucf_name.indexOf("json") == -1) {
                opt.ucf_name = default_ucfs[0];
            }

            $('#ucf_pulldown').val(opt.ucf_name);


            chooseUCF();
        }
    });
}

function removeOptions(selectbox)
{
    if(selectbox != null) {
        var i;
        for (i = selectbox.options.length - 1; i >= 0; i--) {
            selectbox.remove(i);
        }
    }
}


$('#ucf_pulldown').on('change', function() {
    chooseUCF();
    opt.ucf_name = $('#ucf_pulldown').val();
    saveOptions();
});

$('#collection_pulldown').on('change', function() {
    chooseUCF();
});


function chooseUCF() {

    var className = $('#ucf_pulldown').find('option:selected').attr("class");

    var url = "";
    var filename = $('#ucf_pulldown').val();
    var owner = "default";

    if(className == "user_ucf") {
        owner = sessionStorage.username;
    }


    $.ajax({
        url: "ucf/" + filename,
        type: "GET",
        headers: {
            "Authorization": "Basic " + btoa(sessionStorage.username + ":" + sessionStorage.password)
        },
        data: {
            owner: owner
        },
        success: function (response) {
            var jsonString = JSON.stringify(response['ucf']);
            jsonUCF = JSON.parse(jsonString);
            showCollection();
        }
    });
}


function showCollection() {

    var objects = [];

    for (var i = 0; i < jsonUCF.length; ++i) {

        var obj = jsonUCF[i];
        for (var key in obj) {
            if (key == "collection" && obj[key] == $('#collection_pulldown').val().trim()) {
                objects.push(obj);
            }
        }
    }

    var json_string = JSON.stringify(objects, null, 2) + "\n";

    $('#ucf_collection').text(json_string);
}

function processUploadUCF(that) {

    if(that.files && that.files[0])
    {
        var reader = new FileReader();
        reader.readAsText(that.files[0]);
        reader.onload = function (e) {

            var filename = that.files[0].name;
            var ucf_text = e.target.result;
            console.log(ucf_text);

            //write to disk
            $.ajax({
                url: "ucf/" + filename,
                type: "POST",
                headers: {
                    "Authorization": "Basic " + btoa(sessionStorage.username + ":" + sessionStorage.password)
                },
                data:  {
                    filetext:  ucf_text
                },
                dataType: "json",
                success: function (response) {
                    var message = response['message'];
                    findUCFs();
                    validateUCF(filename);
                }
            });
        }
    }
}



$("#validating_ucf_dialog").dialog({
    hide: 'slide',
    show: 'slide',
    autoOpen: false
});

function validateUCF(filename) {

    $("#validating_ucf_dialog").dialog({width:300});
    $("#validating_ucf_dialog").dialog('open').html("<p>please wait...</p>");

    $.ajax({
        url: "ucf/" + filename + "/validate",
        type: "GET",
        headers: {
            "Authorization": "Basic " + btoa(sessionStorage.username + ":" + sessionStorage.password)
        },
        data: {
        },
        dataType:"json",
        success: function(response) {
            var rhtml = "<div class='alert alert-success'>"+response['status']+"</div>";
            if(response['status'] == "VALID") {
                $('#ucf_pulldown').val(filename);
                opt.ucf_name = filename;
                $('#validating_ucf_dialog').html(rhtml);
                $('#validating_ucf_dialog').on('dialogclose', function(event) {
                    chooseUCF();
                });
            }
            else {
                var rhtml = "<div class='alert alert-danger'>"+response['status']+"</div>";
                $('#ucf_pulldown').val(filename);
                $('#validating_ucf_dialog').html(rhtml);
                $('#validating_ucf_dialog').on('dialogclose', function(event) {
                    deleteUCF();
                    location.reload();
                });
            }
        }
    });

    return true;
}


var pageHeight = jQuery(window).height();
var navHeight = pageHeight - 80;
$( ".dynamic_height_div" ).css( "max-height", navHeight );
$( ".dynamic_height_div" ).css( "height", navHeight );

$( window ).resize(function() {
    pageHeight = jQuery(window).height();
    navHeight = pageHeight - 80;
    $( ".dynamic_height_div" ).css( "max-height", navHeight );
    $( ".dynamic_height_div" ).css( "height", navHeight );
});


function loadSettings() {
    $('#nA').val(opt.nA);
    $('#nP').val(opt.nP);
}

function saveOptions()
{
    var text_options = "";
    text_options += " -nA "  + opt.nA;
    text_options += " -nP "  + opt.nP;
    text_options += " -UCF " + opt.ucf_name;

    if($('#nP').val() < Number($('#nP').attr('min'))) {
        $('#nP').val(opt.nP);
    }
    else if($('#nP').val() > Number($('#nP').attr('max'))) {
        $('#nP').val(opt.nP);
    }
    else if($('#nA').val() < Number($('#nA').attr('min'))) {
        $('#nA').val(opt.nA);
    }
    else if($('#nA').val() > Number($('#nA').attr('max'))) {
        $('#nA').val(opt.nA);
    }

    opt.nA = $('#nA').val();
    opt.nP = $('#nP').val();
    opt.ucf_name = $('#ucf_pulldown').val();
    opt.options_string = text_options;

    console.log(text_options);
}


$('#save_options').on("click", function(){

    //will check max/min values specified in html
    if($('#nP').val() < Number($('#nP').attr('min'))) {
    }
    else if($('#nP').val() > Number($('#nP').attr('max'))) {
    }
    else if($('#nA').val() < Number($('#nA').attr('min'))) {
    }
    else if($('#nA').val() > Number($('#nA').attr('max'))) {
    }
    else {
        saveOptions();
        $(function() {
            $('#saved_message').css('display', 'inline-block');
            $('#saved_message').delay(1000).fadeOut();
        });
        return false; //do not refresh page
    }

});


$(window).bind('beforeunload', function() {
    saveOptions();
    sessionStorage.options_page = JSON.stringify(opt);
});