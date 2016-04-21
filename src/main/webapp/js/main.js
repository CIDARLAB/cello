

/**
 * These click handlers prevent the new page from flashing up for a split second if isRegistered == false
 */
$('#verilog_link').on('click', function() {
    if(isRegistered()) {
        window.location.replace('verilog.html');
        return false; // prevents current page from reloading so that the new page can be loaded
    }
    else if(window.location.href.indexOf("about.html") > -1){
        window.location.replace('index.html');
        return false;
    }
});
$('#results_link').on('click', function() {
    if(isRegistered()) {
        window.location.replace('result.html');
        return false; // prevents current page from reloading so that the new page can be loaded
    }
    else if(window.location.href.indexOf("about.html") > -1){
        window.location.replace('index.html');
        return false;
    }
});
$('#options_link').on('click', function() {
    if(isRegistered()) {
        window.location.replace('options.html');
        return false; // prevents current page from reloading so that the new page can be loaded
    }
    else if(window.location.href.indexOf("about.html") > -1){
        window.location.replace('index.html');
        return false;
    }
});
$('#about_link').on('click', function() {
    //if(isRegistered()) {
        window.location.replace('about.html');
        return false; // prevents current page from reloading so that the new page can be loaded
    //}
});


//force the X close button to show up in dialog box.
var bootstrapButton = $.fn.button.noConflict() // return $.fn.button to previously assigned value
$.fn.bootstrapBtn = bootstrapButton            // give $().bootstrapBtn the Bootstrap functionality



//smart modification of padding-top based on navbar size when shrinking the window.
$(window).resize(function () {
    $('body').css('padding-top', parseInt($('#main-navbar').css("height"))+10);
});
$(window).load(function () {
    $('body').css('padding-top', parseInt($('#main-navbar').css("height"))+10);
});


//dialog box takes up ~half the screen width, unless it's a huge screen.
var dialogWidth = jQuery(window).width() * 0.6;
if(dialogWidth > 600) {
    dialogWidth = 600;
}

//all dialog boxes are given the .dialog className for consistent properties upon opening
$('.dialog').dialog({
    autoOpen: false, // Do not open on page load
    modal: true, // Freeze the background behind the overlay
    width: dialogWidth
});


/**
 * Knowing the window size allows custom resizing of content/divs when elements are moved to a new line
 * in the document due to bootstrap responsive. (currently only used for the results_well padding-top)
 */
function findBootstrapEnvironment() {
    var envs = ['xs', 'sm', 'md', 'lg'];

    $el = $('<div>');
    $el.appendTo($('body'));

    for (var i = envs.length - 1; i >= 0; i--) {
        var env = envs[i];

        $el.addClass('hidden-'+env);
        if ($el.is(':hidden')) {
            $el.remove();
            return env
        }
    };
}