package org.cellocad.MIT.dnacompiler;

/**
 * Created by Bryan Der on 3/26/14.
 */

import lombok.Getter;
import lombok.Setter;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Runtime arguments given in the command line
 */

public class Args {

    //paths
    @Getter @Setter private String _username = ""; // to search results directory of user for custom UCF
    @Getter @Setter private String _jobID = ""; // unique identifier for a job
    @Getter @Setter private String _home = "";  // absolute path to project root directory
    @Getter @Setter private String _datapath = "/resources/data/"; // subpath to data files (relative to _home)
    @Getter @Setter private String _UCFfilepath = ""; //filepath for the UCF .json file
    @Getter @Setter private DNACompiler.CircuitType _circuit_type = DNACompiler.CircuitType.combinational;

    @Getter @Setter private String _output_directory = ""; // path to directory where output files will be saved

    //synthesis (NetSynth)
    @Getter @Setter private String _synthesis   = "defaultmode"; // defaultmode uses ABC and Espresso, and chooses the best
    @Getter @Setter private String _output_or   = "false"; // allow OUTPUT_OR

    // this option allows NOR gates from the UCF to also be used as NOT gates.
    // if a gate is listed as 'NOT' in the UCF, it will only be assigned to NOT gates (will not be assigned to NOR gates)
    @Getter @Setter private boolean _NOTequalsNOR1 = true;

    // permute all input assignments
    // e.g.
    // in1, in2 = pTac, pTet
    // in1, in2 = pTet, pTac
    @Getter @Setter private boolean _permute_inputs = false;
    @Getter @Setter private HashMap<String, Double> _input_values = new HashMap<>();

    // not tested
    @Getter @Setter private ArrayList<Integer> _dontcare_rows = new ArrayList<Integer>();  //allows d.c. rows to be ignored when scoring circuits

    //files
    @Getter @Setter private String _fin_verilog = ""; // verilog file with .v extension.  case, assign, or structural
    @Getter @Setter private String _fin_input_promoters = ""; // list of inputs and inlow and inhigh values
    @Getter @Setter private String _fin_output_genes = ""; // list of outputs, name and dna sequence
    @Getter @Setter private String _fin_reload = ""; // reload a circuit from SBOL file.
    @Getter @Setter private String _fin_preset = ""; // hard-code the desired assignment
    @Getter @Setter private String _fin_sensor_module = ""; // hard-code the desired assignment
    @Getter @Setter private String _fin_sequential_waveform = ""; // truth table for sequential circuit

    //score filters
    @Getter @Setter private boolean _toxicity = true; //assignments must pass toxicity threshold
    @Getter @Setter private boolean _noise_margin = true; // assignments must pass threshold analysis
    @Getter @Setter private boolean _check_roadblocking = true; // less-than-or-equal-to 1 roadblocking promoter per txn unit
    @Getter @Setter private boolean _tandem_promoter = false;
    @Getter @Setter private boolean _tandem_NOR = true; //allow tandem promoter in NOR gate, otherwise use two txns for NOR gate
    @Getter @Setter private Double _gate_onoff_threshold = 10.0;
    @Getter @Setter private boolean _snr = false; // generate signal-to-noise ratio plots
    @Getter @Setter private boolean _tpmodel = false; //use tandem promoter model to calculate promoter strength


    @Getter @Setter private boolean _histogram = true; // use cytometry data for a distribution-based score
    @Getter @Setter private Double _histogram_threshold = 0.50; // circuits below threshold will not pass

    @Getter @Setter private Double _toxicity_threshold = 0.75; // circuits below threshold will not pass
    @Getter @Setter private boolean _pareto_frontier = false; // pareto-optimal circuits trade-off circuit score and growth score

    //search
    @Getter @Setter private int _hill_trajectories = 1; // hill climbing and sim annealing, number of trajectories
    @Getter @Setter private int _hill_iterations = 500; // hill climbing and sim annealing, number of iterations per trajectory
    @Getter @Setter private boolean _hill_climb_seed = false; // to use (or not to use) seeding for random number reproducibility

    @Getter @Setter ArrayList<String> _exclude_groups = new ArrayList<>();

    @Getter @Setter private BuildCircuits.AssignmentAlgorithm _assignment_algorithm = BuildCircuits.AssignmentAlgorithm.sim_annealing; //
//    @Getter @Setter private int _timelimit = 10; // seconds.
    @Getter @Setter private boolean _unique_repressor_assignments = true; // only output unique repressor assignments
    @Getter @Setter private boolean _unique_rbs_assignments = false; // rbs variants count as unique assignments
    @Getter @Setter private boolean _output_all_assignments = false; // output all 'unique' assignments
    @Getter @Setter private boolean _assign_C_pBAD = false; //for Lauren's latches/flipflops

    //Eugene
    @Getter @Setter private boolean _eugene = true; // use Eugene to permute plasmid architectures
    @Getter @Setter private boolean _eugene_scars = true; // include scars (replicate sequence that results from GoldenGate assembly of gates)
    @Getter @Setter private boolean _eugene_dnaseq = true; // include the DNA sequence of each part/device in Eugene design
    @Getter @Setter private boolean _plasmid = true; // output plasmid DNA sequences for assigned logic circuits
    @Getter @Setter private boolean _yeast = false; // yeast system for putting DNA sequence to two integration sites
    @Getter @Setter private boolean _genome = false; // genome system for putting DNA sequence to two integration sites


    @Getter @Setter private int _nA = 1; // number of assignments to output
    @Getter @Setter private int _nP = 5; // number of plasmids to output

    //figures
    @Getter @Setter private boolean _figures = true; // make figures (PNG, PDF)
    @Getter @Setter private boolean _dnaplotlib = true; // make dnaplotlib figure
    @Getter @Setter private boolean _truthtable_rpu = true; // make truthtable figures
    @Getter @Setter private boolean _truthtable_tox = false; // make toxicity bargraph figures
    @Getter @Setter private boolean _response_fn = true; // make response function figures
    @Getter @Setter private boolean _write_circuit_json = false; // LogicCircuit to JSON (~10 Mb in size)


    public Args() {

        String _filepath = Args.class.getClassLoader().getResource(".").getPath();

        if (_filepath.contains("/target/")) {
            _filepath = _filepath.substring(0, _filepath.lastIndexOf("/target/"));
        }
        else if(_filepath.contains("/build/")) {
            _filepath = _filepath.substring(0, _filepath.lastIndexOf("/build/"));
        }
        else if(_filepath.contains("/src/")) {
            _filepath = _filepath.substring(0, _filepath.lastIndexOf("/src/"));
        }

        String[] split_rootPath = _filepath.split("/");

        String rootPath = "";
        for(int i=0; i< split_rootPath.length - 1; ++i ) {
            rootPath += "/" + split_rootPath[i];
        }

        String projectName = split_rootPath[split_rootPath.length-1];

        String sourceDir = projectName;
        String sourcePath = rootPath + "/" + sourceDir;
        sourcePath = sourcePath.replace("//", "/");

        this._home = sourcePath;
    }


    public void parse(String args[]) {

        for (int i=0; i < args.length; i++) {

        }

        for (int i=0; i < args.length; i++) {

            if(args[i].equals("-verilog")) {
                String verilog = args[i+1];
                String verilog_file_extension = verilog.substring( verilog.length() - 2 ); //last two chars are .v
                if(verilog_file_extension.equals(".v")) {
                    _fin_verilog = verilog;
                }
                else {
                    throw new IllegalArgumentException("incorrect verilog file type, expecting .v extension");
                }

                File f = new File(_fin_verilog);
                if(!f.exists() || f.isDirectory()) {
                    throw new IllegalArgumentException("_fin_verilog file path does not exist.");
                }
            }

            if(args[i].equals("-jobID")) {
                _jobID = args[i+1];
                String jobid_regex = "[a-zA-Z][0-9a-zA-Z_-]+";
                if(!_jobID.matches(jobid_regex)) {
                    //SBOL displayID's cannot start with a number.
                    throw new IllegalArgumentException("jobID must start with a letter, adhering to this regex: " + jobid_regex);
                }
            }
            if(args[i].equals("-UCF")) {
                boolean fileFound = false;

                // Check exact path
                _UCFfilepath = args[i+1];

                File f = new File(_UCFfilepath);
                if(f.exists()) {
                    fileFound = true;
                }

                // Check user's results directory
                if(! fileFound) {
                    String resultPath = _home + "_results";
                    String userPath = resultPath + "/" + _username;
                    String webUCFPath = userPath + "/" + _UCFfilepath;

                    f = new File(webUCFPath);

                    if(f.exists()) {
                        _UCFfilepath = webUCFPath;
                        fileFound = true;
                    }
                }

                // Check resources/UCF directory
                if (!fileFound) {
                    String ucfPath = _home + "/resources/UCF/" + _UCFfilepath;

                    f = new File(ucfPath);

                    if(f.exists()) {
                        _UCFfilepath = ucfPath;
                        fileFound = true;
                    }
                }

                if(! fileFound) {
                    throw new IllegalArgumentException("_UCFfilepath file path does not exist.");
                }
            }

            if(args[i].equals("-synthesis")) {
                _synthesis = args[i+1];
            }

            if(args[i].equals("-output_or")) {
                _output_or = args[i+1];
            }

            if(args[i].equals("-input_promoters")) {
                _fin_input_promoters = args[i+1];
                File f = new File(_fin_input_promoters);
                if(!f.exists() || f.isDirectory()) {
                    throw new IllegalArgumentException("_fin_input_promoters file path does not exist.");
                }
            }
            if(args[i].equals("-output_genes")) {
                _fin_output_genes = args[i+1];
                File f = new File(_fin_output_genes);
                if(!f.exists() || f.isDirectory()) {
                    throw new IllegalArgumentException("_fin_output_genes file path does not exist.");
                }
            }

            if(args[i].equals("-yeast")) {
                if(args[i+1].equals("true")) {_yeast = true;}
                if(args[i+1].equals("false")){_yeast = false;}
            }

            if(args[i].equals("-genome")) {
                if(args[i+1].equals("true")) {_genome = true;}
                if(args[i+1].equals("false")){_genome = false;}
            }

            if(args[i].equals("-eugene")) {
                if(args[i+1].equals("true")) {_eugene = true;}
                if(args[i+1].equals("false")){_eugene = false;}
            }
            if(args[i].equals("-eugene_scars")) {
                if(args[i+1].equals("true")) {_eugene_scars = true;}
                if(args[i+1].equals("false")){_eugene_scars = false;}
            }
            if(args[i].equals("-eugene_dnaseq")) {
                if(args[i+1].equals("true")) {_eugene_dnaseq = true;}
                if(args[i+1].equals("false")){_eugene_dnaseq = false;}
            }
            if(args[i].equals("-nA")) {
                _nA = Integer.parseInt(args[i+1]);
            }
            if(args[i].equals("-nP")) {
                _nP = Integer.parseInt(args[i+1]);
            }
            if(args[i].equals("-output_all_assignments")) {
                if(args[i+1].equals("true")) {_output_all_assignments = true;}
                if(args[i+1].equals("false")){_output_all_assignments = false;}
            }
            if(args[i].equals("-plasmid")) {
                if(args[i+1].equals("true")) {_plasmid = true;}
                if(args[i+1].equals("false")){_plasmid = false;}
            }
            if(args[i].equals("-reload")) {
                _fin_reload = args[i+1];
                File f = new File(_fin_reload);
                if(!f.exists() || f.isDirectory()) {
                    throw new IllegalArgumentException("_fin_reload file path does not exist: " + _fin_reload);
                }
            }
            if(args[i].equals("-preset")) {
                _fin_preset = args[i+1];
                File f = new File(_fin_preset);
                if(!f.exists() || f.isDirectory()) {
                    throw new IllegalArgumentException("_fin_preset file path does not exist.");
                }
            }
            if(args[i].equals("-waveform")) {
                _fin_sequential_waveform = args[i+1];
                File f = new File(_fin_sequential_waveform);
                if(!f.exists() || f.isDirectory()) {
                    throw new IllegalArgumentException("_fin_sequential_waveform file path does not exist.");
                }
            }
            if(args[i].equals("-tandem_promoter")) {
                if(args[i+1].equals("true")) {_tandem_promoter = true;}
                if(args[i+1].equals("false")){_tandem_promoter = false;}
            }
            if(args[i].equals("-tandem_NOR")) {
                if(args[i+1].equals("true")) {_tandem_NOR = true;}
                if(args[i+1].equals("false")){_tandem_NOR = false;}
            }
            if(args[i].equals("-tpmodel")) {
                if(args[i+1].equals("true")) {_tpmodel = true;}
                if(args[i+1].equals("false")){_tpmodel = false;}
            }
            if(args[i].equals("-permute_inputs")) {
                if(args[i+1].equals("true")) {_permute_inputs = true;}
                if(args[i+1].equals("false")){_permute_inputs = false;}
            }
            if(args[i].equals("-assignment_algorithm")) {
                //_assignment_algorithm = args[i+1];
                String assignment_algorithm = args[i+1];
                _assignment_algorithm = BuildCircuits.getAssignmentAlgorithm(assignment_algorithm);
            }
//            if(args[i].equals("-timelimit")) {
//                _timelimit = Integer.parseInt(args[i+1]);
//            }
            if(args[i].equals("-hill_iterations")) {
                _hill_iterations = Integer.parseInt(args[i+1]);
            }
            if(args[i].equals("-hill_trajectories")) {
                _hill_trajectories = Integer.parseInt(args[i+1]);
            }
            if(args[i].equals("-hill_climb_seed")) {
                if(args[i+1].equals("true")) {_hill_climb_seed = true;}
                if(args[i+1].equals("false")){_hill_climb_seed = false;}
            }
            if(args[i].equals("-figures")) {
                if(args[i+1].equals("true")) {_figures = true;}
                if(args[i+1].equals("false")){_figures = false;}
            }
            if(args[i].equals("-dnaplotlib")) {
                if(args[i+1].equals("true")) {_dnaplotlib = true;}
                if(args[i+1].equals("false")){_dnaplotlib = false;}
            }
            if(args[i].equals("-noise_margin")) {
                if(args[i+1].equals("true")) {_noise_margin = true;}
                if(args[i+1].equals("false")){_noise_margin = false;}
            }
            if(args[i].equals("-roadblock")) {
                if(args[i+1].equals("true")) {_check_roadblocking = true;}
                if(args[i+1].equals("false")){_check_roadblocking = false;}
            }
            if(args[i].equals("-histogram")) {
                if(args[i+1].equals("true")) {_histogram = true;}
                if(args[i+1].equals("false")){_histogram = false;}
            }
            if(args[i].equals("-toxicity")) {
                if(args[i+1].equals("true")) {_toxicity = true;}
                if(args[i+1].equals("false")){_toxicity = false;}
            }
            if(args[i].equals("-toxicity_threshold")) {
                _toxicity_threshold = Double.valueOf(args[i+1]);
            }
            if(args[i].equals("-pareto_frontier")) {
                if(args[i+1].equals("true")) {_pareto_frontier = true;}
                if(args[i+1].equals("false")){_pareto_frontier = false;}
            }
            if(args[i].equals("-truthtable_rpu")) {
                if(args[i+1].equals("true")) {_truthtable_rpu = true;}
                if(args[i+1].equals("false")){_truthtable_rpu = false;}
            }
            if(args[i].equals("-truthtable_tox")) {
                if(args[i+1].equals("true")) {_truthtable_tox = true;}
                if(args[i+1].equals("false")){_truthtable_tox = false;}
            }
            if(args[i].equals("-response_fn")) {
                if(args[i+1].equals("true")) {_response_fn = true;}
                if(args[i+1].equals("false")){_response_fn = false;}
            }
            if(args[i].equals("-gate_onoff_threshold")) {
                _gate_onoff_threshold = Double.valueOf(args[i+1]);
            }

            if(args[i].equals("-output_directory")) {
                _output_directory = args[i+1];
            }
            if(args[i].equals("-unique_repressor_assignments")) {
                if(args[i+1].equals("true")) {_unique_repressor_assignments = true;}
                if(args[i+1].equals("false")){_unique_repressor_assignments = false;}
            }
            if(args[i].equals("-unique_rbs_assignments")) {
                if(args[i+1].equals("true")) {_unique_rbs_assignments = true;}
                if(args[i+1].equals("false")){_unique_rbs_assignments = false;}
            }
            if(args[i].equals("-assign_C_pBAD")) {
                if(args[i+1].equals("true")) {_assign_C_pBAD = true;}
                if(args[i+1].equals("false")){_assign_C_pBAD = false;}
            }
            if(args[i].equals("-circuit_type")) {
                String type = args[i+1];
                if(DNACompiler.CircuitType.combinational.toString().equals(type)) {
                    _circuit_type = DNACompiler.CircuitType.combinational;
                }
                else if(DNACompiler.CircuitType.sequential.toString().equals(type)) {
                    _circuit_type = DNACompiler.CircuitType.sequential;
                }
            }

            //Feature: dontcare_rows
            //Syntax for commandline: -dontcare_rows 0,2-4 (ignores rows 0,2,3,4)
            if(args[i].equals("-dontcare_rows")) {
                String[] rows = args[i+1].split("\\,");
                for(String row: rows) {

                    if(row.contains(("-"))) {
                        String[] range = row.split("\\-");

                        Integer range_begin = Integer.valueOf(range[0]);
                        Integer range_end = Integer.valueOf(range[1]);

                        for (int j = range_begin; j <= range_end; ++j) {
                            _dontcare_rows.add(Integer.valueOf(j));
                        }
                    }
                    else {
                        _dontcare_rows.add(Integer.valueOf(row));
                    }
                }
            }

            if(args[i].equals("-exclude_groups")) {
                String comma_separated_list = args[i+1];
                ArrayList<String> groups = Util.lineTokenizer(comma_separated_list);
                _exclude_groups = groups;
            }

            if(args[i].equals("-options")) {
                String file  = args[i+1];
                ArrayList<String> fileLines = Util.fileLines(file);
                ArrayList<String> tokens = new ArrayList<>();
                for(String line: fileLines) {
                    tokens.addAll(Util.lineTokenizer(line));
                }
                String[] args_array = tokens.toArray(new String[tokens.size()]);
                parse(args_array);
            }
        }
    }

}
