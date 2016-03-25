import org.apache.tools.ant.DirectoryScanner;
import org.cellocad.MIT.dnacompiler.DNACompiler;
import org.cellocad.MIT.dnacompiler.Util;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by Bryan Der on 2/28/16.
 */
public class ReloadCircuitsTest {


    /**
     * This will reload and simulate >60 circuits
     */
    //@Test
    public void reloadCircuits() {

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
        }


    }
}


