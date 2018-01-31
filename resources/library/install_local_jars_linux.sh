#!/bin/bash

mvn install:install-file -Dfile=NetSynth.jar -DgroupId=org.cellocad -DartifactId=netsynth -Dversion=1.0 -Dpackaging=jar
mvn install:install-file -Dfile=eugene-2.0.1-SNAPSHOT-jar-with-dependencies.jar -DgroupId=org -DartifactId=eugene -Dversion=2.0.1-SNAPSHOT -Dpackaging=jar
