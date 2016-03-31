## Installing Cello

###### Note that Cello can be used with no downloads/installations required at www.cellocad.org.

If you experience bugs or the site is down, please contact cellohelp@gmail.com.


###### Get the code from Git:
 * https://github.com/CIDARLAB/cello

###### Make a results directory:
At the same directory-level as the repository, make a directory with the same name appended with '_results'.

For example, if the repository name is ~/cello/:
```
mkdir ~/cello_results/
```

If the repository is ~/cello_v2/:
```
mkdir ~/cello_v2_results/
```

When running Cello using the web interface, results are stored in this directory.



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
 * mvn install:install-file -Dfile=NetSynth.jar -DgroupId=org.cellocad -DartifactId=netsynth -Dversion=1.0 -Dpackaging=jar
 * mvn install:install-file -Dfile=eugene-2.0.0-SNAPSHOT-jar-with-dependencies.jar -DgroupId=org -DartifactId=eugene -Dversion=2.0.0 -Dpackaging=jar


###### Install other dependencies for automated figure generation

If Eugene is used (it's optional), then Java 1.7 is required.  It will not work with 1.8.


Linux
```
sudo yum install gnuplot
sudo yum install ghostscript
sudo yum install ImageMagick
sudo yum install python-matplotlib
sudo yum install graphviz
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



###### Summary of all steps for installing Cello on a fresh Amazon EC2 instance:

```
sudo yum groupinstall "Development tools"
sudo yum install git
sudo yum install gnuplot
sudo yum install ghostscript
sudo yum install graphviz
sudo yum install python-matplotlib
sudo yum install ImageMagick
sudo yum install /lib/libgcc_s.so.1
sudo yum install ld-linux.so.2

sudo yum install wget
sudo wget http://repos.fedorapeople.org/repos/dchen/apache-maven/epel-apache-maven.repo -O /etc/yum.repos.d/epel-apache-maven.repo
sudo yum install -y apache-maven

sudo yum search java | grep 'java-'
sudo yum install java-1.7.0-openjdk.x86_64

cd ~/cello/resources/library
bash install_local_jars.sh
cd ~/cello/
mvn clean compile
mvn spring-boot:run &
sudo lsof -i:8080 (find the process running on port 8080)
```

