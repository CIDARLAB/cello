package org.cellocad.api;

import org.cellocad.MIT.dnacompiler.Args;
import org.cellocad.MIT.dnacompiler.UCF;
import org.cellocad.MIT.dnacompiler.Util;
import org.cellocad.adaptors.ucfadaptor.UCFReader;
import org.cellocad.adaptors.ucfadaptor.UCFValidator;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

@RestController
public class UCFController extends BaseController {


    @RequestMapping(value="/ucf/{filename:.+}", method = RequestMethod.GET)
    public @ResponseBody
    String getUCF(
            @RequestHeader("Authorization") String basic,
            @PathVariable("filename") String filename,
            @RequestParam String owner
    ) throws IOException, ParseException {

        if(!auth.login(basic)) {
            throw new CelloUnauthorizedException("invalid username/password");
        }
        String username = auth.getUsername(basic);
        if(owner.equals("default")) {
            username = "default";
        }

        String filePath = _resultPath + "/" + username + "/" + filename;

        FileReader reader = new FileReader(filePath);
        JSONParser jsonParser = new JSONParser();
        JSONArray jsonArray = (JSONArray) jsonParser.parse(reader);
        return jsonArray.toJSONString();
    }


    @RequestMapping(value="/ucf/{filename:.+}", method = RequestMethod.POST)
    public @ResponseBody
    String postUCF(
            @RequestHeader("Authorization") String basic,
            @PathVariable("filename") String filename,
            @RequestParam String filetext
    ) throws IOException, ParseException {

        if(!auth.login(basic)) {
            throw new CelloUnauthorizedException("invalid username/password");
        }
        String username = auth.getUsername(basic);

        String filePath = _resultPath + "/" + username + "/" + filename;
        Util.fileWriter(filePath, filetext, false);

        JSONObject toReturn = new JSONObject();
        toReturn.put("message", "wrote file " + filename);
        return toReturn.toJSONString();
    }


    @RequestMapping(value="/ucf/{filename:.+}/validate",method= RequestMethod.GET)
    public @ResponseBody
    String validateUploadedUCF(
            @RequestHeader("Authorization") String basic,
            @PathVariable String filename
    ) throws IOException {

        if(!auth.login(basic)) {
            throw new CelloUnauthorizedException("invalid username/password");
        }
        String username = auth.getUsername(basic);


        String filePath = _resultPath + "/" + username + "/" + filename;

        UCFReader ucf_reader = new UCFReader();
        UCF ucf = ucf_reader.readAllCollections(filePath);

        UCFValidator ucf_validator = new UCFValidator();
        JSONObject ucf_validation_map = ucf_validator.validateAllUCFCollections(ucf, new Args());
        boolean is_ucf_valid = (boolean) ucf_validation_map.get("is_valid");

        JSONObject toReturn = new JSONObject();

        if(is_ucf_valid) {
            try {
                toReturn.put("status", "VALID");
                toReturn.put("filepath", filePath);
            }catch(Exception e) {
                e.printStackTrace();
            }
        }
        else {
            try {
                toReturn.put("status", "INVALID");
                toReturn.put("filepath", filePath);
            }catch(Exception e) {
                e.printStackTrace();
            }
        }

        return toReturn.toJSONString();
    }


    @RequestMapping(value="/ucf/{filename:.+}", method = RequestMethod.DELETE)
    public @ResponseBody
    String deleteFile(
            @RequestHeader("Authorization") String basic,
            @PathVariable("filename") String filename
    ) {

        if(!auth.login(basic)) {
            throw new CelloUnauthorizedException("invalid username/password");
        }
        String username = auth.getUsername(basic);

        String filepath = _resultPath + "/" + username + "/" + filename;
        Util.deleteFile(new File(filepath));

        JSONObject toReturn = new JSONObject();
        toReturn.put("message", "deleted file " + filepath);
        return toReturn.toJSONString();
    }



}
