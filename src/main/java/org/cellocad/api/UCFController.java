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
import java.util.ArrayList;

@RestController
public class UCFController extends BaseController {


    @RequestMapping(value="/ucf",method= RequestMethod.GET, produces = "application/json")
    public @ResponseBody
    JSONObject getUCFFiles(
            @RequestHeader("Authorization") String basic
    ) {

        if(!auth.login(basic)) {
            throw new CelloUnauthorizedException("invalid username/password");
        }
        String username = auth.getUsername(basic);

        String keyword = "UCF";
        String extension = "json";

        String filePath = _resultPath + "/" + username;
        File f = new File(filePath);

        if (f.exists()) {
            ArrayList<String> fileNames = new ArrayList<>();

            File files[] = f.listFiles();
            for (int i = 0; i < files.length; ++i) {
                if(files[i].isDirectory()) {
                    continue;
                }
                String fileName = files[i].getName();
                if (fileName.contains(keyword) && fileName.endsWith(extension)) {
                    fileNames.add(fileName);
                }
            }
            String[] fileArray = fileNames.toArray(new String[fileNames.size()]);
            JSONObject response = new JSONObject();
            response.put("files", fileArray);
            return response;
        } else {
            return null;
        }
    }


    @RequestMapping(value="/ucf/{filename:.+}", method = RequestMethod.GET, produces = "application/json")
    public @ResponseBody
    JSONObject getUCF(
            @RequestHeader("Authorization") String basic,
            @PathVariable("filename") String filename,
            @RequestParam String owner
    ) throws IOException, ParseException {


        if(!auth.login(basic)) {
            throw new CelloUnauthorizedException("invalid username/password");
        }
        String username = auth.getUsername(basic);


        String filePath = "";

        if(owner.equals("default")) {
            filePath = _srcPath + "/resources/UCF/" + filename;
        }
        else if(!username.equals(owner)) {
            throw new CelloUnauthorizedException("owner is not the authenticated user.");
        }
        else {
            filePath = _resultPath + "/" + username + "/" + filename;
        }


        FileReader reader = new FileReader(filePath);
        JSONParser jsonParser = new JSONParser();
        JSONArray jsonArray = (JSONArray) jsonParser.parse(reader);

        JSONObject response = new JSONObject();
        response.put("ucf", jsonArray);
        return response;
    }


    @RequestMapping(value="/ucf/{filename:.+}",method= RequestMethod.POST)
    public @ResponseBody
    String postUCF(
            @RequestHeader("Authorization") String basic,
            @PathVariable("filename") String filename,
            @RequestParam String filetext
    ) {

        if(!auth.login(basic)) {
            throw new CelloUnauthorizedException("invalid username/password");
        }
        String username = auth.getUsername(basic);

        String filePath = _resultPath + "/" + username + "/" + filename;
        Util.fileWriter(filePath, filetext, false);

        JSONObject response = new JSONObject();
        response.put("message", "wrote file " + filename);
        return response.toJSONString();
    }


    @RequestMapping(value="/ucf/{filename:.+}/validate",method= RequestMethod.GET, produces = "application/json")
    public @ResponseBody
    JSONObject validateUploadedUCF(
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
                toReturn.put("filepath", filename);
            }catch(Exception e) {
                e.printStackTrace();
            }
        }
        else {
            try {
                toReturn.put("status", "INVALID");
                toReturn.put("filepath", filename);
            }catch(Exception e) {
                e.printStackTrace();
            }
        }

        return toReturn;
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
        toReturn.put("message", "deleted file " + filename);
        return toReturn.toJSONString();
    }



}
