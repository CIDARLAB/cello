import org.apache.tools.ant.DirectoryScanner;
import org.cellocad.MIT.dnacompiler.*;
import org.junit.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Bryan Der on 2/28/16.
 */
public class ReloadCircuitsTest {


    /**
     * This will reload and simulate >60 circuits
     */
    //@Test
    public void reloadCircuits() {

        HashMap<String,String> correct_seq_map = new HashMap<>();

        ArrayList<ArrayList<String>> correct_seqs = Util.fileTokenizer("resources/tested_circuits/circuit_DNA_sequences_v2.csv");
        for(ArrayList<String> row: correct_seqs) {
            String name = row.get(0);
            String dnaseq = row.get(1);
            correct_seq_map.put(name, dnaseq);
        }




        System.out.println("reloadCircuits :: ");
        String baseDirectory = "resources/tested_circuits/";

        DirectoryScanner scanner = new DirectoryScanner();
        scanner.setBasedir(baseDirectory);
        scanner.scan();

        String[] directory_names = scanner.getIncludedDirectories();
        for(String directory: directory_names) {

            if(directory.isEmpty()) {
                continue;
            }

            System.out.println("directory: " + directory);


            String verilogFile = "";
            String circuitFile = "";

            File folder = new File(baseDirectory + directory);
            File[] listOfFiles = folder.listFiles();
            for(File f: listOfFiles) {
                if(f.getName().endsWith(".v")) {
                    verilogFile = f.getAbsolutePath();
                }
                if(f.getName().endsWith("logic_circuit.txt")) {
                    circuitFile = f.getAbsolutePath();
                }
            }

            if(circuitFile.isEmpty()) {
                //System.out.println("Emtpy: " + directory);
                /**
                 Emtpy: AIMPLYB
                 Emtpy: AND
                 Emtpy: ANIMPLYB
                 Emtpy: BIMPLYA
                 Emtpy: BNIMPLYA
                 Emtpy: NAND
                 Emtpy: XNOR
                 Emtpy: XOR
                 */
                continue;
            }
//            else if(!circuitFile.contains("0x01")) {
//                continue;
//            }

            String options = "-histogram false " +
                    "-figures false " +
                    "-assignment_algorithm reload " +
                    "-reload " + circuitFile + " " +
                    "-verilog " + verilogFile + " " +
                    "-jobID " + "circuit_" + directory + " " +
                    "-output_directory " + baseDirectory + directory + "/ ";

            ArrayList<String> optList = Util.lineTokenizer(options);
            String[] optArr = new String[optList.size()];
            optArr = optList.toArray(optArr);

            DNACompiler dnaCompiler = new DNACompiler();
            dnaCompiler.run(optArr);

            LogicCircuit lc = dnaCompiler.get_logic_circuits().get(0);


            String correct_seq = correct_seq_map.get(directory);
            String fulldna = "";

            for(ArrayList<Part> module: lc.get_circuit_module_parts()) {

                for (Part p : module) {


                    //System.out.println(p.get_name() + ", " + p.get_seq());
                    if (p.get_type().equalsIgnoreCase("scar")) {
                        String seq = correct_seq.substring(fulldna.length(), fulldna.length() + 4);
                        p.set_seq(seq);
                    }
                    fulldna += p.get_seq();

                    if (!correct_seq.toUpperCase().startsWith(fulldna.toUpperCase())) {
                        System.out.println("############# Problem with \n" + p.get_name() + " \n" + p.get_seq());
                        Integer length_last = p.get_seq().length();
                        String expected = correct_seq.substring(fulldna.length() - length_last, fulldna.length());
                        //System.out.println(expected);
                        if(!directory.equals("0xB9")) {
                            assert(false);
                        }

                    } else {
                        //System.out.println("match " + p.get_name());
                    }
                }
            }

            if(!directory.equals("0xB9")) {
                assert(correct_seq.toUpperCase().startsWith(fulldna.toUpperCase()));
            }

            System.out.println("sequence, " + directory + ", " + fulldna);

//            System.out.println(correct_seq.length());
//            System.out.println(fulldna.length());

//            assert(correct_seq.length() == fulldna.length());
//            assert(correct_seq.toUpperCase().equals(fulldna.toUpperCase()));
//            System.out.println(directory + ", " + fulldna);

//            System.out.println(correct_seq);
//            System.out.println(fulldna);

//            assert(best_lc.get_scores().get_score() > 10.0);
//            assert(Toxicity.mostToxicRow(best_lc) > 0.50);

        }


    }
}


