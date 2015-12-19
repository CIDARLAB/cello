# How to run Cello

Note: Maven is used for software project management and should be installed on your computer.  See INSTALL.md.




## 1. Using the web application in a web browser

Cello can be used without downloading/installing anything!  All you need is a web browser:

* login at www.cellocad.org
* specify input promoters (name, REU ON/OFF levels, DNA sequence)
* specify output gene(s) (name, DNA sequence)
* specify Verilog (use the drop-down list to get started with case, assign, or structural Verilog)
* click "Validate Verilog"
* click "Run", and the results page opens when completed.  Results will be stored and can be viewed when logging in at a different time.
* optionally, a different UCF can be selected or uploaded in the Settings tab



## 2. Using the code on your computer

compile

```
cd ~/cello/
mvn compile
```

## Starting an instance of the web application on your computer

```
cd ~/cello/
mvn spring-boot:run
```
You'll notice messages indicating that the Spring logging system is not found.  A log4j logging system is being used, so the Spring logging is excluded (see pom.xml).

In a web browser, go to: http://localhost:8080/index.html


Putting the process in the background allows you to logout without terminating the process.
```
ctrl-z
bg
exit
```
Then, to find the process ID on port 8080 upon logging back in, this command can be used:
(note: lsof will have to be installed)
```
sudo lsof -i :8080
```


## Source code / command line

The CelloMain Java class can be run from any working directory:

First, use a text editor to write/save a Verilog file with a .v extension.  This is the only required input.

Command line options are specified using a dash to indicate the option name, followed by a space, followed by the value you'd like to specify for that option.

```
mvn -f ~/cello/pom.xml -PCelloMain -Dexec.args="-verilog /path/to/verilog.v -figures false -plasmid false -dateID myTest"
```

To specify your own inputs, outputs, and UCF, this command line can be used:

```
mvn -f ~/cello/pom.xml -PCelloMain -Dexec.args="-verilog /path/to/verilog.v -input_promoters /path/to/inputs.txt -output_genes /path/to/outputs.txt -UCF /path/to/myUCF.json"
```

[input promoters example file](resources/data/inputs/Inputs.txt)

[output genes example file](resources/data/outputs/Outputs.txt)

[UCF-guide.md](UCF-guide.md)


## Miscellaneous

Other Java classes with "public static void main(String[] args)" functions can be run from any working directory:

```
mvn -f ~/cello/pom.xml exec:java -Dexec.mainClass="org.cellocad.MIT.misc.ScarMap" -Dexec.args="notA_nor3_tus.csv"
```

You can also batch a large set of designs using the Wrapper class.  You will likely have to modify the code in Wrapper.java to suit your needs.
```
mvn -f ~/cello/pom.xml -PWrapper -Dexec.args="list_of_verilog_files.txt"
```
