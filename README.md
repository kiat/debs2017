# DEBS 2017 Grand Challenge



# How to compile and build a single jar file 


	mvn clean package 





# Push a Docker Image 

	docker build -t git.project-hobbit.eu:4567/dj16/rice  .

	docker login git.project-hobbit.eu:4567

	docker push git.project-hobbit.eu:4567/dj16/rice 

