FROM java
RUN mkdir -p /usr/src/debs
WORKDIR /usr/src/debs
ADD ./debs-parrotbenchmark-system-1.0-SNAPSHOT.jar /usr/src/debs
CMD ["java", "-cp", "debs-parrotbenchmark-system-1.0-SNAPSHOT.jar", "DebsParrotBenchmarkSystemRunner"]