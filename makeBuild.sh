#!/bin/bash

mvn clean package



# remove all images with this name
docker images | grep "git.project-hobbit.eu:4567/dj16/rice" | awk '{print $3}' | xargs docker  rmi -f 


docker build -t git.project-hobbit.eu:4567/dj16/rice  .
