FROM ubuntu:latest

EXPOSE 8080:8080

# Install dependencies
RUN apt-get update
RUN apt-get install -y tzdata
RUN DEBIAN_FRONTEND=noninteractive apt-get install -y git gcc-multilib  openjdk-8-jdk openjdk-8-jre-headless maven \
gnuplot ghostscript graphviz imagemagick python3-matplotlib 


# Copy the local (outside Docker) source into the working directory,
RUN mkdir /cello
WORKDIR /cello

COPY demo ./demo
COPY resources ./resources
COPY src ./src
COPY tools ./tools
COPY pom.xml .
COPY src/main ./main

RUN cd ./resources/library && bash install_local_jars.sh

RUN mvn -e clean compile 

ENTRYPOINT ["mvn", "spring-boot:run"]


