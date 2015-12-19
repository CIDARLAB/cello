
if(!isRegistered()) {
    /**
     * No redirect is included for the home page, but
     * calling the 'isRegistered' function populates the loginArea in the upper right with the username.
     */
}


$(document).ready(function() {

    $('.carousel').carousel({
        interval: 4000 //4 seconds between carousel image change
    });

});


/**
 * Open a dialog box with a registration form upon clicking the signup button
 */
$('#btn_get_started').click(function() {
    $('#signup_dialog').dialog( 'open' );
});


/**
 * Allow the user to log in using the enter/return key.
 */
$("#loginArea").keypress(function(event) {
    if (event.which == 13) {
        event.preventDefault();
        $('#btnLogin').click();
    }
});

