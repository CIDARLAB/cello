package org.cellocad.api;


import org.cellocad.MIT.dnacompiler.Util;
import org.json.simple.JSONObject;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Map;

@RestController
public class InOutController extends BaseController {


    @RequestMapping(value = "/in_out", method = RequestMethod.GET, produces = "application/json")
    public
    @ResponseBody
    JSONObject getResultFiles(
            @RequestHeader("Authorization") String basic,
            @RequestParam Map<String, String> params
    ) {

        if (!auth.login(basic)) {
            throw new CelloUnauthorizedException("invalid username/password");
        }
        String username = auth.getUsername(basic);

        String keyword = "";
        String extension = "";

        if (params.containsKey("keyword")) {
            keyword = params.get("keyword");
        }
        if (params.containsKey("extension")) {
            extension = params.get("extension");
        }

        String filePath = _resultPath + "/" + username;
        File f = new File(filePath);

        if (f.exists()) {
            ArrayList<String> fileNames = new ArrayList<>();

            File files[] = f.listFiles();
            for (int i = 0; i < files.length; ++i) {
                if (files[i].isDirectory()) {
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

    @RequestMapping(value = "/in_out/{filename:.+}", method = RequestMethod.GET, produces = "text/plain")
    public
    @ResponseBody
    String getResultFile(
            @RequestHeader("Authorization") String basic,
            @PathVariable("filename") String filename
    ) {

        if (!auth.login(basic)) {
            throw new CelloUnauthorizedException("invalid username/password");
        }
        String username = auth.getUsername(basic);

        String filePath = _resultPath + "/" + username + "/" + filename;
        String fileContents = "";
        try {
            fileContents = readFile(filePath, Charset.defaultCharset());
        } catch (IOException e) {
            e.printStackTrace();
        }

        return fileContents;
    }

    private static String readFile(String path, Charset encoding) throws IOException {
        byte[] encoded = Files.readAllBytes(Paths.get(path));
        return new String(encoded, encoding);
    }


    @RequestMapping(value = "/in_out/{filename:.+}", method = RequestMethod.POST)
    public
    @ResponseBody
    String writeFile(
            @RequestHeader("Authorization") String basic,
            @PathVariable("filename") String filename,
            @RequestParam String filetext
    ) {

        if (!auth.login(basic)) {
            throw new CelloUnauthorizedException("invalid username/password");
        }
        String username = auth.getUsername(basic);

        String filePath = _resultPath + "/" + username + "/" + filename;
        Util.fileWriter(filePath, filetext, false);

        JSONObject response = new JSONObject();
        response.put("message", "wrote file " + filename);
        return response.toJSONString();
    }


    @RequestMapping(value = "/in_out/{filename:.+}", method = RequestMethod.DELETE)
    public
    @ResponseBody
    String deleteFile(
            @RequestHeader("Authorization") String basic,
            @PathVariable("filename") String filename
    ) {

        if (!auth.login(basic)) {
            throw new CelloUnauthorizedException("invalid username/password");
        }
        String username = auth.getUsername(basic);

        String filepath = _resultPath + "/" + username + "/" + filename;
        File f = new File(filepath);

        if (!f.exists()) {
            throw new CelloNotFoundException("file " + filename + " not found");
        }


        Util.deleteFile(new File(filepath));
        JSONObject response = new JSONObject();
        response.put("message", "deleted file " + filename);
        return response.toJSONString();
    }
}

