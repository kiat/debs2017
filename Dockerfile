FROM java
RUN mkdir -p /usr/src/debs
WORKDIR /usr/src/debs

ADD ./target/debs2017rice-1.0-SNAPSHOT-jar-with-dependencies.jar  /usr/src/debs

CMD ["java", "-cp", "debs2017rice-1.0-SNAPSHOT-jar-with-dependencies.jar", "edu.rice.system.DebsParrotBenchmarkSystemRunner"]



