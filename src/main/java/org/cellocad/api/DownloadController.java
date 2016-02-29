package org.cellocad.api;


import org.apache.commons.codec.binary.Base64;
import org.json.simple.JSONObject;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@RestController
public class DownloadController extends BaseController {

    //works for .sbol, but not .json
    @RequestMapping(value="/downloaducf/{filename:.+}",method= RequestMethod.GET)
    public @ResponseBody
    String downloadFile(
            @RequestHeader("Authorization") String basic,
            @PathVariable("filename") String filename,
            @RequestParam String owner
    ) {

        if(!auth.login(basic)) {
            throw new CelloUnauthorizedException("invalid username/password");
        }
        String username = auth.getUsername(basic);

        if(owner.equals("default")) {
            username = "default";
        }

//        String filePath = _resultPath + "/" + username + "/" + filename;

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


        File file = new File(filePath);
        if(!file.exists()){
            throw new CelloNotFoundException("UCF file " + filename + " not found");
        }

//            HttpServletResponse response) throws IOException {
//            String src= DestLocation.concat("\\"+fileName+".jar");
//            InputStream is = new FileInputStream(src);
//            IOUtils.copy(is, response.getOutputStream());
//            response.flushBuffer();


        try {
            //Convert file to byte array to base64 encoded string
            FileInputStream mFileInputStream = new FileInputStream(filePath);
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            byte[] b = new byte[1024];
            int bytesRead = 0;
            while ((bytesRead = mFileInputStream.read(b)) != -1) {
                bos.write(b, 0, bytesRead);
            }

            byte[] ba = bos.toByteArray();
            String file_content = Base64.encodeBase64String(ba);

            mFileInputStream.close();
            bos.close();

            JSONObject toReturn = new JSONObject();
            toReturn.put("data", file_content);
            return toReturn.toJSONString();
//            return file_content;
        }
        catch( java.io.IOException e ) {
            e.printStackTrace();
        }

        /*try {
            InputStream fis = new FileInputStream(file);

            String mimeType = ctx.getMimeType(file.getAbsolutePath());
            response.setContentType(mimeType != null ? mimeType : "application/octet-stream");
            response.setContentLength(-1);
            response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");

            try {
                OutputStream os = response.getOutputStream();

                try {
                    byte[] bufferData = new byte[4096];

                    int read = 0;
                    while ((read = fis.read(bufferData)) != -1) {
                        os.write(bufferData, 0, read);
                    }
                    os.flush();
                    os.close();
                    fis.close();
                }catch (IOException e) {
                    e.printStackTrace();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }*/

        return "error";
    }


    @RequestMapping(value="/downloadzip/{jobid}",method= RequestMethod.GET)
    public @ResponseBody
    String zipDownload(
            @RequestHeader("Authorization") String basic,
            @PathVariable("jobid") String jobid
    ) {

        if(!auth.login(basic)) {
            throw new CelloUnauthorizedException("invalid username/password");
        }
        String username = auth.getUsername(basic);


        try {
            String path = _resultPath + "/" + username + "/" + jobid;

            File directory = new File(path);
            String[] files = directory.list();

            if (files != null && files.length > 0) {
                byte[] zip = zipFiles(directory, files);
                String zipstr = Base64.encodeBase64String(zip);
                return zipstr;
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Compress the given directory with all its files.
     */
    private byte[] zipFiles(File directory, String[] files) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ZipOutputStream zos = new ZipOutputStream(baos);
        //byte bytes[] = new byte[2048];

        addFolderToZip("", directory.getPath(), zos);

        zos.flush();
        baos.flush();
        zos.close();
        baos.close();

        return baos.toByteArray();
    }

    private void addFolderToZip(String path, String srcFolder, ZipOutputStream zip) throws IOException {
        File folder = new File(srcFolder);
        if (folder.list().length == 0) {
            addFileToZip(path , srcFolder, zip, true);
        }
        else {
            for (String fileName : folder.list()) {
                if (path.equals("")) {
                    addFileToZip(folder.getName(), srcFolder + "/" + fileName, zip, false);
                }
                else {
                    addFileToZip(path + "/" + folder.getName(), srcFolder + "/" + fileName, zip, false);
                }
            }
        }
    }

    private void addFileToZip(String path, String srcFile, ZipOutputStream zip, boolean flag) throws IOException {
        File folder = new File(srcFile);
        if (flag) {
            zip.putNextEntry(new ZipEntry(path + "/" +folder.getName() + "/"));
        }
        else {
            if (folder.isDirectory()) {
                addFolderToZip(path, srcFile, zip);
            }
            else {
                byte[] buf = new byte[1024];
                int len;
                FileInputStream in = new FileInputStream(srcFile);
                zip.putNextEntry(new ZipEntry(path + "/" + folder.getName()));
                while ((len = in.read(buf)) > 0) {
                    zip.write(buf, 0, len);
                }
                in.close();
            }
        }
    }


}
