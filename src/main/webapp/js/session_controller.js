function isRegistered() {
    if(sessionStorage.registered === "true") {
        $('#loginArea').html('<div class="navbar-form pull-right">' +
            'You are logged in as <strong>' + sessionStorage.username + '</strong>&nbsp;&nbsp;&nbsp;&nbsp;'
            + '<button id="btnLogout" class="btn btn-warning">Logout</button>');
        return true;
    }
    else {
        return false;
    }
}


$(document).ready(function() {


//-----------------------------------------------
// AUTHENTICATION
//-----------------------------------------------

// SIGNUP Button
    $('#btnSignUp').click(function() {
        
        
        if($('#signup_password').val() !== $('#reenter_signup_password').val()){
            $('#signupError').html('<div class="alert alert-danger"> Passwords don\'t match. </div>');
            
        }else{
        
        var jsonRequest = {
            "command": "signup",
            "username": $('#signup_username').val(),
            "password": $('#signup_password').val()
        };

        $.post("authentication", jsonRequest, function (response) {

            // if there was an error, then we display the error
            if (response['status'] === 'exception') {
                $('#signupError').html('<div class="alert alert-danger">' + response['result'] + '</div>');
            } else {
                $('#signupError').html('<div class="alert alert-success"> Success! </div>');

                sessionStorage.registered = "true";
                sessionStorage.username = $('#signup_username').val();

                //alert('SUCCESSFUL SIGNUP !\n' + JSON.stringify(response));
            }
        });
        }
    });

// LOGIN button
    $('#btnLogin').click(function() {

        $.ajax({
            url: "authentication",
            type: "POST",
            data: {
                command: "login",
                username: $('#login_username').val(),
                password: $('#login_password').val()
            },
            success: function (response) {

                console.log('login response ' + JSON.stringify(response));

                if (response['status'] === 'exception') {
                    $('#loginError').html('<div class="alert alert-danger" style="margin-top:5px">' + response['result'] + '</div>');
                } else {
                    $('#loginError').html('');

                    sessionStorage.registered = "true";
                    sessionStorage.username = $('#login_username').val();
                    sessionStorage.password = $('#login_password').val();

                    window.location.replace('verilog.html');
                }
            }
        });

    });

// LOGOUT Button
    $('#btnLogout').click(function() {
        sessionStorage.clear();
        window.location.replace('index.html');
    });



});
