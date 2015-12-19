## Installing Cello

###### Note that Cello can be used with no downloads/installations required at www.cellocad.org.


###### Get the code from Git:
 * https://github.com/CIDARLAB/cello
 * Click "Download ZIP"


###### Recommended: Homebrew package manager for Unix
http://brew.sh/
```
ruby -e "$(curl -fsSL https://raw.githubusercontent.com/Homebrew/install/master/install)"
```


###### Install Maven, the software package manager used in Cello
 * Option 1: https://maven.apache.org/download.cgi

 * Option 2: Linux commands
```
wget http://repos.fedorapeople.org/repos/dchen/apache-maven/epel-apache-maven.repo -O /etc/yum.repos.d/epel-apache-maven.repo
yum install apache-maven
```

 * Option 3: Unix commands
```
brew update
brew install maven
```


###### Add local jars to the local maven repository (.m2 directory)
```
cd ~/cello/resources/library
bash install_local_jars.sh
```
the install_local_jars.sh script adds these local jars to the maven repository ~/.m2/repository/
 * mvn install:install-file -Dfile=NetSynth-1.0.jar -DgroupId=org.cellocad -DartifactId=netsynth -Dversion=1.0 -Dpackaging=jar
 * mvn install:install-file -Dfile=eugene-2.0.0-SNAPSHOT-jar-with-dependencies.jar -DgroupId=org -DartifactId=eugene -Dversion=2.0.0 -Dpackaging=jar
 * mvn install:install-file -Dfile=libSBOLj-core2-2.0.0-SNAPSHOT-withDependencies.jar -DgroupId=org.sbolstandard -DartifactId=libSBOLj-core2 -Dversion=2.0.2 -Dpackaging=jar

###### Install other dependencies for automated figure generation

Linux
```
sudo yum install gnuplot
sudo yum install ghostscript
sudo yum install ImageMagick
sudo yum install python-matplotlib
```

Mac
```
brew update
brew install gnuplot
brew install ghostscript
brew install imagemagick
brew install graphviz
```
Matplotlib (available in anaconda python) http://continuum.io/downloads


###### Compile Cello

```
cd ~/cello/
mvn compile
```

###### Run Cello (see [RUN.md](RUN.md) )


### when running Cello using the web interface, results are stored in a directory called 'cello_results', which must live at the same level as the 'cello' directory containing the source code.  Please make this directory.

