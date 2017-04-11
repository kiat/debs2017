# DEBS 2017 Grand Challenge



# How to compile and build a single jar file 


	mvn clean compile assembly:single


# Running 

	java -XX:-UseGCOverheadLimit  -Xms4g -Xmx15g  -cp ./target/readingRDF-1.0-SNAPSHOT-jar-with-dependencies.jar edu.rice.readingRDF.ReadingRDFMain

 


# Push a Docker Image 

	docker build -t git.project-hobbit.eu:4567/dimitrijejankov/adapter  .

	docker login git.project-hobbit.eu:4567

	docker push git.project-hobbit.eu:4567/dimitrijejankov/adapter 

