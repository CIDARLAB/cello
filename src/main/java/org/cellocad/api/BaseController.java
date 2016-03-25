package org.cellocad.api;

import org.apache.tools.ant.DirectoryScanner;
import org.cellocad.authenticate.Authenticator;
import org.json.simple.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
public class BaseController {

    private static final String USER_DB_NAME = "CELLO";
    public Authenticator auth = new Authenticator(USER_DB_NAME);
    public final String _resultPath;
    public final String _srcPath;

    public BaseController() {

        String _filepath = FileController.class.getClassLoader().getResource(".").getPath();

        if (_filepath.contains("/target/")) {
            _filepath = _filepath.substring(0, _filepath.lastIndexOf("/target/"));
        }
        else if(_filepath.contains("/build/")) {
            _filepath = _filepath.substring(0, _filepath.lastIndexOf("/build/"));
        }
        else if(_filepath.contains("/src/")) {
            _filepath = _filepath.substring(0, _filepath.lastIndexOf("/src/"));
        }

        String[] split_rootPath = _filepath.split("/");
        String rootPath = "";
        for(int i=0; i< split_rootPath.length - 1; ++i ) {
            if(!split_rootPath[i].isEmpty()) {
                rootPath += "/" + split_rootPath[i];
            }
        }

        String projectName = split_rootPath[split_rootPath.length-1];

        _resultPath = rootPath + "/" + projectName + "_results";
        _srcPath = rootPath + "/" + projectName;
    }




    @ExceptionHandler(CelloUnauthorizedException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ErrorResponse handle(CelloUnauthorizedException e) {
        return new ErrorResponse(e.getMessage()); // use message from the original exception
    }

    public static class CelloUnauthorizedException extends RuntimeException {
        public CelloUnauthorizedException(String message) {
            super(message);
        }
    }

    @ExceptionHandler(CelloNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handle(CelloNotFoundException e) {
        return new ErrorResponse(e.getMessage()); // use message from the original exception
    }

    public static class CelloNotFoundException extends RuntimeException {
        public CelloNotFoundException(String message) {
            super(message);
        }
    }

    public static class ErrorResponse {
        public String message;

        public ErrorResponse(String message) {
            this.message = message;
        }
    }



}
