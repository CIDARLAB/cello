package org.cellocad.api;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/test")
public class TestController extends BaseController {


    @RequestMapping(method = RequestMethod.GET)
    public String test() {
        return "hi";
    }


    @RequestMapping(value="/testpostmap", method = RequestMethod.POST)
    public ResponseEntity<String> test1(@RequestParam Map<String, String> data) {
        return new ResponseEntity<>(data.toString(), HttpStatus.OK);
    }

    @RequestMapping(value="/testget", method = RequestMethod.GET)
    public ResponseEntity<String> test1(
            @RequestHeader("Authorization") String basic
    ) {
        if(!auth.login(basic)) {
            throw new CelloUnauthorizedException("invalid username/password");
        }
        String username = auth.getUsername(basic);

        return new ResponseEntity<>("GET test1", HttpStatus.OK);
    }



}


