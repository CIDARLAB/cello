package org.cellocad.api;

import org.apache.commons.io.FileUtils;
import org.cellocad.BU.DAG.DAGW;
import org.cellocad.BU.netsynth.NetSynth;
import org.cellocad.BU.netsynth.NetSynthSwitch;
import org.cellocad.MIT.dnacompiler.*;
import org.json.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

/**
 * Created by Bryan Der on 8/21/15.
 */

@RestController
public class MainController extends BaseController {

//    @RequestMapping(value="",method= RequestMethod.GET)
//    public @ResponseBody
//    String dnaCompiler(
//            @RequestHeader("Authorization") String basic
//    ) {
//        if(!auth.login(basic)) {
//            throw new CelloUnauthorizedException("invalid username/password");
//        }
//        String username = auth.getUsername(basic);
//        return "Welcome, " + username;
//    }


    @RequestMapping(value="/submit",method= RequestMethod.POST, produces = "application/json")
    public @ResponseBody
    JSONObject dnaCompiler(
            @RequestHeader("Authorization") String basic,
            @RequestParam Map<String, String> params
    ) {

        if(!auth.login(basic)) {
            throw new CelloUnauthorizedException("invalid username/password");
        }
        String username = auth.getUsername(basic);

        String id = params.get("id");
        String inputs  = params.get("input_promoter_data");
        String outputs = params.get("output_gene_data");
        String verilog_text = params.get("verilog_text");
        String user_options = params.get("user_options");


        String user_directory = _resultPath + "/" + username + "/";

        String output_directory = user_directory + id + "/";

        File f = new File(user_directory);
        if (!f.exists()) {
            Util.createDirectory(user_directory);
        }

        File f2 = new File(output_directory);
        if (f2.exists()) {
            try {
                FileUtils.deleteDirectory(f2);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        Util.createDirectory(output_directory);

        JSONObject out = new JSONObject();
        out.put("username", username);
        out.put("id", id);
        out.put("input_promoter_data", inputs);
        out.put("output_gene_data", outputs);
        out.put("verilog_text", verilog_text);
        out.put("user_options", user_options);


        String verilog_filename = output_directory + id + "_verilog.v";
        String inputs_filename  = output_directory + id + "_inputs.txt";
        String outputs_filename = output_directory + id + "_outputs.txt";

        Util.fileWriter(verilog_filename, verilog_text, false);
        Util.fileWriter(inputs_filename,  inputs, false);
        Util.fileWriter(outputs_filename, outputs, false);

        String options =
                " -username " + username +
                        " -verilog " + verilog_filename +
                        " -jobID " + id +
                        " -output_directory " + output_directory +
                        " -input_promoters " + inputs_filename +
                        " -output_genes " + outputs_filename +
                        " -figures " + "true" +
                        " -plasmid " + "true" +
                        " " + user_options;


        ArrayList<String> optList = Util.lineTokenizer(options);
        String[] optArr = new String[optList.size()];
        optArr = optList.toArray(optArr);


        DNACompiler dnaCompiler = new DNACompiler();
        dnaCompiler.run(optArr);


        String message = "WARNING: Unknown result status.";
        
        if(dnaCompiler.get_result_status() == DNACompiler.ResultStatus.success) {
             message = "SUCCESS: Loading results page.";
        }

        else if(dnaCompiler.get_result_status() == DNACompiler.ResultStatus.wiring_diagram_invalid) {
             message = "Wiring diagram invalid.";
        }

        else if(dnaCompiler.get_result_status() == DNACompiler.ResultStatus.ucf_invalid) {
             message = "UCF invalid.";
        }

        else if(dnaCompiler.get_result_status() == DNACompiler.ResultStatus.not_enough_gates_in_library) {
             message = "Not enough gates in library.";
        }

        else if(dnaCompiler.get_result_status() == DNACompiler.ResultStatus.no_assignments_found) {
             message = "No assignments found.";
        }

        else if(dnaCompiler.get_result_status() == DNACompiler.ResultStatus.roadblocking_inputs) {
             message = "WARNING: roadblocking inputs. Loading results page.";
        }

        JSONObject response = new JSONObject();
        response.put("message", message);
        return response;
    }





    @RequestMapping(value="/netsynth",method= RequestMethod.POST)
    public @ResponseBody
    String getNetlist(
            @RequestHeader("Authorization") String basic,
            @RequestParam String verilog_text
    ) {

        if(!auth.login(basic)) {
            throw new CelloUnauthorizedException("invalid username/password");
        }
        String username = auth.getUsername(basic);

        DAGW GW = NetSynth.runNetSynthCode(verilog_text, new ArrayList<NetSynthSwitch>(), new JSONArray());

        org.cellocad.MIT.dnacompiler.LogicCircuit abstract_logic_circuit = new LogicCircuit(GW.Gates, GW.Wires);
        LogicCircuitUtil.renameGatesWires(abstract_logic_circuit);

        String netlist = Netlist.getNetlist(abstract_logic_circuit);
        String netlist2 = abstract_logic_circuit.printNetlist();

        if(netlist.contains("INPUT") && netlist.contains("OUTPUT")) {

            String[] lines = netlist.split("\n");
            for(int i=0; i<lines.length; ++i) {
                String line = lines[i];
                if(line.contains("INPUT")) {
                    continue;
                }
                if(!line.contains("(") || !line.contains(")")) {
                    return "Invalid netlist. Are any outputs always 0 or always 1?";
                }
            }
            return netlist2;
        }

        return "NetSynth finished but the netlist is invalid";
    }



    public String newUserSetup(String username) {

        String user_directory = _resultPath + "/" + username + "/";

        File f = new File(user_directory);
        if (!f.exists()) {
            Util.createDirectory(user_directory);
        }

        String pTac = "pTac 0.0034 2.8 AACGATCGTTGGCTGTGTTGACAATTAATCATCGGCTCGTATAATGTGTGGAATTGTGAGCGCTCACAATT";
        String pTet = "pTet 0.0013 4.4 TACTCCACCGTTGGCTTTTTTCCCTATCAGTGATAGAGATTGACATCCCTATCAGTGATAGAGATAATGAGCAC";
        String pBAD = "pBAD 0.0082 2.5 ACTTTTCATACTCCCGCCATTCAGAGAAGAAACCAATTGTCCATATTGCATCAGACATTGCCGTCACTGCGTCTTTTACTGGCTCTTCTCGCTAACCAAACCGGTAACCCCGCTTATTAAAAGCATTCTGTAACAAAGCGGGACCAAAGCCATGACAAAAACGCGTAACAAAAGTGTCTATAATCACGGCAGAAAAGTCCACATTGATTATTTGCACGGCGTCACACTTTGCTATGCCATAGCATTTTTATCCATAAGATTAGCGGATCCTACCTGACGCTTTTTATCGCAACTCTCTACTGTTTCTCCATACCCGTTTTTTTGGGCTAGC";
        String pLuxStar = "pLuxStar 0.025 0.31 ATAGCTTCTTACCGGACCTGTAGGATCGTACAGGTTTACGCAAGAAAATGGTTTGTTACTTTCGAATAAA";
        String YFP = "YFP CTGAAGCTGTCACCGGATGTGCTTTCCGGTCTGATGAGTCCGTGAGGACGAAACAGCCTCTACAAATAATTTTGTTTAATACTAGAGAAAGAGGGGAAATACTAGATGGTGAGCAAGGGCGAGGAGCTGTTCACCGGGGTGGTGCCCATCCTGGTCGAGCTGGACGGCGACGTAAACGGCCACAAGTTCAGCGTGTCCGGCGAGGGCGAGGGCGATGCCACCTACGGCAAGCTGACCCTGAAGTTCATCTGCACCACAGGCAAGCTGCCCGTGCCCTGGCCCACCCTCGTGACCACCTTCGGCTACGGCCTGCAATGCTTCGCCCGCTACCCCGACCACATGAAGCTGCACGACTTCTTCAAGTCCGCCATGCCCGAAGGCTACGTCCAGGAGCGCACCATCTTCTTCAAGGACGACGGCAACTACAAGACCCGCGCCGAGGTGAAGTTCGAGGGCGACACCCTGGTGAACCGCATCGAGCTGAAGGGCATCGACTTCAAGGAGGACGGCAACATCCTGGGGCACAAGCTGGAGTACAACTACAACAGCCACAACGTCTATATCATGGCCGACAAGCAGAAGAACGGCATCAAGGTGAACTTCAAGATCCGCCACAACATCGAGGACGGCAGCGTGCAGCTCGCCGACCACTACCAGCAGAACACCCCAATCGGCGACGGCCCCGTGCTGCTGCCCGACAACCACTACCTTAGCTACCAGTCCGCCCTGAGCAAAGACCCCAACGAGAAGCGCGATCACATGGTCCTGCTGGAGTTCGTGACCGCCGCCGGGATCACTCTCGGCATGGACGAGCTGTACAAGTAACTCGGTACCAAATTCCAGAAAAGAGGCCTCCCGAAAGGGGGGCCTTTTTTCGTTTTGGTCC";
        String RFP = "RFP CTGAAGTGGTCGTGATCTGAAACTCGATCACCTGATGAGCTCAAGGCAGAGCGAAACCACCTCTACAAATAATTTTGTTTAATACTAGAGTCACACAGGAAAGTACTAGATGGCTTCCTCCGAAGACGTTATCAAAGAGTTCATGCGTTTCAAAGTTCGTATGGAAGGTTCCGTTAACGGTCACGAGTTCGAAATCGAAGGTGAAGGTGAAGGTCGTCCGTACGAAGGTACCCAGACCGCTAAACTGAAAGTTACCAAAGGTGGTCCGCTGCCGTTCGCTTGGGACATCCTGTCCCCGCAGTTCCAGTACGGTTCCAAAGCTTACGTTAAACACCCGGCTGACATCCCGGACTACCTGAAACTGTCCTTCCCGGAAGGTTTCAAATGGGAACGTGTTATGAACTTCGAAGACGGTGGTGTTGTTACCGTTACCCAGGACTCCTCCCTGCAAGACGGTGAGTTCATCTACAAAGTTAAACTGCGTGGTACCAACTTCCCGTCCGACGGTCCGGTTATGCAGAAAAAAACCATGGGTTGGGAAGCTTCCACCGAACGTATGTACCCGGAAGACGGTGCTCTGAAAGGTGAAATCAAAATGCGTCTGAAACTGAAAGACGGTGGTCACTACGACGCTGAAGTTAAAACCACCTACATGGCTAAAAAACCGGTTCAGCTGCCGGGTGCTTACAAAACCGACATCAAACTGGACATCACCTCCCACAACGAAGACTACACCATCGTTGAACAGTACGAACGTGCTGAAGGTCGTCACTCCACCGGTGCTTAATAACAGATAAAAAAAATCCTTAGCTTTCGCTAAGGATGATTTCT";
        String BFP = "BFP CTGAAGTTCCAGTCGAGACCTGAAGTGGGTTTCCTGATGAGGCTGTGGAGAGAGCGAAAGCTTTACTCCCGCACAAGCCGAAACTGGAACCTCTACAAATAATTTTGTTTAAGAGTCACACAGGAAAGTACTAGATGAGCGAGCTGATTAAGGAGAACATGCACATGAAGCTGTACATGGAGGGCACCGTGGACAACCATCACTTCAAGTGCACATCCGAGGGCGAAGGCAAGCCCTACGAGGGCACCCAGACCATGAGAATCAAGGTGGTCGAGGGCGGCCCTCTCCCCTTCGCCTTCGACATCCTGGCTACTAGCTTCCTCTACGGCAGCAAGACCTTCATCAACCACACCCAGGGCATCCCCGACTTCTTCAAGCAGTCCTTCCCTGAGGGCTTCACATGGGAGAGAGTCACCACATACGAAGATGGGGGCGTGCTGACCGCTACCCAGGACACCAGCCTCCAGGACGGCTGCCTCATCTACAACGTCAAGATCAGAGGGGTGAACTTCACATCCAACGGCCCTGTGATGCAGAAGAAAACACTCGGCTGGGAGGCCTTCACCGAGACGCTGTACCCCGCTGACGGCGGCCTGGAAGGCAGAAACGACATGGCCCTGAAGCTCGTGGGCGGGAGCCATCTGATCGCAAACATCAAGACCACATATAGATCCAAGAAACCCGCTAAGAACCTCAAGATGCCTGGCGTCTACTATGTGGACTACAGACTGGAAAGAATCAAGGAGGCCAACAACGAGACCTACGTCGAGCAGCACGAGGTGGCAGTGGCCAGATACTGCGACCTCCCTAGCAAACTGGGGCACTAACCAGGCATCAAATAAAACGAAAGGCTCAGTCGAAAGACTGGGCCTTTCGTTTTATCTGTTGTTTGTCGGTGAACGCTCTCTACTAGAGTCACACTGGCTCACCTTCGGGTGGGCCTTTCTGCGTTTATA";

        String pTac_file = user_directory + "input_pTac.txt";
        String pTet_file = user_directory + "input_pTet.txt";
        String pBAD_file = user_directory + "input_pBAD.txt";
        String pLuxStar_file = user_directory + "input_pLuxStar.txt";

        String YFP_file = user_directory + "output_YFP.txt";
        String RFP_file = user_directory + "output_RFP.txt";
        String BFP_file = user_directory + "output_BFP.txt";

        Util.fileWriter(pTac_file, pTac + "\n", false);
        Util.fileWriter(pTet_file, pTet + "\n", false);
        Util.fileWriter(pBAD_file, pBAD + "\n", false);
        Util.fileWriter(pLuxStar_file, pLuxStar + "\n", false);

        Util.fileWriter(YFP_file, YFP + "\n", false);
        Util.fileWriter(RFP_file, RFP + "\n", false);
        Util.fileWriter(BFP_file, BFP + "\n", false);

        return "First login: creating default input promoters and output genes.";
    }


}
