FROM java
RUN mkdir -p /usr/src/debs

WORKDIR /usr/src/debs

ADD ./target/debs-rice-1.0-SNAPSHOT.jar /usr/src/debs
ADD ./1000molding_machine.metadata.data  /usr/src/debs
ADD ./molding_machine_5000dp.metadata.data  /usr/src/debs
ADD ./system.ttl  /usr/src/debs


CMD ["java", "-cp", "debs-rice-1.0-SNAPSHOT.jar", "edu.rice.DebsParrotBenchmarkSystemRunner"]
