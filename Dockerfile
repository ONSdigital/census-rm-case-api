FROM openjdk:11-slim

ARG JAR_FILE=census-rm-case-api*.jar
COPY target/$JAR_FILE /opt/census-rm-case-api.jar

#RUN apt-get update
#RUN apt-get -yq install curl
#RUN apt-get -yq clean

CMD exec /usr/bin/java $JAVA_OPTS -jar /opt/census-rm-case-api.jar
