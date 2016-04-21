# How to run Cello

Note: Maven is used for software project management and should be installed on your computer.  See [INSTALL.md](INSTALL.md).


## 1. Web application at http://cellocad.org.

Cello can be used without downloading/installing anything!  All you need is a web browser:

* login at www.cellocad.org
* specify input promoters (name, RPU ON/OFF levels, DNA sequence)
* specify output gene(s) (name, DNA sequence)
* specify Verilog (use the drop-down list to get started with case, assign, or structural Verilog)
* click "Run":
	* If the Verilog is not valid, an error message will be displayed.
	* If the Verilog is valid, and the design runs successfully, the results page opens when completed.  Results will be stored and can be viewed when logging in at a later time.
* optionally, a different UCF can be selected or uploaded in the Options tab
	* New UCF's can be generated with a helper python script in ~/cello/tools/pycello/ucf_writer.py.




## 2. Web application installed and run on your computer at http://127.0.0.1:8080.

#### Running Cello locally (localhost = http://127.0.0.1:8080)

First, follow the installation instructions in [INSTALL.md](INSTALL.md).
(Note that other dependencies are required for figure generation, and Java 1.7 will be required to run Eugene within Cello).


Then, compile the code:
```
cd ~/cello/
mvn compile
```

Next, launch the web application
```
cd ~/cello/
mvn spring-boot:run
```

* In a web browser, go to: http://127.0.0.1:8080/index.html

* Go to http://127.0.0.1:8080 and Sign Up by choosing a username and password.






## 3. Using the API.

The Cello API can also be used to run Cello.  The Cello instance deployed at the www.cellocad.org can be used, but you can also launch an instance of the application on your localhost to test the API.


For a complete reference documentation, go to the Home page, click 'About', then click 'API'.  A few use cases are given below.  To try these commands, first go to the ~/cello/demo/ directory where the example files are given.



#### 3a. Curl requests

###### Simple curl test, returns 'pong'
```
curl -u "username:password" http://127.0.0.1:8080/ping
```


###### Get a netlist
```
curl -u "username:password" http://127.0.0.1:8080/netsynth -X POST --data-urlencode "verilog_text@demo_verilog.v"
```

###### Design a circuit
```
curl -u "username:password" -X POST http://127.0.0.1:8080/submit --data-urlencode "id=demo001" --data-urlencode "verilog_text@demo_verilog.v" --data-urlencode "input_promoter_data@demo_inputs.txt" --data-urlencode "output_gene_data@demo_outputs.txt"
```

###### Design a circuit with custom options (see Args.java)
```
curl -u "username:password" -X POST http://127.0.0.1:8080/submit --data-urlencode "id=demo001" --data-urlencode "verilog_text@demo_verilog.v" --data-urlencode "input_promoter_data@demo_inputs.txt" --data-urlencode "output_gene_data@demo_outputs.txt" --data-urlencode "options=-assignment_algorithm hill_climbing -toxicity false"
```


###### Working with custom UCFs

(warning: if posting a UCF with cytometry data, an error may result if the file is too large.  Please contact cellohelp@gmail.com.)

###### There is a python helper (tools/pycello/ucf_writer.py) that reads a CSV and generates a UCF.
###### See also: ~/cello/resources/csv_gate_libraries/
```
python ucf_writer.py  ../../resources/csv_gate_libraries/gates_Eco1C1G1T1.csv ../../resources/csv_gate_libraries/scars.csv > myName.UCF.json
```

post the UCF
```
curl -u "username:password" -X POST http://127.0.0.1:8080/ucf/test.UCF.json --data-urlencode "filetext@myName.UCF.json"
```

validate the UCF
```
curl -u "username:password" -X GET http://127.0.0.1:8080/ucf/myName.UCF.json/validate
```

delete the UCF, if invalid
```
curl -u "username:password" -X DELETE http://127.0.0.1:8080/ucf/myName.UCF.json
```

Run Cello with the new UCF that you created.
```
curl -u "username:password" -X POST http://127.0.0.1:8080/submit --data-urlencode "id=demo001" --data-urlencode "verilog_text@demo_verilog.v" --data-urlencode "input_promoter_data@demo_inputs.txt" --data-urlencode "output_gene_data@demo_outputs.txt" --data-urlencode "options=-UCF myName.UCF.json"
```

Note that extra options can be passed using the options request parameter:
```
--data-urlencode "options=-UCF myName.UCF.json -plasmid false -eugene false -assignment_algorithm hill_climbing"
```

For a list of all available options, look in the source code for the file: ~/cello/src/main/java/org/cellocad/MIT/dnacompiler/Args.java


###### Get a list of your completed jobs
```
curl -u "username:password" -X GET http://127.0.0.1:8080/results 
```

###### Get a list of result file names from a job result.
```
curl -u "username:password" -X GET http://127.0.0.1:8080/results/demo001 
```

###### Get the contents of a specified file.  For example, the file specifying the top-scoring assignment:
```
curl -u "username:password" -X GET http://127.0.0.1:8080/results/demo001/demo001_A000_logic_circuit.txt 
```

###### Get the contents of a specified file.  For example, a Genbank plasmid file:
```
curl -u "username:password" -X GET http://127.0.0.1:8080/results/demo001/demo001_A000_plasmid_circuit_P000.ape
```



###### Create a new input promoter
File name must start with 'input_' and end with '.txt'
```
curl -u "username:password" -X POST http://127.0.0.1:8080/in_out/input_pA.txt --data-urlencode "filetext=pA 0.01 10.0 ATG"
```

###### Create a new output gene
File name must start with 'output_' and end with '.txt'
```
curl -u "username:password" -X POST http://127.0.0.1:8080/in_out/output_Gene1.txt --data-urlencode "filetext=Gene1 ATGCCC"
```


###### Delete an input promoter or output gene
```
curl -u "username:password" -X DELETE http://127.0.0.1:8080/in_out/output_Gene1.txt"
```



#### 3b. Cello command-line interface.

The command-line interface is written as a python-based tool, which allows API calls via concise shell commands.  These commands are functionally equivalent to the curl commands but may prove more user-friendly.

Go to ~/cello/tools/pycello/

Please follow the install/run instructions found in the pycello directory.




## 4. Executing the compiled source code

(Note: if Eugene is used but no rules are specified in the UCF, long runtimes may result due to combinatorics of enumerating variants)


The CelloMain Java class can be run from any working directory.  However, the same demo files can be used for your convenience.
```
cd ~/cello/demo/
```

A verilog file is the minimum requirement to run Cello.  The default input promoters (pTac, pTet, pBAD), the default gate library, and the default output gene (YFP) will be used to construct a genetic circuit that implements the specified logic function.
```
mvn -f ~/cello/pom.xml -DskipTests=true -PCelloMain -Dexec.args="-verilog demo_verilog.v"
```
If successful, this will create a directory in your current working directory called 'job_1458918104011', for example.


To specify your own input promoter data and output genes:
```
mvn -f ~/cello/pom.xml -DskipTests=true -PCelloMain -Dexec.args="-verilog demo_verilog.v -input_promoters demo_inputs.txt -output_genes demo_outputs.txt"
```

To specify your own gate library (User Constraint File, UCF):
```
mvn -f ~/cello/pom.xml -DskipTests=true -PCelloMain -Dexec.args="-verilog demo_verilog.v -input_promoters demo_inputs.txt -output_genes demo_outputs.txt -UCF /path/to/myUCF.json"
```



[input promoters example file](resources/data/inputs/Inputs.txt)

(column 1: promoter name)
(column 2: low RPU)
(column 3: high RPU)
(column 4: DNA sequence)

[output genes example file](resources/data/outputs/Outputs.txt)

(column 1: gene name)
(column 2: DNA sequence)

