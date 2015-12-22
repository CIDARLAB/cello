# How to run Cello

Note: Maven is used for software project management and should be installed on your computer.  See INSTALL.md.


## 1. Web application.

Cello can be used without downloading/installing anything!  All you need is a web browser:

* login at www.cellocad.org
* specify input promoters (name, REU ON/OFF levels, DNA sequence)
* specify output gene(s) (name, DNA sequence)
* specify Verilog (use the drop-down list to get started with case, assign, or structural Verilog)
* click "Validate Verilog"
* click "Run", and the results page opens when completed.  Results will be stored and can be viewed when logging in at a different time.
* optionally, a different UCF can be selected or uploaded in the Settings tab



## 2. Using the API.

The Cello API can also be used to run Cello.  The Cello instance deployed at the www.cellocad.org can be used, but you can also launch an instance of the application on your localhost to test the API.


#### Running Cello locally (localhost = http://127.0.0.1:8080)

First, compile the code:
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

* In the repository, go to the ~/cello/demo/ directory


#### 2a. Curl requests

###### Simple curl test
```
curl -u "username:password" http://127.0.0.1:8080
```

###### Get a netlist
```
curl -u "username:password" http://127.0.0.1:8080/netsynth -X POST --data-urlencode "verilog_text@demo_verilog.v"
```

###### Design a circuit
```
curl -u "username:password" -X POST http://127.0.0.1:8080/submit --data-urlencode "id=demo001" --data-urlencode "verilog_text@demo_verilog.v" --data-urlencode "input_promoter_data@demo_inputs.txt" --data-urlencode "output_gene_data@demo_outputs.txt"
```

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


#### 2b. Cello command-line interface.

The command-line interface is written as a python-based tool, which allows API calls via concise shell commands.  These commands are functionally equivalent to the curl commands but may prove more user-friendly.

Go to ~/cello/tools/pycello/

Please follow the install/run instructions found in the pycello directory.


## 3. Executing compile source code

The CelloMain Java class can be run from any working directory.  However, the same demo files can be used for your convenience.
```
cd ~/cello/demo/
```

A verilog file is the minimum requirement to run Cello.  The default input promoters (pTac, pTet, pBAD), the default gate library, and the default output gene (YFP) will be used to construct a genetic circuit that implements the specified logic function.
```
mvn -f ~/cello/pom.xml -DskipTests=true -PCelloMain -Dexec.args="-verilog demo_verilog.v"
```

To specify your own input promoter data and output genes:
```
mvn -f ~/cello/pom.xml -DskipTests=true -PCelloMain -Dexec.args="-verilog demo_verilog.v -input_promoters demo_inputs.txt -output_genes demo_outputs.txt"
```

To specify your own gate library (User Constraint File, UCF):
```
mvn -f ~/cello/pom.xml -DskipTests=true -PCelloMain -Dexec.args="-verilog demo_verilog.v -input_promoters demo_inputs.txt -output_genes demo_outputs.txt -UCF /path/to/myUCF.json"
```


To write your own UCF, a UCF writer directory can be copied and modified.

See: ~/cello/src/main/java/org/cellocad/adaptors/ucfwriters

The main class is:
ConstraintFileWriter.java

A class exists to write each collection in the UCF.  These can be modified to write your own custom UCF.  Note that some of the gate library data is read from a CSV in the existing examples (data includes: part names, sequences, and response function parameters).  

See: ~/cello/resources/csv_gate_libraries/

You can make your own gate library by modifying the CSV, then modifying the classes in the ucf writer directory to read the customized CSV.



[input promoters example file](resources/data/inputs/Inputs.txt)

[output genes example file](resources/data/outputs/Outputs.txt)

[UCF-guide.md](UCF-guide.md)

