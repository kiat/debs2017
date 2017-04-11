FROM java

RUN mkdir -p /usr/src/debs

WORKDIR /usr/src/debs

ADD  target/adapter-0.1-jar-with-dependencies.jar  /usr/src/debs


ENV RABBIT_MQ_HOST_NAME_KEY=rabbit
ENV HOBBIT_SESSION_ID_KEY=mySessionId
ENV SYSTEM_URI_KEY=http://project-hobbit.eu/resources/debs2017/debsricesystem
ENV SYSTEM_PARAMETERS_MODEL_KEY={}
ENV HOBBIT_EXPERIMENT_URI_KEY=exp1


CMD java -cp adapter-0.1-jar-with-dependencies.jar  org.hobbit.core.run.ComponentStarter edu.rice.SystemAdapter