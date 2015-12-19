package org.cellocad.MIT.dnacompiler;


import java.util.ArrayList;

public class Wrapper {


    public static void main(String args[]){

        //"-verilog structural_NOR3_NOT1.v -synthesis originalstructural -netsynth_swaps false -prefix test_ -assignment_algorithm reload -reload 1432325311140_A000_logic_circuit.txt"

        ArrayList<String> lc_files = Util.fileLines(args[0]);

        int counter = 0;

        for(String lc_txt: lc_files) {

            //String jobid = lc_txt.split("_")[3];

            String jobid = String.format("%03d", counter);

            String arglist = "-verilog structural_NOR3_NOT2.v " +
                    "-synthesis originalstructural " +
                    "-netsynth_swaps false " +
                    //"-histogram false " +
                    "-assignment_algorithm reload " +
                    "-reload " + lc_txt + " " +
                    "-jobID " + jobid;

            String[] argarray = arglist.split(" ");

            DNACompiler dnaCompiler = new DNACompiler();
            dnaCompiler.run(argarray);

            counter++;
        }
    }
}
