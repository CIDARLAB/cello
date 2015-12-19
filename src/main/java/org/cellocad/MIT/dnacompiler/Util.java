package org.cellocad.MIT.dnacompiler;
/**
 * Created by Bryan Der on 3/26/14.
 */

import org.apache.tools.ant.DirectoryScanner;

import java.io.*;
import java.util.ArrayList;
import java.util.StringTokenizer;


/***********************************************************************

 Synopsis    [ methods that do not belong to any class will be put here. ]

 functions include:
 execute command
 write file
 read and tokenize file
 read file lines

 ***********************************************************************/

public class Util{


    /***********************************************************************

     Synopsis    [  ]

     four decimal places for Double

     ***********************************************************************/
    public static String sc(Double score) {
        String score_str = String.format("%-5.4f", score);
        return score_str;
    }


    /***********************************************************************

     Synopsis    [  ]

     terminal shell command executed from within Java code,
     waits for process to complete and returns String result from shell command

     ***********************************************************************/
    public static String executeCommand(String command) {

        StringBuffer output = new StringBuffer();

        Process p;
        try {
            p = Runtime.getRuntime().exec(command);
            p.waitFor();
            try(BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()))) {
                String line = "";
                while ((line = reader.readLine())!= null) {
                    output.append(line + "\n");
                }
                reader.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return output.toString();

    }

    /***********************************************************************

     Synopsis    [  ]

     String to file, with append to file true/false option

     ***********************************************************************/
    public static void fileWriter(String outfile, String contents, boolean append) {
        try{
            BufferedWriter bw  = new BufferedWriter(new FileWriter(new File(outfile), append)); //append
            bw.write(contents);
            bw.close();
        }catch(Exception e) {
        }
    }


    /***********************************************************************

     Synopsis    [  ]

     outer ArrayList = lines
     inner ArrayList = tokens

     ***********************************************************************/
    public static ArrayList< ArrayList<String> > fileTokenizer(String fin) {

        File file = new File(fin);
        BufferedReader br;
        FileReader fr;
        ArrayList<String> lines = new ArrayList<String>();
        try {
            fr = new FileReader(file);
            br = new BufferedReader(fr);
            String line;
            try {
                while((line = br.readLine()) != null ) {
                    //line = line.replaceAll("\\(","");
                    //line = line.replaceAll("\\)","");
                    //line = line.replaceAll(","," ");   //tokenize based on whitespace, not commas
                    if(line.length() > 0) {
                        lines.add(line);
                    }
                }
            }
            catch (IOException ex) {
                //System.out.println("IOException when reading input file");
            }
        }
        catch (FileNotFoundException ex) {
            //System.out.println("FileNotFoundException when reading input file");
        }
        ArrayList< ArrayList<String> > tokenized_list = new ArrayList< ArrayList<String> >();

        for(String s: lines) {
            ArrayList<String> tokens = new ArrayList<String>();
            StringTokenizer st = new StringTokenizer(s, " \t\n\r\f,");
            while (st.hasMoreTokens()) {
                tokens.add(st.nextToken());
            }
            tokenized_list.add(tokens);
        }

        return tokenized_list;
    }

    /***********************************************************************

     Synopsis    [  ]

     line to tokens

     ***********************************************************************/
    public static ArrayList<String> lineTokenizer(String line) {

        ArrayList<String> tokens = new ArrayList<String>();
        StringTokenizer st = new StringTokenizer(line, " \t\n\r\f,");
        while (st.hasMoreTokens()) {
            tokens.add(st.nextToken());
        }
        return tokens;
    }

    /***********************************************************************

     Synopsis    [  ]

     read file lines to ArrayList<String>

     ***********************************************************************/
    public static ArrayList<String> fileLines(String fin) {

        File file = new File(fin);
        BufferedReader br;
        FileReader fr;
        ArrayList<String> lines = new ArrayList<String>();
        try {
            fr = new FileReader(file);
            br = new BufferedReader(fr);
            String line;
            try {
                while((line = br.readLine()) != null ) {
                    //line = line.replaceAll("\\(","");
                    //line = line.replaceAll("\\)","");
                    //line = line.replaceAll(",","");
                    if(line.length() > 0) {
                        lines.add(line);
                    }
                }
            }
            catch (IOException ex) {
                //System.out.println("IOException when reading " + fin);
            }
        }
        catch (FileNotFoundException ex) {
            //System.out.println("FileNotFoundException when reading " + fin);
        }

        return lines;
    }

    public static boolean createDirectory(String dirname) {
        File file = new File(dirname);
        if (!file.exists()) {
            if (file.mkdir()) {
                ////System.out.println("Directory " + dirname + " is created.");
            } else {
                ////System.out.println("Failed to create directory " + dirname);
            }
            return true;
        }
        else {
            return false;
        }
    }


    public static String[] filenamesInDirectory(String dirname) {
        File f = new File(dirname);

        if (f.exists()) {

            DirectoryScanner scanner = new DirectoryScanner();
            //scanner.setIncludes(new String[]{"*"+keyword+"*"+extension});
            scanner.setBasedir(dirname);
            scanner.setCaseSensitive(false);
            scanner.scan();
            String[] files = scanner.getIncludedFiles();

            return files;
        }
        else {
            return null;
        }
    }

    public static void deleteFilesInDirectory(File fileOrDirectory) {

        if(fileOrDirectory.isDirectory()){

            if(fileOrDirectory.list().length>0){
                for (File child : fileOrDirectory.listFiles()) {
                    if(!child.isDirectory()) {
                        //System.out.println("deleting " + child.getName());
                        child.delete();
                    }
                }
            }else{
                //System.out.println("Directory is empty!");
            }

        }else{
            //System.out.println("This is not a directory");
        }
    }

    public static void deleteEmptyDirectory(File fileOrDirectory) {
        if(fileOrDirectory.isDirectory()){
            if(fileOrDirectory.list().length==0){
                //System.out.println("deleting " + fileOrDirectory.getName());
                fileOrDirectory.delete();
            }
        }
    }

    public static void deleteFile(File file) {
        if(!file.isDirectory()){
            file.delete();
            //System.out.println("deleting " + file.getName());
        }
    }



}
